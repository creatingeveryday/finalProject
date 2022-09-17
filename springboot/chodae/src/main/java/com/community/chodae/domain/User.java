package com.community.chodae.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import com.community.chodae.group.MemberRole;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;

import com.community.chodae.group.MemberRole;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Getter
@Setter
@Table(name = "user_info")
@Entity
@DynamicInsert
@DynamicUpdate
public class User implements Serializable{
	
	@JsonIgnore
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private boolean social; // 소셜회원 여부 .. 새로 추가함.
	
	@Column(unique = true)// DB테이블 필드 이름 login_id , 중복없는 유니크 필드
	private String loginId; //로그인용 아이디
	
	private String password;
	
	private String name;
	
	@Column(length = 45)
	private String email;
	
	@Column(columnDefinition = "integer default 1")
	private Integer level; //회원등급 
	
	@NotNull
	@Column(unique = true)
	private String nickname; //닉네임
	
	private String status;  //유저상태
	
//	@Column(columnDefinition = "varchar(50) default 'normal'")
//	private String role;  //유저역할,권한
//	
//	public List<String> getRoleList(){
//		if(this.role.length()>0) {
//			return Arrays.asList(this.role.split(","));
//		}
//		return new ArrayList<>();
//	}
	
	//주석처리
	@ElementCollection(fetch = FetchType.LAZY)
	@Builder.Default
	private Set<MemberRole> roleSet = new HashSet<>();
	
	//리프레쉬 토큰 저장할때 추가해줄 예정 : 저장된 리프레시토큰은 access token 을 발급해주기 위한 용도로만 사용
	private String refreshToken;

	private String language;// 주로 사용하는 언어
	private int phone; // 전화번호 
	private String mailAds; 
	private String campus; //교육기관
	
	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_date")
	private Date createDate;
	
	@UpdateTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "modify_date")
	private Date modifyDate;
	
	//private Integer test; // 테스트 점수
	//private String profileimg; // 프로필 이미지
	
	
	public void addMemberRole(MemberRole memberRole) {
		roleSet.add(memberRole);
	}
}
