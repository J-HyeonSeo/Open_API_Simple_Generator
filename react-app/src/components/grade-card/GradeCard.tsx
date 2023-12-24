import React from "react";
import {GradeInfo} from "../../constants/interfaces";
import * as S from "../../styles/common-card/Card.styled";
import * as S2 from "../../styles/grade-payment/GradePayment.style";
import BronzeGradeImg from "../../assets/grade-image/bronze-mark.png";
import SilverGradeImg from "../../assets/grade-image/silver-mark.png";
import GoldGradeImg from "../../assets/grade-image/gold-mark.png";
import DiaGradeImg from "../../assets/grade-image/dia-mark.png";
import {palette} from "../../constants/Styles";
import {CommonBtn} from "../../styles/control/CommonBtn.styled";
import {Line} from "../../styles/line/line.styled";

const GradeCard: React.FC<{item: GradeInfo}> = ({item}) => {

  let nowImage = BronzeGradeImg;

  switch (item.gradeId) {
    case 1:
      nowImage = BronzeGradeImg;
      break;
    case 2:
      nowImage = SilverGradeImg;
      break;
    case 3:
      nowImage = GoldGradeImg;
      break;
    case 4:
      nowImage = DiaGradeImg;
      break;
  }

  return (
      <S.Card $h={480} $d={"column"}>
        <img src={nowImage} alt={"gradeImg"} width={"30px"} height={"70px"}/>
        <S2.GradeNameText>{`${item.gradeName}(${item.price === 0 ? "무료" : item.price.toLocaleString()})`}</S2.GradeNameText>
        <S.InnerCardWrapper $w={210}>
          <S.Card $h={220} $p={5} $c={palette["--color-gray-300"]}>
            <div>
              <S2.GradeText>API 갯수</S2.GradeText>
              <S2.GradeText>접근 가능 인원</S2.GradeText>
              <S2.GradeText>필드 갯수</S2.GradeText>
              <S2.GradeText>레코드 갯수</S2.GradeText>
              <S2.GradeText>데이터 용량</S2.GradeText>
              <S2.GradeText>질의 인수 갯수</S2.GradeText>
              <S2.GradeText>활동 기록 보존일</S2.GradeText>
            </div>
            <Line $h={200} $c={palette["--color-gray-500"]}/>
            <div>
              <S2.GradeText>{`${item.apiMaxCount}개`}</S2.GradeText>
              <S2.GradeText>{`${item.accessorMaxCount}명`}</S2.GradeText>
              <S2.GradeText>{`${item.fieldMaxCount}개`}</S2.GradeText>
              <S2.GradeText>{`${item.recordMaxCount}개`}</S2.GradeText>
              <S2.GradeText>{`${item.dbMaxSize / 1000000}MB`}</S2.GradeText>
              <S2.GradeText>{`${item.queryMaxCount}개`}</S2.GradeText>
              <S2.GradeText>{`${item.historyStorageDays}일`}</S2.GradeText>
            </div>
          </S.Card>
        </S.InnerCardWrapper>
        {!item.isPaid && <CommonBtn $color={palette["--color-primary-100"]}
                   $hover-color={palette["--color-primary-900"]}>결제하기</CommonBtn>}
        {item.isPaid && <CommonBtn $color={palette["--color-red-500"]}
                                    $hover-color={palette["--color-red-500"]}>보유중</CommonBtn>}
      </S.Card>
  )
}

export default GradeCard;