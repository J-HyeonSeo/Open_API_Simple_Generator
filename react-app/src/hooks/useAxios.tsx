import {useRecoilState} from "recoil";
import {tokenData} from "../store/RecoilState";
import {useState} from "react";
import * as A from "../utils/CustomAxios";
import axios, {AxiosError, AxiosResponse} from "axios";
import {ErrorFormat, TokenDto} from "../constants/interfaces";

const useAxios = () =>  {
  const [token, setToken] = useRecoilState(tokenData);
  const [res, setRes] = useState<AxiosResponse>();
  const [errorMessage, setErrorMessage] = useState<ErrorFormat>();
  const [isError, setIsError] = useState(false);

  const innerRequest = async (url: string, method: string, body?: any, accessToken?: string, isDouble?: boolean) => {
    try {
      switch (method) {
        case "get":
          setRes((await A.customAxios(accessToken).get(url)));
          return;
        case "post":
          setRes((await A.customAxios(accessToken).post(url, body)));
          return;
        case "put":
          setRes((await A.customAxios(accessToken).put(url, body)));
          return;
        case "patch":
          setRes((await A.customAxios(accessToken).patch(url, body)));
          return;
        case "delete":
          setRes((await A.customAxios(accessToken).delete(url, body)));
          return;
      }
    } catch (e) {
      const axiosError = e as AxiosError<ErrorFormat>;

      if (isDouble) { //토큰 재발급 후에도 오류가 나면, 리턴 처리.
        return axiosError.response?.data;
      }

      if (axiosError.response?.status === axios.HttpStatusCode.Unauthorized) {
        A.customAxios(accessToken).post("/auth", {refreshToken: localStorage.getItem("refreshToken")})
        .then((tokenRes) => {
          const tokenData: TokenDto = tokenRes.data;
          localStorage.setItem("accessToken", tokenData.accessToken);

          //재요청
          innerRequest(url, method, body, tokenData.accessToken, true);

        }).catch(() => { //로그인 필요
          localStorage.setItem("accessToken", '');
          localStorage.setItem("refreshToken", '');
          setToken(null);
          setIsError(true);
          return;
        });
      } else {
        setIsError(true);
        setErrorMessage(axiosError.response?.data);
        return;
      }
    }
  }

  const request = async (url: string, method: string, body?: any) => {
    let accessToken = token?.accessToken || window.localStorage.getItem("accessToken");
    let refreshToken = token?.refreshToken || window.localStorage.getItem("refreshToken");

    accessToken = accessToken || '';
    refreshToken = refreshToken || '';

    if (token == null && accessToken != null && refreshToken != null) {
      setToken(
          {
            accessToken: accessToken,
            refreshToken: refreshToken
          }
      );
    }
    await innerRequest(url, method, body, accessToken);
  }

  return {
    res, errorMessage, isError, request
  };
}

export default useAxios;