package com.community.chodae.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.community.chodae.domain.User;
import com.community.chodae.dto.MemberDTO;

public interface UserFindService {
	
	String searchId(String name, String email);
	String isUser(String login_id,String email);
	int updatePassword(String id, String password);
	
	User getUserEntityByCredentials(String loginId, String password, PasswordEncoder passwordEncoder);
	
	int updateRefreshToken(String nickname, String token);	
	User checkRefreshToken(String nickname, String token);
	String getAccessToken(String nickname);
	String isExistNickname(String nickname);
	MemberDTO getUserInfo(String nickname);
	String updateUserInfo(String currentNickname, String newNickname);
	MemberDTO sleepUser(String nickname);
	

}
