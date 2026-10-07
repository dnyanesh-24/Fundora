package com.fundora.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Module I & III: Smart Nudge Reminder Log
 */
@Entity
@Table(name = "nudge_logs")
public class NudgeLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sender_user_id", nullable = false)
    private Long senderUserId;

    @Column(name = "receiver_user_id", nullable = false)
    private Long receiverUserId;

    @Column(name = "group_id", nullable = false)
    private Long groupId;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 255)
    private String message;

    @Column(name = "nudge_type", length = 50)
    private String nudgeType;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public NudgeLog() {
        this.nudgeType = "FRIENDLY_REMINDER";
        this.createdAt = LocalDateTime.now();
    }

    public NudgeLog(Long senderUserId, Long receiverUserId, Long groupId, BigDecimal amount, String message) {
        this();
        this.senderUserId = senderUserId;
        this.receiverUserId = receiverUserId;
        this.groupId = groupId;
        this.amount = amount;
        this.message = message;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSenderUserId() {
        return senderUserId;
    }

    public void setSenderUserId(Long senderUserId) {
        this.senderUserId = senderUserId;
    }

    public Long getReceiverUserId() {
        return receiverUserId;
    }

    public void setReceiverUserId(Long receiverUserId) {
        this.receiverUserId = receiverUserId;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getNudgeType() {
        return nudgeType;
    }

    public void setNudgeType(String nudgeType) {
        this.nudgeType = nudgeType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
