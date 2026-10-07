package com.fundora.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Module I (OOP Constructs & Encapsulation): Custom User domain class
 * Represents Students, Flatmates, Hostel/PG Managers, and Merchants.
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 120)
    private String email;

    @Column(name = "phone_number", nullable = false, unique = true, length = 15)
    private String phoneNumber;

    @Column(name = "upi_id", nullable = false, length = 50)
    private String upiId;

    @Column(nullable = false, length = 30)
    private String role; // STUDENT, HOSTEL_MANAGER, MERCHANT

    @Column(name = "wallet_balance", nullable = false, precision = 12, scale = 2)
    private BigDecimal walletBalance;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Default Constructor (Required by JPA & JavaBeans Specification)
    public User() {
        this.walletBalance = BigDecimal.ZERO;
        this.createdAt = LocalDateTime.now();
        this.role = "STUDENT";
    }

    // Overloaded Constructor 1: Basic registration constructor
    public User(String name, String email, String phoneNumber, String upiId) {
        this();
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.upiId = upiId;
    }

    // Overloaded Constructor 2: Full parameter constructor
    public User(Long id, String name, String email, String phoneNumber, String upiId, String role, BigDecimal walletBalance) {
        this(name, email, phoneNumber, upiId);
        this.id = id;
        this.role = role != null ? role : "STUDENT";
        this.walletBalance = walletBalance != null ? walletBalance : BigDecimal.ZERO;
    }

    // Module I: Method Overloading for Wallet Credit/Debit Operations
    public void creditWallet(BigDecimal amount) {
        if (amount != null && amount.compareTo(BigDecimal.ZERO) > 0) {
            this.walletBalance = this.walletBalance.add(amount);
        }
    }

    public void creditWallet(double amount) {
        this.creditWallet(BigDecimal.valueOf(amount));
    }

    public boolean debitWallet(BigDecimal amount) {
        if (amount != null && amount.compareTo(BigDecimal.ZERO) > 0 && this.walletBalance.compareTo(amount) >= 0) {
            this.walletBalance = this.walletBalance.subtract(amount);
            return true;
        }
        return false;
    }

    // Getters and Setters (Encapsulation)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUpiId() {
        return upiId;
    }

    public void setUpiId(String upiId) {
        this.upiId = upiId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public BigDecimal getWalletBalance() {
        return walletBalance;
    }

    public void setWalletBalance(BigDecimal walletBalance) {
        this.walletBalance = walletBalance;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) || Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", upiId='" + upiId + '\'' +
                ", role='" + role + '\'' +
                ", walletBalance=" + walletBalance +
                '}';
    }
}
