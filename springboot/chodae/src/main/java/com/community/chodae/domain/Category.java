package com.community.chodae.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString(exclude = "post")
@Getter
@Entity
@Table(name = "category_info")
@Setter
public class Category {
	// 게시글의 카테고리를 모아놓고 관리
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "category_no")
	private Long categoryNo; // 카테고리 번호 
	
	private String categoryKind;//카테고리 종류  ex) 지역, 등급 ?
	private String categoryName; //카테고리 이름 
	private String categoryOrder;//카테고리 순서??
	
	//게시글 번호 외래키
	@JsonBackReference
	@ManyToOne
	@JoinColumn(name = "post_no")
	private Post post;//게시글번호 외래키 
}	
