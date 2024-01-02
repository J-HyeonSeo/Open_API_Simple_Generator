import {Card, CardWrapper} from "../../styles/common-card/Card.styled";
import * as S from "../../styles/my-page/CardText.styled";
import React, {useState} from "react";
import {customAxios} from "../../utils/CustomAxios";
import {useRecoilState} from "recoil";
import {profileData, tokenData} from "../../store/RecoilState";
import useAxios from "../../hooks/useAxios";
import Modal from "../modal/Modal";

interface MyPageProps {
  openNicknameModal: () => void,
  openPaymentModal: () => void,
  openIvReModal: (isInvite: boolean) => void
}

const MyPageCard: React.FC<MyPageProps> = (props) => {

  const [token, setToken] = useRecoilState(tokenData);
  const [isShowLogoutModal, setIsShowLogoutModal] = useState(false);
  const [_, setProfile] = useRecoilState(profileData);

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

  return (
      <CardWrapper $w={450}>
        <Card $h={445} $m={50} $d={"column"}>
          <S.CardText onClick={props.openNicknameModal}>닉네임 변경하기</S.CardText>
          <S.CardText onClick={props.openPaymentModal}>결제 내역 조회하기</S.CardText>
          <S.CardText onClick={() => props.openIvReModal(true)}>초대 내역 조회하기</S.CardText>
          <S.CardText onClick={() => props.openIvReModal(false)}>신청 내역 조회하기</S.CardText>
          <S.CardText onClick={() => setIsShowLogoutModal(true)}>로그아웃</S.CardText>
        </Card>
        {isShowLogoutModal && <Modal
            mark={"question"}
            isButton={true}
            text={"로그아웃 하시겠습니까?"}
            yesCallback={logoutHandler}
            title={"로그아웃"} closeHandler={() => setIsShowLogoutModal(false)}/>}
      </CardWrapper>
  )
}

export default MyPageCard;