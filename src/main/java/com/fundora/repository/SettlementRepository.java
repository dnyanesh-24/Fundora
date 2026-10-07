package com.fundora.repository;

import com.fundora.model.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Module III & VI: JPA Repository for Settlements
 */
@Repository
public interface SettlementRepository extends JpaRepository<Settlement, Long> {
    List<Settlement> findByGroupIdOrderBySettledAtDesc(Long groupId);
}
