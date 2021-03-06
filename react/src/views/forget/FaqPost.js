import React, { useEffect, useState } from "react";
import { useNavigate, useLocation, useParams } from "react-router-dom";
import axios from "../../plugins/axios";
import styles from "./FaqPost.module.css";
import CareerBoardTable from "../../component/CareerBoardTable";
import PostReply from "../../component/PostReply";

import useStore from "../../plugins/store";
import { AiFillHeart } from "react-icons/ai";
import { AiOutlineHeart } from "react-icons/ai";

function FaqPost() {
  const store = useStore();
  const nickname =
    useStore.getState().nickname !== null
      ? useStore.getState().nickname
      : null;

  const params = useParams();
  const postNo = params.postno;
  const location = useLocation();
  const navigate = useNavigate();

  // console.log(location);
  const idx = location.pathname.indexOf("/", 1);
  // console.log(idx);
  const boardGroup = location.pathname.slice(1, idx);
  // console.log(boardGroup);

  const idx2 = location.pathname.indexOf("/", idx + 1);
  // console.log(idx2);

  const boardName = location.pathname.slice(idx + 1, idx2);
  // console.log(boardName);

  const [postObject, setPostObject] = useState(null);
  const [comments, setComments] = useState(null);
  const [newComment, setNewComment] = useState("");
  const [postRecommendOrNot, setPostRecommendOrNot] = useState(false);

  useEffect(() => {
    getPost(postNo);
  }, [postNo]);

  const getPost = function (postNo) {
    axios
      .get(`/${boardName}/${postNo}`)
      .then((response) => {
        // console.log(response.data);
        const post = response.data;

        post.finduser2.map((like2) => {
          // console.log(post.finduser2);
          if (like2 === nickname) {
            setPostRecommendOrNot(true);
          }
        });

        post.postRegdate = dateFormat(new Date(post.postRegdate));

        for (const reply of post.replies) {
          reply.replyRegdate = dateFormat(new Date(reply.replyRegdate));
        }

        setPostObject(post);
        setComments(post.replies);
      })
      .catch((error) => {
        console.log(error);
      });
  };
  function dateFormat(date) {
    let month = date.getMonth() + 1;
    let day = date.getDate();
    let hour = date.getHours();
    let minute = date.getMinutes();
    let second = date.getSeconds();

    month = month >= 10 ? month : "0" + month;
    day = day >= 10 ? day : "0" + day;
    hour = hour >= 10 ? hour : "0" + hour;
    minute = minute >= 10 ? minute : "0" + minute;
    second = second >= 10 ? second : "0" + second;

    return `${date.getFullYear()}-${month}-${day} ${hour}:${minute}:${second}`;
  }

  // ????????? ??????
  const deletePost = (postNo) => {
    axios.delete(`/${boardName}/${postNo}/${nickname}`).then(() => {
      navigate(-1);
    });
  };

  // ?????? ??????
  const addComment = async function () {
    const formData = new FormData();

    formData.append("content", newComment);
    formData.append("nickname", nickname);

    await axios
      .post(`/${boardName}/${postNo}/reply`, formData, {
        headers: {
          "Content-Type": "multipart/form-data",
        },
      })
      .then((response) => {
        window.location.reload();
      })
      .catch((error) => {
        console.log(error);
      });
  };

  //?????? ?????? ??????   //????????? ?????? //?????? ??????
  const addLike = async (type, targetNo, nickname) => {
    const formData = new FormData();
    formData.append("nickname", nickname);

    await axios
      .post(`/${boardName}/recomm/${type}/${targetNo}`, formData, {
        headers: {
          "Content-Type": "multipart/form-data",
        },
      })
      .then((response) => {
        // console.log(response.data);
        alert("?????????????????????");
      })
      .catch((error) => {
        console.log(error);
      });
    setPostRecommendOrNot(true);
    setPostObject({
      ...postObject,
      postLike: postObject.postLike + 1,
    });
  };

  //?????? ??????
  const deleteLike = async (type, targetNo, nickname) => {
    await axios
      .delete(`/${boardName}/recomm/${type}/${targetNo}/${nickname}`)
      .then((response) => {
        alert("????????? ?????????????????????");
      })
      .catch((error) => {
        console.log(error);
      });
    setPostRecommendOrNot(false);
    setPostObject({
      ...postObject,
      postLike: postObject.postLike - 1,
    });
  };

  return (
    <div className={styles.postContainer}>
      {postObject && (
        <div className={styles.postSection}>
          <div className={styles.postCommentWrapper}></div>
          <CareerBoardTable tableData={postObject} />
          <div>
            {postObject !== null && nickname === postObject.nickname && (
              <div className={styles.commentAddBtnWrapper}>
                <button
                  className={styles.commentAddBtn}
                  onClick={() => {
                    deletePost(postObject.postNo);
                  }}
                >
                  ??????
                </button>
                <button
                  className={styles.commentAddBtn}
                  onClick={() => {
                    navigate("update");
                  }}
                >
                  ??????
                </button>
              </div>
            )}
          </div>

          {/* ???????????? ???????????? ????????? ???????????? ??????????????? / ?????? ????????????  */}
          {postObject !== null && nickname !== null ? (
            <div>
              {postRecommendOrNot ? (
                <AiFillHeart
                  className={styles.recommend}
                  onClick={() => {
                    deleteLike("post", postObject.postNo, nickname);
                  }}
                />
              ) : (
                <AiOutlineHeart
                  className={styles.notRecommend}
                  onClick={() => {
                    addLike("post", postObject.postNo, nickname);
                  }}
                />
              )}
            </div>
          ) : (
            <AiOutlineHeart
              className={styles.recommend}
              onClick={() => {
                alert("???????????? ????????? ????????? ??? ????????????.");
              }}
            />
          )}
          <div>
            ????????? &nbsp;
            <span className={styles.likeCount}>{postObject.postLike}</span>
          </div>

          {/* ?????? ???????????? ????????? ????????? ?????? */}
          <div className={styles.listOfComments}>
            {postObject != null &&
              postObject.replies.map((reply, index) => {
                return <PostReply key={index} reply={reply} />;
              })}
          </div>

          {nickname !== null ? (
            <div className={styles.commentSection}>
              <div className={styles.commentTextWrapper}>
                <div className={styles.commentNickname}>{nickname}</div>
                <div className={styles.commentInputWrapper}>
                  <input
                    className={styles.commentInputBox}
                    type="text"
                    placeholder="????????? ???????????????"
                    autoComplete="off"
                    value={newComment}
                    onChange={(event) => {
                      setNewComment(event.target.value);
                    }}
                  ></input>
                  <div className={styles.commentAddBtnWrapper}>
                    <button
                      className={styles.commentAddBtn}
                      onClick={() => {
                        addComment();
                      }}
                    >
                      ??????
                    </button>
                  </div>
                </div>
              </div>
            </div>
          ) : null}
        </div>
      )}
    </div>
  );
}

export default FaqPost;
