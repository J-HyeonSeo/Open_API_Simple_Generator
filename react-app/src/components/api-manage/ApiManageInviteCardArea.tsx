import * as S from "../../styles/common-card/Card.styled";
import {Card} from "../../styles/common-card/Card.styled";
import PageNavBar from "../page-nav-bar/PageNavBar";
import ApiManageInviteCard from "./card/ApiManageInviteCard";
import {CommonBtn} from "../../styles/control/CommonBtn.styled";
import {palette} from "../../constants/Styles";
import {useEffect, useState} from "react";
import MemberSearchModal from "../modal/MemberSearchModal";
import useAxios from "../../hooks/useAxios";
import {IvReData} from "../../constants/interfaces";
import {useParams} from "react-router-dom";
import Modal from "../modal/Modal";

const ApiManageInviteCardArea = () => {

  //조회 관련
  const id = useParams().id;
  const {res: contentRes, request: contentRequest} = useAxios();
  const [inviteList, setInviteList] = useState<Array<IvReData>>([]);
  const [pageIdx, setPageIdx] = useState(0);

  //초대 관련
  const [isShowMemberSearchModal, setIsShowMemberSearchModal] = useState(false);
  const {res: inviteRes, request: inviteRequest, isError, errorMessage, setIsError, setRes} = useAxios();
  const sendInvite = (targetMemberId: number) => {
    inviteRequest(`/api/invite/${id}/${targetMemberId}`, "post");
  }

  const getInviteList = () => {
    contentRequest(`/api/invite/owner/${id}/${pageIdx}/4`, "get");
  }

  useEffect(() => {
    getInviteList();
  }, [pageIdx]);

  useEffect(() => {
    setInviteList(contentRes?.data.content || []);
  }, [contentRes]);

  return (
      <S.CardWrapper $m={80}>
        <S.CardTitle>초대 관리</S.CardTitle>
        <Card $d={"column"} $notAround={true} $p={50}>
          <CommonBtn onClick={() => setIsShowMemberSearchModal(true)}
                     $color={palette["--color-primary-100"]}
                     $hover-color={palette["--color-primary-900"]}>
            + 추가
          </CommonBtn>
          {inviteList.map(item => (
            <ApiManageInviteCard item={item} key={item.id}/>
          ))}
          <PageNavBar page={
            {total: contentRes?.data.totalElements || 0,
              index: pageIdx + 1, displaySize: 4, navBarSize: 5}}
                      setPageIdx={setPageIdx}
                      margin={1}/>
        </Card>
        {isShowMemberSearchModal && <MemberSearchModal
            callback={sendInvite}
            closeHandler={() => setIsShowMemberSearchModal(false)}/>}
        {inviteRes && <Modal
            title={"성공"}
            mark={"success"}
            text={"성공적으로 초대하였습니다."}
            isButton={true}
            yesCallback={() => setRes(undefined)}
            closeHandler={() => setRes(undefined)}/>}
        {isError && <Modal
            title={"실패"}
            mark={"error"}
            text={errorMessage?.message || "초대할 수 없는 대상입니다."}
            isButton={true}
            yesCallback={() => setIsError(false)}
            closeHandler={() => setIsError(false)}/>}
      </S.CardWrapper>
  )
}

export default ApiManageInviteCardArea;