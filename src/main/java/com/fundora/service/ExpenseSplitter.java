package com.fundora.service;

import com.fundora.model.Expense;
import com.fundora.model.ExpenseSplit;
import com.fundora.model.User;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Module II (Interfaces & Polymorphism): Contract for Expense Splitting Strategies
 */
public interface ExpenseSplitter {

    /**
     * Splits an expense among participants
     * @param expense The parent expense
     * @param participants The users sharing the cost
     * @param splitData Optional custom breakdown (for exact or percentage splits)
     * @return List of generated ExpenseSplit records
     */
    List<ExpenseSplit> calculateSplits(Expense expense, List<User> participants, Map<Long, BigDecimal> splitData);
}
