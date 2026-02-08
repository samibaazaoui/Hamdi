package com.inn.cafe.dao;

import com.inn.cafe.PROJO.User;
import com.inn.cafe.rest.UserRest;
import com.inn.cafe.wrapper.UserWrapper;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserDao extends JpaRepository<User, Integer> {
    User findByEmailId(@Param("email") String email);
    List<UserWrapper> getAllUser();
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.status = :status WHERE u.id = :id")

    Integer updateStatus(@Param("status") String status, @Param("id") Integer id);
    List<String> getAllAdmin();
    User findByEmail(String email);
}
