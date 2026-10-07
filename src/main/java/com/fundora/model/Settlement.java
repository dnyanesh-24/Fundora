package com.fundora.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Module I & III: Settlement Entity representing UPI and cash settlements
 */
@Entity
@Table(name = "settlements")
public class Settlement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "payer_user_id", nullable = false)
    private User payer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "payee_user_id", nullable = false)
    private User payee;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "payment_mode", nullable = false, length = 30)
    private String paymentMode; // UPI, WALLET, CASH

    @Column(name = "transaction_reference", length = 100)
    private String transactionReference;

    @Column(name = "settlement_status", nullable = false, length = 20)
    private String settlementStatus; // COMPLETED, PENDING, FAILED

    @Column(name = "settled_at")
    private LocalDateTime settledAt;

    public Settlement() {
        this.paymentMode = "UPI";
        this.settlementStatus = "COMPLETED";
        this.settledAt = LocalDateTime.now();
    }

    public Settlement(Group group, User payer, User payee, BigDecimal amount, String paymentMode, String transactionReference) {
        this();
        this.group = group;
        this.payer = payer;
        this.payee = payee;
        this.amount = amount;
        this.paymentMode = paymentMode != null ? paymentMode : "UPI";
        this.transactionReference = transactionReference;
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

    public User getPayer() {
        return payer;
    }

    public void setPayer(User payer) {
        this.payer = payer;
    }

    public User getPayee() {
        return payee;
    }

    public void setPayee(User payee) {
        this.payee = payee;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }

    public String getSettlementStatus() {
        return settlementStatus;
    }

    public void setSettlementStatus(String settlementStatus) {
        this.settlementStatus = settlementStatus;
    }

    public LocalDateTime getSettledAt() {
        return settledAt;
    }

    public void setSettledAt(LocalDateTime settledAt) {
        this.settledAt = settledAt;
    }
}
