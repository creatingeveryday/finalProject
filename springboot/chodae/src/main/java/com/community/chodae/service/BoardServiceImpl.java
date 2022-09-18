package com.community.chodae.service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.community.chodae.domain.Board;
import com.community.chodae.domain.Category;
import com.community.chodae.domain.Post;
import com.community.chodae.domain.PostContent;
import com.community.chodae.domain.Recommendation;
import com.community.chodae.domain.Reply;
import com.community.chodae.domain.User;
import com.community.chodae.group.BoardGroup;
import com.community.chodae.image2.Image2;
import com.community.chodae.image2.ImageRepository;
import com.community.chodae.repository.CategoryRepo;
import com.community.chodae.repository.PostRepo;
import com.community.chodae.repository.RecommendationRepo;
import com.community.chodae.repository.ReplyRepo;
import com.community.chodae.repository.UserRepo;

import lombok.extern.java.Log;

@Log
@Service
public class BoardServiceImpl implements BoardService {
	
	private final PostRepo postRepo;
	private final ReplyRepo replyRepo;
	private final RecommendationRepo recommRepo;
	private final CategoryRepo categoryRepo;
	private final UserRepo userRepo;
	private final ImageRepository imageRepo;
	
	
	@Autowired
	public BoardServiceImpl(PostRepo postRepo, ReplyRepo replyRepo, RecommendationRepo recommRepo,
			CategoryRepo categoryRepo, UserRepo userRepo,ImageRepository imageRepo) {
		
		this.postRepo = postRepo;
		this.replyRepo = replyRepo;
		this.recommRepo = recommRepo;
		this.categoryRepo = categoryRepo;
		this.userRepo = userRepo;
		this.imageRepo = imageRepo;
	}
	
