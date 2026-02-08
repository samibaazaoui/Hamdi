package com.inn.cafe.dao;

import com.inn.cafe.PROJO.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CategoryDao extends JpaRepository<Category, Integer> {
    @Query("select c from Category c where c.id in(select p.category.id from Product p where p.status='true')")
    List<Category> getAllCategory();
    @Modifying
    @Transactional
    @Query("UPDATE Category c SET c.name = :name WHERE c.id = :id")

    Integer update(@Param("name") String name, @Param("id") Integer id);
}
