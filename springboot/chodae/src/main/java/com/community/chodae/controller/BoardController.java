package com.community.chodae.controller;

import javax.transaction.Transactional;

import com.community.chodae.domain.Post;
import com.community.chodae.domain.Reply;
import com.community.chodae.domain.User;
import com.community.chodae.dto.PageDTO;
import com.community.chodae.dto.PostDTO;
import com.community.chodae.service.BoardService;
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
import org.springframework.web.multipart.MultipartFile;



import lombok.extern.java.Log;

@Log
@RestController
public class BoardController {

	private final BoardService boardService;
	
	
	@Autowired
	public BoardController( BoardService boardService) {
		this.boardService = boardService;
		
	}

	//전체 통합검색 (제목+내용)
	@Transactional
	@GetMapping("/unified/{boardName}")
	Page<Post> getUniSearchPost(@PathVariable String boardName, PageDTO pageVo,
								@RequestParam String searchType,
								@RequestParam String keyword,
								@RequestParam(required = false, defaultValue = "postRegdate") String order){

		
		Page<Post> result = boardService.getUnifiedSearch(boardName, searchType, keyword, pageVo.makePageable(0, order));
		
		return result;	
	}
	
	
	//각 게시판 별 전체 조회 + 검색 
	@Transactional
	@GetMapping("/{boardName}")
	Page<Post> getSearchPost(@PathVariable String boardName, PageDTO pageVo,
			@RequestParam String searchType,
			@RequestParam String keyword,
			@RequestParam(required = false, defaultValue = "postRegdate") String order){

		
		Page<Post> result = boardService.searchPost(boardName, searchType, keyword, pageVo.makePageable(0, order));
		
		return result;	
	}
	
	//내가쓴 게시글 조회 (닉네임)
	@Transactional
	@GetMapping("/mypage/post/{nickname}")
	Page<Post> getMyPost(@PathVariable String nickname, PageDTO pageVo,
			@RequestParam String searchType,
			@RequestParam String keyword,
			@RequestParam(required = false, defaultValue = "postRegdate") String order){
		
		Page<Post> result = boardService.findMyPost(nickname, searchType, keyword,pageVo.makePageable(0, order));
		
		return result;	
	}
	//내가쓴 댓글 조회 (닉네임)
	@Transactional
	@GetMapping("/mypage/reply/{nickname}")
	Page<Reply> getMyReply(@PathVariable String nickname, PageDTO pageVo,
						   @RequestParam String searchType,
						   @RequestParam String keyword,
						   @RequestParam(required = false, defaultValue = "replyRegdate") String order){
		
		Page<Reply> result = boardService.findMyReply(nickname, searchType, keyword,pageVo.makePageable(0, order));
		
		return result;	
	}
	
	//특정 게시글 조회
	@Transactional
	@GetMapping("/{boardName}/{postNo}")
	PostDTO getPost(@PathVariable String boardName, @PathVariable Long postNo){
		Post post = boardService.getPost(postNo);
		
		PostDTO dto = boardService.entityToDto(post);
	
		return dto;	
	}
	
	//게시글 추가
	@Transactional
	@PostMapping("/{boardName}")
	ResponseEntity<Long> insertPost(@PathVariable String boardName,
			@RequestParam String title,
			@RequestParam String content,
			@RequestParam String nickname,
			@RequestParam(required = false, defaultValue = "[]") String category,
			@RequestParam(required = false) MultipartFile file
			){
	
		

		Post post = boardService.insertPost(boardName, title, content, nickname,category);

		
		
		if(file != null) {
			//새로운 이미지 저장
			boardService.saveImg(file, post);
		}
		
		return new ResponseEntity<Long>(post.getPostNo(), HttpStatus.OK);	
	}
//	@Transactional
//	@PostMapping("/{boardName}/{postNo}")
//	ResponseEntity<Long> updateImg(
//			@RequestParam(required = false) MultipartFile file,@PathVariable Long postNo){
//			if(file != null) {
//				
//				//기존 이미지 삭제
//				boardService.deleteImg(postNo);
//			}
//			return null;		
//				}
	//게시글 업데이트
	@Transactional
	@PutMapping("/{boardName}/{postNo}")
	ResponseEntity<Long> updatePost(@PathVariable String boardName, @PathVariable Long postNo,
			@RequestParam String title,
			@RequestParam String content,
			@RequestParam String nickname,// 닉네임은 아직 사용필요 x 
			@RequestParam(required = false, defaultValue = "[]") String category,
			@RequestParam(required = false) MultipartFile file
			){
		
		//작성자 닉네임와 현재 로그인된 id의 닉네임이 일치할 때 업데이트? 아직
		//게시판변경도 업데이트 되나? 
		
		//기존 카테고리 모두 삭제
		boardService.deleteCategoryAll(postNo);
		
		Post post = boardService.updatePost(postNo, title, content, category);
			
			
		// 기존 이미지 삭제 후 -> 새로운 이미지 저장
		if(file != null) {
			
			//기존 이미지 삭제
			boardService.updateImg(post, file);
			
		
			//새로운 이미지 저장
			
		}
		
		return new ResponseEntity<Long>(post.getPostNo(), HttpStatus.OK);	
	}
		
