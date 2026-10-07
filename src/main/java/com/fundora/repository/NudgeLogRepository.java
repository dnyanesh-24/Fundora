package com.fundora.repository;

import com.fundora.model.NudgeLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Module III & VI: Repository for Smart Nudge Logs
 */
@Repository
public interface NudgeLogRepository extends JpaRepository<NudgeLog, Long> {
    List<NudgeLog> findByReceiverUserIdOrderByCreatedAtDesc(Long receiverUserId);
    List<NudgeLog> findByGroupIdOrderByCreatedAtDesc(Long groupId);
}
