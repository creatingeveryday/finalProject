package com.community.chodae.mypage;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserRequest {
	@NotNull private String loginId, email, nickname, name, language, campus; 
	@NotNull private int phone;
	@NotNull private Integer level;

}
