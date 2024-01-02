import {Profile} from "../../../styles/profile/profile.styled";
import * as S from "../../../styles/header/Header.styled";
import {SmallBtn} from "../../../styles/control/SmallBtn.styled";
import styled from "styled-components";
import {useRecoilState} from "recoil";
import {profileData, tokenData} from "../../../store/RecoilState";
import {Fragment, useEffect, useState} from "react";
import useAxios from "../../../hooks/useAxios";
import Modal from "../../modal/Modal";
import {customAxios} from "../../../utils/CustomAxios";
import {useNavigate} from "react-router-dom";

const ProfileHeader = styled.h3`
      font-weight: 600;
      font-size: 16px;
      margin: 0;
    `;

const HeaderProfile = () => {

  const navigate = useNavigate();

  const [token, setToken] = useRecoilState(tokenData);
  const [isShowLogoutModal, setIsShowLogoutModal] = useState(false);
  const [profile, setProfile] = useRecoilState(profileData);
  const {res, request} = useAxios();

  const loadProfileHandler = () => {
      request("/member/profile", "get");
  }
  const logoutHandler = async () => {
    await customAxios().delete("/auth/signout",
        {
          data: {
            refreshToken: token?.refreshToken || window.localStorage.getItem("refreshToken")
          }
        });
    setToken(null);
    window.localStorage.setItem("accessToken", '');
    window.localStorage.setItem("refreshToken", '');
    setProfile(null);
    setIsShowLogoutModal(false);
    window.location.href = "/";
  }

  useEffect(() => {
    if(profile === null) {
      loadProfileHandler();
    }
    if (res != undefined) {
      setProfile(res?.data);
    }
  }, [res]);

  return (
      <S.HeaderProfile>
        {profile && <Fragment>
          <Profile src={profile.profileUrl} alt={"profileImage"}/>
          <div style={{marginLeft: "10px"}}>
            <ProfileHeader>{profile.nickname}</ProfileHeader>
            <SmallBtn onClick={() => setIsShowLogoutModal(true)}>로그아웃</SmallBtn>
          </div>
        </Fragment>}
        {!profile &&
            <SmallBtn onClick={() => navigate("/login/0")}>로그인</SmallBtn>
        }
        {isShowLogoutModal && <Modal
            mark={"question"}
            isButton={true}
            text={"로그아웃 하시겠습니까?"}
            yesCallback={logoutHandler}
            title={"로그아웃"} closeHandler={() => setIsShowLogoutModal(false)}/>}
      </S.HeaderProfile>
  )
}

export default HeaderProfile;