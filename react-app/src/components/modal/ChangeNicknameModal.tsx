import * as S from "../../styles/modal/my-page/ChangeNicknameModal.styled";
import {ModalInput} from "../../styles/control/ModalInput.styled";
import React, {useRef, useState} from "react";
import Modal from "./Modal";
import useAxios from "../../hooks/useAxios";

const ChangeNicknameModal: React.FC<{nickname: string | undefined, callback: () => void}> = ({nickname, callback}) => {

  const modifyNickname = useRef<HTMLInputElement>(null);
  const [isShowChangeModal, setIsShowChangeModal] = useState(false);
  const {res, setRes, request, isError, setIsError, errorMessage} = useAxios();

  const changeNickname = () => {
    request("/member/nickname", "patch", {nickname: modifyNickname.current?.value});
    setIsShowChangeModal(false);
  }

  const changeSuccessHandler = () => {
    window.location.href = "/my-page";
  }

  return (
      <Modal
          isButton={true}
          mark={"question"}
          title={"닉네임 변경하기"}
          yesCallback={() => setIsShowChangeModal(true)}
          closeHandler={callback}
          yesText={"변경"}
      >
        <S.ContentWrapper>
          <S.Content>
            <S.ContentText>현재 닉네임</S.ContentText>
            <ModalInput $w={290} value={nickname} disabled={true}/>
          </S.Content>
          <S.Content>
            <S.ContentText>변경 닉네임</S.ContentText>
            <ModalInput ref={modifyNickname} $w={290}/>
          </S.Content>
          {isShowChangeModal && <Modal
              title={"변경"}
              mark={"question"}
              isButton={true}
              text={`닉네임을 '${modifyNickname.current?.value}' 로\n변경하시겠습니까?`}
              yesCallback={changeNickname}
              closeHandler={() => setIsShowChangeModal(false)} />}
          {res && <Modal
              title={"성공"}
              mark={"success"}
              isButton={true}
              text={`성공적으로 닉네임을 변경하였습니다.`}
              yesCallback={changeSuccessHandler}
              closeHandler={changeSuccessHandler} />}
          {isError && <Modal
              title={"실패"}
              mark={"error"}
              isButton={true}
              text={errorMessage?.message || "닉네임을 변경할 수 없습니다."}
              yesCallback={() => setIsError(false)}
              closeHandler={() => setIsError(false)} />}
        </S.ContentWrapper>
      </Modal>
  )
}

export default ChangeNicknameModal;