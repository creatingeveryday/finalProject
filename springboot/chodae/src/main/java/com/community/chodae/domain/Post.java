package com.community.chodae.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.community.chodae.image.Image;
import com.community.chodae.image2.Image2;
import com.community.chodae.image2.Image2;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Entity
@Table(name = "post_info")
@Setter
public class Post {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "post_no")
	private Long postNo;//게시글 번호
	
	
	@ManyToOne
	@JoinColumn(name = "board_no")
	private Board board;//게시판 번호(외래키)
	
	@Transient
	private String korBoardName;
	
	
	@OneToOne(cascade = CascadeType.ALL ) //케스케이드 옵션을 설정하지 않으면 오류가 발생한다. 1대1 관계는 페치타입 기본 eager lazy하니까 오류가 난다.
	@JoinColumn(name = "content_no")
	@JsonIgnore
	private PostContent postContent; //게시글 내용 : 1대1 관계 (게시글 내용 테이블의 기본키와 연결)    1 대 1 단방향만 연결설정함. 
	
	private Long id;//작성자 
	
	@Transient // 객체에 임시 값 보관할때 사용. 매핑하지 않는 필드로 데이터베이스 저장, 조회하지 않음 
	private String nickname;
	
	private String postTitle;//제목
	
	
	private Integer replyCount;//이 게시글에 달린 댓글수
	
	
	private Integer postViews;// 조회수
	
	
	private Integer postLike;// 추천수
	
	private LocalDateTime postRegdate;//작성일자
	
	private LocalDateTime postModdate;//수정일자
	
	
	private Integer level;  //회원 등급 
	
	
	private Integer postLevel; //게시글 등급
	
	
	private String postNotice;//공지사항여부
	
	
	private String postComment;//댓글사항 여부 
	
	
	private String postDisplay;// 게시 여부
	
	//mappedBy로 설정된 컬럼은 db 테이블 구조에 영향을 주지 않는다.  
	//댓글과의 일 대 다 관계, 주인키는 아님. 
	//@Fetch(FetchMode.SUBSELECT)
	@JsonManagedReference
	@OneToMany(mappedBy = "post",cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Reply> replies = new ArrayList<Reply>();
	
	//카테고리와 일 대 다 관계, 주인키는 아님. 
	//@Fetch(FetchMode.SUBSELECT)
	@JsonManagedReference
	@OneToMany(mappedBy = "post",cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Category> category = new ArrayList<Category>();
	
	@JsonManagedReference
	@OneToMany(mappedBy ="post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)

	private List<Image2> image = new ArrayList<Image2>();
	
	@JsonManagedReference
	@OneToMany(mappedBy ="post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Recommendation> recomm = new ArrayList<Recommendation>();


	@Transient
	private List finduser;
	@Transient
	private List finduser2;
}
