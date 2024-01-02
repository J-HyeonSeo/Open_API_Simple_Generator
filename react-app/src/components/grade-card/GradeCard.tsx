import React, {useEffect} from "react";
import {GradeInfo, PaymentRedirectData} from "../../constants/interfaces";
import * as S from "../../styles/common-card/Card.styled";
import * as S2 from "../../styles/grade-payment/GradePayment.style";
import BronzeGradeImg from "../../assets/grade-image/bronze-mark.png";
import SilverGradeImg from "../../assets/grade-image/silver-mark.png";
import GoldGradeImg from "../../assets/grade-image/gold-mark.png";
import DiaGradeImg from "../../assets/grade-image/dia-mark.png";
import {palette} from "../../constants/Styles";
import {CommonBtn} from "../../styles/control/CommonBtn.styled";
import {Line} from "../../styles/line/line.styled";
import useAxios from "../../hooks/useAxios";
import Modal from "../modal/Modal";

const GradeCard: React.FC<{item: GradeInfo, gradeId: number}> = ({item, gradeId}) => {

  //payment
  const {res, isError, setIsError, errorMessage, request} = useAxios();

  const payment = (gradeId: number) => {
    request(`/payment/${gradeId}`, "post");
  }

  useEffect(() => {
    if (res === undefined) {
      return;
    }
    const data: PaymentRedirectData = res.data;
    window.location.href = data.next_redirect_pc_url;
  }, [res]);

  //set grade image.
  let nowImage = BronzeGradeImg;

  switch (item.id) {
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
        {item.id === 1 && <CommonBtn
            $color={palette["--color-gray-500"]}
            $hover-color={palette["--color-gray-500"]}>기본등급</CommonBtn>}
        {item.id > 1 && item.id !== gradeId && <CommonBtn
            onClick={() => payment(item.id)}
            $color={palette["--color-primary-100"]}
            $hover-color={palette["--color-primary-900"]}>결제하기</CommonBtn>}
        {item.id > 1 && item.id === gradeId && <CommonBtn
            $color={palette["--color-red-500"]}
            $hover-color={palette["--color-red-500"]}>보유중</CommonBtn>}
        {isError && <Modal
            title={"결제 실패"}
            mark={"error"}
            isButton={true}
            text={errorMessage?.message || "결제에 실패하였습니다."}
            yesCallback={() => setIsError(false)}
            closeHandler={() => setIsError(false)} />}
      </S.Card>
  )
}

export default GradeCard;