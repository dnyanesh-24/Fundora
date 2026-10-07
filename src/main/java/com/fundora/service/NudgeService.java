package com.fundora.service;

import com.fundora.dto.NudgeRequestDTO;
import com.fundora.exception.UserNotFoundException;
import com.fundora.model.NudgeLog;
import com.fundora.model.User;
import com.fundora.repository.NudgeLogRepository;
import com.fundora.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Module I, II & VI: Fundora Smart Nudge & Reminder System
 * Generates friendly, socially comfortable peer reminder messages
 * to eliminate interpersonal awkwardness in money collection.
 */
@Service
public class NudgeService {

    @Autowired
    private NudgeLogRepository nudgeLogRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Sends a smart, non-intrusive nudge notification
     */
    public NudgeLog sendNudge(NudgeRequestDTO dto) {
        User sender = userRepository.findById(dto.getSenderUserId())
                .orElseThrow(() -> new UserNotFoundException(dto.getSenderUserId()));
        User receiver = userRepository.findById(dto.getReceiverUserId())
                .orElseThrow(() -> new UserNotFoundException(dto.getReceiverUserId()));

        String message = dto.getCustomMessage();
        if (message == null || message.trim().isEmpty()) {
            // Auto-generated polite smart nudge
            message = String.format("Hey %s! Just a friendly reminder from %s for ₹%s on Fundora.",
                    receiver.getName().split(" ")[0],
                    sender.getName().split(" ")[0],
                    dto.getAmount().toPlainString());
        }

        NudgeLog log = new NudgeLog(
                sender.getId(),
                receiver.getId(),
                dto.getGroupId(),
                dto.getAmount(),
                message
        );
        log.setNudgeType("SMART_NUDGE");
        log.setCreatedAt(LocalDateTime.now());

        return nudgeLogRepository.save(log);
    }

    /**
     * Retrieves nudges received by a user
     */
    public List<NudgeLog> getUserNudges(Long userId) {
        return nudgeLogRepository.findByReceiverUserIdOrderByCreatedAtDesc(userId);
    }
}
