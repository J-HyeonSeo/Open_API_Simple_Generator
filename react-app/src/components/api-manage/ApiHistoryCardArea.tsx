import * as S from "../../styles/common-card/Card.styled";
import {Card} from "../../styles/common-card/Card.styled";
import PageNavBar from "../page-nav-bar/PageNavBar";
import ApiHistoryCard from "./card/ApiHistoryCard";
import {CommonBtn} from "../../styles/control/CommonBtn.styled";
import {palette} from "../../constants/Styles";
import {Date} from "../../styles/control/Date.styled";

const ApiHistoryCardArea = () => {
  return (
      <S.CardWrapper $m={80}>
        <S.CardTitle>API 로그 확인</S.CardTitle>
        <Card $d={"column"} $notAround={true} $p={50}>
          <S.CardWrapper $w={550}>
            <Card $r={15} $isNotShadow={true}>
              <Date type={"date"}/>
              -
              <Date type={"date"}/>
              <CommonBtn $color={palette["--color-primary-100"]} $hover-color={palette["--color-primary-900"]}>
                조회하기
              </CommonBtn>
            </Card>
          </S.CardWrapper>
          <ApiHistoryCard />
          <ApiHistoryCard />
          <ApiHistoryCard />
          <ApiHistoryCard />
          <PageNavBar page={{total: 20, index: 2, displaySize: 4, navBarSize: 5}} margin={1}/>
        </Card>
      </S.CardWrapper>
  )
}

export default ApiHistoryCardArea;