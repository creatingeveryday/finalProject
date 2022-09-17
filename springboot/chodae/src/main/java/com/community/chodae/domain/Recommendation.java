package com.community.chodae.domain;

import javax.persistence.CascadeType;
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

//@ToString//(exclude = {"post","reply","user"})
@ToString(exclude = {"post","reply"})
@Getter
@Setter
@Entity
@Table(name = "recommendations")
public class Recommendation {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "reco_no")
	private Long recoNo;
	
	
	@ManyToOne
	@JoinColumn(name = "id")
	private User user;//회원번호 외래키 다대일관계     
	

	private String application;
	
	

	@JsonBackReference
	@ManyToOne
	@JoinColumn(name = "reply_no")
	private Reply reply;//댓글번호 외래키  다대일관계
	
	@JsonBackReference
	@ManyToOne
	@JoinColumn(name = "post_no")
	private Post post;//게시글 번호 외래키 다대일관계
 	
 	private Integer boardNo; //게시판 번호 
	
}
