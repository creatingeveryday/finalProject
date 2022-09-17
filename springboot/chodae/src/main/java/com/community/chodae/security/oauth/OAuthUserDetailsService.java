package com.community.chodae.security.oauth;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.community.chodae.domain.SnsInfo;
import com.community.chodae.domain.User;
import com.community.chodae.group.MemberRole;
import com.community.chodae.repository.SnsRepo;
import com.community.chodae.repository.UserRepo;
import com.community.chodae.security.dto.MemberAuthDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuthUserDetailsService extends DefaultOAuth2UserService {
	
	private final UserRepo userRepo;
	private final SnsRepo snsRepo;
	private final PasswordEncoder passwordEncoder;
	
	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		log.info("------------------");
		log.info("userRequest="+userRequest);
		
		String clientName = userRequest.getClientRegistration().getClientName();
		
		log.info("clientName="+clientName);
		log.info("getAdditionalParameters="+userRequest.getAdditionalParameters());
		
		OAuth2User oAuth2User = super.loadUser(userRequest);
		log.info("oAuth2User="+oAuth2User);
		
		oAuth2User.getAttributes().forEach((k,v)->{
			log.info("@@@@@@@속성값 모두 출력@@@@@@@@@@");
			log.info(k+" === "+v);
			log.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@");
		});
		
		String nickname = null; 
		String snsId = null; 
		
		if(clientName.equals("Naver")) {
			
		Map<String, Object> response = oAuth2User.getAttribute("response");
			snsId = (String) response.get("id");
			nickname = (String) response.get("nickname");
			
			log.info("네이버의  snsId ::"+snsId);
			log.info("네이버의 닉네임 ::"+nickname);
			
		}else if (clientName.equals("Kakao")) {
			
			snsId = ""+oAuth2User.getAttribute("id");
			Map<String, Object> properties = oAuth2User.getAttribute("properties");
			nickname = (String) properties.get("nickname");
			
			log.info("카카오  @@@@응답 ::"+properties);
			log.info("카카오  snsId ::"+snsId);
			log.info("카카오 닉네임 ::"+nickname);

		}

		User member = saveSocialMember(nickname, clientName, snsId);
		log.info("member ::"+member);
		
		
		
		
		MemberAuthDTO authMember = new MemberAuthDTO(
				member.getLoginId(),
				member.getPassword(),
				true,
				member.getRoleSet().stream().map(
						role -> new SimpleGrantedAuthority("ROLE_"+ role.name()))
									.collect(Collectors.toList()),
				oAuth2User.getAttributes());
		
		
		
		log.info("authMember::"+authMember);
		
		authMember.setNickname(member.getNickname());
		
		return authMember;
		
		
		
		
	}

	private User saveSocialMember(String nickname, String clientName, String snsId) {
		log.info("@@@saveSocialMember=>@@");
		
		if(nickname == null && clientName == null) {
			return null;
		}

		Optional<User> result = userRepo.findByLoginId(snsId, true); //고유식별자는 loginId에 저장하고 있음.
		
		String nicknameInfo = null;
		
		if(result.isPresent()) {
			log.info("@@@이미 가입됨. 가입된 정보 반환@@@");
			
			User user = result.get();
			log.info("user::::"+user);
			
			//탈퇴된 회원일 경우 재연동
			if(user.getStatus().equals("F")) {
				user.setStatus("T");
				userRepo.saveAndFlush(user);
			}
			return user;
		}else {
			
		}
		
		
		//닉네임이 중복된 경우 ex)'고양이' 닉네임 이미 존재하여 중복시 => '고양이1234' 이런식으로 임의의 숫자를 뒤에 붙여서 생성해야함. 
		Optional<User> nicknameCheck = userRepo.findUserByNickname(nickname);
		nicknameInfo = nickname;

		if(nicknameCheck.isPresent()) {
			
			nicknameInfo = nicknameCheck.get().getNickname();
			
			while(nicknameCheck.isPresent()) {
				
				int n = (int) (Math.random()*50);
				
				String newNickname =  nicknameInfo+""+n; 
				
				nicknameInfo = newNickname;
				
				nicknameCheck = userRepo.findUserByNickname(newNickname);
						
			}//while
			
		}
		
		User user = User.builder()
						.loginId(snsId)  //고유식별자를 loginId에 저장하는 방식
						.password(passwordEncoder.encode("11111"))
						.nickname(nicknameInfo)
						.social(true)
						.status("T")
						.build();
		
		user.addMemberRole(MemberRole.USER);
		
		User savedUser= userRepo.save(user);
		
		SnsInfo snsInfo = new SnsInfo();
		snsInfo.setSnsId(snsId);
		snsInfo.setSnsType(clientName);
		snsInfo.setSnsCondate(LocalDateTime.now());
		snsInfo.setUser(savedUser);
		log.info("@@@user"+user);
		
		snsRepo.save(snsInfo);
		
		return savedUser;
	}
}
