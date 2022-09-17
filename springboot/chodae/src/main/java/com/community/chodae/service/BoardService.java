package com.community.chodae.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.community.chodae.domain.Post;
import com.community.chodae.domain.Reply;
import com.community.chodae.domain.User;
import com.community.chodae.dto.PostDTO;
import com.community.chodae.dto.ReplyDTO;
import com.community.chodae.image2.Image2;

public interface BoardService {
//	
//	1.게시글 조회 추가 수정 삭제
//	2.댓글 조회 추가 수정 삭제 
//	3. 추천 조회 추가 수정 삭제 
//	4.이미지 리스트로 추가  => 특정 위치에 저장...?  / 조회 / 수정/ 삭제
	
	//1. 게시글 조회 추가 수정 삭제
	
	
	
	// 게시판 검색  및 조회 
	Page<Post> searchPost(String boardName, String searchType, String keyword, Pageable pageable);
	
	Post getPost(Long boardNo);


	Post insertPost(String boardName, String title, String content, String nickname, String category);
	
	void deleteCategoryAll(Long postNo);
	Post updatePost(Long postNo, String title, String content, String category);
	
	Long deletePost(String boardName, Long postNo, String nickname);
	
	//리뷰게시판 인덱스로 게시글 검색
	Post findPostByIndex(String index);
	
	
	//2. 댓글 조회 추가 수정 삭제
//	List<Reply> getReply(); //댓글조회는 게시글조회할때 조회할 수 있어서 아직은 필요가 없을 것 같음. 
	
	Long insertReply(String boardName, Long postNo, String content, String nickname);
	Long updateReply(String boardName, Long postNo, Long replyNo, String content, String nickname);
	Long deleteReply(String boardName, Long postNo, Long replyNo, String nickname);
	
	
	//3.추천
	
	Long insertRecommend(String boardName, String nickname, String type, Long targetNo);
	
	Long deleteRecommend(String boardName, String nickname, String type, Long targetNo);
	

	long saveImg(MultipartFile file, Post post);
	
	Optional<Image2> updateImg(Post post, MultipartFile file);
	
	//내가 쓴 글
	Page<Post> findMyPost(String nickname, String searchType, String keyword, Pageable pageable);
	
	//내가 쓴 댓글
	Page<Reply> findMyReply(String nickname, String searchType, String keyword, Pageable pageable);
	
	//전체 통합검색
	Page<Post> getUnifiedSearch(String boardName, String searchType, String keyword, Pageable pageable);
	
	default PostDTO entityToDto(Post entity) {
		PostDTO dto = PostDTO.builder()
				.postNo(entity.getPostNo())
				.board(entity.getBoard())
				.category(entity.getCategory())
				.postTitle(entity.getPostTitle())
				.postContent(entity.getPostContent())
				.id(entity.getId())
				.nickname(entity.getNickname())
				.finduser(entity.getFinduser())
				.finduser2(entity.getFinduser2())
				.replyCount(entity.getReplyCount())
				.replies(entity.getReplies())
				.postViews(entity.getPostViews())
				.postLike(entity.getPostLike())
				.postRegdate(entity.getPostRegdate())
				.postModdate(entity.getPostModdate())
				.level(entity.getLevel())
				.postLevel(entity.getPostLevel())
				.postNotice(entity.getPostNotice())
				.postComment(entity.getPostComment())
				.postDisplay(entity.getPostDisplay())
				.filename(entity.getImage())
				.build();
		
		return dto;
	}
	default ReplyDTO entityToReplyDto(Reply entity) {
		ReplyDTO dto = ReplyDTO.builder()
				.replyNo(entity.getReplyNo())
				.post(entity.getPost())
				.boardNo(entity.getBoardNo()) //+추가로 게시판이름
				.replyContent(entity.getReplyContent())
				.replyRegdate(entity.getReplyRegdate())
				.replyModdate(entity.getReplyModdate())
				.id(entity.getId())
				.nickname(entity.getNickname())
				.replyLike(entity.getReplyLike())
				.level(entity.getLevel())
				.upperReply(entity.getUpperReply())
				.Finduser3(entity.getFinduser3())
				.recomm(entity.getRecomm())
				.build();
		
		return dto;
	}



	User applyStudy(String boardName, Long targetNo, String nickname);

	User accept(String nickname, Long postNo);

	User decline(String nickname, Long postNo);

	Page<User> application(long postNo);

	

	

	

	
	
	
	

}