	@Override
	public long saveImg(MultipartFile file, Post post) {
		//새로운 이미지 저장

		
			String fileName = file.getOriginalFilename();
			String saveFileName= post.getId()+"-"+post.getPostNo()+"-"+fileName;
			String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
					.path("/files/")
					.path(saveFileName)
					.toUriString();
			
			imageRepo.save(Image2.builder()
					.filename(saveFileName)
					.fileDownloadUri(fileDownloadUri)
					.fileType(file.getContentType())
					.post(post)
					.size(file.getSize())
					.build());
			
			
			File newFileName = new File("/home/final/img/"+post.getId()+"-"+post.getPostNo()+"-"+fileName);
			
			
			if(!newFileName.exists()) { //파일 경로 없으면 생성.
				if(newFileName.getParentFile().mkdirs()) {
					try {
						newFileName.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
	        
	        
	        try{
	        	file.transferTo(newFileName);
	            
	        }catch (IllegalStateException | IOException e){
	            e.printStackTrace();
	        }
	        
	        		
					
			
		//	Image2 fileResponse = new Image2( fileName, fileDownloadUri, file.getContentType(), file.getSize(), post);
			return post.getPostNo();
			}
		
		
			
 
	
	@Override
	public Optional<Image2> updateImg(Post post ,MultipartFile file) {
		
		Optional<Image2> list = imageRepo.findByPostNo(post.getPostNo());
		

		
		
		if(list.isPresent()) {
		String filename= file.getOriginalFilename();
		String saveFileName= post.getId()+"-"+post.getPostNo()+"-"+filename;
			
			Image2 newImg = list.get();
			
			String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
					.path("/files/")
					.path(saveFileName)
					.toUriString();
			
			newImg.setFilename(saveFileName);
			newImg.setFileDownloadUri(fileDownloadUri);
			newImg.setPost(post);
			newImg.setSize(file.getSize());
			newImg.setFileType(file.getContentType());
			
			imageRepo.save(newImg);
			
			File newFileName = new File("/home/final/img/"+post.getId()+"-"+post.getPostNo()+"-"+filename);
			
			
			if(!newFileName.exists()) { //파일 경로 없으면 생성.
				if(newFileName.getParentFile().mkdirs()) {
					try {
						newFileName.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
	        
	        
	        try{
	        	file.transferTo(newFileName);
	            
	        }catch (IllegalStateException | IOException e){
	            e.printStackTrace();
	        }
			
	        return list;
		}
		
		return null;
		
	}
		
	
	
	

	@Override // 특정 게시글 조회. 조회시 글 작성자 및 댓글작성자도 닉네임 전달
	public Post getPost(Long postNo) {
		
		Post post = postRepo.findById(postNo).get();

		post.setPostViews(post.getPostViews()+1);
		postRepo.save(post);
		
		User user = userRepo.findById(post.getId()).get();
		post.setNickname(user.getNickname());
		
		List finduser2 = recommRepo.existPostRecomm2(post.getPostNo());

		post.setFinduser2(finduser2);

		List finduser = recommRepo.existReplyRecomm2(post.getPostNo());

		
		post.setFinduser(finduser);
		
		List<Reply> replies = post.getReplies();

		
		replies.forEach(reply -> {

			User replyUser = userRepo.findById(reply.getId()).get();
			reply.setNickname(replyUser.getNickname());
			List finduser3 = recommRepo.existReplyRecomm3(reply.getReplyNo());
	        reply.setFinduser3(finduser3); 
						
	
		});

		

	
		

		post.setReplies(replies);

		return post;
	}
	

	@Override  //게시글 검색
	public Page<Post> searchPost(String boardName, String searchType, String keyword, Pageable pageable) {
		// type : 제목 title, 내용 content, 작성자 writer ,제목+내용 titleOrContent
		// 분류 :  카테고리(지역,언어...), 평점, 
		// 정렬: 추천순 , 댓글순, 조회순,  
		int boardNo = BoardGroup.valueOf(boardName).getValue();//게시판 번호 조회
		
		Page<Post> list = null;
		
		// 타입에 맞춰서 메소드 호출
		if(searchType.equals("titleOrContent")) {
			list = postRepo.getPostLikeTitleOrContent(boardNo, keyword, pageable);
			
		} else if (searchType.equals("title")) {
			list = postRepo.getPostLikeTitle(boardNo, keyword,pageable);
			
		} else if (searchType.equals("content")) {
			list = postRepo.getPostLikeContent(boardNo, keyword,pageable);
			
		} else if (searchType.equals("writer")) {
			
			User user = null;
			Optional<User> result = userRepo.findUserByNickname(keyword);
			if(result.isPresent()) {
				user = result.get();			
			}
			
			list = postRepo.getPostFromWriter(boardNo, user.getId(),pageable);
		} else if(searchType.equals("location")) {
			list = postRepo.getPostLikeLocation(boardNo, keyword, pageable);
			
		} else {
			// 검색조건 없이 조회시
			list = postRepo.findPostByBoardAndPage(BoardGroup.valueOf(boardName).getValue(), pageable);
		}
		
		list.forEach(post -> {
			User user = userRepo.findById(post.getId()).get();
			post.setNickname(user.getNickname());
		});
		
		return list;
	}
	@Override  //게시글 통합검색
	public Page<Post> getUnifiedSearch(String boardName, String searchType, String keyword, Pageable pageable) {
		//메인페이지 통합검색: 제목+내용 / 모든 게시판 게시글을 대상으로 검색후 각 게시판 별로 조회가능.
		
		Page<Post> list = null;
		int boardNo = 0;
		
		if(boardName.equals("all")) {
			list = postRepo.getUniSearchPostLikeTitleOrContent(keyword, pageable);
			
		}else {

			boardNo = BoardGroup.valueOf(boardName).getValue();//게시판 번호 조회
			
			list = postRepo.getPostLikeTitleOrContent(boardNo, keyword, pageable);
		}
		
		list.forEach(post -> {
			User user = userRepo.findById(post.getId()).get();
			post.setNickname(user.getNickname());
			post.setKorBoardName(BoardGroup.getBoardGroupByNo(post.getBoard().getBoardNo()).getKorName());
			
		});
		
		return list;
	}
	
	@Override  //내가 쓴 게시글 검색
	public Page<Post> findMyPost(String nickname, String searchType, String keyword, Pageable pageable) {
		// type : 제목 title, 내용 content ,제목+내용 titleOrContent
		
		
		//닉네임으로 id조회
		User user = null;
		
		Optional<User> result = userRepo.findUserByNickname(nickname);
		if(result.isPresent()) {
			user = result.get();			
		}
		
		Page<Post> list = null;
		
		// 타입에 맞춰서 메소드 호출
		if(searchType.equals("titleOrContent")) {
			list = postRepo.getMyPostLikeTitleOrContent(user.getId(), keyword, pageable);
			
		} else if (searchType.equals("title")) {
			list = postRepo.getMyPostLikeTitle(user.getId(), keyword,pageable);
			
		} else if (searchType.equals("content")) {
			list = postRepo.getMyPostLikeContent(user.getId(), keyword,pageable);
			
		} else if(searchType.equals("location")) {
			list = postRepo.getMyPostLikeLocation(user.getId(), keyword, pageable);
			
		} else {
			// 검색조건 없이 조회시
			list = postRepo.findMyPostById(user.getId(), pageable);
		}
		
		list.forEach(post -> {
			
			post.setNickname(nickname);
		});
		
		return list;
	}
	@Override  //내가 쓴 댓글 검색
	public Page<Reply> findMyReply(String nickname, String searchType, String keyword, Pageable pageable) {
		// type : 내용 content
		 
		
		//닉네임으로 id조회
		User user = null;
		
		Optional<User> result = userRepo.findUserByNickname(nickname);
		if(result.isPresent()) {
			user = result.get();			
		}
		
		Page<Reply> list = null;
		
		// 타입에 맞춰서 메소드 호출
		if(searchType.equals("content")) {
			list = postRepo.getMyReplyLikeContent(user.getId(), keyword, pageable);
			
		} else {
			// 검색조건 없이 조회시
			list = postRepo.getMyReplyById(user.getId(), pageable);
		}
		
		list.forEach(reply -> {
			reply.setPostNo(reply.getPost().getPostNo());
			reply.setBoardName(BoardGroup.getBoardGroupByNo(reply.getBoardNo()).name());
			reply.setNickname(nickname);
		});
		
		return list;
	}
	

	@Override
	public Post insertPost(String boardName,String title, String content, String nickname,String category) {
		
		//닉네임으로 id조회
		User user = null;
		
		Optional<User> result = userRepo.findUserByNickname(nickname);
		if(result.isPresent()) {
			user = result.get();			
		}
		int boardNo = BoardGroup.valueOf(boardName).getValue();//게시판 번호 조회
		
		Post post = new Post();
		
		Board board = new Board();
		board.setBoardNo(boardNo);
		post.setBoard(board);
		
		PostContent postContent = new PostContent();
		postContent.setContent(content);
		post.setPostContent(postContent);
		
		post.setId(user.getId());
		
		post.setPostRegdate(LocalDateTime.now());
		post.setPostModdate(LocalDateTime.now());
	
		post.setPostTitle(title);
		post.setReplyCount(0);
		post.setPostViews(0);
		post.setPostLike(0);
		post.setLevel(3);
		post.setPostLevel(3);
		post.setPostNotice("F");//공지사항 등록 여부
		post.setPostComment("T");//댓글 작성 여부
		post.setPostDisplay("T");//게시글 공개 여부

		Post post2 = postRepo.save(post); //카테고리도 잘 추가되나 확인

		
		
		
		// 카테고리 추가

		
		JSONArray js = new JSONArray(category);
		
		if(js.length() != 0) { //배열이 비어있지 않으면 
			
			for(int i =0; i < js.length();i++) {
				
				Iterator<String> it = js.getJSONObject(i).keys();
				
				while(it.hasNext()) {
					String key = (String) it.next();
					String value = js.getJSONObject(i).getString(key);

					
					Category cate = new Category();
					cate.setPost(post);
					cate.setCategoryKind(key);
					cate.setCategoryName(value);
					cate.setCategoryOrder("순서");
					categoryRepo.save(cate);
				}
			}//for end
			
		}//if end
		
		
		return post2; //추가된 게시판번호 반환 
		 
	}

	@Override
	public void deleteCategoryAll(Long postNo){
		
		Optional<Post> result = postRepo.findById(postNo);
		if(result.isPresent()) {
			
			Post post = result.get();
			
			//기존 카테고리 모두 삭제
			List<Category> list =  categoryRepo.findByPostNo(post.getPostNo());
			
			for(Category ca : list) {
				
				categoryRepo.deleteById(ca.getCategoryNo());;
			}
				
			
			
		}
		
	}
	
	
	
	@Override
	public Post updatePost(Long postNo, String title, String content,String category) {
		Optional<Post> result = postRepo.findById(postNo);
		Long updatedPostNo = 0L;
		
		if(result.isPresent()) {
			Post post = result.get();

			post.setPostTitle(title); //제목 수정 
			
			PostContent postContent = post.getPostContent();
			postContent.setContent(content);
			post.setPostContent(postContent); //내용 수정 
			
			post.setPostModdate(LocalDateTime.now()); // 수정일자 반영
			
			
			//카테고리 업데이트 
			JSONArray js = new JSONArray(category);
			
			if(js.length() != 0) { //배열이 비어있지 않으면 
				
				for(int i =0; i < js.length();i++) {
					
					Iterator<String> it = js.getJSONObject(i).keys();
					
					while(it.hasNext()) {
						String key = (String) it.next();
						String value = js.getJSONObject(i).getString(key);
						
						Category cate = new Category();
						cate.setPost(post);
						cate.setCategoryKind(key);
						cate.setCategoryName(value);
						cate.setCategoryOrder("순서");
						categoryRepo.save(cate);
					}
				}//for end
			}//if end
			
			
			
			
			postRepo.save(post);
			updatedPostNo = post.getPostNo();
			return post;		//업데이트된 글 번호를 반환
		}
		
		return null;
		
	}
	@Transactional
	@Override
	public Long deletePost(String boardName,Long postNo, String nickname) {
		
//		List list = recommRepo.findAllRecommInPost(postNo);
//		recommRepo.deleteAll(list);
		
		//2.게시글 번호로 게시글 객체 불러와서 삭제후 삭제된 게시글 번호 반환
		postRepo.deleteById(postNo);
	
		return postNo;
	}
	
	@Transactional
	@Override
	public Long insertReply(String boardName, Long postNo,String content, String nickname) {
		
			Reply reply = new Reply();//댓글 엔티티 생성

			reply.setBoardNo(BoardGroup.valueOf(boardName).getValue());//게시판 이름을 전달받아 enum으로 게시판 번호로 변환
		
			Post post =  postRepo.findById(postNo).get();
			post.setReplyCount(post.getReplyCount()+1); //댓글수 1 증가 
			reply.setPost(post);//게시글 번호, 댓글수 1 증가 반영
			
			//닉네임 => id로 변환하여 설정
			
			User user = null;
			
			Optional<User> result = userRepo.findUserByNickname(nickname);
			if(result.isPresent()) {
				user = result.get();			
			}
			
			reply.setId(user.getId());//작성 회원번호  (중복체크한 닉네임을  id로 바꿔서 등록) 
			
			
			reply.setReplyContent(content);//댓글 내용
			reply.setReplyRegdate(LocalDateTime.now());//작성일자
			reply.setReplyModdate(LocalDateTime.now());//수정일자
			reply.setReplyLike(0);//추천수 (기본값 0) 
			reply.setLevel(3);//회원등급
			
			reply.setUpperReply(0);//상위댓글번호(임의로 기본값 0으로 설정. 아직 사용하지 않음.) 
			
			replyRepo.save(reply);
			
		
		return postNo;
	}

	@Override
	public Long updateReply(String boardName, Long postNo, Long replyNo, String content, String nickname) {
		
		Reply reply = replyRepo.findById(replyNo).get();
		
		reply.setReplyContent(content);
		reply.setReplyModdate(LocalDateTime.now());
		
		return reply.getReplyNo();
		
	}
	
	@Transactional
	@Override
	public Long deleteReply(String boardName, Long postNo, Long replyNo, String nickname) {
		
		
		Optional<Reply> result =  replyRepo.findById(replyNo);
		
		if(result.isPresent()) {
			Reply reply = result.get();
			log.info(""+reply);
			Post post = reply.getPost();
			post.setReplyCount(post.getReplyCount()-1);
			
			postRepo.saveAndFlush(post);
			
			replyRepo.deleteById(replyNo);
			return replyNo;
		}
		
		return 0L;
	}

	@Override
	public Long insertRecommend(String boardName,String nickname,String type,Long targetNo) {
		// 공통 : 회원번호, 게시판 번호
		//댓글 추천시 댓글번호           
		// 게시글 추천시 게시글번호
		
		User user = null;
		
		Optional<User> result = userRepo.findUserByNickname(nickname);
		if(result.isPresent()) {
			user = result.get();			
		}
		
		int boardNo = BoardGroup.valueOf(boardName).getValue();//게시판 번호(공통)
		
		Long num = -1L;
		if(type.equals("reply")) {
			
			//댓글 추천 여부 확인 후  저장
			Optional<Recommendation> recom = recommRepo.existReplyRecomm(user.getId(), targetNo);
			
			if(!recom.isPresent()) {
				
				Reply reply =  replyRepo.findById(targetNo).get();//추천할 댓글 객체
				
//				if(reply.getId() == user.getId()) {
//					return 0L; //본인이 스스로 추천 불가 
//				}
				
				reply.setReplyLike(reply.getReplyLike()+1); //댓글의 추천수 1 증가
				
				Recommendation recomm = new Recommendation();
				recomm.setUser(user);
				recomm.setBoardNo(boardNo);
				recomm.setReply(reply);
				recommRepo.save(recomm);
				
				num = reply.getReplyNo();// 추천한 댓글 번호 반환
			}
			
		}else if(type.equals("post")) {
			
			//게시글 추천 여부 확인 후 저장
			Optional<Recommendation> recom = recommRepo.existPostRecomm(user.getId(), targetNo);
			
			if(!recom.isPresent()) {
				
				Post post = postRepo.findById(targetNo).get(); //추천할 게시글 객체
				
//				if(post.getId() == user.getId()) {
//					return 0L; //본인이 스스로 추천 불가 
//				}
				
				post.setPostLike(post.getPostLike()+1); // 게시글의 추천수 1 증가 
				
				Recommendation recomm = new Recommendation();
				recomm.setUser(user);
				recomm.setBoardNo(boardNo);
				recomm.setPost(post);
				recommRepo.save(recomm);
				
				num = post.getPostNo(); // 추천한 게시글 번호 반환
				
			}
		}
		
		return num;//추천된 게시글번호 또는 댓글 번호 반환 
	}

	
	
	@Override
	public Long deleteRecommend(String boardName,String nickname,String type,Long targetNo) {
		
		User user = null;
		
		Optional<User> result = userRepo.findUserByNickname(nickname);
		if(result.isPresent()) {
			user = result.get();			
		}
		
		Long num = -1L;
		//우선 타입으로 댓글과 게시글 파악,  닉네임과 타겟번호로 추천기록을 조회후 삭제
		if(type.equals("reply")) {
			
			
			Optional<Recommendation> recom = recommRepo.existReplyRecomm(user.getId(), targetNo);
			
			if(recom.isPresent()) {
				
				//추천기록 존재시 삭제
				Recommendation recomEntity = recom.get();
				recommRepo.delete(recomEntity);
				
				Reply reply =  replyRepo.findById(targetNo).get();//추천삭제할 댓글 객체
				reply.setReplyLike(reply.getReplyLike()-1); //댓글의 추천수 1 감소
				
				num = reply.getReplyNo();// 추천삭제한 댓글 번호 반환
				
			}
			
			
		}else if(type.equals("post")) {
			
			Optional<Recommendation> recom = recommRepo.existPostRecomm(user.getId(), targetNo);
			
			if(recom.isPresent()) {
				//추천기록 존재시 삭제
				Recommendation recomEntity = recom.get();
				recommRepo.delete(recomEntity);
				
				Post post = postRepo.findById(targetNo).get(); //추천할 게시글 객체
				post.setPostLike(post.getPostLike()-1); // 게시글의 추천수 1 감소
				
				num = post.getPostNo(); // 추천삭제한 게시글 번호 반환
			}
			
		}
		
		return num;//추천삭제한 게시글번호 또는 댓글 번호 반환 
	}

	@Override
	public Post findPostByIndex(String index) {
		
		Optional<Post> result = postRepo.findCateKindAndName("index", index);
		
		if(result.isPresent()) {
			
			Post post = result.get();
			
			List<Reply> replies = post.getReplies();
			replies.forEach(reply -> {
				User replyUser = userRepo.findById(reply.getId()).get();
				reply.setNickname(replyUser.getNickname());
			});
			post.setReplies(replies);
			
			return post;
		}
		
		
		
		return null;
	}

	@Override
	public Page<User> application(long postNo) {
	
	//	User user = recommRepo.
		
		return null;
	}

	@Override
	public User applyStudy(String boardName, Long targetNo, String nickname) {
		
		int boardNo = BoardGroup.valueOf(boardName).getValue();//게시판 번호(공통)
		Post post = postRepo.findById(targetNo).get();
		
		User user = null;
		
		Optional<User> result = userRepo.findUserByNickname(nickname);
		if(result.isPresent()) {
			user = result.get();			
		}
		
			
		Recommendation reco = new Recommendation();
		reco.setBoardNo(boardNo);
		reco.setPost(post);
		reco.setUser(user);
		reco.setApplication("T");
		recommRepo.save(reco);
		
		
		return user;
	}

	@Override
	public User accept(String nickname, Long postNo) {
		
		
	long count = recommRepo.countByApplication("T");

		return null;
	}

	@Override
	public User decline(String nickname, Long postNo) {

		
		
		return null;
	}

	



}
