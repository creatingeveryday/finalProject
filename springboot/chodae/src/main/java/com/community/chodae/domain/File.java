package com.community.chodae.domain;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

//@ToString
//@Getter
//@Setter
//@Entity
//@Table(name = "file_info")
public class File {
	//아직 관계설정안함.
	
//첨부 파일 및 이미지 파일 모두 통합. 
	// 파일유형코드로 이미지파일구분가능 // 이미지, 워드문서,, 등등등
	
	private Long fileNo;
		private String fileCode; //파일유형코드
	private Integer fileOrder; // 파일 순번???
	private String fileName; // 파일명
	private Long fileWriter; //등록자
		private String fileUniquec; //파일식별코드???
		private String fileTypec; //파일유형코드??  중복되네 2번쨰 필드랑.
	private String fileLocation; //파일 경로명
	private LocalDateTime fileRegdate; //파일등록일 
	private String fileOname; //원본파일명
	private Integer fileDelete; //파일 삭제여부
	private Integer fileDowns; //파일다운횟수
	private String fileExt; //파일확장명
	
}
