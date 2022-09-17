package com.community.chodae.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.community.chodae.domain.Post;
import com.community.chodae.domain.Recommendation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ReplyDTO {
	private Long replyNo;//댓글 번호
	
	private Post post;//게시글번호
	
	private Integer boardNo;//게시판번호
	private String boardName;
	
	private String replyContent;//댓글내용
	private LocalDateTime replyRegdate;//작성일자
	private LocalDateTime replyModdate;//수정일자
	private Long id; //작성회원번호
	private String nickname;
	
	private Integer replyLike; //추천수
	private Integer level; //회원등급
	private Integer upperReply;//상위 댓글번호    = 아직 미구현으로 관계설정해주지 않음.
    private List Finduser3;
	
	private List<Recommendation> recomm = new ArrayList<Recommendation>();
}
