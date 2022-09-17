import axios from 'axios'
import useStore from "../plugins/store";
import jwt_decode from 'jwt-decode';



const baseURL = "https://www.chodae.shop:8000";

let accessToken = (localStorage.hasOwnProperty("accessToken")) ? localStorage.getItem("accessToken") : null;
let refreshToken = (localStorage.hasOwnProperty("refreshToken")) ? localStorage.getItem("refreshToken") : null;


// axios 인스턴스를 생성, 설정 정보 저장
const instance = axios.create({
    baseURL, //기본  루트 url
    timeout: 20000,
    // withCredentials: true
});


instance.interceptors.request.use(
    async function (config) {

        accessToken = (localStorage.hasOwnProperty("accessToken")) ? localStorage.getItem("accessToken") : null;

        if (accessToken == null) {
            // console.log("토큰없음");
            return config;
        }

        const info = jwt_decode(accessToken);
        // console.log(info);

        const currentTime = Math.floor(new Date().getTime() / 1000.0);
        const expTime = info.exp;
        const isExpired = expTime - currentTime <= 0;  //만료됨.
        // console.log(isExpired);
        // console.log(expTime - currentTime);
        // console.log(currentTime);
        // console.log(expTime);


        const nickname = info.nickname;

        if (!isExpired) {
            // console.log("아직 만료안됨");
            if (localStorage.hasOwnProperty("accessToken")) {

                config.headers.Authorization = `Bearer ${accessToken}`;
            }
            return config;

        } else {
            // console.log('accessToken 만료됨');
            //토큰 만료시 리프레시 토큰을 전달하여로그인시 발급한 리프레쉬토큰(DB에 저장)과 비교하여 일치하면 액세스토큰 발급
            //새로 발급 받은 액세스 토큰을 헤더로 설정후 요청.

            const formData = new FormData();
            formData.append("nickname", nickname);
            formData.append("refreshToken", refreshToken);


            try {
                const response = await axios.post(`/api/refresh`, formData);
                // console.log(response.data);
                localStorage.setItem("accessToken", response.data);

                config.headers.Authorization = 'Bearer ' + response.data;
                // console.log(config);
                return config;

            } catch (err) {
                // console.log(err);
            }



        }


    },


    function (error) {

        return Promise.reject(error);
    }
);


instance.interceptors.response.use(
    function (response) {


        return response;
    },
    async function (error) {
        // console.log(error);
        return Promise.reject(error);
    }
);


export default instance;


//토큰이 유효하지 않을 때
// code: "403"
// message: "PLEASE CHECK TOKEN"
//==================================
//권한 없을시 에러 응답
// error: "Forbidden"
// message: "Forbidden"
// path: "/faq"
// status: 403
// timestamp: "2022-03-17T15:47:52.849+00:00"