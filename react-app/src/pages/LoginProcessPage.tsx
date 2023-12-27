import {useNavigate} from "react-router-dom";
import {customAxios} from "../utils/CustomAxios";
import {TokenDto} from "../constants/interfaces";
import {Fragment, useEffect, useState} from "react";
import {useRecoilState} from "recoil";
import {tokenData} from "../store/RecoilState";

const LoginProcessPage = () => {

  const [token, setToken] = useRecoilState(tokenData);
  const navigate = useNavigate();

  const getTokenData = async () => {
    try {
      const res = await customAxios().get(window.location.pathname + window.location.search);
      return res.data;
    } catch (e) {
      throw e;
    }
  }

  useEffect(() => {
    getTokenData().then((res) => {
      localStorage.setItem("accessToken", res.accessToken);
      localStorage.setItem("refreshToken", res.refreshToken);
      setToken({
        accessToken: res.accessToken,
        refreshToken: res.refreshToken
      });
      navigate("/");
    }).catch(() => {
      navigate("/login/1");
    });
  }, []);

  return (
      <Fragment/>
  )
}

export default LoginProcessPage;