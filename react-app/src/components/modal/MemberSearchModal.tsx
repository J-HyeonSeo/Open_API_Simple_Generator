import Modal from "./Modal";
import React from "react";
import * as S from "../../styles/modal/api-manage/MemberSearchModal.styled";
import * as S2 from "../../styles/common-card/Card.styled";
import {ModalInput} from "../../styles/control/ModalInput.styled";
import ProfileArea from "../api-card/ProfileArea";
import TestProfileImg from "../../assets/test-profile.png";

const MemberSearchModal: React.FC<{closeHandler: () => void}> = ({closeHandler}) => {
  return (
      <Modal title={"회원 검색"}
             mark={"question"}
             isButton={true}
             closeHandler={closeHandler}>
        <S.ContentWrapper>
          <S.Content>
            <S.Title>이메일 주소</S.Title>
            <ModalInput $w={310} placeholder={"작성 후 ENTER를 눌러주세요!"}/>
          </S.Content>
          <S.Content>
            <S2.CardWrapper $w={500}>
              <S2.Card $h={90} $r={10}>
                <ProfileArea
                    w={450}
                    isLine={true}
                    isEmail={true}
                    item={{profileImage: TestProfileImg, name: "Adam Smith", email: "AdamSmith@test.com"}} />
              </S2.Card>
            </S2.CardWrapper>
          </S.Content>
        </S.ContentWrapper>
      </Modal>
  )
}

export default MemberSearchModal;