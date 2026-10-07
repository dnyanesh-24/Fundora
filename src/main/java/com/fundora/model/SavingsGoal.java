package com.fundora.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Module I & V: Collaborative Group Savings Goal
 * Powers the trip savings wallet and campus event funding.
 */
@Entity
@Table(name = "savings_goals")
public class SavingsGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(name = "target_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal targetAmount;

    @Column(name = "current_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal currentAmount;

    private LocalDate deadline;

    @Column(nullable = false, length = 20)
    private String status; // ACTIVE, COMPLETED, CANCELLED

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public SavingsGoal() {
        this.currentAmount = BigDecimal.ZERO;
        this.status = "ACTIVE";
        this.createdAt = LocalDateTime.now();
    }

    public SavingsGoal(Group group, String title, BigDecimal targetAmount, LocalDate deadline) {
        this();
        this.group = group;
        this.title = title;
        this.targetAmount = targetAmount;
        this.deadline = deadline;
    }

    // Business Logic: Calculate completion percentage
    public double getProgressPercentage() {
        if (targetAmount == null || targetAmount.compareTo(BigDecimal.ZERO) == 0) {
            return 0.0;
        }
        return currentAmount.divide(targetAmount, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
    }

    public void deposit(BigDecimal amount) {
        if (amount != null && amount.compareTo(BigDecimal.ZERO) > 0) {
            this.currentAmount = this.currentAmount.add(amount);
            if (this.currentAmount.compareTo(this.targetAmount) >= 0) {
                this.status = "COMPLETED";
            }
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(BigDecimal targetAmount) {
        this.targetAmount = targetAmount;
    }

    public BigDecimal getCurrentAmount() {
        return currentAmount;
    }

    public void setCurrentAmount(BigDecimal currentAmount) {
        this.currentAmount = currentAmount;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
