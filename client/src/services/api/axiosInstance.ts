import axios from "axios";
import { getAuthSession } from "../../utils/storage";

const axiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  headers: {
    "Content-Type": "application/json",
    Accept: "application/json",
  },
});
axiosInstance.interceptors.request.use(
  (config) => {
    const auth = getAuthSession();
    if (auth && "accessToken" in auth && auth.accessToken) {
      config.headers.Authorization = `${auth.tokenType || "Bearer"
        } ${auth.accessToken}`;
    } else {
      delete config.headers.Authorization;
    }
    return config;
  },
  (error) => Promise.reject(error)
);
export default axiosInstance;
