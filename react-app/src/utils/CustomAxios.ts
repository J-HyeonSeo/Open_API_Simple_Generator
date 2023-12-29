import axios, {AxiosError, AxiosInstance, AxiosResponse} from "axios";

export const BASE_URL = "http://localhost:8080";

//서버와 토큰을 지정함.
export const customAxios: (accessToken?: string) => AxiosInstance = (accessToken?: string) => {
  return axios.create(
      {
        baseURL: BASE_URL,
        headers: {
          "Authorization": 'Bearer ' + accessToken || localStorage.getItem("accessToken")
        },
        withCredentials: true
      }
  );
}
