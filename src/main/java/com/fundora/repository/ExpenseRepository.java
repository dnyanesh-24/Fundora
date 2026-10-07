package com.fundora.repository;

import com.fundora.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Module III & VI: JPA Repository for Expenses
 */
@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    
    List<Expense> findByGroupIdOrderByExpenseDateDesc(Long groupId);

    @Query("SELECT e FROM Expense e WHERE e.group.id = :groupId AND e.paidBy.id = :userId")
    List<Expense> findByGroupIdAndPaidById(@Param("groupId") Long groupId, @Param("userId") Long userId);
}
