import Modal from "./Modal";
import React, {useEffect, useRef, useState} from "react";
import * as S from "../../styles/modal/api-manage/MemberSearchModal.styled";
import * as S2 from "../../styles/common-card/Card.styled";
import {ModalInput} from "../../styles/control/ModalInput.styled";
import ProfileArea from "../api-card/ProfileArea";
import useAxios from "../../hooks/useAxios";
import {MemberSearchData} from "../../constants/interfaces";

const MemberSearchModal: React.FC<{closeHandler: () => void, callback: (id: number | undefined) => void}> = ({closeHandler, callback}) => {

  const {res, setRes, request, isError, setIsError} = useAxios();
  const inputElement = useRef<HTMLInputElement>(null);
  const [memberData, setMemberData] = useState<MemberSearchData>();

  const onKeyChangeHandler = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter') {
      setRes(undefined);
      request(`/member/search?email=${inputElement.current?.value}`, "get");
    }
  }

  useEffect(() => {
    setMemberData(res?.data || undefined);
  }, [res]);

  return (
      <Modal title={"회원 검색"}
             mark={"question"}
             isButton={true}
             yesText={"추가"}
             yesCallback={() => callback(memberData?.memberId)}
             closeHandler={closeHandler}>
        <S.ContentWrapper>
          <S.Content>
            <S.Title>이메일 주소</S.Title>
            <ModalInput
                onKeyDown={(e) => onKeyChangeHandler(e)}
                ref={inputElement}
                $w={310} placeholder={"작성 후 ENTER를 눌러주세요!"}/>
          </S.Content>
          {memberData && <S.Content>
            <S2.CardWrapper $w={500}>
              <S2.Card $h={90} $r={10}>
                <ProfileArea
                    w={450}
                    isLine={true}
                    isEmail={true}
                    item={{profileImage: memberData.profileUrl, name: memberData.memberNickname, email: memberData.memberEmail}} />
              </S2.Card>
            </S2.CardWrapper>
          </S.Content>}
          {isError && <S.Content>
            <S2.CardWrapper $w={500}>
              <S2.Card $h={90} $r={10}>
                <S2.CardTitle>회원을 찾을 수 없습니다!</S2.CardTitle>
              </S2.Card>
            </S2.CardWrapper>
          </S.Content>}
        </S.ContentWrapper>
      </Modal>
  )
}

export default MemberSearchModal;