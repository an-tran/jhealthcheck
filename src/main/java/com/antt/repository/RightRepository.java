package com.antt.repository;

import com.antt.domain.Right;
import com.google.common.base.Optional;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the Right entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RightRepository extends JpaRepository<Right, Long> {
    Optional<Right> findOneByName(String name);
}
