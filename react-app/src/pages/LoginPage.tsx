import {Fragment} from "react";
import mainLogoLarge from "../assets/main-logo-large.png";
import kakaoLoginImg from "../assets/kakao_login_large_wide.png";
import * as S from "../styles/login/Login.styled";
import {useNavigate} from "react-router-dom";

const LoginPage = () => {
  const navigate = useNavigate();
  return (
      <Fragment>
        <S.PageWrapper>
          <S.MainLogo onClick={() => navigate("/")}  src={mainLogoLarge} />
          <S.LoginWrapper>
            <S.LoginButton src={kakaoLoginImg} />
          </S.LoginWrapper>
          <S.CustomParagraph>해당 서비스는 오직 카카오 로그인 만을 지원합니다.</S.CustomParagraph>
          <S.CustomParagraph>This service only supports Kakao login.</S.CustomParagraph>
          <S.CustomParagraph>수집 하는 항목은 다음과 같습니다.</S.CustomParagraph>
          <S.CustomParagraph>닉네임, 이메일 (사용자 구별 용도)</S.CustomParagraph>
        </S.PageWrapper>
      </Fragment>
  )
}

export default LoginPage;