import * as S from "../../styles/modal/my-page/IvReModal.styled";
import ProfileArea from "../api-card/ProfileArea";
import {CommonBtn} from "../../styles/control/CommonBtn.styled";
import {palette} from "../../constants/Styles";
import React, {Fragment, useEffect, useRef, useState} from "react";
import {IvReData} from "../../constants/interfaces";
import useAxios from "../../hooks/useAxios";
import useScroll from "../../hooks/useScroll";

const IvReModal: React.FC<{isRequest: boolean}> = ({isRequest}) => {

  //communication states..
  const {res: ivReRes, request: ivReRequest} = useAxios();

  //data states..
  const [ivReList, setIvReList] = useState<Array<IvReData>>([]);

  //callback 함수
  const getIvReList = (pageIdx: number) => {
    ivReRequest(`/api/request/member/${pageIdx}/5`, "get");
  }
  const {target} = useScroll<IvReData>(getIvReList, ivReRes, setIvReList);

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
                    <CommonBtn $w={150} $color={palette["--color-gray-500"]} $hover-color={palette["--color-gray-900"]}>
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
                    승인됨
                  </CommonBtn>}
                  {item.requestStateType === "REJECT" &&
                      <CommonBtn $color={palette["--color-red-500"]} $hover-color={palette["--color-red-700"]}>
                    거절됨
                  </CommonBtn>}
                </Fragment>}
                {!isRequest && <Fragment>
                  <CommonBtn $color={palette["--color-primary-100"]} $hover-color={palette["--color-primary-900"]}>
                    수락하기
                  </CommonBtn>
                  <CommonBtn $color={palette["--color-red-500"]} $hover-color={palette["--color-red-700"]}>
                    거절하기
                  </CommonBtn>
                </Fragment>}
              </S.ButtonArea>
            </S.Content>
        ))}
        {ivReList.length === 0 && <h2 style={{textAlign: "center"}}>조회할 데이터가 없습니다.</h2>}
        <div ref={target}/>
      </S.ContentWrapper>
  )
}

export default IvReModal;