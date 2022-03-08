package com.chodae.find5.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.chodae.find.domain.Board;
import com.chodae.find.domain.Post;
import com.chodae.find.domain.User;

public interface PostRepo  extends JpaRepository<Post, Long>{
	
	List<Post> findPostByBoard(Board board);
	
	@Query("SELECT p FROM Post p WHERE p.board.boardNo = ?1")
	List<Post> findPostByBoard(int boardNo);
	
	@Query("SELECT p FROM Post p left join p.board b WHERE p.board.boardNo = :boardNo")
	List<Post> findPost3(@Param("boardNo") int boardNo);
	
	@Query("SELECT p.id, count(u) FROM Post p left outer join User u on u.id = p.id WHERE p.id = ?1 GROUP BY u")
	List<Object[]> getPostCountByWriter(Long id);
	
	///////////////////////테스트중 /////////////////////////////////////////
	
	//페이징 처리와 정렬 : 모든 쿼리메소드의 마지막 파라미터로 Pageable 인터페이스와 sort인터페이스 사용가능 
	//반환타입 : Slice 타입, Page 타입 , List 타입 이용 
//	@Query("SELECT p FROM Post p WHERE p.board.boardNo = ?1")
//	List<Post> findPostByBoard2(int boardNo, Pageable paging);
		
	@Query("SELECT p FROM Post p WHERE p.board.boardNo = ?1")
	Page<Post> findPostByBoardAndPage(int boardNo, Pageable paging);

		
	/////////////////////////////////////////////////////////////////////////
	
	//게시글 검색
	
	@Query("SELECT p FROM Post p WHERE p.board.boardNo = ?1 and p.postTitle Like %?2% ")
	Page<Post> getPostLikeTitle(int boardNo, String keyword, Pageable paging);
	
	@Query("SELECT p FROM Post p WHERE p.board.boardNo = ?1 and p.postContent.content Like %?2% ")
	Page<Post> getPostLikeContent(int boardNo, String keyword, Pageable paging);
	
	@Query("SELECT p FROM Post p WHERE p.board.boardNo = ?1 and (p.postTitle Like %?2% OR p.postContent.content Like %?2% ) ")
	Page<Post> getPostLikeTitleOrContent(int boardNo, String keyword, Pageable paging);
	
	@Query("SELECT p FROM Post p WHERE p.board.boardNo = ?1 and p.id = ?2 ")
	Page<Post> getPostFromWriter(int boardNo, Long id, Pageable paging);
	
	
	
	
}