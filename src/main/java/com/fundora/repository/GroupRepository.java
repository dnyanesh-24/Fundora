package com.fundora.repository;

import com.fundora.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Module III & VI: Spring Data JPA Repository for Group Entity
 */
@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    
    @Query("SELECT g FROM Group g JOIN g.members m WHERE m.id = :userId")
    List<Group> findGroupsByUserId(@Param("userId") Long userId);
}
