package com.fundora.repository;

import com.fundora.model.ExpenseSplit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Module III & VI: Repository for Expense Split calculations
 */
@Repository
public interface ExpenseSplitRepository extends JpaRepository<ExpenseSplit, Long> {

    @Query("SELECT es FROM ExpenseSplit es WHERE es.expense.group.id = :groupId")
    List<ExpenseSplit> findByGroupId(@Param("groupId") Long groupId);

    @Query("SELECT es FROM ExpenseSplit es WHERE es.user.id = :userId AND es.settled = false")
    List<ExpenseSplit> findPendingSplitsByUserId(@Param("userId") Long userId);
}
