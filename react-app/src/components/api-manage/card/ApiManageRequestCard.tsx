import * as S from "../../../styles/common-card/Card.styled";
import {Card} from "../../../styles/common-card/Card.styled";
import {palette} from "../../../constants/Styles";
import ProfileArea from "../../api-card/ProfileArea";
import React, {useRef, useState} from "react";
import {CommonBtn} from "../../../styles/control/CommonBtn.styled";
import {IvReData} from "../../../constants/interfaces";
import useAxios from "../../../hooks/useAxios";
import Modal from "../../modal/Modal";

const ApiManageRequestCard: React.FC<{item: IvReData, reload: () => void}> = ({item, reload}) => {

  const {res, setRes, isError, errorMessage, setIsError, request} = useAxios();
  const selectId = useRef(0);
  const modalKeyword = useRef("수락");
  const [isShowAssignModal, setIsShowAssignModal] = useState(false);
  const [isShowRejectModal, setIsShowRejectModal] = useState(false);

  const assignRejectRequest = (isAssign: boolean) => {
    request(`/api/request/${isAssign ? 'assign' : 'reject'}/${selectId.current}`, "patch");
    setIsShowAssignModal(false);
    setIsShowRejectModal(false);
  }

  const modalHandler = (isAssign: boolean, id: number) => {
    selectId.current = id;
    if (isAssign) {
      modalKeyword.current = "수락"
      setIsShowAssignModal(true);
    } else {
      modalKeyword.current = "거절";
      setIsShowRejectModal(true);
    }
  }

  const successHandler = () => {
    reload();
    setRes(undefined);
  }

  return (
    <S.CardWrapper $w={550} $m={10}>
      <Card $p={5} $r={10} $h={50} $c={palette["--color-gray-300"]}>
        <ProfileArea item={{profileImage: item.profileUrl, name: item.memberNickname}} isLine={true}/>
        <div>
          <CommonBtn
              onClick={() => modalHandler(true, item.id)}
              $color={palette["--color-primary-100"]}
              $hover-color={palette["--color-primary-900"]}
              $w={90} $h={30} $m={5}>
            수락하기
          </CommonBtn>
          <CommonBtn
              onClick={() => modalHandler(false, item.id)}
              $color={palette["--color-red-500"]}
              $hover-color={palette["--color-red-700"]}
              $w={90} $h={30}>
            거절하기
          </CommonBtn>
        </div>
      </Card>
      {isShowAssignModal && <Modal
          mark={"question"}
          title={"수락"}
          isButton={true}
          text={"해당 신청을 수락하시겠습니까?"}
          yesCallback={() => assignRejectRequest(true)}
          closeHandler={() => setIsShowAssignModal(false)} />}
      {isShowRejectModal && <Modal
          mark={"question"}
          title={"거절"}
          isButton={true}
          text={"해당 신청을 거절하시겠습니까?"}
          yesCallback={() => assignRejectRequest(true)}
          closeHandler={() => setIsShowAssignModal(false)} />}
      {res && <Modal
          mark={"success"}
          title={"성공"}
          isButton={true}
          text={`성공적으로 ${modalKeyword.current}하였습니다.`}
          yesCallback={successHandler}
          closeHandler={successHandler} />}
      {isError && <Modal
          mark={"error"}
          title={"실패"}
          isButton={true}
          text={errorMessage?.message || "해당 요청을 수행할 수 없습니다."}
          yesCallback={() => setIsError(false)}
          closeHandler={() => setIsError(false)} />}
    </S.CardWrapper>
  )
}

export default ApiManageRequestCard;