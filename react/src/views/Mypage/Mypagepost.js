import React, { useEffect, useState } from "react";
import { useNavigate, useLocation, useParams } from "react-router-dom";
import axios from "../../plugins/axios";
import styles from "./MypagePost.module.css";
import CareerBoardTable from "../../component/CareerBoardTable";
import moment from "moment";
import CommentList from "../../component/CommentList";
import Comment from "../../component/Comment";

import useStore from "../../plugins/store";
import { FaThumbsUp } from "react-icons/fa";
// import { FaThumbsDown } from "react-icons/fa";

function Mypagepost() {
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
  const [updateClicked, setUpdateClicked] = useState(false);
  const [sendComment, setSendComment] = useState(false);
  const [postRecommendOrNot, setPostRecommendOrNot] = useState(false);
  const [replyRecommendOrNot, setReplyRecommendOrNot] = useState(false);

  useEffect(() => {
    getPost(nickname);
  }, []);

  const getPost = function (nickname) {
    axios
      .get(`/mypage/reply/${nickname}`)
      .then((response) => {
        // console.log(response.data);
        const post = response.data;

        // console.log(nickname)

        if (post.finduser === null) {
          // console.log("sorry")
        } else if (post.finduser !== null) {
          // console.log(post.finduser)
          post.finduser.map((like) => {
            if (like === nickname) {

              setReplyRecommendOrNot(true);
            }
          })

        }

        post.finduser2.map((like2) => {
          // console.log(post.finduser2)
          if (like2 === nickname) {

            setPostRecommendOrNot(true);
          }
        })

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
        // console.log(response.data);
        window.location.reload();
      })
      .catch((error) => {
        console.log(error);
      });
  };

  //?????? ??????
  const deleteReply = async function (replyNo) {
    await axios({
      method: "DELETE",
      url: `/${boardName}/${postNo}/reply/${replyNo}/${nickname}`,
    }).then(() => {
      setComments(
        comments.filter((val) => {
          return val.replyNo !== replyNo;
        })
      );
      window.location.reload();
    });
  };

  //?????? ??????
  const updateReply = async function (updatedComment, replyNo) {
    const formData = new FormData();
    formData.append("content", updatedComment);
    formData.append("nickname", nickname);

    await axios
      .put(`/${boardName}/${postNo}/reply/${replyNo}`, formData, {
        headers: {
          "Content-Type": "multipart/form-data",
        },
      })
      .then((response) => {
        // console.log(response.data);
        window.location.reload();
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

        window.location.reload();
      })
      .catch((error) => {
        console.log(error);
      });
  };

  //?????? ??????
  const deleteLike = async (type, targetNo, nickname) => {
    await axios
      .delete(`/${boardName}/recomm/${type}/${targetNo}/${nickname}`)
      .then((response) => {
        // console.log(response.data);
        alert("????????? ?????????????????????");
        window.location.reload();
      })
      .catch((error) => {
        console.log(error);
      });
  };

  const props = {
    nickname: nickname,
    addLike: addLike,
    deleteLike: deleteLike,
  };

  return (
    <div className={styles.postContainer}>
      {postObject && (
        <div className={styles.postSection}>
          <div className={styles.postCommentWrapper}></div>
          <CareerBoardTable moment={moment} tableData={postObject} />
          <div className="tempo">
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
              {!postRecommendOrNot ? (
                <FaThumbsUp
                  className={styles.recommend}
                  onClick={() => {
                    addLike("post", postObject.postNo, nickname);
                    setPostRecommendOrNot(!postRecommendOrNot);
                  }}
                />
              ) : (
                <FaThumbsUp
                  className={styles.notRecommend}
                  onClick={() => {
                    deleteLike("post", postObject.postNo, nickname);
                    setPostRecommendOrNot(!postRecommendOrNot);
                  }}
                />
              )}
            </div>
          ) : (
            <FaThumbsUp
              className={styles.recommend}
              onClick={() => {
                alert("???????????? ????????? ????????? ??? ????????????.");
              }}
            />
          )}

          <div className={styles.listOfComments}>
            {postObject != null &&
              postObject.replies.map((reply, index) => {
                return (
                  <div className={styles.comment} key={index}>
                    <div className={styles.commentNickname}>
                      {reply.nickname}
                    </div>
                    {updateClicked === true ? (
                      <CommentList
                        sendComment={sendComment}
                        updateReply={updateReply}
                        reply={reply}
                      />
                    ) : (
                      <div>{reply.replyContent}</div>
                    )}
                    <span className={styles.commentTime}>
                      {moment(reply.replyRegdate).format("LLL")}
                    </span>
                    <span>
                      {nickname === reply.nickname && (
                        <div className={styles.commentAddBtnWrapper}>
                          <button
                            className={styles.commentAddBtn}
                            onClick={() => {
                              setUpdateClicked(!updateClicked);
                            }}
                          >
                            {updateClicked ? "??????" : "??????"}
                          </button>
                          {updateClicked ? (
                            <button
                              className={styles.commentAddBtn}
                              onClick={() => {
                                setSendComment(true);
                              }}
                            >
                              ??????
                            </button>
                          ) : (
                            <button
                              className={styles.commentAddBtn}
                              onClick={() => {
                                deleteReply(reply.replyNo);
                              }}
                            >
                              ??????
                            </button>
                          )}
                        </div>
                      )}
                      {/* db?????? ?????? ?????? ?????? ?????? ?????? */}
                      {nickname !== null ? (
                        <Comment props={props} reply={reply} />
                      ) : (
                        <FaThumbsUp
                          className={styles.replyRecommend}
                          onClick={() => {
                            alert("???????????? ????????? ????????? ??? ????????????.");
                          }}
                        />
                      )}
                    </span>
                  </div>
                );
              })}
          </div>

          {nickname !== null ? (
            <div className={styles.commentSection}>
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
          ) : null}
        </div>
      )}
    </div>
  );
}

export default Mypagepost;
