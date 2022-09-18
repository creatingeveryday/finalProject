package com.community.chodae.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.community.chodae.domain.Post;
import com.community.chodae.domain.Recommendation;
import org.springframework.data.repository.query.Param;

public interface RecommendationRepo extends JpaRepository<Recommendation, Long> {

    //해당 게시글에 대한 추천 존재 여부 확인
    @Query("SELECT r FROM Recommendation r WHERE r.user.id = :id and r.post.postNo = :postNo ")
    Optional<Recommendation> existPostRecomm(@Param("id") Long id, @Param("postNo") Long postNo);

    //해당 댓글에 대한 추천 존재 여부 확인
    @Query("SELECT r FROM Recommendation r WHERE r.user.id = :id and r.reply.replyNo = :replyNo ")
    Optional<Recommendation> existReplyRecomm(@Param("id") Long id, @Param("replyNo") Long replyNo);

    @Query("SELECT r.user.nickname FROM Recommendation r JOIN Reply k ON r.reply.replyNo = k.replyNo JOIN Post p on p.postNo = k.post.postNo WHERE k.post.postNo = :postNo ")
    List existReplyRecomm2(@Param("postNo") Long postNo);

    @Query("SELECT r.user.nickname FROM Recommendation r WHERE r.post.postNo = :postNo ")
    List existPostRecomm2(@Param("postNo") Long postNo);

    @Query("SELECT r.user.nickname FROM Recommendation r WHERE r.reply.replyNo = :replyNo ")
    List existReplyRecomm3(@Param("replyNo") Long replyNo);

    @Query("SELECT r FROM Recommendation r WHERE r.post.postNo = :postNo ")
    List<Recommendation> findAllRecommInPost(@Param("postNo") Long postNo);

    long countByApplication(String string);

    //	long countByApplication("T");


}
