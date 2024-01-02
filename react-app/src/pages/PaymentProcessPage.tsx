import {Fragment, useEffect} from "react";
import {customAxios} from "../utils/CustomAxios";
import {useNavigate} from "react-router-dom";

const PaymentProcessPage = () => {

  const navigate = useNavigate();

  const paymentAssignProceed = async () => {
    try {
      const res = await customAxios().get(window.location.pathname + window.location.search);
      return res.data;
    } catch (e) {
      throw e;
    }
  }

  useEffect(() => {
    paymentAssignProceed().then(() => {
      //리액트로 경로 변경을 수행하는 것이 아닌, 새롭게 로딩하여, 등급 데이터를 다시 불러오기 위함.
      window.location.href = "/grade-payment";
    }).catch(() => {
      navigate("/grade-payment");
    });
  }, []);

  return <Fragment />
}

export default PaymentProcessPage;