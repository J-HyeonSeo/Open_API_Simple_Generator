import * as S from "../../styles/modal/my-page/PaymentModal.styled";
import {Line} from "../../styles/line/line.styled";
import {palette} from "../../constants/Styles";
import {CommonBtn} from "../../styles/control/CommonBtn.styled";
import useAxios from "../../hooks/useAxios";
import React, {useRef, useState} from "react";
import {PaymentData} from "../../constants/interfaces";
import useScroll from "../../hooks/useScroll";
import bronzeImg from "../../assets/grade-image/bronze-mark.png";
import silverImg from "../../assets/grade-image/silver-mark.png";
import goldImg from "../../assets/grade-image/gold-mark.png";
import diaImg from "../../assets/grade-image/dia-mark.png";
import Modal from "./Modal";

const PaymentModal = () => {

  //communication states..
  const {res: paymentRes, request: paymentRequest} = useAxios();
  const {res: refundRes, request: refundRequest, isError, setIsError, errorMessage, setRes} = useAxios();

  //data states..
  const [paymentList, setPaymentList] = useState<Array<PaymentData>>([]);
  const selectId = useRef(1);
  const [isShowRefundModal, setIsShowRefundModal] = useState(false);

  //데이터 호출 콜백 함수 정의 및, 무한 스크롤 설정하기.
  const getPaymentList = (pageIdx: number) => {
    paymentRequest(`/payment/${pageIdx}/5`, "get");
  }
  const {target} = useScroll<PaymentData>(getPaymentList, paymentRes, setPaymentList);

  //환불 관련
  const refundPayment = () => {
    refundRequest(`/payment/${selectId.current}`, "patch");
    setIsShowRefundModal(false);
  }

  const openRefundModal = (id: number) => {
    selectId.current = id;
    setIsShowRefundModal(true);
  }

  const refundSuccessHandler = () => {
    window.location.href = "/my-page";
  }

  const gradePictureSelector = (gradeId: number): string => {
    switch (gradeId) {
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

  //날짜를 계산하여 환불 가능 여부를 판단. 일주일이 지났는지 확인하는 함수.
  function isRefundPossible(paymentDate: string): boolean {
    const paymentDateTime = new Date(paymentDate);
    const oneWeekLater = new Date(paymentDateTime.getTime());
    oneWeekLater.setDate(oneWeekLater.getDate() + 7);
    const today = new Date();
    return today <= oneWeekLater;
  }

  return (
      <S.ContentWrapper>
        {paymentList.map((item) => (
          <S.Content key={item.id}>
            <S.GradeArea>
              <img src={gradePictureSelector(item.gradeId)} alt={"gradeImg"}/>
              <h2 style={{marginLeft: "20px"}}>{item.grade} 등급</h2>
            </S.GradeArea>
            <Line $h={50} $c={palette["--color-gray-300"]}/>
            <S.MetaDataOuterArea>
              <S.MetaDataInnerArea>
                <S.MetaDataFieldText>결제일</S.MetaDataFieldText>
                <Line $h={15} $c={palette["--color-gray-500"]} $m={15}/>
                <S.MetaDataValueText>{item.paidAt}</S.MetaDataValueText>
              </S.MetaDataInnerArea>
              <S.MetaDataInnerArea>
                <S.MetaDataFieldText>결제금액</S.MetaDataFieldText>
                <Line $h={15} $c={palette["--color-gray-500"]} $m={15}/>
                <S.MetaDataValueText>{item.paymentAmount}원</S.MetaDataValueText>
              </S.MetaDataInnerArea>
            </S.MetaDataOuterArea>
            {item.paymentState === "SUCCESS" && isRefundPossible(item.paidAt) &&  <CommonBtn
                onClick={() => openRefundModal(item.id)}
                $color={palette["--color-primary-100"]}
                $hover-color={palette["--color-primary-900"]}>
              환불하기
            </CommonBtn>}
            {item.paymentState === "SUCCESS" && !isRefundPossible(item.paidAt) &&  <CommonBtn
                $color={palette["--color-gray-500"]}
                $hover-color={palette["--color-gray-700"]}>
              환불불가
            </CommonBtn>}
            {item.paymentState === "REFUND" && <CommonBtn
                $color={palette["--color-red-500"]}
                $hover-color={palette["--color-red-700"]}>
              환불완료
            </CommonBtn>}
          </S.Content>
        ))}
        <div ref={target}/>
        {isShowRefundModal && <Modal
            title={"환불"}
            mark={"question"}
            isButton={true}
            text={"당월 두 번 이상 환불 시,\n\n등급 결제가 불가능 합니다.\n\n환불 하시겠습니까?"}
            yesCallback={refundPayment}
            closeHandler={() => setIsShowRefundModal(false)} />}
        {refundRes && <Modal
            title={"환불 성공"}
            mark={"success"}
            isButton={true}
            text={"성공적으로 환불되었습니다."}
            yesCallback={refundSuccessHandler}
            closeHandler={refundSuccessHandler} />}
        {isError && <Modal
            title={"환불 실패"}
            mark={"error"}
            isButton={true}
            text={errorMessage?.message || "환불에 실패하였습니다."}
            yesCallback={() => setIsError(false)}
            closeHandler={() => setIsError(false)} />}
      </S.ContentWrapper>
  )
}

export default PaymentModal;