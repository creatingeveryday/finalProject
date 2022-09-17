package com.community.chodae.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.community.chodae.domain.Category;


public interface CategoryRepo extends JpaRepository<Category, Long>{
	
	@Query("SELECT c FROM Category c WHERE c.post.postNo = ?1")
	List<Category> findByPostNo(Long postNo);

}
