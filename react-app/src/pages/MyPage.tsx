import {Fragment} from "react";
import Header from "../components/header/Header";
import {Profile} from "../styles/profile/profile.styled";
import testProfileImg from "../assets/test-profile.png";
import testGradeImg from "../assets/gold-mark.png";
import MyPageCard from "../components/my-page/MyPageCard";
import * as S from "../styles/my-page/ProfileWrapper.styled";

const MyPage = () => {
  return (
      <Fragment>
        <Header />
        <S.ProfileWrapper>
          <S.GradeMargin>
            <img src={testGradeImg} alt={"gradeImg"} width={"30px"} height={"70px"}/>
          </S.GradeMargin>
          <Profile src={testProfileImg} $size={120}/>
          <S.TextWrapper>
            <S.TextStyle><strong>Adam Smith</strong> 님</S.TextStyle>
            <S.TextStyle>안녕하세요?</S.TextStyle>
          </S.TextWrapper>
        </S.ProfileWrapper>
        <MyPageCard />
      </Fragment>
  )
}

export default MyPage;