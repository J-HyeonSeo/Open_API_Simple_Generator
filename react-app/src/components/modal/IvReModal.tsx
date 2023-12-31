import * as S from "../../styles/modal/my-page/IvReModal.styled";
import ProfileArea from "../api-card/ProfileArea";
import {CommonBtn} from "../../styles/control/CommonBtn.styled";
import {palette} from "../../constants/Styles";
import React, {Fragment, useRef, useState} from "react";
import {IvReData} from "../../constants/interfaces";
import useAxios from "../../hooks/useAxios";
import useScroll from "../../hooks/useScroll";
import Modal from "./Modal";
import {useNavigate} from "react-router-dom";

const IvReModal: React.FC<{isRequest: boolean}> = ({isRequest}) => {

  const navigate = useNavigate();

  //communication states..
  const {res: ivReRes, request: ivReRequest} = useAxios();
  const {res: assignRejectRes, request: assignRejectRequest, isError, setIsError, errorMessage, setRes} = useAxios();

  //data states..
  const [ivReList, setIvReList] = useState<Array<IvReData>>([]);
  const selectId = useRef(1);
  const modalKeyword = useRef("수락");
  const [isShowAssignModal, setIsShowAssignModal] = useState(false);
  const [isShowRejectModal, setIsShowRejectModal] = useState(false);

  //데이터 호출 콜백 함수 정의 및, 무한 스크롤 설정하기.
  const getIvReList = (pageIdx: number) => {
    ivReRequest(`/api/${isRequest ? 'request' : 'invite'}/member/${pageIdx}/5`, "get");
  }
  const {target} = useScroll<IvReData>(getIvReList, ivReRes, setIvReList);

  //초대 내역 수락/거절 함수
  const assignRejectInvite = (isAssign: boolean) => {
      assignRejectRequest(`/api/invite/${isAssign ? 'assign' : 'reject'}/${selectId.current}`, "patch");
      setIsShowAssignModal(false);
      setIsShowRejectModal(false);
  }

  //모달 제어 함수
  const modalHandler = (isAssign: boolean, id: number) => {
    if (isAssign) {
      setIsShowAssignModal(true);
      modalKeyword.current = "수락";
    } else {
      setIsShowRejectModal(true);
      modalKeyword.current = "거절";
    }
    selectId.current = id;
  }

  //성공시 데이터 제거 함수.
  const successHandler = () => {
    setRes(undefined); //결과 초기화
    const targetIndex = ivReList.findIndex((item) => {return item.id === selectId.current});
    setIvReList(
        (prev) => {
          const newIvReList = [...prev];
          newIvReList.splice(targetIndex, 1);
          return newIvReList;
        }
    );
  }

  return (
      <S.ContentWrapper>
        {ivReList.map((item) => (
            <S.Content key={item.id}>
              <S.DataOuterArea>
                <S.DataInnerArea>
                  <S.Title>{item.apiName}</S.Title>
                </S.DataInnerArea>
                <S.DataInnerArea>
                  <S.BottomArea>
                    <ProfileArea item={{profileImage: item.profileUrl, name: item.memberNickname}} isLine={true}/>
                    <CommonBtn
                        onClick={() => navigate(`/api/intro/${item.apiInfoId}/0`)}
                        $w={150} $color={palette["--color-gray-500"]}
                        $hover-color={palette["--color-gray-900"]}>
                      API 보러가기
                    </CommonBtn>
                  </S.BottomArea>
                </S.DataInnerArea>
              </S.DataOuterArea>
              <S.ButtonArea>
                {isRequest && <Fragment>
                  {item.requestStateType === "REQUEST" &&
                      <CommonBtn $color={palette["--color-gray-500"]} $hover-color={palette["--color-gray-700"]}>
                    승인대기
                  </CommonBtn>}
                  {item.requestStateType === "ASSIGN" &&
                      <CommonBtn $color={palette["--color-primary-100"]} $hover-color={palette["--color-primary-900"]}>
                    수락됨
                  </CommonBtn>}
                  {item.requestStateType === "REJECT" &&
                      <CommonBtn $color={palette["--color-red-500"]} $hover-color={palette["--color-red-700"]}>
                    거절됨
                  </CommonBtn>}
                </Fragment>}
                {!isRequest && <Fragment>
                  <CommonBtn
                      onClick={() => modalHandler(true, item.id)}
                      $color={palette["--color-primary-100"]}
                      $hover-color={palette["--color-primary-900"]}>
                    수락하기
                  </CommonBtn>
                  <CommonBtn
                      onClick={() => modalHandler(false, item.id)}
                      $color={palette["--color-red-500"]}
                      $hover-color={palette["--color-red-700"]}>
                    거절하기
                  </CommonBtn>
                </Fragment>}
              </S.ButtonArea>
            </S.Content>
        ))}
        {ivReList.length === 0 && <h2 style={{textAlign: "center"}}>조회할 데이터가 없습니다.</h2>}
        <div ref={target}/>
        {isShowAssignModal && <Modal
            mark={"question"}
            title={"수락?"}
            text={"해당 OpenAPI 초대를 수락하시겠습니까?"}
            isButton={true}
            yesCallback={() => assignRejectInvite(true)}
            closeHandler={() => setIsShowAssignModal(false)} />}
        {isShowRejectModal && <Modal
            mark={"question"}
            title={"거절?"}
            text={"해당 OpenAPI 초대를 거절하시겠습니까?"}
            isButton={true}
            yesCallback={() => assignRejectInvite(false)}
            closeHandler={() => setIsShowRejectModal(false)} />}
        {assignRejectRes && <Modal
            mark={"success"}
            title={"성공"}
            text={`성공적으로 해당 초대를 ${modalKeyword.current}하였습니다.`}
            isButton={true}
            yesCallback={() => successHandler()}
            closeHandler={() => successHandler()} />}
        {isError && <Modal
            mark={"error"}
            title={"실패"}
            text={errorMessage?.message}
            isButton={true}
            yesCallback={() => setIsError(false)}
            closeHandler={() => setIsError(false)} />}
      </S.ContentWrapper>
  )
}

export default IvReModal;