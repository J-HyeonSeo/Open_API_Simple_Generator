import * as S from "../../styles/common-card/Card.styled";
import {Card} from "../../styles/common-card/Card.styled";
import ApiManageRequestCard from "./card/ApiManageRequestCard";
import PageNavBar from "../page-nav-bar/PageNavBar";
import useAxios from "../../hooks/useAxios";
import React, {useEffect, useState} from "react";
import {IvReData} from "../../constants/interfaces";
import {useParams} from "react-router-dom";

const ApiManageRequestCardArea = () => {

  const id = useParams().id;
  const {res, request} = useAxios();
  const [requestList, setRequestList] = useState<Array<IvReData>>([]);
  const [pageIdx, setPageIdx] = useState(0);

  const getRequestList = () => {
    request(`/api/request/owner/${id}/${pageIdx}/4`, "get");
  }

  useEffect(() => {
    getRequestList();
  }, [pageIdx]);

  useEffect(() => {
    if (res === undefined) {
      return;
    }
    setRequestList(res.data.content);
  }, [res]);

  return (
      <S.CardWrapper $m={80}>
        <S.CardTitle>신청 관리</S.CardTitle>
        <Card $d={"column"} $notAround={true} $p={50}>
          {requestList.map((item) => (
            <ApiManageRequestCard key={item.id} item={item} reload={getRequestList}/>
          ))}
          {requestList.length === 0 && <S.CardTitle>신청 목록이 없습니다.</S.CardTitle>}
          <PageNavBar
              page={{total: res?.data.totalElements || 0,
                index: pageIdx + 1, displaySize: 4, navBarSize: 5}}
              setPageIdx={setPageIdx}
              margin={1}/>
        </Card>
      </S.CardWrapper>
  )
}

export default ApiManageRequestCardArea;