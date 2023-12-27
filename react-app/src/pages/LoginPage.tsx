import {Fragment, useEffect, useState} from "react";
import mainLogoLarge from "../assets/main-logo-large.png";
import kakaoLoginImg from "../assets/kakao_login_large_wide.png";
import * as S from "../styles/login/Login.styled";
import {useNavigate, useParams} from "react-router-dom";
import Modal from "../components/modal/Modal";

const LoginPage = () => {
  const param = useParams().error;
  const [isShowErrorModal, setIsShowErrorModal] = useState(false);

  useEffect(() => {
    if (param === '1') {
      setIsShowErrorModal(true);
    }
  }, []);

  const navigate = useNavigate();
  return (
      <Fragment>
        <S.PageWrapper>
          <S.MainLogo onClick={() => navigate("/")}  src={mainLogoLarge} />
          <S.LoginWrapper>
            <S.LoginButton onClick={() => window.location.href = "http://localhost:8080/oauth2/authorization/kakao"} src={kakaoLoginImg} />
          </S.LoginWrapper>
          <S.CustomParagraph>해당 서비스는 오직 카카오 로그인 만을 지원합니다.</S.CustomParagraph>
          <S.CustomParagraph>This service only supports Kakao login.</S.CustomParagraph>
          <S.CustomParagraph>수집 하는 항목은 다음과 같습니다.</S.CustomParagraph>
          <S.CustomParagraph>닉네임, 이메일 (사용자 구별 용도)</S.CustomParagraph>
        </S.PageWrapper>
        {isShowErrorModal && < Modal title={"오류"}
               text={"로그인에 실패하였습니다!"}
               mark={"error"}
               isButton={true}
               yesCallback={() => setIsShowErrorModal(false)}
               closeHandler={() => setIsShowErrorModal(false)}
        />}
      </Fragment>
  )
}

export default LoginPage;