package com.community.chodae.service;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.community.chodae.domain.User;
import com.community.chodae.dto.MemberDTO;
import com.community.chodae.repository.UserRepo;
import com.community.chodae.security.util.JWTUtil;

import io.jsonwebtoken.security.InvalidKeyException;
import lombok.extern.java.Log;

@Log
@Service
public class UserFindServiceImpl implements UserFindService {
	
	private final UserRepo userRepo;
	private final PasswordEncoder passwordEncoder;
	private final JWTUtil jwtUtil;
	
	@Autowired
	public UserFindServiceImpl(UserRepo userRepo, PasswordEncoder passwordEncoder, JWTUtil jwtUtil) {
		this.userRepo = userRepo;
		this.passwordEncoder = passwordEncoder;
		this.jwtUtil = jwtUtil;
	}

	@Override
	public MemberDTO sleepUser(String nickname) {
		User user = null;
		Optional<User> result = userRepo.findUserByNickname(nickname);
		
		if(result.isPresent()) {
			user = result.get();
			user.setStatus("F");
			userRepo.saveAndFlush(user);
			
			MemberDTO dto = new MemberDTO();
			dto.setNickname(nickname);
			return dto;
		}
		
		return null; 
		
	}
	
	@Override
	public String updateUserInfo(String currentNickname, String newNickname) {
		
		User user = null;
		
		Optional<User> result = userRepo.findUserByNickname(currentNickname);
		
		if(result.isPresent()) {
			user = result.get();
			user.setNickname(newNickname);
			
			userRepo.saveAndFlush(user);
			
			return "1";
		}
		
		return null;
		
	}
	@Override
	public MemberDTO getUserInfo(String nickname) {
		
		User user = null;
		MemberDTO dto = null;
		
		Optional<User> result = userRepo.findUserByNickname(nickname);
		
		if(result.isPresent()) {
			user = result.get();
			
			dto.setNickname(user.getNickname());
			
			
			return dto;
		}
		
		
		
		return null;
		
	}
	@Override
	public String isExistNickname(String nickname) {
		
		Optional<User> result = userRepo.findUserByNickname(nickname);
		
		if(result.isPresent()) {
			return result.get().getNickname();
		}
		return null;
		
	}
	
	@Override
	public int updateRefreshToken(String nickname, String token) {
		//로그인시  새로발급한 리프레쉬 토큰으로 업데이트

		
		int i = userRepo.updateRefreshToken(token, nickname);
		
		return i;
		
	}
	
	
	@Override
	public String getAccessToken(String nickname) {
		
		String accessToken = null;
		
		User member = null;
		
		Optional<User> result = userRepo.findUserByNickname(nickname);
		if(result.isPresent()) {
			member = result.get();			
		}
		 
		 
		 
		 Collection<SimpleGrantedAuthority> authorities = member.getRoleSet()
																 .stream()
																 .map(role -> new SimpleGrantedAuthority("ROLE_"+role.name()))
																 .collect(Collectors.toSet());
		 
		//검증되면 액세스토큰 반환
		try {
			
			accessToken = jwtUtil.generateAccessToken(member.getNickname(), authorities);
			
			
		} catch (InvalidKeyException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return accessToken;
	}
		
	
	@Override
	public User checkRefreshToken(String nickname, String token) {
		
		//로그인시 발급하여 저장한 리프레시 토큰을 전달받은 리프레시토큰과  비교 하여 찾은 유저 정보 반환
		User member = null;
		
		Optional<User> result = userRepo.findUserByNickname(nickname);
		if(result.isPresent()) {
			member = result.get();			
		}
		
		if(member.getRefreshToken().equals(token)) {

			
			return member;
		}
	
		return null;
	}
	
	//로그인시 아이디 비밀번호 일치여부 확인할떄 사용가능
	@Override
	public User getUserEntityByCredentials(String loginId, String password, PasswordEncoder passwordEncoder) {
		
		Optional<User> result = userRepo.findByLoginId(loginId, false);
		
		User member = null;
		
		if(result.isPresent()) {
			member = result.get();
			if(passwordEncoder.matches(password, member.getPassword())) {
				return member;
			}
			
		}
		
		return null;
		
	}
 



	@Override
	public String searchId(String name, String email) {

		Optional<User> result = userRepo.findUserByNameAndEmail(name, email);
		
		if(result.isPresent()) {
			User user = result.get();
			return user.getLoginId();
		}
		
		return null;
	}


	@Override
	public String isUser(String loginId, String email) {

		Optional<User> result = userRepo.findUserByLoginIdAndEmail(loginId, email);
		
		if(result.isPresent()) {
			return result.get().getLoginId();
		}
		
		return null;
	}

	//업데이트 
	@Override
	public int updatePassword(String id, String password) {

		return userRepo.updatePassword(id, passwordEncoder.encode(password));
	}
	

}
