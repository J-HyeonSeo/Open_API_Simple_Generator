import * as S from "../../styles/common-card/Card.styled";
import {Card} from "../../styles/common-card/Card.styled";
import PageNavBar from "../page-nav-bar/PageNavBar";
import ApiPermissionCard from "./card/ApiPermissionCard";
import {useParams} from "react-router-dom";
import useAxios from "../../hooks/useAxios";
import React, {useEffect, useState} from "react";
import {PermissionData} from "../../constants/interfaces";

const ApiPermissionCardArea = () => {

  //조회 관련
  const id = useParams().id;
  const {res: contentRes, request: contentRequest} = useAxios();
  const [permissionList, setPermissionList] = useState<Array<PermissionData>>([]);
  const [pageIdx, setPageIdx] = useState(0);

  const getPermissionList = () => {
    contentRequest(`/api/permission/owner/${id}/${pageIdx}/4`, "get");
  }

  useEffect(() => {
    getPermissionList();
  }, [pageIdx]);

  useEffect(() => {
    setPermissionList(contentRes?.data.content || []);
  }, [contentRes]);

  return (
      <S.CardWrapper $m={80}>
        <S.CardTitle>권한 관리</S.CardTitle>
        <Card $d={"column"} $notAround={true} $p={50}>
          {permissionList.map(item => (
              <ApiPermissionCard callback={getPermissionList} item={item} key={item.permissionId}/>
          ))}
          {permissionList.length === 0 && <S.CardTitle>권한 목록이 없습니다.</S.CardTitle>}
          <PageNavBar page={
            {total: contentRes?.data.totalElements || 0,
              index: pageIdx + 1, displaySize: 4, navBarSize: 5}}
                      setPageIdx={setPageIdx}
                      margin={1}/>
        </Card>
      </S.CardWrapper>
  )
}

export default ApiPermissionCardArea;