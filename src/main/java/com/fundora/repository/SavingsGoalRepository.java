package com.fundora.repository;

import com.fundora.model.SavingsGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Module III & VI: Repository for Collaborative Trip / Event Savings Goals
 */
@Repository
public interface SavingsGoalRepository extends JpaRepository<SavingsGoal, Long> {
    List<SavingsGoal> findByGroupId(Long groupId);
}
