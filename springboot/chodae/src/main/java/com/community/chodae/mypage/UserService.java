package com.community.chodae.mypage;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.community.chodae.domain.User;
import com.community.chodae.repository.UserRepo;

import lombok.RequiredArgsConstructor;


@Transactional (readOnly = true)
@RequiredArgsConstructor
@Service
public class UserService {
	
	private final UserRepo userRepo;
	
	public Optional<User> selectById(Long id){
		return userRepo.findById(id);
	}
	@Transactional //post 회원가입이랑 같은거임 insert api로 정보 넣어주면 db에 저장
	public boolean insert(UserRequest u) {
		if(userRepo.findByEmail(u.getEmail()).isPresent()) {
			return false;
		}
		userRepo.save(User.builder()
				.loginId(u.getLoginId()).email(u.getEmail()).nickname(u.getNickname())
				.name(u.getName()).language(u.getLanguage()).campus(u.getCampus())
				.phone(u.getPhone()).level(u.getLevel())
				.build());
		
		return true;
	}
	
	@Transactional
	public int update(long id, final UserRequest u) {
		Optional<User> oUser = userRepo.findById(id);//PK로 조회 결과가 존재할경우에만 업데이트
		if(oUser.isPresent())//아이디 비교해서 있으면 0을 반환
			return 0;
		
		User user = oUser.get();
		user.setLoginId(u.getLoginId());
		user.setEmail(u.getEmail());
		user.setNickname(u.getNickname());
		user.setName(u.getName());
		user.setLanguage(u.getLanguage());
		user.setCampus(u.getCampus());
		user.setPhone(u.getPhone());
		user.setLevel(u.getLevel());
			return 1;
	}
	
	@Transactional
	public int partialUpdate(long id, final UserRequest u) {
		Optional<User> oUser= userRepo.findById(id);
		if(oUser.isPresent())
			return 0;
		
		User user = oUser.get();
		if(StringUtils.isNotBlank(u.getLoginId())) user.setLoginId(u.getLoginId());
		if(StringUtils.isNotBlank(u.getEmail())) user.setEmail(u.getEmail());
		if(StringUtils.isNotBlank(u.getNickname())) user.setNickname(u.getNickname());
		if(StringUtils.isNotBlank(u.getName())) user.setName(u.getName());
		if(StringUtils.isNotBlank(u.getLanguage())) user.setLanguage(u.getLanguage());
		if(StringUtils.isNotBlank(u.getCampus())) user.setCampus(u.getCampus());
		user.setPhone(u.getPhone());
		user.setLevel(u.getLevel());
		userRepo.save(user);
		return 1;
		
	}
	@Transactional
	public int delete(long id) {
		Optional<User> oUser = userRepo.findById(id);
		if(oUser.isPresent()) {
			userRepo.delete(oUser.get());
			return 1;
		}
		return 0;
	}

}
