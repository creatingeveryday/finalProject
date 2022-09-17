package com.community.chodae.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.community.chodae.domain.Board;
import com.community.chodae.domain.Category;
import com.community.chodae.domain.PostContent;
import com.community.chodae.domain.Reply;
import com.community.chodae.image2.Image2;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PostDTO {
	//엔티티 객체를 넘기면 포스트 내용이 안넘어가서 따로 dto를 반환하는 방법으로 처리.
	
	private Long postNo;//게시글 번호
	private Board board;//게시판 번호(외래키)
	private PostContent postContent;
	
	private Long id;//작성자 
	private String nickname; //작성자닉네임
	
	private String postTitle;//제목
	private Integer replyCount;//이 게시글에 달린 댓글수
	private Integer postViews;// 조회수
	private Integer postLike;// 추천수
	private LocalDateTime postRegdate;//작성일자
	private LocalDateTime postModdate;//수정일자
	private Integer level;  //회원 등급 
	private Integer postLevel; //게시글 등급
	private List<Image2> filename;
	private String postNotice;//공지사항여부
	private String postComment;//댓글사항 여부 
	private String postDisplay;// 게시 여부
	private List<Reply> replies;
	private List finduser;
	private List finduser2;
	private List<Category> category;
	
}
