import {Fragment, useState} from "react";
import Header from "../components/header/Header";
import {Profile} from "../styles/profile/profile.styled";
import testProfileImg from "../assets/test-profile.png";
import testGradeImg from "../assets/grade-image/gold-mark.png";
import MyPageCard from "../components/my-page/MyPageCard";
import * as S from "../styles/my-page/ProfileWrapper.styled";
import Modal from "../components/modal/Modal";

const MyPage = () => {
  const [isShowNicknameModal, setIsShowNicknameModal] = useState(false);
  const [isShowPaymentModal, setIsShowPaymentModal] = useState(false);
  // IvRe == Invite & Request(초대/신쳥)
  const [isShowIvReModal, setIsShowIvReModal] = useState(false);

  const modalHandler = (type: string, value: boolean) => {
    switch(type) {
      case "nickname":
        setIsShowNicknameModal(value);
        break;
      case "payment":
        setIsShowPaymentModal(value);
        break;
      case "IvRe":
        setIsShowIvReModal(value);
        break;
    }
  }

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
        <MyPageCard
          openNicknameModal={() => modalHandler("nickname", true)}
          openPaymentModal={() => modalHandler("payment", true)}
          openIvReModal={() => modalHandler("IvRe", true)}
        />
        {isShowNicknameModal && <Modal isButton={true} mark={"question"} title={"닉네임 변경하기"} closeHandler={() => modalHandler("nickname", false)}>

        </Modal>}
      </Fragment>
  )
}

export default MyPage;