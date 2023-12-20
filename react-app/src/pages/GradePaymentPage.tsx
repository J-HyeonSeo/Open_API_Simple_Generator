import {Fragment} from "react";
import Header from "../components/header/Header";
import GradeCardArea from "../components/grade-card/GradeCardArea";
import * as S from "../styles/grade-payment/GradePayment.style";

const GradePaymentPage = () => {

  return (
      <Fragment>
        <Header />
        <S.GradePaymentTitle>등급 결제하기</S.GradePaymentTitle>
        <GradeCardArea />
      </Fragment>
  )
}

export default GradePaymentPage;