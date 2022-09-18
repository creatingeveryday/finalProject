package com.community.chodae.controller;

import javax.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.community.chodae.domain.User;
import com.community.chodae.service.RegService;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;



@Log
@RequiredArgsConstructor
@RestController
public class RegisterController {

	private final RegService regService;
	
	
	@Transactional
	@PostMapping("/reg")
	public ResponseEntity<?> saveAll(@RequestBody User user) {
		log.info(""+user);
		return new ResponseEntity<>(regService.regSave(user), HttpStatus.CREATED);
	}
	@Transactional
	@GetMapping("/reg")
	public ResponseEntity<?> getAllReg(@RequestBody User user) {
		return new ResponseEntity<>(regService.getAllReg(), HttpStatus.OK);
	}
	@Transactional
	@GetMapping("/reg/{id}")
	public ResponseEntity<?> getOneReg(@PathVariable Long id) {
		return new ResponseEntity<>(regService.getOneReg(id), HttpStatus.OK);
	}
	@Transactional
	@PutMapping("/reg/{id}")
	public ResponseEntity<?> updateReg(@PathVariable Long id,@RequestBody User user) {
		return new ResponseEntity<>(regService.updateReg(id, user), HttpStatus.OK);
	}
	@Transactional
	@DeleteMapping("/reg/{id}")
	public ResponseEntity<?> deleteReg(@PathVariable Long id,@RequestBody User user) {
		return new ResponseEntity<>(regService.deleteReg(id), HttpStatus.OK);
	}
	
	@Transactional
	@GetMapping("/reg/me/email")
	public ResponseEntity<?> getCheckMail(@RequestParam("email") String email) throws Exception{


		String Eresults =regService.mailCheck(email);
		if(Eresults==null) {

		}else if (Eresults != null) {

		}
		return new ResponseEntity<>(Eresults,HttpStatus.OK);
	}
	// 위랑 같은데 합쳐서 보여줄수 있을까? 시간 남으면 알아보기
	@Transactional
	@GetMapping("/reg/me/nickname")
	public ResponseEntity<?> getCheckNick(@RequestParam("nickname") String nickname) throws Exception{

		String Nresults =regService.nickCheck(nickname);
		if(Nresults==null) {

		}else if (Nresults != null) {

		}
		return new ResponseEntity<>(Nresults,HttpStatus.OK);
	}
	@Transactional
	@GetMapping("/reg/me/loginId")
	public ResponseEntity<?> getCheckId(@RequestParam("loginId") String loginId) throws Exception{

		String Iresults =regService.idCheck(loginId);
		if(Iresults==null) {

		}else if (Iresults != null) {

		}
		return new ResponseEntity<>(Iresults,HttpStatus.OK);
	}
}
