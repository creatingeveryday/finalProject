package com.community.chodae.security.filter;

import java.awt.PageAttributes.MediaType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import com.community.chodae.security.dto.MemberAuthDTO;
import com.community.chodae.security.util.JWTUtil;
import com.community.chodae.service.UserFindService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.java.Log;

@Log // authentication 
public class LoginFilter extends AbstractAuthenticationProcessingFilter {
	
	private final JWTUtil jwtUtil;
	private final UserFindService userFindService;
	
	public LoginFilter(String defaultFilterProcessesUrl, JWTUtil jwtUtil, UserFindService userFindService) {
		super(defaultFilterProcessesUrl);
		this.jwtUtil = jwtUtil;
		this.userFindService = userFindService;
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException, IOException, ServletException {


		log.info("@@@@@@@@@@loginFilter@@@@@@@@@@@@@");
		log.info("@@@@@@@@@attemptAuthentication@@@@@@@@@");
		
		 
		String loginId = request.getParameter("loginId");
		String pw = request.getParameter("password");
		log.info(loginId+" ::::::" + pw);
		
		//포스트 방식으로 로그인하고 파라미터는 암호화해서 처리
		
		
		if(loginId == null) {
			throw new BadCredentialsException("아이디가 null입니다. ");
		}
		
//		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(loginId, pw);
//		log.info(":::@@@authToken:@@@::" + authToken);
		
		//직접인증처리함. => 다음단계는 인증후 성공 , 실패에 대한 후속 작업
		
		UsernamePasswordAuthenticationToken authToken2 = new UsernamePasswordAuthenticationToken(loginId, pw, new ArrayList<>());
		
//		Authentication authentication = getAuthenticationManager().authenticate(authToken);
		Authentication authentication = getAuthenticationManager().authenticate(authToken2);
		
		return authentication;
	}
	
	@Override  //인증 성공시 
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain,
			Authentication authResult) throws IOException, ServletException {
		
		log.info("@@@@@@@@@@@@@@apilogin=> success@@@@@@@@@@@@@@@@@");
		log.info("authResult:"+authResult);
		log.info("authResult.getPrincipal(): "+authResult.getPrincipal());
		log.info("authResult.getCredentials(): "+authResult.getCredentials());
		
		MemberAuthDTO userDTO = (MemberAuthDTO)authResult.getPrincipal();
		String userName = userDTO.getUsername();
		Object info = authResult.getPrincipal();
		
		
		log.info("info => "+info);
		log.info("MemberAuthDTO => "+userDTO);
		log.info("getUsername() => "+userName);
		
		String accessToken = null;
		String refreshToken = null;
		
		try {
			
			accessToken = jwtUtil.generateAccessToken(userDTO.getNickname(), authResult.getAuthorities());
			refreshToken = jwtUtil.generateRefreshToken(userDTO.getNickname());
			
			 Map<String, String> tokens = new HashMap<>();
		        tokens.put("accessToken",accessToken);
		        tokens.put("refreshToken",refreshToken);
			
			response.setContentType("aplication/json;charset=utf-8");
//			response.getOutputStream().write(accessToken.getBytes());
			new ObjectMapper().writeValue(response.getOutputStream(), tokens);
			
			userFindService.updateRefreshToken(userDTO.getNickname(), refreshToken);//발급받은 새로운 리프레시토큰으로 업데이트.
			
		} catch(Exception e ) {
			e.printStackTrace();
		}
		
	}
}
