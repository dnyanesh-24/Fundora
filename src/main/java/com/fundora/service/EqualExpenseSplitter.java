package com.fundora.service;

import com.fundora.exception.LedgerException;
import com.fundora.model.Expense;
import com.fundora.model.ExpenseSplit;
import com.fundora.model.User;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Module I & II: Equal split implementation with fractional paisa balance correction
 */
@Component("equalExpenseSplitter")
public class EqualExpenseSplitter implements ExpenseSplitter {

    @Override
    public List<ExpenseSplit> calculateSplits(Expense expense, List<User> participants, Map<Long, BigDecimal> splitData) {
        if (participants == null || participants.isEmpty()) {
            throw new LedgerException("Cannot split expense among zero participants.");
        }

        int totalMembers = participants.size();
        BigDecimal totalAmount = expense.getAmount();
        BigDecimal baseShare = totalAmount.divide(BigDecimal.valueOf(totalMembers), 2, RoundingMode.DOWN);
        BigDecimal remainder = totalAmount.subtract(baseShare.multiply(BigDecimal.valueOf(totalMembers)));

        List<ExpenseSplit> splits = new ArrayList<>();

        for (int i = 0; i < participants.size(); i++) {
            User user = participants.get(i);
            BigDecimal userShare = baseShare;
            // Distribute fractional remainder cents/paise to the first few users to avoid rounding loss
            if (remainder.compareTo(BigDecimal.ZERO) > 0 && i < remainder.multiply(BigDecimal.valueOf(100)).intValue()) {
                userShare = userShare.add(new BigDecimal("0.01"));
            }

            // If user is the payer, mark their split as settled automatically
            boolean isPayer = user.getId().equals(expense.getPaidBy().getId());
            splits.add(new ExpenseSplit(expense, user, userShare, isPayer));
        }

        return splits;
    }
}
