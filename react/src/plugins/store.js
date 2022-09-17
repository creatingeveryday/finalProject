import create from 'zustand'
import axios from "../plugins/axios";
import jwt_decode from 'jwt-decode'
import { Link, useNavigate } from "react-router-dom";
import { persist } from "zustand/middleware"

//로컬스토리지 저장. 리프레쉬토큰 , 액세스 토큰
const useStore = create(persist(
    (set, get) => ({
        nickname: null,
        role: null,
        isLogin: false,
        frontUrl: "https://www.chodae.shop",
        url: "https://www.chodae.shop:8000",
        serachWord: null, //이전 검색어로 활용? 
        getSearchWord: () => {
            if (get().serachWord !== null) {
                return get().serachWord;
            }
        },
        setSearchWord: (word) => {
            const replaced = word.replace(/(\s*)/g, "");
            if (replaced.length < 1) {
                // console.log("검색어 없음");
                return;
            }
            set({ serachWord: word });
        },
        getBaseUrl: () => {
            return get().url;
        },
        getFrontUrl: () => {
            return get().frontUrl;
        },
        continueLogin: async (nickname, accessToken, refreshToken) => {

            let nicknameInfo = null;

            if (nickname === undefined) {
                const old = jwt_decode(accessToken);
                nicknameInfo = old.nickname;
            } else {
                nicknameInfo = nickname;
            }


            try {

                const formData = new FormData();
                formData.append("nickname", nicknameInfo);
                formData.append("refreshToken", refreshToken);

                const response = await axios.post(`/api/refresh`, formData);

                localStorage.setItem("accessToken", response.data);

                const newToken = response.data;
                const newInfo = jwt_decode(newToken);

                get().setMemberInfo(newInfo.nickname, newInfo.role);

            } catch (err) {
                console.log(err);
                get().logout();

            }

        },
        setMemberInfo: (nickname, role) => {
            set({ nickname: nickname, role: role, isLogin: true });

        },
        getNickname: () => {   //getMemberInfo
            if (get().nickname !== null) {
                return get().nickname;
            }
        },
        getMemberRole: () => {
            if (get().role !== null) {
                return get().role[0].authority;// "ROLE_USER" ????????????
            }
        },
        logout: () => {
            localStorage.clear();
            set({
                nickname: null,
                role: null,
                isLogin: false,
            });

        },
    }),
    {
        name: "info-storage", // unique name
        getStorage: () => sessionStorage, // (optional) by default, 'localStorage' is used
    }
))


export default useStore;