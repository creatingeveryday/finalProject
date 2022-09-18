package com.community.chodae.image;

import com.community.chodae.domain.Board;
import com.community.chodae.domain.Post;
import com.community.chodae.domain.PostContent;
import com.community.chodae.domain.User;
import com.community.chodae.group.BoardGroup;
import com.community.chodae.repository.PostRepo;
import com.community.chodae.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
@Service
public class postService  {

	private final UserRepo userRepo;
	
	@Autowired
	public postService(PostRepo postRepo, UserRepo userRepo) {
		this.postRepo = postRepo;
		this.userRepo = userRepo;
	}
	 
	
private PostRepo postRepo;
	
	public Post insertPost(String boardName, String title,String nickname,  String content) {

		User user = null;
		Optional<User> result = userRepo.findUserByNickname(nickname);
		if(result.isPresent()) {
			user = result.get();			
		}
		
		int boardNo = BoardGroup.valueOf(boardName).getValue();
		Post post = new Post();
		Board board = new Board();
		board.setBoardNo(boardNo);
		post.setBoard(board);
		post.setPostRegdate(LocalDateTime.now());
		post.setPostModdate(LocalDateTime.now());
		PostContent postContent = new PostContent();
		postContent.setContent(content);
		post.setPostContent(postContent);

	//	post.setId(null);
		post.setPostTitle(title);
		post.setReplyCount(0);	
		post.setPostViews(0);
		post.setPostLike(0);
		post.setLevel(3);
		post.setPostLevel(3);
		post.setPostNotice("F");
		post.setPostComment("T");
		post.setPostDisplay("T");
		//post set image?

		Post post2 = postRepo.save(post);


		

		
		return post;
	}


}
