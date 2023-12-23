import mainLogo from "../../assets/main-logo.png";
import * as S from "../../styles/header/Header.styled";
import HeaderProfile from "./profile/HeaderProfile";
import styled from "styled-components";
import {useNavigate} from "react-router-dom";

const LogoStyle = styled.img`
    width: 244px;
    height: 72px;
  
    &:hover {
        cursor: pointer;
    }
  `;

const Header = () => {
  const navigate = useNavigate();
  return (
      <S.Header>
          <LogoStyle src={mainLogo} alt="mainLogo" onClick={() => navigate("/")}/>
          <S.ListStyle>
            <S.ListDetailStyle onClick={() => navigate("/api/owner")}>API 관리</S.ListDetailStyle>
            <S.ListDetailStyle onClick={() => navigate("/api/accessible")}>접근 가능 API 목록</S.ListDetailStyle>
            <S.ListDetailStyle onClick={() => navigate("/my-page")}>마이페이지</S.ListDetailStyle>
          </S.ListStyle>
          <HeaderProfile/>
      </S.Header>
  )
}

export default Header;