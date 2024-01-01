import * as S from "../../styles/common-card/Card.styled";
import * as S2 from "../../styles/control/Date.styled";
import {Card} from "../../styles/common-card/Card.styled";
import PageNavBar from "../page-nav-bar/PageNavBar";
import ApiHistoryCard from "./card/ApiHistoryCard";
import {CommonBtn} from "../../styles/control/CommonBtn.styled";
import {palette} from "../../constants/Styles";
import React, {useEffect, useRef, useState} from "react";
import useAxios from "../../hooks/useAxios";
import {useParams} from "react-router-dom";
import {ApiIntroData, HistoryData} from "../../constants/interfaces";

const ApiHistoryCardArea: React.FC<{introData: ApiIntroData | undefined}> = ({introData}) => {

  const id = useParams().id;
  const today = new Date().toISOString().substring(0, 10);
  const startDateEle = useRef<HTMLInputElement>(null);
  const endDateEle = useRef<HTMLInputElement>(null);

  const {res, request} = useAxios();
  const [historyList, setHistoryList] = useState<Array<HistoryData>>([]);
  const [pageIdx, setPageIdx] = useState(0);

  const getHistoryList = () => {
    if (startDateEle.current?.value && endDateEle.current?.value &&
        startDateEle.current.value <= endDateEle.current.value) {
      request(`/api/history/${id}/${pageIdx}/4?startDate=${startDateEle.current.value}&endDate=${endDateEle.current.value}`, "get");
    }
  }

  useEffect(() => {
    getHistoryList();
  }, [pageIdx]);

  useEffect(() => {
    if (res === undefined) {
      return;
    }
    setHistoryList(res.data.content);
  }, [res]);

  return (
      <S.CardWrapper $m={80}>
        <S.CardTitle>API 로그 확인</S.CardTitle>
        <Card $d={"column"} $notAround={true} $p={50}>
          <S.CardWrapper $w={550}>
            <Card $r={15} $isNotShadow={true}>
              <S2.Date ref={startDateEle} defaultValue={today} type={"date"}/>
              -
              <S2.Date ref={endDateEle} defaultValue={today} type={"date"}/>
              <CommonBtn
                  onClick={getHistoryList}
                  $color={palette["--color-primary-100"]}
                  $hover-color={palette["--color-primary-900"]}>
                조회하기
              </CommonBtn>
            </Card>
          </S.CardWrapper>
          {historyList.map(item => (
              <ApiHistoryCard item={item} introData={introData} key={item._id}/>
          ))}
          {historyList.length === 0 && <S.CardTitle>기록을 조회해주세요.</S.CardTitle>}
          <PageNavBar page={
            {total: res?.data.totalElements || 0,
              index: pageIdx + 1, displaySize: 4, navBarSize: 5}}
                      setPageIdx={setPageIdx}
                      margin={1}/>
        </Card>
      </S.CardWrapper>
  )
}

export default ApiHistoryCardArea;