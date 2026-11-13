
// import axios from "axios";

// const axiosInstance = axios.create({
//   baseURL: import.meta.env.VITE_API_BASE_URL,
//   timeout: 15000,

//   headers: {
//     "Content-Type": "application/json",
//     Accept: "application/json",
//   },
// });

// export default axiosInstance;

import axios from "axios";
import { getAuthSession } from "../../utils/storage";

const axiosInstance = axios.create({
  baseURL: "http://localhost:9090",
  headers: {
    "Content-Type": "application/json",
    Accept: "application/json",
  },
});
axiosInstance.interceptors.request.use(
  (config) => {
    const auth = getAuthSession();
    if (auth?.accessToken) {
      config.headers.Authorization = `${auth.tokenType || "Bearer"} ${auth.accessToken}`;
    } else {
      delete config.headers.Authorization;
    }
    return config;
  },
  (error) => Promise.reject(error)
);
export default axiosInstance;
