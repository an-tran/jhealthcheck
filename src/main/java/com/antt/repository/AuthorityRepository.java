package com.antt.repository;

import com.antt.domain.Authority;

import com.antt.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the Authority entity.
 */
public interface AuthorityRepository extends JpaRepository<Authority, String> {
//
//    @Query("select au.name from Authority au where au.path like %?1% ")
//    List<String> findAllGroupHierachyByName(String name);

//    @Modifying(clearAutomatically = true)
//    @Query("update Authority au set au.enabled = true where au.name in ?1")
//    List<Authority> disableGroupsByName(List<String> groupNames);
    @Query
    List<Authority> findAllByOwner(User user);

    @Modifying(clearAutomatically = true)
    @Query("update Authority au set au.enabled = false where au.name = ?1")
    int disableByName(String groupName);

    @Modifying(clearAutomatically = true)
    @Query("update Authority au set au.enabled = true where au.name = ?1")
    int enableByName(String groupName);


    @Query(value = "select ua.user_id from jhi_user_authority ua where ua.authority_name = ?1", nativeQuery = true)
    List<Long> findUserIdByGroupName(String name);

    Optional<Authority> findOneByName(String name);

    @Modifying
    @Query(value =
        "insert into JHI_AUTHORITY_PATH (ancestor, descendant)" +
        "  select ap.ancestor, :auth_name" +
        "  from JHI_AUTHORITY_PATH ap" +
        "  where ap.descendant = :parent_name ;"
        , nativeQuery = true)
    int addAuthorityPathFor(@Param("parent_name") String parent_name, @Param("auth_name") String auth_name);

    @Modifying
    @Query(value =
        "insert into JHI_AUTHORITY_PATH (ancestor, descendant)" +
            "  select :auth_name, :auth_name ;"
        , nativeQuery = true)
    int addSelfAuthorityPath(@Param("auth_name") String auth_name);

    @Query(value =
        "WITH aap AS ( SELECT * FROM JHI_AUTHORITY_PATH ap join JHI_AUTHORITY  au on au.name = ap.ancestor) " +
        "SELECT * FROM JHI_AUTHORITY " +
        "WHERE name in (SELECT descendant FROM aap where owner = ?1) ;", nativeQuery = true)
    List<Authority> findAuthorityHierarchyOf(Long userId);

    @Query(value = "SELECT au.* FROM JHI_AUTHORITY_PATH ap join JHI_AUTHORITY  au " +
        "on au.name = ap.ancestor " +
        "where descendant = ?1", nativeQuery = true)
    List<Authority> finAllAncestor(String authority);
}
