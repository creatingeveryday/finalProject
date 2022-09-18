package com.community.chodae.security.service;

import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.community.chodae.domain.User;
import com.community.chodae.repository.UserRepo;
import com.community.chodae.security.dto.MemberAuthDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

@Log
@Service
@RequiredArgsConstructor
public class MemberUserDetailsService implements UserDetailsService{
	//UserDetailsService 인터페이스는 DB에서 유저 정보를 가져오는 역할

	private final UserRepo userRepo;
	private final PasswordEncoder passwordEncoder;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		
		Optional<User> result = userRepo.findByLoginId(username, false);
		
		if(!result.isPresent()) {
			throw new UsernameNotFoundException("loginId를 다시 확인해주세요");
		}
		
		User member = result.get();

		
		if(member.getStatus().equals("F")) {
			throw new UsernameNotFoundException("탈퇴된 회원입니다.");
		}
		
		
		
		MemberAuthDTO authMember = new MemberAuthDTO(
				member.getLoginId(),
				member.getPassword(),
				member.isSocial(),
				member.getRoleSet().stream().map(role -> new SimpleGrantedAuthority("ROLE_"+role.name())).collect(Collectors.toSet()));
		
		authMember.setNickname(member.getNickname());
		authMember.setEmail(member.getEmail());
		
		
		
		return authMember;
	}
	
	
}
