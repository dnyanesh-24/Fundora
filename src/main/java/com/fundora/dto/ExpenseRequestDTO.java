package com.fundora.dto;

import com.fundora.model.SplitType;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Module VI: Request Payload DTO for Adding a New Expense
 */
public class ExpenseRequestDTO {
    private Long groupId;
    private Long paidByUserId;
    private String title;
    private BigDecimal amount;
    private String category;
    private SplitType splitType = SplitType.EQUAL;
    private List<Long> participantUserIds;
    private Map<Long, BigDecimal> exactSplits;

    public ExpenseRequestDTO() {}

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Long getPaidByUserId() {
        return paidByUserId;
    }

    public void setPaidByUserId(Long paidByUserId) {
        this.paidByUserId = paidByUserId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public SplitType getSplitType() {
        return splitType;
    }

    public void setSplitType(SplitType splitType) {
        this.splitType = splitType;
    }

    public List<Long> getParticipantUserIds() {
        return participantUserIds;
    }

    public void setParticipantUserIds(List<Long> participantUserIds) {
        this.participantUserIds = participantUserIds;
    }

    public Map<Long, BigDecimal> getExactSplits() {
        return exactSplits;
    }

    public void setExactSplits(Map<Long, BigDecimal> exactSplits) {
        this.exactSplits = exactSplits;
    }
}
