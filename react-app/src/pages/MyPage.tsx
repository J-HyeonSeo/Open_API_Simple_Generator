import {Fragment, useState} from "react";
import Header from "../components/header/Header";
import {Profile} from "../styles/profile/profile.styled";
import testProfileImg from "../assets/test-profile.png";
import testGradeImg from "../assets/grade-image/gold-mark.png";
import MyPageCard from "../components/my-page/MyPageCard";
import * as S from "../styles/my-page/ProfileWrapper.styled";
import * as S2 from "../styles/common-card/Card.styled";
import Modal from "../components/modal/Modal";
import ChangeNicknameModal from "../components/modal/ChangeNicknameModal";
import PaymentModal from "../components/modal/PaymentModal";
import IvReModal from "../components/modal/IvReModal";
import {palette} from "../constants/Styles";
import {Line} from "../styles/line/line.styled";

const MyPage = () => {
  const [isShowNicknameModal, setIsShowNicknameModal] = useState(false);
  const [isShowPaymentModal, setIsShowPaymentModal] = useState(false);
  // IvRe == Invite & Request(초대/신쳥)
  const [isShowInviteModal, setIsShowInviteModal] = useState(false);
  const [isShowRequestModal, setIsShowRequestModal] = useState(false);

  const modalHandler = (type: string, value: boolean) => {
    switch(type) {
      case "nickname":
        setIsShowNicknameModal(value);
        break;
      case "payment":
        setIsShowPaymentModal(value);
        break;
      case "invite":
        setIsShowInviteModal(value);
        break;
      case "request":
        setIsShowRequestModal(value);
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
          <S.NicknameTextWrapper>
            <S.NicknameText><strong>Adam Smith</strong> 님</S.NicknameText>
            <S.NicknameText>안녕하세요?</S.NicknameText>
          </S.NicknameTextWrapper>
          <S2.CardWrapper $w={450} $m={20}>
            <S2.Card $h={50} $p={1} $r={10}>
              <S.EmailText>Email</S.EmailText>
              <Line $h={30} $c={palette["--color-gray-500"]}/>
              <S.EmailText style={{width: "300px"}}>AdamSmith@test.com</S.EmailText>
            </S2.Card>
          </S2.CardWrapper>
        </S.ProfileWrapper>
        <MyPageCard
          openNicknameModal={() => modalHandler("nickname", true)}
          openPaymentModal={() => modalHandler("payment", true)}
          openIvReModal={(isInvite: boolean) => isInvite ? modalHandler("invite", true) : modalHandler("request", true)}
        />
        {isShowNicknameModal && <Modal
            isButton={true}
            mark={"question"}
            title={"닉네임 변경하기"}
            closeHandler={() => modalHandler("nickname", false)}
            yesText={"변경"}
        >
          <ChangeNicknameModal nickname={"Adam Smith"}/>
        </Modal>}

        {isShowPaymentModal && <Modal title={"결제 내역 조회하기"}
               w={800}
               h={600}
               closeHandler={() => modalHandler("payment", false)}>
          <PaymentModal/>
        </Modal>}

        {isShowInviteModal && <Modal title={"초대 내역 조회하기"}
                                      w={800}
                                      h={600}
                                      closeHandler={() => modalHandler("invite", false)}>
          <IvReModal />
        </Modal>}

        {isShowRequestModal && <Modal title={"신청 내역 조회하기"}
                                     w={800}
                                     h={600}
                                     closeHandler={() => modalHandler("request", false)}>
          <IvReModal />
        </Modal>}

      </Fragment>
  )
}

export default MyPage;