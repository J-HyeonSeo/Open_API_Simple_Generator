import React from "react";
import ApiCard from "./ApiCard";
import {ApiData} from "../../constants/interfaces";
import * as S from "../../styles/api-card/ApiCard.styled";
import {CardWrapper} from "../../styles/common-card/Card.styled";
import {CommonBtn} from "../../styles/control/CommonBtn.styled";
import {palette} from "../../constants/Styles";

const ApiCardArea: React.FC<{item: Array<ApiData>, total: number, title: string}> = ({item, total, title}) => {

  return (
      <CardWrapper>
        <S.ApiCardTitleArea>
          <S.LeftTitleStyle>총 {total}개</S.LeftTitleStyle>
          <S.MiddleTitleStyle>{title}</S.MiddleTitleStyle>
          <S.RightDivStyle>
            <CommonBtn $w={150} $color={palette["--color-primary-100"]}
                       $hover-color={palette["--color-primary-900"]}>+ 새 Open API</CommonBtn>

          </S.RightDivStyle>
        </S.ApiCardTitleArea>
        {item.length == 0 && <h2 style={{textAlign: "center"}}>표시할 데이터가 없습니다.</h2>}
        <ul>
          {item.map(item =>
              <li key={item.apiId}><ApiCard item={item}/></li>
          )}
        </ul>
      </CardWrapper>
  )
}

export default ApiCardArea;