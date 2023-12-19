import ApiCard from "./ApiCard";
import {ApiData} from "../../constants/interfaces";
import * as S from "../../styles/api-card/ApiCard.styled";
import styled from "styled-components";

const LeftTitleStyle = styled.h2`
      width: 300px;
    `;

const MiddleTitleStyle = styled.h3`
      width: 300px;
      font-weight: 600;
      text-align: center;
    `;

const RightDivStyle = styled.div`
      width: 300px;
    `;

const ApiCardArea = () => {
  const mockApiList: Array<ApiData> = [
    {
      apiId: 1,
      profileUrl: "test",
      username: "Adam Smith",
      apiName: "2020 ~ 2023년도 경제 시장 분석 데이터 OPEN API",
      accessible: false
    },
    {
      apiId: 1,
      profileUrl: "test",
      username: "Adam Smith",
      apiName: "2020 ~ 2023년도 경제 시장 분석 데이터 OPEN API",
      accessible: true
    },
    {
      apiId: 1,
      profileUrl: "test",
      username: "Adam Smith",
      apiName: "2020 ~ 2023년도 경제 시장 분석 데이터 OPEN API",
      accessible: false
    },
    {
      apiId: 1,
      profileUrl: "test",
      username: "Adam Smith",
      apiName: "2020 ~ 2023년도 경제 시장 분석 데이터 OPEN API",
      accessible: true
    },
    {
      apiId: 1,
      profileUrl: "test",
      username: "Adam Smith",
      apiName: "2020 ~ 2023년도 경제 시장 분석 데이터 OPEN API",
      accessible: true
    }
  ];
  return (
      <S.ApiCardAreaWrapper>
        <S.ApiCardTitleArea>
          <LeftTitleStyle>총 100개</LeftTitleStyle>
          <MiddleTitleStyle>공개 API 목록</MiddleTitleStyle>
          <RightDivStyle/>
        </S.ApiCardTitleArea>
        {mockApiList.map(item =>
          <ApiCard item={item}/>
        )}
      </S.ApiCardAreaWrapper>
  )
}

export default ApiCardArea;