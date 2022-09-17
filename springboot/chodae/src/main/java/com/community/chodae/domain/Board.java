package com.community.chodae.domain;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Entity
@Table(name = "board_info")
@Setter
public class Board {
//게시판을 모아놓은 곳 
	
	
//	@Column(name = "board_no") 자동으로 스네이크 케이스로 변환되므로 안써도 됨.
	@Id
	private Integer boardNo;
	
	private String boardName;
	
	private Long boardWriter;
	
	private String boardCategory; // 스터디모집 ??? / 고객센터   게시판 자체의 카테고리
	
	private LocalDateTime boardDate; // 게시판 생성일자
	
	private String boardOrder; //게시판 순서 정렬순서 ? 
	
	private String boardStatus; //게시판 상태 T = 활성화? 
}
