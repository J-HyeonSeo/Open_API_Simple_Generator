import mainLogo from "../../assets/main-logo.png";
import * as S from "../../styles/header/Header.styled";
import HeaderProfile from "./profile/HeaderProfile";
import styled from "styled-components";
import {useNavigate} from "react-router-dom";
import {useRecoilState} from "recoil";
import {profileData} from "../../store/RecoilState";
import {useState} from "react";
import Modal from "../modal/Modal";

const LogoStyle = styled.img`
    width: 244px;
    height: 72px;
  
    &:hover {
        cursor: pointer;
    }
  `;

const Header = () => {
  const [profile, _] = useRecoilState(profileData);
  const [isShowErrorModal, setIsShowErrorModal] = useState(false);
  const navigate = useNavigate();

  const checkNavigateHandler = (url: string) => {
    if (profile == null) {
      setIsShowErrorModal(true);
      return;
    }
    navigate(url);
  }

  return (
      <S.Header>
          <LogoStyle src={mainLogo} alt="mainLogo" onClick={() => navigate("/")}/>
          <S.ListStyle>
            <S.ListDetailStyle onClick={() => checkNavigateHandler("/api/owner")}>API 관리</S.ListDetailStyle>
            <S.ListDetailStyle onClick={() => checkNavigateHandler("/api/accessible")}>접근 가능 API 목록</S.ListDetailStyle>
            <S.ListDetailStyle onClick={() => navigate("/grade-payment")}>등급 결제하기</S.ListDetailStyle>
            <S.ListDetailStyle onClick={() => checkNavigateHandler("/my-page")}>마이페이지</S.ListDetailStyle>
          </S.ListStyle>
          <HeaderProfile/>
          {isShowErrorModal && <Modal title={"접근 불가"}
                   mark={"error"}
                   text={"로그인이 필요한 서비스입니다."}
                   yesText={"로그인"}
                   yesCallback={() => navigate("/login/0")}
                   isButton={true}
                   closeHandler={() => setIsShowErrorModal(false)}/>}
      </S.Header>
  )
}

export default Header;