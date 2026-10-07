package com.fundora.dto;

import java.math.BigDecimal;

/**
 * Module I & VI: DTO for pairwise debt representation ("Who Owes Whom")
 */
public class DebtRelationDTO {
    private Long fromUserId;
    private String fromUserName;
    private Long toUserId;
    private String toUserName;
    private String toUserUpiId;
    private BigDecimal amount;

    public DebtRelationDTO() {}

    public DebtRelationDTO(Long fromUserId, String fromUserName, Long toUserId, String toUserName, String toUserUpiId, BigDecimal amount) {
        this.fromUserId = fromUserId;
        this.fromUserName = fromUserName;
        this.toUserId = toUserId;
        this.toUserName = toUserName;
        this.toUserUpiId = toUserUpiId;
        this.amount = amount;
    }

    public Long getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(Long fromUserId) {
        this.fromUserId = fromUserId;
    }

    public String getFromUserName() {
        return fromUserName;
    }

    public void setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName;
    }

    public Long getToUserId() {
        return toUserId;
    }

    public void setToUserId(Long toUserId) {
        this.toUserId = toUserId;
    }

    public String getToUserName() {
        return toUserName;
    }

    public void setToUserName(String toUserName) {
        this.toUserName = toUserName;
    }

    public String getToUserUpiId() {
        return toUserUpiId;
    }

    public void setToUserUpiId(String toUserUpiId) {
        this.toUserUpiId = toUserUpiId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
