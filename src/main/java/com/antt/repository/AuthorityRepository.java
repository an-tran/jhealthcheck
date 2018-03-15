package com.antt.repository;

import com.antt.domain.Authority;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the Authority entity.
 */
public interface AuthorityRepository extends JpaRepository<Authority, String> {

    @Query("select au.name from Authority au where au.path like %?1% ")
    List<String> findAllGroupHierachyByName(String name);

//    @Modifying(clearAutomatically = true)
//    @Query("update Authority au set au.enabled = true where au.name in ?1")
//    List<Authority> disableGroupsByName(List<String> groupNames);

    @Modifying(clearAutomatically = true)
    @Query("update Authority au set au.enabled = false where au.name = ?1")
    int disableByName(String groupName);

    @Modifying(clearAutomatically = true)
    @Query("update Authority au set au.enabled = true where au.name = ?1")
    int enableByName(String groupName);


    @Query(value = "select ua.user_id from jhi_user_authority ua where ua.authority_name = ?1", nativeQuery = true)
    List<Long> findUserIdByGroupName(String name);

    Optional<Authority> findOneByName(String name);

}
