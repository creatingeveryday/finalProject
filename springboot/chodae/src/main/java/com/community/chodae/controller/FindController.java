package com.community.chodae.controller;

import com.community.chodae.domain.User;
import com.community.chodae.dto.MemberDTO;
import com.community.chodae.service.UserFindService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import lombok.extern.java.Log;

@Log
@RestController
@RequestMapping("/api")
public class FindController {
	
	private final UserFindService userFindService;
	
	@Autowired
	public FindController(UserFindService userFindService) {
		this.userFindService = userFindService;
	}
	
	@PostMapping("/refresh")
	public ResponseEntity<String> getTokenByRefreshToken(@RequestParam String nickname, @RequestParam String refreshToken ) {
		
		String accessToken = "no";
		log.info(nickname+"::::"+refreshToken);
		//로그인시 발급하여 저장한 리프레시 토큰을 전달받은 리프레시토큰과  비교 
		User user = userFindService.checkRefreshToken(nickname, refreshToken);
		
		if(user == null) 
			return new ResponseEntity<String>(accessToken, HttpStatus.FORBIDDEN);
		
		log.info(accessToken);
		accessToken = userFindService.getAccessToken(nickname);
		
		return new ResponseEntity<String>(accessToken, HttpStatus.OK);
		
	}
	
	//성함+메일로 [로그인용 아이디] 찾기
	@GetMapping("/find/id")
	public ResponseEntity<String> test(@RequestParam("name") String name, @RequestParam("email") String email) {
		log.info("이름:"+name+",이메일:"+email);
		//서비스 호출
		String foundId = userFindService.searchId(name, email);
		
		log.info(foundId);
		
		if(foundId == null) {
			return new ResponseEntity<String>("NOT FOUND", HttpStatus.FORBIDDEN);	
		}
		
		return new ResponseEntity<String>(foundId, HttpStatus.OK);	
	}
	
	
	//아이디와 이메일 일치하는 회원 존재시 -> 결과값 반환, 프론트에서는 비밀번호 재설정 창으로 이동
	@GetMapping("/find/user")
	public ResponseEntity<String> isUser(@RequestParam("loginId") String loginId, @RequestParam("email") String email) {
		String foundId = userFindService.isUser(loginId, email);
		
		if(foundId ==null) {
			return new ResponseEntity<String>("NOT FOUND", HttpStatus.FORBIDDEN);
		}
		
		return new ResponseEntity<String>(foundId, HttpStatus.OK);
	}

	//비밀번호 재설정 : 비밀번호 업데이트 
	//암호화하여 저장
	@PutMapping("/find/ps")
	public int updatePs(@RequestParam("id") String id, @RequestParam("password") String password) {
		log.info("로그인아이디:"+id+",업데이트 비밀번호:"+password);
		return userFindService.updatePassword(id, password);
	}
	
	@GetMapping("/user/check")
	public ResponseEntity<String> isExistNickname(@RequestParam("nickname") String nickname) {
		
		String foundNickname = userFindService.isExistNickname(nickname);
		
		if(foundNickname != null) {
			return new ResponseEntity<String>("EXIST", HttpStatus.FORBIDDEN);
		}
		
		return new ResponseEntity<String>("NO", HttpStatus.OK);
	}
	
	@GetMapping("/user/info")
	public ResponseEntity<MemberDTO> getUserInfo(@RequestParam("nickname") String nickname) {
		
		MemberDTO dto = userFindService.getUserInfo(nickname);
		
		if(dto == null) {
			return new ResponseEntity<MemberDTO>(dto, HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<MemberDTO>(dto, HttpStatus.OK);
	}
	
	@PutMapping("/user/info")
	public ResponseEntity<String> updateUserInfo(
											@RequestParam("currentNickname") String currentNickname,
											@RequestParam("newNickname") String newNickname
											) {
		
		if(currentNickname.equals(newNickname)) {
			return new ResponseEntity<String>("SAME", HttpStatus.FORBIDDEN);
		}
		String msg = userFindService.updateUserInfo(currentNickname, newNickname);
		
		
		if(msg == null) {
			return new ResponseEntity<String>("FAIL", HttpStatus.FORBIDDEN);
		}
		
		return new ResponseEntity<String>("SUCCESS", HttpStatus.OK);
	}
	
	@DeleteMapping("/user/bye/{nickname}")
	public ResponseEntity<MemberDTO> sleepUser(@PathVariable("nickname") String nickname) {
		
		MemberDTO dto = userFindService.sleepUser(nickname);
		
		if(dto == null) {
			return new ResponseEntity<MemberDTO>(dto, HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<MemberDTO>(dto, HttpStatus.OK);
	}
	
	
	
	
}
