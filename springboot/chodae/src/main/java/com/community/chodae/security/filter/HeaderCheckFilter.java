package com.community.chodae.security.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.community.chodae.security.util.JWTUtil;

import io.jsonwebtoken.Claims;
import lombok.extern.java.Log;

@Log   //리퀘스트 헤더 Authorization 헤더 추출하여 헤더의 값이 일치하는지 확인 
public class HeaderCheckFilter extends OncePerRequestFilter {
	
	private AntPathMatcher antPathMatcher;
	private String pattern;
	private JWTUtil jwtUtil;
	
	public HeaderCheckFilter(String pattern, JWTUtil jwtUtil) {
		this.antPathMatcher = new AntPathMatcher();
		this.pattern = pattern;
		this.jwtUtil = jwtUtil;
	}
	
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		

		
		if(antPathMatcher.match(pattern, request.getRequestURI())) {

			
			if(request.getRequestURI().equals("/api/login")
					||request.getRequestURI().equals("/api/refresh")
					||request.getRequestURI().equals("/api/find/**")
					||request.getRequestURI().equals("/reg/**")) {

				filterChain.doFilter(request, response);
				return;
			} else {

				
				String authHeader = request.getHeader("Authorization");
				
				if(StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
					

					
					try {
						

						
						Claims claim = jwtUtil.validateAccessTokenExtract(authHeader.substring(7));

						
						
						String nickname = (String) claim.get("nickname");

						
						Collection<SimpleGrantedAuthority> authoritiesList = new ArrayList<>();
						List roleList =  (List) claim.get("role");
						
						String role = null;
						String username = null;
						
						Iterator it = roleList.iterator();
						while(it.hasNext()) {
							Map roleMap = (Map) it.next();

							
							role = (String) roleMap.get("authority");

							
							authoritiesList.add(new SimpleGrantedAuthority(role));
						}
						
						
						UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(nickname, null, authoritiesList);
//						UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, null, authoritiesList);
						SecurityContextHolder.getContext().setAuthentication(authToken);
						filterChain.doFilter(request, response);
						
					} catch(Exception e) {

						response.setStatus(HttpServletResponse.SC_FORBIDDEN);
						response.setContentType("aplication/json;charset=utf-8");
						
						String message = "PLEASE CHECK TOKEN";
						JSONObject json = new JSONObject();
						json.put("code", "403");
						json.put("message", message);
						
						PrintWriter out = response.getWriter();
						out.print(json);
						return;
						
						
					}
					
				}else {

					filterChain.doFilter(request, response);
					
				}	
				
				
			}//else
		}//filter - if
		
	}
	

}
