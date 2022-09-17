package com.community.chodae.dto;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.community.chodae.group.MemberRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class MemberDTO {
	
	private String token;//jwt토큰 전달

	private Long id;
	
	private boolean social; // 소셜회원 여부 .. 새로 추가함.
	private String loginId; //로그인용 아이디
	private String password;
	
	private String name;
	private String email;
	private String nickname; //닉네임
	
	private Integer level; //회원등급 
	private String status;  //유저상태
	
	@Builder.Default
	private Set<MemberRole> roleSet = new HashSet<>();  //유저역할,권한

	private String language;// 주로 사용하는 언어
	private int phone; // 전화번호 
	private String mailAds; 
	private String campus; //교육기관
	
	private Date createDate;
	private Date modifyDate;
	
	
	//private Integer test; // 테스트 점수
	//private String profileimg; // 프로필 이미지 
	
	public void addMemberRole(MemberRole memberRole) {
		roleSet.add(memberRole);
	}
	
}
