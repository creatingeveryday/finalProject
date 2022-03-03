package com.chodae.find.controller;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.chodae.find.category.BoardGroup;
import com.chodae.find.domain.Post;
import com.chodae.find.dto.PostDTO;
import com.chodae.find.service.BoardService;
import com.chodae.find5.repository.CategoryRepo;

import lombok.extern.java.Log;

@Log
@RestController
public class BoardController {

	private final BoardService boardService;
	
	@Autowired
	public BoardController( BoardService boardService) {
		this.boardService = boardService;
	}
	


	//게시판 전체조회
	@Transactional
	@GetMapping("/{boardName}")
	Page<Post> getPostList2(@PathVariable String boardName,
			@RequestParam int page){
		Page<Post> result = boardService.getPostList3(boardName, page);
		
		return result;	
	}
//	//게시판 전체조회
//	@Transactional
//	@GetMapping("/{boardName}/list/{page}")
//	Page<Post> getPostList2(@PathVariable String boardName,@PathVariable int page,
//			@RequestParam int page){
//		Page<Post> result = boardService.getPostList3(boardName, page);
//		
////		System.out.println(result);
////		System.out.println(result.getTotalPages());
////		System.out.println(result.getTotalElements());
////		System.out.println(result.getNumber());
////		System.out.println(result.hasPrevious());
////		System.out.println(result.hasNext());
////		System.out.println(result.nextPageable());
////		System.out.println(result.previousPageable());
////		System.out.println(result.getContent());
//		return result;	
//	}
	
//	//게시판 전체조회
//	@Transactional
//	@GetMapping("/{boardName}/list")
//	List<Post> getPostList(@PathVariable String boardName){
//		List<Post> list = boardService.getPostList(boardName);
//		
//		list.forEach(post -> {
//			log.info(""+post);
//		});
//		
//		return list;	
//	}
	 
	//게시글 검색 (특정한 게시판에서 -- 제목, 내용 , 작성자 , 제목+내용,) 
	//분류 :  카테고리(지역,사용언어, 수준, 프로그램,분야:풀스택, 프론트엔드, 백엔드 ...), 평점, 
	@Transactional
	@GetMapping("/{boardName}/list/search")
	List<Post> getSearchPost(@PathVariable String boardName,
			@RequestParam String searchType,
			@RequestParam String keyword){
		
		List<Post> list = boardService.searchPost(boardName, searchType, keyword);
		
		return list;	
	}
	//특정 게시글 조회
	@Transactional
	@GetMapping("/{boardName}/{postNo}")
	PostDTO getPost(@PathVariable String boardName,@PathVariable Long postNo){
		Post post = boardService.getPost(postNo);
		PostDTO dto = boardService.entityToDto(post);
		System.out.println(dto);
		return dto;	
	}
	
	//게시글 추가
	@Transactional
	@PostMapping("/{boardName}")
	ResponseEntity<Integer> insertPost(@PathVariable String boardName,
			@RequestParam String title,
			@RequestParam String content,
			@RequestParam String nickname,
			@RequestParam(required = false) String category
			){
		
		Post post = boardService.insertPost(boardName, title, content, nickname,category);
		log.info(""+post);
		return new ResponseEntity<Integer>(BoardGroup.valueOf(boardName).getValue(), HttpStatus.OK);	
	}
	
	//게시글 업데이트
		@Transactional
		@PutMapping("/{boardName}/{postNo}")
		ResponseEntity<Long> updatePost(@PathVariable String boardName, @PathVariable Long postNo,
				@RequestParam String title,
				@RequestParam String content,
				@RequestParam String nickname,// 닉네임은 아직 사용필요 x 
				@RequestParam(required = false) String category
				){
			
			//작성자 닉네임와 현재 로그인된 id의 닉네임이 일치할 때 업데이트? 아직
			
			//기존 카테고리 모두 삭제
			boardService.deleteCategoryAll(postNo);
			
			Long updatedPostNo = boardService.updatePost(postNo, title, content, category);
			
			return new ResponseEntity<Long>(updatedPostNo, HttpStatus.OK);	
		}
		
	//게시글 삭제
		@Transactional
		@DeleteMapping("/{boardName}/{postNo}")
		ResponseEntity<Long> deletePost(@PathVariable String boardName, @PathVariable Long postNo,
				@RequestParam String nickname){
			
			//프론트에서는 삭제가능한 버튼이 작성자 본인에게만 표시되어야 함.?
			//작성자 닉네임와 현재 로그인된 id의 닉네임이 일치할 때 업데이트는 아직
			
			Long deletedPostNo = boardService.deletePost(boardName,postNo,nickname); //게시글 삭제시 연결된 카테고리, 댓글, 게시글 내용 모두 자동 삭제됨
			
			return new ResponseEntity<Long>(deletedPostNo, HttpStatus.OK);	
		}
		
	//댓글추가
	@Transactional
	@PostMapping("/{boardName}/{postNo}/reply")
	public ResponseEntity<Long> insertReply(@PathVariable String boardName,@PathVariable Long postNo,
			@RequestParam String content,
			@RequestParam String nickname) {
			
		Long insertedpostNo = boardService.insertReply(boardName, postNo, content, nickname);
		
		return new ResponseEntity<Long>(insertedpostNo,HttpStatus.OK);
	}
	
	//댓글 수정
	@Transactional
	@PutMapping("/{boardName}/{postNo}/reply/{replyNo}")
	public ResponseEntity<Long> updateReply(@PathVariable String boardName,@PathVariable Long postNo, @PathVariable Long replyNo,
			@RequestParam String content,
			@RequestParam String nickname) {
			
		Long updatedReplyNo = boardService.insertReply(boardName, postNo, content, nickname);
		
		return new ResponseEntity<Long>(updatedReplyNo,HttpStatus.OK); 
	}
	
	//댓글 삭제
	@Transactional
	@DeleteMapping("/{boardName}/{postNo}/reply/{replyNo}")
	public ResponseEntity<Long> deleteReply(@PathVariable String boardName,@PathVariable Long postNo, @PathVariable Long replyNo,
			@RequestParam String nickname) {
			
		Long deletedReplyNo = boardService.deleteReply(boardName, postNo, replyNo, nickname);
		
		return new ResponseEntity<Long>(deletedReplyNo,HttpStatus.OK); 
	}
	
	
	//추천 작성
	//회원번호,게시판 번호, (댓글번호  or 게시글번호) 
	@Transactional
	@PostMapping("/{boardName}/recomm/{type}/{targetNo}") //type: 게시글 추천인지 댓글 추천인지, targetNo: 게시글번호, 댓글번호
	public ResponseEntity<Long> insertRecomm(@PathVariable String boardName,@PathVariable String type, @PathVariable Long targetNo,
			@RequestParam String nickname) {
			
		Long number = boardService.insertRecommend(boardName, nickname, type, targetNo);
		
		return new ResponseEntity<Long>(number,HttpStatus.OK); 
	}
	
	//추천 취소(삭제)
	@Transactional
	@DeleteMapping("/{boardName}/recomm/{type}/{targetNo}") //type: 게시글 추천인지 댓글 추천인지, targetNo: 게시글번호, 댓글번호
	public ResponseEntity<Long> deleteRecomm(@PathVariable String boardName,@PathVariable String type, @PathVariable Long targetNo,
			@RequestParam String nickname) {
			
		Long number = boardService.deleteRecommend(boardName, nickname, type, targetNo);
		
		return new ResponseEntity<Long>(number,HttpStatus.OK); 
	}
	
	//게시글 카테고리 반영 ? 
	//게시글 검색 ? 
	 
	
}
