package com.community.chodae.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.community.chodae.domain.Category;
import org.springframework.data.repository.query.Param;


public interface CategoryRepo extends JpaRepository<Category, Long> {

    @Query("SELECT c FROM Category c WHERE c.post.postNo = :postNo")
    List<Category> findByPostNo(@Param("postNo") Long postNo);

}
