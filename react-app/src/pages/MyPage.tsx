import {Fragment, useState} from "react";
import Header from "../components/header/Header";
import {Profile} from "../styles/profile/profile.styled";
import bronzeImg from "../assets/grade-image/bronze-mark.png";
import silverImg from "../assets/grade-image/silver-mark.png";
import goldImg from "../assets/grade-image/gold-mark.png";
import diaImg from "../assets/grade-image/dia-mark.png";
import MyPageCard from "../components/my-page/MyPageCard";
import * as S from "../styles/my-page/ProfileWrapper.styled";
import * as S2 from "../styles/common-card/Card.styled";
import Modal from "../components/modal/Modal";
import ChangeNicknameModal from "../components/modal/ChangeNicknameModal";
import PaymentModal from "../components/modal/PaymentModal";
import IvReModal from "../components/modal/IvReModal";
import {palette} from "../constants/Styles";
import {Line} from "../styles/line/line.styled";
import {useRecoilState} from "recoil";
import {profileData} from "../store/RecoilState";

const MyPage = () => {

  const [profile, _] = useRecoilState(profileData);
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

  const gradePictureSelector = (): string => {
    switch (profile?.gradeId) {
      case 1:
        return bronzeImg;
      case 2:
        return silverImg;
      case 3:
        return goldImg;
      case 4:
        return diaImg;
    }
    return bronzeImg;
  }

  return (
      <Fragment>
        <Header />
        <S.ProfileWrapper>
          <S.GradeMargin>
            <img src={gradePictureSelector()} alt={"gradeImg"} width={"30px"} height={"70px"}/>
          </S.GradeMargin>
          <Profile src={profile?.profileUrl} $size={120}/>
          <S.NicknameTextWrapper>
            <S.NicknameText><strong>{profile?.nickname}</strong> 님</S.NicknameText>
            <S.NicknameText>안녕하세요?</S.NicknameText>
          </S.NicknameTextWrapper>
          <S2.CardWrapper $w={450} $m={20}>
            <S2.Card $h={50} $p={10} $r={10}>
              <S.EmailText>Email</S.EmailText>
              <Line $h={30} $c={palette["--color-gray-500"]}/>
              <S.EmailText style={{width: "300px"}}>{profile?.email}</S.EmailText>
            </S2.Card>
          </S2.CardWrapper>
        </S.ProfileWrapper>
        <MyPageCard
          openNicknameModal={() => modalHandler("nickname", true)}
          openPaymentModal={() => modalHandler("payment", true)}
          openIvReModal={(isInvite: boolean) => isInvite ? modalHandler("invite", true) : modalHandler("request", true)}
        />
        {isShowNicknameModal &&
          <ChangeNicknameModal nickname={profile?.nickname} callback={() => modalHandler("nickname", false)}/>
        }
        {isShowPaymentModal && <Modal
            title={"결제 내역 조회하기"}
            w={800}
            h={600}
            closeHandler={() => modalHandler("payment", false)}>
          <PaymentModal/>
        </Modal>}

        {isShowInviteModal && <Modal
            title={"초대 내역 조회하기"}
            w={800}
            h={600}
            closeHandler={() => modalHandler("invite", false)}>
          <IvReModal isRequest={false}/>
        </Modal>}

        {isShowRequestModal && <Modal
            title={"신청 내역 조회하기"}
            w={800}
            h={600}
            closeHandler={() => modalHandler("request", false)}>
          <IvReModal isRequest={true}/>
        </Modal>}

      </Fragment>
  )
}

export default MyPage;