	//게시글 삭제
	@Transactional
	@DeleteMapping("/{boardName}/{postNo}/{nickname}")
	ResponseEntity<Long> deletePost(@PathVariable String boardName, @PathVariable Long postNo,
			@PathVariable String nickname){
		
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
			

		Long updatedReplyNo = boardService.updateReply(boardName, postNo,replyNo, content, nickname);

		
		return new ResponseEntity<Long>(updatedReplyNo,HttpStatus.OK); 
	}
	
	//댓글 삭제
	@Transactional
	@DeleteMapping("/{boardName}/{postNo}/reply/{replyNo}/{nickname}")
	public ResponseEntity<Long> deleteReply(@PathVariable String boardName,@PathVariable Long postNo, @PathVariable Long replyNo, 
			@PathVariable String nickname) {
			
		Long deletedReplyNo = boardService.deleteReply(boardName, postNo, replyNo, nickname);
		
		return new ResponseEntity<Long>(deletedReplyNo,HttpStatus.OK); 
	}
	
	
	//리뷰게시글 및 댓글 목록 조회
	@Transactional
	@GetMapping("/{boardName}/index/{index}")
	PostDTO getReviewPost(@PathVariable String boardName,@PathVariable String index){
		//1. 카테고리의 index로  게시글 객체 찾기
		Post post = boardService.findPostByIndex(index);
		
		if(post == null) {
			return null;
		}
		
		PostDTO dto = boardService.entityToDto(post);
	
		return dto;	
	}
	
	//리뷰게시판 댓글 추가
	@Transactional
	@PostMapping("/{boardName}/reply/{index}")
	public ResponseEntity<Long> insertReplyInReview(@PathVariable String boardName,@PathVariable String index,
			@RequestParam String content,
			@RequestParam String nickname) {
		
		//1. 카테고리의 index로  게시글 객체 찾기
		Post post = boardService.findPostByIndex(index);
		
		if(post == null) {
			return new ResponseEntity<Long>(0L,HttpStatus.FORBIDDEN);
		}
			
		Long insertedpostNo = boardService.insertReply(boardName, post.getPostNo(), content, nickname);
		
		return new ResponseEntity<Long>(insertedpostNo,HttpStatus.OK);
	}
		
	//리뷰 게시판 댓글 수정
	@Transactional
	@PutMapping("/{boardName}/index/{index}/reply/{replyNo}")
	public ResponseEntity<Long> updateReplyInReview(@PathVariable String boardName,@PathVariable String index, @PathVariable Long replyNo,
			@RequestParam String content,
			@RequestParam String nickname) {
			
		//1. 카테고리의 index로  게시글 객체 찾기
		Post post = boardService.findPostByIndex(index);
		
		if(post == null) {
			return new ResponseEntity<Long>(0L,HttpStatus.FORBIDDEN);
		}

		Long updatedReplyNo = boardService.updateReply(boardName, post.getPostNo(),replyNo, content, nickname);

		
		return new ResponseEntity<Long>(updatedReplyNo,HttpStatus.OK); 
	}
		
	// 리뷰게시판 댓글 삭제
	@Transactional
	@DeleteMapping("/{boardName}/post/{postNo}/reply/{replyNo}/{nickname}")
	public ResponseEntity<Long> deleteReplyInReview(@PathVariable String boardName,@PathVariable Long postNo, @PathVariable Long replyNo, 
			@PathVariable String nickname) {
		
		Long deletedReplyNo = boardService.deleteReply(boardName, postNo, replyNo, nickname);
		
		return new ResponseEntity<Long>(deletedReplyNo, HttpStatus.OK); 
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
	@DeleteMapping("/{boardName}/recomm/{type}/{targetNo}/{nickname}") //type: 게시글 추천인지 댓글 추천인지, targetNo: 게시글번호, 댓글번호
	public ResponseEntity<Long> deleteRecomm(@PathVariable String boardName,@PathVariable String type, @PathVariable Long targetNo,
			@PathVariable String nickname) {
			

		Long number = boardService.deleteRecommend(boardName, nickname, type, targetNo);
		
		return new ResponseEntity<Long>(number,HttpStatus.OK); 
	}
	//마이페이지 글
	@Transactional
	@PostMapping("/{boardName}/{nickname}")
	Page<User> getApplications (@RequestParam long postNo){
		
	 
		
		
	Page<User> userResult= boardService.application(postNo);
		return userResult;
		
		
		
	}
	
	// 신청
	@Transactional
	@PostMapping("/{boardName}/application/{targetNo}")
	ResponseEntity<Long> applyStatus(@RequestParam String nickname, @PathVariable String boardName,
			@PathVariable Long targetNo) {

		User user = boardService.applyStudy(boardName, targetNo, nickname);

		return new ResponseEntity<Long>(user.getId(), HttpStatus.OK);

	}

	// 마이페이지 수락
	@Transactional
	@GetMapping("/mypage/")
	ResponseEntity<Long> accept(@RequestParam String nickname, @RequestParam Long postNo){
	
	User user = boardService.accept(nickname ,postNo);
	
	
	
	return null;
	}

	// 마이페이지 거부
	@Transactional
	@DeleteMapping("/mypage/")
			ResponseEntity<Long> decline(@RequestParam String nickname, @RequestParam Long postNo){
		
		User user = boardService.decline(nickname, postNo);
		return null;
		}

	
}
