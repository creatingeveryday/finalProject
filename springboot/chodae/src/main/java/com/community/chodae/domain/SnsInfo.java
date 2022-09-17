package com.community.chodae.domain;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;

import com.community.chodae.group.MemberRole;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString(exclude = "user")
@Getter
@Setter
@Entity
@EqualsAndHashCode(of = "snsId")
@Table(name = "sns_info")
@NoArgsConstructor
@AllArgsConstructor
public class SnsInfo implements Serializable {
	
	@Id
	private String snsId; //64자 이내로 구성된 BASE64 형식의 문자열
	
	@NotNull
	private String snsType; //ex) naver, kakao
	
	
	@NotNull
	private LocalDateTime snsCondate;//네이버로그인 연동날짜
	
//	@Column(length = 21)
//	private String snsNickname;
//	
//	private String snsName;
//	private String snsProfile;
//	private String snsEmail;
//	private String snsContact;
	
	
	//다대일 관계 : 회원정보 테이블의 회원번호 컬럼을 외래키로. / 단방향 설정
	@ManyToOne
	@JoinColumn(name = "id")
	private User user;

}