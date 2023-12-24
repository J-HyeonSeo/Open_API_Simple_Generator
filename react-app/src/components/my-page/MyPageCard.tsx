import {Card, CardWrapper} from "../../styles/common-card/Card.styled";
import * as S from "../../styles/my-page/CardText.styled";
import React from "react";

interface MyPageProps {
  openNicknameModal: () => void,
  openPaymentModal: () => void,
  openIvReModal: (isInvite: boolean) => void
}

const MyPageCard: React.FC<MyPageProps> = (props) => {
  return (
      <CardWrapper $w={450}>
        <Card $h={445} $m={50} $d={"column"}>
          <S.CardText onClick={props.openNicknameModal}>닉네임 변경하기</S.CardText>
          <S.CardText onClick={props.openPaymentModal}>결제 내역 조회하기</S.CardText>
          <S.CardText onClick={() => props.openIvReModal(true)}>초대 내역 조회하기</S.CardText>
          <S.CardText onClick={() => props.openIvReModal(false)}>신청 내역 조회하기</S.CardText>
          <S.CardText>로그아웃</S.CardText>
        </Card>
      </CardWrapper>
  )
}

export default MyPageCard;