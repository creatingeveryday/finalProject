package com.community.chodae.domain;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Entity
@Table(name = "post_content")
@Setter
public class PostContent {
//게시글 번호랑 연결되는 게시글 내용
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "content_no")
	private Long contentNo;
	
	@Column(length = 10000)
	private String content; //게시글 내용 . 저장된 게시글 내용을 어떻게 db에 저장후 그대로 돌려주지? html 태그 자체를 저장? summernote?
	
}
