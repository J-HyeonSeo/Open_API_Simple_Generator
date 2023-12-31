import * as S from "../../styles/common-card/Card.styled";
import {Card} from "../../styles/common-card/Card.styled";
import {CommonBtn} from "../../styles/control/CommonBtn.styled";
import {palette} from "../../constants/Styles";
import PageNavBar from "../page-nav-bar/PageNavBar";
import ApiBlackListCard from "./card/ApiBlackListCard";
import React, {useEffect, useState} from "react";
import MemberSearchModal from "../modal/MemberSearchModal";
import useAxios from "../../hooks/useAxios";
import {useParams} from "react-router-dom";
import {BlackListData} from "../../constants/interfaces";
import Modal from "../modal/Modal";

const ApiBlackListCardArea = () => {

  //조회 관련
  const id = useParams().id;
  const {res: contentRes, request: contentRequest} = useAxios();
  const [blackList, setBlackList] = useState<Array<BlackListData>>([]);
  const [pageIdx, setPageIdx] = useState(0);

  //추가 관련
  const [isShowMemberSearchModal, setIsShowMemberSearchModal] = useState(false);
  const {res: addRes, request: addRequest, isError, errorMessage, setIsError, setRes} = useAxios();
  const registerBlackList = (targetMemberId: number) => {
    addRequest(`/api/blacklist/${id}/${targetMemberId}`, "post");
  }

  const successHandler = () => {
    setRes(undefined);
    getBlackList();
  }

  const getBlackList = () => {
    contentRequest(`/api/blacklist/${id}/${pageIdx}/4`, "get");
  }

  useEffect(() => {
    getBlackList();
  }, [pageIdx]);

  useEffect(() => {
    setBlackList(contentRes?.data.content || []);
  }, [contentRes]);


  return (
      <S.CardWrapper $m={80}>
        <S.CardTitle>블랙리스트 관리</S.CardTitle>
        <Card $d={"column"} $notAround={true} $p={50}>
          <CommonBtn onClick={() => setIsShowMemberSearchModal(true)}
                     $color={palette["--color-primary-100"]}
                     $hover-color={palette["--color-primary-900"]}>
            + 추가
          </CommonBtn>
          {blackList.map(item => (
              <ApiBlackListCard item={item} callback={getBlackList} key={item.id}/>
          ))}
          {blackList.length === 0 && <S.CardTitle>블랙리스트가 없습니다.</S.CardTitle>}
          <PageNavBar page={
            {total: contentRes?.data.totalElements || 0,
              index: pageIdx + 1, displaySize: 4, navBarSize: 5}}
                      setPageIdx={setPageIdx}
                      margin={1}/>
        </Card>
        {isShowMemberSearchModal && <MemberSearchModal
            callback={registerBlackList}
            closeHandler={() => setIsShowMemberSearchModal(false)}/>}
        {addRes && <Modal
            title={"성공"}
            mark={"success"}
            text={"성공적으로 추가하였습니다."}
            isButton={true}
            yesCallback={successHandler}
            closeHandler={successHandler}/>}
        {isError && <Modal
            title={"실패"}
            mark={"error"}
            text={errorMessage?.message || "추가할 수 없는 대상입니다."}
            isButton={true}
            yesCallback={() => setIsError(false)}
            closeHandler={() => setIsError(false)}/>}
      </S.CardWrapper>
  )
}

export default ApiBlackListCardArea;