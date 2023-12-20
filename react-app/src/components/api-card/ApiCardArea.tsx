import ApiCard from "./ApiCard";
import {ApiData} from "../../constants/interfaces";
import * as S from "../../styles/api-card/ApiCard.styled";
import {CardWrapper} from "../../styles/common-card/Card.styled";

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
      apiId: 2,
      profileUrl: "test",
      username: "Adam Smith",
      apiName: "2020 ~ 2023년도 경제 시장 분석 데이터 OPEN API",
      accessible: true
    },
    {
      apiId: 3,
      profileUrl: "test",
      username: "Adam Smith",
      apiName: "2020 ~ 2023년도 경제 시장 분석 데이터 OPEN API",
      accessible: false
    },
    {
      apiId: 4,
      profileUrl: "test",
      username: "Adam Smith",
      apiName: "2020 ~ 2023년도 경제 시장 분석 데이터 OPEN API",
      accessible: true
    },
    {
      apiId: 5,
      profileUrl: "test",
      username: "Adam Smith",
      apiName: "2020 ~ 2023년도 경제 시장 분석 데이터 OPEN API",
      accessible: true
    }
  ];
  return (
      <CardWrapper>
        <S.ApiCardTitleArea>
          <S.LeftTitleStyle>총 100개</S.LeftTitleStyle>
          <S.MiddleTitleStyle>공개 API 목록</S.MiddleTitleStyle>
          <S.RightDivStyle/>
        </S.ApiCardTitleArea>
        <ul>
          {mockApiList.map(item =>
              <li key={item.apiId}><ApiCard item={item}/></li>
          )}
        </ul>
      </CardWrapper>
  )
}

export default ApiCardArea;