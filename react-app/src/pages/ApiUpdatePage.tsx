import Header from "../components/header/Header";
import * as S from "../styles/api-create/ApiCreate.styled";
import * as S2 from "../styles/common-card/Card.styled";
import {CommonBtn} from "../styles/control/CommonBtn.styled";
import {palette} from "../constants/Styles";
import React, {Fragment, useState} from "react";
import {useLocation, useNavigate, useParams} from "react-router-dom";
import {CheckBox, CheckBoxLabel, CheckBoxWrapper} from "../styles/control/CheckBox.styled";
import Modal from "../components/modal/Modal";
import useAxios from "../hooks/useAxios";

const ApiUpdatePage = () => {

  const navigate = useNavigate();
  const id = useParams().id;
  const {state} = useLocation();

  //data states..
  const [apiName, setApiName] = useState(state.apiName);
  const [apiIntroduce, setApiIntroduce] = useState(state.apiIntroduce);
  const [isPublic, setIsPublic] = useState(state.isPublic);

  //modal state
  const [isShowModal, setIsShowModal] = useState(false);

  //communication state
  const {isError, setIsError, errorMessage, res, request} = useAxios();

  const updateHandler = async () => {
    const body = {
      apiName: apiName,
      apiIntroduce: apiIntroduce,
      isPublic: isPublic
    }
    await request(`/api/${id}`, "patch", body);
    setIsShowModal(false);
  }

  return (
      <Fragment>
        <Header />
        <S.ApiCreateTitle>Open API 수정하기</S.ApiCreateTitle>
        <S2.CardWrapper $w={700}>
          <S2.CardTitle>API 이름</S2.CardTitle>
          <S.ApiCreateInput
              onChange={(e) => setApiName(e.target.value)}
              value={apiName}
              type={"input"} placeholder={"OpenAPI 이름을 입력해주세요."}/>
        </S2.CardWrapper>
        <S2.CardWrapper $w={700} $m={80}>
          <S2.CardTitle>API 설명</S2.CardTitle>
          <S.ApiCreateTextArea
              onChange={(e) => setApiIntroduce(e.target.value)}
              value={apiIntroduce}
              rows={6} placeholder={"OpenAPI 설명을 입력해주세요."}/>
        </S2.CardWrapper>
        <S2.CardWrapper $w={700} $m={80}>
          <S2.CardTitle>공개 여부</S2.CardTitle>
          <CheckBoxWrapper>
            <CheckBox
                checked={isPublic}
                id={"isPublic"}
                onChange={(e) => setIsPublic(e.target.checked)}/>
            <CheckBoxLabel $c={palette["--color-gray-500"]} $m={10} htmlFor={"isPublic"}>해당 OpenAPI에 대해 검색을 허용하고 신청요청을 받겠습니다.</CheckBoxLabel>
          </CheckBoxWrapper>
        </S2.CardWrapper>
        <S2.CardWrapper $w={700} $m={80} $isFlex={true} >
          <S2.Card $isNotShadow={true} $c={"white"} $p={120}>
            <CommonBtn
                onClick={() => setIsShowModal(true)}
                $color={palette["--color-primary-100"]}
                $hover-color={palette["--color-primary-900"]}>
              수정 하기
            </CommonBtn>
            <CommonBtn
                onClick={() => navigate(-1)}
                $color={palette["--color-gray-500"]}
                $hover-color={palette["--color-gray-900"]}>
              취소 하기
            </CommonBtn>
          </S2.Card>
        </S2.CardWrapper>
        {isShowModal && <Modal title={"확인"}
               mark={"question"}
               isButton={true} text={"해당 정보로 OpenAPI를 수정하시겠습니까?"}
               yesCallback={updateHandler}
               closeHandler={() => setIsShowModal(false)} />}
        {res && <Modal
            title={"성공"}
            mark={"success"}
            isButton={true} text={"성공적으로 OpenAPI 정보를 수정하였습니다."}
            yesCallback={() => navigate(-1)}
            closeHandler={() => navigate(-1)} />}
        {isError && <Modal
            title={"확인"}
            mark={"error"}
            isButton={true} text={errorMessage?.message || "OpenAPI를 수정하는 도중에\n오류가 발생하였습니다."}
            closeHandler={() => setIsShowModal(false)} />}
      </Fragment>
  )
}

export default ApiUpdatePage;