import {useNavigate, useParams} from "react-router-dom";
import useAxios from "../../../hooks/useAxios";
import React, {useState} from "react";
import * as S from "../../../styles/common-card/Card.styled";
import {Card} from "../../../styles/common-card/Card.styled";
import {CommonBtn} from "../../../styles/control/CommonBtn.styled";
import {palette} from "../../../constants/Styles";
import Modal from "../../modal/Modal";

const ApiEnableCard = () => {

  const id = useParams().id;

  const {res, setRes, isError, setIsError, errorMessage, request} = useAxios();
  const [isShowEnableModal, setIsShowEnableModal] = useState(false);

  const deleteRequest = () => {
    request(`/api/enable/${id}`, "patch");
    setIsShowEnableModal(false);
  }

  return (
      <S.CardWrapper $m={80}>
        <S.CardTitle>Open API 활성화</S.CardTitle>
        <Card $h={70}>
          <CommonBtn onClick={() => setIsShowEnableModal(true)}
                     $color={palette["--color-primary-100"]}
                     $hover-color={palette["--color-primary-900"]}>API 활성화</CommonBtn>
        </Card>
        {isShowEnableModal && <Modal
            title={"확인"}
            mark={"question"}
            isButton={true}
            yesCallback={() => deleteRequest()}
            text={"해당 OpenAPI를 활성화 하시겠습니까?"}
            closeHandler={() => setIsShowEnableModal(false)}/>}
        {res && <Modal
            title={"확인"}
            mark={"success"}
            isButton={true}
            yesCallback={() => setRes(undefined)}
            text={"성공적으로 해당 OpenAPI를 활성화하였습니다!"}
            closeHandler={() => () => setRes(undefined)}/>}
        {isError && <Modal
            title={"확인"}
            mark={"error"}
            isButton={true}
            yesCallback={() => setIsError(false)}
            text={errorMessage?.message || "해당 OpenAPI를 활성화 할 수 없습니다!"}
            closeHandler={() => setIsError(false)}/>}
      </S.CardWrapper>
  )
}

export default ApiEnableCard;