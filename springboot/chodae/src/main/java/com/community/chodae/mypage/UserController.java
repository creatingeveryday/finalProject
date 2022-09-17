package com.community.chodae.mypage;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.community.chodae.domain.User;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping(path="/api/users")
@RestController
public class UserController {
	
	private final UserService userService;
	
	@GetMapping("/{id}")
	public Map<String, Object> selectUserById(@PathVariable("id") long id){
		Map<String, Object> response = new HashMap<>();
		
		Optional<User> oUser = userService.selectById(id);
		if(oUser.isPresent()) {
			response.put("result","SUCCESS");
			response.put("user",oUser.get());
	}else {
		response.put("result","FAIL");
		response.put("reason","일치하는 회원 정보가 없습니다. 사용자 id를 확인해주세요.");
	}
		return response;

	}
	
	@PostMapping("")
	public Map<String, Object> insert(@RequestBody @Valid final UserRequest user){
		Map<String, Object> response = new HashMap<>();
		
		if(userService.insert(user)) {
			response.put("result", "SUCCESS");
		}else {
			response.put("result", "FAIL");
			response.put("reason", "회원 정보 저장 실패");
		}
		return response;
	}
	@PutMapping("/{id}")
	public Map<String, Object> update(@PathVariable("id") long id, @RequestBody @Valid final UserRequest user){
		Map<String, Object> response = new HashMap<>();
		
		int res = userService.update(id, user);
		if(res>0) {
			response.put("result","SUCCESS");
		}else {
			response.put("result","FAIL");
			response.put("reason","일치하는 회원정보가 없습니다. 사용자 id를 확인해주세요");
		}
		return response;
	}
	
	@PatchMapping("/{id}")
	public Map<String, Object> partialUpdate(@PathVariable("id") long id, @RequestBody @Valid final UserRequest user){
		Map<String, Object> response = new HashMap<>();
		
		int res = userService.update(id, user);
		if(res>0) {
			response.put("result","SUCCESS");
		}else {
			response.put("result","FAIL");
			response.put("reason","일치하는 회원정보가 없습니다. 사용자 id를 확인해주세요");
		}
		return response;
	}
	
	@DeleteMapping("/{id}")
	public Map<String, Object> delete(@PathVariable("id") long id){
		Map<String, Object> response = new HashMap<>();
		
		if(userService.delete(id) >0) {
			response.put("result","SUCCESS");
		}else {
			response.put("result","FAIL");
			response.put("reason","일치하는 회원정보가 없습니다. 사용자 id를 확인해주세요");
		}
		return response;
	}
}
