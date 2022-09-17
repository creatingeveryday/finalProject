package com.community.chodae.domain;

import java.time.LocalDateTime;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString(exclude = "user")
@Getter
@Setter
@Entity
@EqualsAndHashCode(of = "login_no")
@Table(name = "user_log")
public class UserLog {
	//유저 로그 테이블
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long login_no;
	
	private LocalDateTime login_date;
	
	private String login_status;
	
	//다대일 관계 : 회원정보 테이블의 회원번호 컬럼을 외래키로. / 단방향 설정
	@ManyToOne
	@JoinColumn(name = "id")
	private User user;

}
