-- =========================================================================
-- FUNDORA SOCIAL FINTECH - DATABASE SCHEMA (MySQL / H2 DDL)
-- Module III: Relational Database & Entity Relationship Modeling
-- =========================================================================

-- 1. Users Table (Students, Flatmates, Hostel Managers, Merchants)
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(120) NOT NULL UNIQUE,
    phone_number VARCHAR(15) NOT NULL UNIQUE,
    upi_id VARCHAR(50) NOT NULL,
    role VARCHAR(30) NOT NULL DEFAULT 'STUDENT',
    wallet_balance DECIMAL(12, 2) NOT NULL DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Groups Table (FLATMATE_RECURRING for rent/bills, TRIP_EVENT for vacation/events)
CREATE TABLE IF NOT EXISTS fundora_groups (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    description VARCHAR(255),
    group_type VARCHAR(30) NOT NULL DEFAULT 'FLATMATE_RECURRING',
    created_by_user_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_group_creator FOREIGN KEY (created_by_user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 3. Group Members Cross-Reference Table
CREATE TABLE IF NOT EXISTS group_members (
    group_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (group_id, user_id),
    CONSTRAINT fk_gm_group FOREIGN KEY (group_id) REFERENCES fundora_groups(id) ON DELETE CASCADE,
    CONSTRAINT fk_gm_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 4. Expenses Table
CREATE TABLE IF NOT EXISTS expenses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    group_id BIGINT NOT NULL,
    paid_by_user_id BIGINT NOT NULL,
    title VARCHAR(150) NOT NULL,
    amount DECIMAL(12, 2) NOT NULL,
    category VARCHAR(50) NOT NULL,
    split_type VARCHAR(20) NOT NULL DEFAULT 'EQUAL',
    expense_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_exp_group FOREIGN KEY (group_id) REFERENCES fundora_groups(id) ON DELETE CASCADE,
    CONSTRAINT fk_exp_payer FOREIGN KEY (paid_by_user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 5. Expense Splits Table (Per-member breakdown)
CREATE TABLE IF NOT EXISTS expense_splits (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    expense_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    share_amount DECIMAL(12, 2) NOT NULL,
    settled BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_split_expense FOREIGN KEY (expense_id) REFERENCES expenses(id) ON DELETE CASCADE,
    CONSTRAINT fk_split_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 6. Settlements Table (UPI settlements and payment logs)
CREATE TABLE IF NOT EXISTS settlements (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    group_id BIGINT NOT NULL,
    payer_user_id BIGINT NOT NULL,
    payee_user_id BIGINT NOT NULL,
    amount DECIMAL(12, 2) NOT NULL,
    payment_mode VARCHAR(30) NOT NULL DEFAULT 'UPI',
    transaction_reference VARCHAR(100),
    settlement_status VARCHAR(20) NOT NULL DEFAULT 'COMPLETED',
    settled_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_settle_group FOREIGN KEY (group_id) REFERENCES fundora_groups(id) ON DELETE CASCADE,
    CONSTRAINT fk_settle_payer FOREIGN KEY (payer_user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_settle_payee FOREIGN KEY (payee_user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 7. Collaborative Savings Goals Table (Trips, Campus Events)
CREATE TABLE IF NOT EXISTS savings_goals (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    group_id BIGINT NOT NULL,
    title VARCHAR(150) NOT NULL,
    target_amount DECIMAL(12, 2) NOT NULL,
    current_amount DECIMAL(12, 2) NOT NULL DEFAULT 0.00,
    deadline DATE,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_savings_group FOREIGN KEY (group_id) REFERENCES fundora_groups(id) ON DELETE CASCADE
);

-- 8. Smart Nudge Logs Table
CREATE TABLE IF NOT EXISTS nudge_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sender_user_id BIGINT NOT NULL,
    receiver_user_id BIGINT NOT NULL,
    group_id BIGINT NOT NULL,
    amount DECIMAL(12, 2) NOT NULL,
    message VARCHAR(255) NOT NULL,
    nudge_type VARCHAR(50) DEFAULT 'FRIENDLY_REMINDER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =========================================================================
-- SEED DATA (Mumbai University College Flatmates & Goa Trip Scenario)
-- =========================================================================

-- Seed Users
INSERT INTO users (id, name, email, phone_number, upi_id, role, wallet_balance) VALUES
(1, 'Aarav Sharma', 'aarav@fundora.app', '9820112233', 'aarav@okhdfcbank', 'STUDENT', 2500.00),
(2, 'Diya Patel', 'diya@fundora.app', '9820223344', 'diya@icici', 'STUDENT', 1800.00),
(3, 'Rohan Verma', 'rohan@fundora.app', '9820334455', 'rohan@paytm', 'STUDENT', 3200.00),
(4, 'Simran Kaur', 'simran@fundora.app', '9820445566', 'simran@axisbank', 'STUDENT', 1200.00),
(5, 'Mr. Kulkarni (Hostel Mgr)', 'kulkarni@hostel.app', '9820556677', 'hostel@sbi', 'HOSTEL_MANAGER', 0.00);

-- Seed Groups
INSERT INTO fundora_groups (id, name, description, group_type, created_by_user_id) VALUES
(1, 'Gokul Flat 402 (Powai)', 'Monthly Rent, WiFi, Cook, & Grocery Split', 'FLATMATE_RECURRING', 1),
(2, 'Goa Semester Break Trip', 'Villa, Beach Sports, Car Rental & Food', 'TRIP_EVENT', 2);

-- Group Memberships
INSERT INTO group_members (group_id, user_id) VALUES
(1, 1), (1, 2), (1, 3), (1, 4),
(2, 1), (2, 2), (2, 3), (2, 4);

-- Seed Expenses
-- Expense 1: Aarav paid 12,000 for Flat Groceries & Water Can
INSERT INTO expenses (id, group_id, paid_by_user_id, title, amount, category, split_type) VALUES
(1, 1, 1, 'Monthly D-Mart Groceries & Supplies', 12000.00, 'GROCERIES', 'EQUAL'),
(2, 1, 2, 'High-Speed Fiber Internet Bill', 2400.00, 'UTILITIES', 'EQUAL'),
(3, 2, 3, 'Goa Beach Resort Booking Advance', 16000.00, 'ACCOMMODATION', 'EQUAL');

-- Seed Expense Splits (4 members share equally)
-- Expense 1 (12000 / 4 = 3000 each)
INSERT INTO expense_splits (expense_id, user_id, share_amount, settled) VALUES
(1, 1, 3000.00, TRUE),
(1, 2, 3000.00, FALSE),
(1, 3, 3000.00, FALSE),
(1, 4, 3000.00, FALSE);

-- Expense 2 (2400 / 4 = 600 each)
INSERT INTO expense_splits (expense_id, user_id, share_amount, settled) VALUES
(2, 1, 600.00, FALSE),
(2, 2, 600.00, TRUE),
(2, 3, 600.00, FALSE),
(2, 4, 600.00, FALSE);

-- Expense 3 (16000 / 4 = 4000 each)
INSERT INTO expense_splits (expense_id, user_id, share_amount, settled) VALUES
(3, 1, 4000.00, FALSE),
(3, 2, 4000.00, FALSE),
(3, 3, 4000.00, TRUE),
(3, 4, 4000.00, FALSE);

-- Seed Savings Goals
INSERT INTO savings_goals (id, group_id, title, target_amount, current_amount, deadline, status) VALUES
(1, 2, 'Goa Scuba Diving & Water Sports Fund', 20000.00, 14500.00, '2026-12-25', 'ACTIVE'),
(2, 1, 'Living Room Smart TV Upgrade', 18000.00, 9000.00, '2026-11-15', 'ACTIVE');
