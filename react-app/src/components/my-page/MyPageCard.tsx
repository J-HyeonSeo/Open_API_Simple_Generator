import {Card, CardWrapper} from "../../styles/common-card/Card.styled";
import * as S from "../../styles/my-page/CardText.styled";

interface MyPageProps {
  openNicknameModal: () => void,
  openPaymentModal: () => void,
  openIvReModal: () => void
}

const MyPageCard: React.FC<MyPageProps> = (props) => {
  return (
      <CardWrapper $w={450}>
        <Card $h={445} $m={50} $d={"column"}>
          <S.CardText onClick={props.openNicknameModal}>닉네임 변경하기</S.CardText>
          <S.CardText onClick={props.openPaymentModal}>결제 내역 조회하기</S.CardText>
          <S.CardText onClick={props.openIvReModal}>초대/신청 내역 조회</S.CardText>
          <S.CardText>등급 결제하기</S.CardText>
          <S.CardText>로그아웃</S.CardText>
        </Card>
      </CardWrapper>
  )
}

export default MyPageCard;