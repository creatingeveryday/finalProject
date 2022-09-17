package com.community.chodae.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.community.chodae.domain.Post;
import com.community.chodae.domain.Recommendation;

public interface RecommendationRepo extends JpaRepository<Recommendation, Long> {
	
	//해당 게시글에 대한 추천 존재 여부 확인 
	@Query("SELECT r FROM Recommendation r WHERE r.user.id = ?1 and r.post.postNo = ?2 ")
	Optional<Recommendation> existPostRecomm(Long id, Long postNo);

	//해당 댓글에 대한 추천 존재 여부 확인 
	@Query("SELECT r FROM Recommendation r WHERE r.user.id = ?1 and r.reply.replyNo = ?2 ")
	Optional<Recommendation> existReplyRecomm(Long id, Long replyNo);

	@Query("SELECT r.user.nickname FROM Recommendation r JOIN Reply k ON r.reply.replyNo = k.replyNo JOIN Post p on p.postNo = k.post.postNo WHERE k.post.postNo = ?1 ")
	List existReplyRecomm2(Long postNo);
	
	@Query("SELECT r.user.nickname FROM Recommendation r WHERE r.post.postNo = ?1 ")
	List existPostRecomm2(Long postNo);
	
	@Query("SELECT r.user.nickname FROM Recommendation r WHERE r.reply.replyNo = ?1 ")
    List existReplyRecomm3(Long replyNo);
  
  @Query("SELECT r FROM Recommendation r WHERE r.post.postNo = ?1 ")
	List<Recommendation> findAllRecommInPost(Long postNo);

long countByApplication(String string);

  //	long countByApplication("T");
	
	
  
}
