package com.dacs2.repository;

import com.dacs2.model.UserDtls;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<UserDtls, Integer> {

    public UserDtls findByEmailAndConfirmed(String email, Boolean confirmed);

    public UserDtls findByEmail(String email);

    public Page<UserDtls> findByRole(Pageable pageable, String role);

    public List<UserDtls> findByRole(String role);

    @Query("SELECT u\n" +
            "FROM UserDtls u\n" +
            "WHERE u.role = :role \n" +
            "AND (u.name LIKE CONCAT('%', :keyword, '%') \n" +
            "OR u.email LIKE CONCAT('%', :keyword, '%') \n" +
            "OR u.mobileNumber LIKE CONCAT('%', :keyword, '%') \n" +
            "OR u.id = :id \n)")
    public Page<UserDtls> findByRoleAndKeyword(
            Pageable pageable,@Param("role") String role, @Param("id") Integer id, @Param("keyword") String keyword);

    public UserDtls findByResetToken(String token);

    UserDtls findByConfirmToken(String confirmToken);

    @Query(value = "select * from user_dtls where role = 'ROLE_ADMIN' limit 1", nativeQuery = true)
    UserDtls getFirstAdmin();
}
