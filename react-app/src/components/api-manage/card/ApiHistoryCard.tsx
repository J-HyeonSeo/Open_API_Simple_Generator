import * as S from "../../../styles/common-card/Card.styled";
import * as S2 from "../../../styles/api-card/ProfileArea.styled";
import {Card} from "../../../styles/common-card/Card.styled";
import {palette} from "../../../constants/Styles";
import TestProfileImg from "../../../assets/test-profile.png";
import {CommonBtn} from "../../../styles/control/CommonBtn.styled";
import React from "react";
import {Profile} from "../../../styles/profile/profile.styled";
import {Line} from "../../../styles/line/line.styled";

const ApiHistoryCard = () => {
  return (
      <S.CardWrapper $w={550} $m={10}>
        <Card $p={5} $r={10} $h={50} $c={palette["--color-gray-300"]}>
          <S2.ProfileAreaWrapper $w={500}>
            <Profile src={TestProfileImg} alt={"ProfileImg"}/>
            <Line $m={15} $h={30}/>
            <S2.HistoryWrapper>
              <S2.HistoryUsername><strong>'Adam Smith'</strong> 님이 API 데이터를 추가하였습니다.</S2.HistoryUsername>
              <S2.HistoryTimeWrapper>
                <S2.HistoryUsername>변경시각</S2.HistoryUsername>
                <Line $h={10} $m={10}></Line>
                <S2.HistoryUsername>2023-12-01T18:00:32</S2.HistoryUsername>
              </S2.HistoryTimeWrapper>
            </S2.HistoryWrapper>
          </S2.ProfileAreaWrapper>
          <div>
            <CommonBtn $color={palette["--color-primary-100"]} $hover-color={palette["--color-primary-900"]} $w={90} $h={30} $m={5}>
              상세보기
            </CommonBtn>
          </div>
        </Card>
      </S.CardWrapper>
  )
}

export default ApiHistoryCard;
