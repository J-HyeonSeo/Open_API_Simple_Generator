import {Card, CardWrapper} from "../../styles/common-card/Card.styled";
import * as S from "../../styles/my-page/CardText.styled";

const MyPageCard = () => {
  return (
      <CardWrapper $w={450}>
        <Card $h={445} $m={50} $d={"column"}>
          <S.CardText>닉네임 변경하기</S.CardText>
          <S.CardText>결제 내역 조회하기</S.CardText>
          <S.CardText>초대/신청 내역 조회</S.CardText>
          <S.CardText>등급 결제하기</S.CardText>
          <S.CardText>로그아웃</S.CardText>
        </Card>
      </CardWrapper>
  )
}

export default MyPageCard;