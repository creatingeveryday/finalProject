package com.community.chodae.dto;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import lombok.ToString;

@ToString
public class PageDTO {
	
	private static final int DEFAULT_SIZE = 10;
	private static final int DEFAULT_MAX_SIZE = 10;
	
	private int page;
	private int size;
	
	public PageDTO() {
		this.page = 1;
		this.size = DEFAULT_SIZE;
	}
	
	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page < 0 ? 1 : page;
	}
	
	public int getSize() {
		return size;
	}
	
	public void setSize(int size) {
		this.size = size < DEFAULT_SIZE || DEFAULT_MAX_SIZE  < size ? DEFAULT_SIZE : size;
	}
	//기본 날짜 순,//  추천순 버튼누름-> 정렬조건 추가되어 재검색? (추천순, 조회순, (댓글순) 많은 순으로 정렬)
	public Pageable makePageable(int direction, String...props) {
		Sort.Direction dir = direction == 0 ? Sort.Direction.DESC : Sort.Direction.ASC;
		return PageRequest.of(this.page -1, this.size, dir, props);
		
	}

}
