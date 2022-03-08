import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import axios from "../../plugins/axios";
import "./Post.css";
import CareerBoardTable from "../../component/CareerBoardTable";
//테이블 컴포넌트로 만들어서 따로 뺴놨음
import moment from "moment";
//날짜 수정하기 위해 모멘트 설치 yarn add moment 하면됨

function Post() {
  let { postno } = useParams();
  const [postObject, setPostObject] = useState();

  const [comments, setComments] = useState([]);
  const [newComment, setNewComment] = useState("");

  useEffect(() => {
    const fetchDataObject = async () => {
      try {
        const { data } = await axios.get(`/career/${postno}`);
        setPostObject(data);
        setComments(data.replies);
      } catch (error) {
        console.error(error);
      }
    };
    fetchDataObject();
  }, []);

  //문제는 페이지를 새로고침해야 추가된 댓글이 보임.....
  const addComment = () => {
    const formData = new FormData();
    formData.append("content", newComment);
    formData.append("nickname", "닉네임");

    axios
      .post(`/career/${postno}/reply`, formData, {
        headers: {
          "Content-Type": "multipart/form-data",
        },
      })
      .then((response) => {
        setNewComment("");
      });
  };

  // 좋아요 버튼 기능
  // useEffect(() => {
  //   axios.get("http://localhost:3001/posts").then((response) => {
  //     setListOfPosts(response.data);
  //   });
  // }, []);

  // const likeAPost = (postId) => {
  //   axios
  //     .post(
  //       "http://localhost:3001/likes",
  //       { PostId: postId },
  //       { headers: { accessToken: localStorage.getItem("accessToken") } }
  //     )
  //     .then((response) => {
  //       setListOfPosts(
  //         listOfPosts.map((post) => {
  //           if (post.id === postId) {
  //             if (response.data.liked) {
  //               return { ...post, Likes: [...post.Likes, 0] };
  //             } else {
  //               const likesArray = post.Likes;
  //               likesArray.pop();
  //               return { ...post, Likes: likesArray };
  //             }
  //           } else {
  //             return post;
  //           }
  //         })
  //       );
  //     });
  // };

  return (
    <div className="postContainer">
      {postObject && (
        <div className="postSection">
          <CareerBoardTable moment={moment} tableData={postObject} />
          <div className="commentSection">
            <input
              type="text"
              placeholder="댓글을 남겨주세요"
              autoComplete="off"
              value={newComment}
              onChange={(event) => {
                setNewComment(event.target.value);
              }}
            ></input>
            <button
              onClick={() => {
                addComment();
              }}
            >
              Add Comment
            </button>
          </div>
          <div className="listOfComments">
            {comments &&
              comments.map((comment, index) => {
                return (
                  <div className="comment" key={index}>
                    {comment.replyContent}
                  </div>
                );
              })}
          </div>
          <button
          // onClick={() => {
          //   likeApost(value.id);
          // }}
          >
            Like
          </button>
        </div>
      )}
    </div>
  );
}

export default Post;