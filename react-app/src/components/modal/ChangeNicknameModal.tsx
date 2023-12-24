import * as S from "../../styles/modal/my-page/ChangeNicknameModal.styled";
import {ModalInput} from "../../styles/control/ModalInput.styled";
import React from "react";

const ChangeNicknameModal: React.FC<{nickname: string}> = ({nickname}) => {
  return (
      <S.ContentWrapper>
        <S.Content>
          <S.ContentText>현재 닉네임</S.ContentText>
          <ModalInput $w={290} value={nickname} disabled={true}/>
        </S.Content>
        <S.Content>
          <S.ContentText>변경 닉네임</S.ContentText>
          <ModalInput $w={290}/>
        </S.Content>
      </S.ContentWrapper>
  )
}

export default ChangeNicknameModal;