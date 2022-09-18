package com.community.chodae.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.community.chodae.domain.Board;
import com.community.chodae.domain.Post;
import com.community.chodae.domain.Reply;

public interface PostRepo extends JpaRepository<Post, Long> {

    /**
     * 페이징 처리와 정렬 : 모든 쿼리메소드의 마지막 파라미터로 Pageable 인터페이스와 sort인터페이스 사용가능
     * 반환타입 : Slice 타입, Page 타입 , List 타입
     */
    List<Post> findPostByBoard(Board board);

    @Query("SELECT p FROM Post p WHERE p.board.boardNo = :boardNo")
    List<Post> findPostByBoard(@Param("boardNo") int boardNo);

    @Query("SELECT p FROM Post p left join p.board b WHERE p.board.boardNo = :boardNo")
    List<Post> findPostByBoardNo(@Param("boardNo") int boardNo);

    @Query("SELECT p.id, count(u) FROM Post p left outer join User u on u.id = p.id WHERE p.id = :id GROUP BY u")
    List<Object[]> getPostCountByWriter(@Param("id") Long id);

    @Query("SELECT p FROM Post p WHERE p.board.boardNo = :boardNo")
    Page<Post> findPostByBoardAndPage(@Param("boardNo") int boardNo, Pageable paging);

    /**
     * 게시글 검색
     */
    @Query("SELECT p FROM Post p WHERE p.board.boardNo = :boardNo and p.postTitle Like %:keyword% ")
    Page<Post> getPostLikeTitle(@Param("boardNo") int boardNo, @Param("keyword") String keyword, Pageable paging);

    @Query("SELECT p FROM Post p WHERE p.board.boardNo = :boardNo and p.postContent.content Like %:keyword% ")
    Page<Post> getPostLikeContent(@Param("boardNo") int boardNo, @Param("keyword") String keyword, Pageable paging);

    @Query("SELECT p FROM Post p WHERE p.board.boardNo = :boardNo and (p.postTitle Like %:keyword% OR p.postContent.content Like %:keyword% ) ")
    Page<Post> getPostLikeTitleOrContent(@Param("boardNo") int boardNo, @Param("keyword") String keyword, Pageable paging);

    @Query("SELECT p FROM Post p WHERE p.board.boardNo = :boardNo and p.id = :id ")
    Page<Post> getPostFromWriter(@Param("boardNo") int boardNo, @Param("id") Long id, Pageable paging);

    @Query("SELECT p FROM Post p left join p.category c where c.categoryName= :keyword and p.board.boardNo= :boardNo")
    Page<Post> getPostLikeLocation(@Param("boardNo") int boardNo, @Param("keyword") String keyword, Pageable paging);


    //리뷰 게시판 인덱스로 특정 게시글 찾기

    @Query("SELECT p FROM Post p left join p.category c WHERE c.categoryKind = :Kind and c.categoryName = :Name")
    Optional<Post> findCateKindAndName(@Param("Kind") String Kind, @Param("Name") String Name);


    /**
     * 나의 게시글
     */
    @Query("SELECT p FROM Post p WHERE p.id = :id")
    Page<Post> findMyPostById(@Param("id") Long id, Pageable paging);

    @Query("SELECT p FROM Post p WHERE p.id = :id and p.postTitle Like %:keyword% ")
    Page<Post> getMyPostLikeTitle(@Param("id") Long id, @Param("keyword") String keyword, Pageable paging);

    @Query("SELECT p FROM Post p WHERE p.id = :id and p.postContent.content Like %:keyword% ")
    Page<Post> getMyPostLikeContent(@Param("id") Long id, @Param("keyword") String keyword, Pageable paging);

    @Query("SELECT p FROM Post p WHERE p.id = :id and (p.postTitle Like %:keyword% OR p.postContent.content Like %:keyword% ) ")
    Page<Post> getMyPostLikeTitleOrContent(@Param("id") Long id, @Param("keyword") String keyword, Pageable paging);

    @Query("SELECT p FROM Post p left join p.category c where c.categoryName= :keyword and p.id = :id")
    Page<Post> getMyPostLikeLocation(@Param("id") Long id, @Param("keyword") String keyword, Pageable paging);

    // 나의 댓글

    @Query("SELECT r FROM Reply r WHERE r.id = :id and r.replyContent Like %:keyword% ")
    Page<Reply> getMyReplyLikeContent(@Param("id") Long id, @Param("keyword") String keyword, Pageable paging);

    @Query("SELECT r FROM Reply r WHERE r.id = :id")
    Page<Reply> getMyReplyById(@Param("id") Long id, Pageable paging);

    // 통합검색 전체게시판(7번 리뷰메인게시판의 게시글은 검색에서 제외)

    @Query("SELECT p FROM Post p WHERE p.board.boardNo <> 7  and (p.postTitle Like %?1% OR p.postContent.content Like %:keyword% ) ")
    Page<Post> getUniSearchPostLikeTitleOrContent(@Param("keyword") String keyword, Pageable paging);

}
