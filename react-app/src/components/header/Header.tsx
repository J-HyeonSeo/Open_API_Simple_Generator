import mainLogo from "../../assets/main-logo.png";
import * as S from "../../styles/header/Header.styled";
import HeaderProfile from "./profile/HeaderProfile";
import styled from "styled-components";

const LogoStyle = styled.img`
    width: 244px;
    height: 72px;
  `;

const Header = () => {
  return (
      <S.Header>
          <LogoStyle src={mainLogo} alt="mainLogo"/>
          <S.ListStyle>
            <S.ListDetailStyle>API 관리</S.ListDetailStyle>
            <S.ListDetailStyle>접근 가능 API 목록</S.ListDetailStyle>
            <S.ListDetailStyle>마이페이지</S.ListDetailStyle>
          </S.ListStyle>
          <HeaderProfile/>
      </S.Header>
  )
}

export default Header;