import {ApiData} from "../../constants/interfaces";
import React from "react";
import * as S from "../../styles/api-card/ApiCard.styled";
import {Profile} from "../../styles/profile/profile.styled";
import TestProfileImg from "../../assets/test-profile.png";
import {CommonBtn} from "../../styles/control/CommonBtn.styled";
import {Line} from "../../styles/line/line.styled";
import styled from "styled-components";
import {palette} from "../../constants/Styles";

const ProfileArea = styled.div`
      width: 210px;
      display: flex;
      align-items: center;
    `;

const Username = styled.h2`
      margin-right: 20px;
      font-size: 18px;
      font-weight: 600;
    `;

const ApiName = styled.h2`
      margin-left: 20px;
      width: 510px;
      font-weight: 600;
      font-size: 20px;
    `;

const ApiCard: React.FC<{ item: ApiData }> = ({ item }) => {
  return (
      <S.ApiCard>
        <ProfileArea>
          <Profile src={TestProfileImg} alt={"ProfileImg"}/>
          <Username>{item.username}</Username>
        </ProfileArea>
        <Line/>
        <ApiName>{item.apiName}</ApiName>
        {!item.accessible &&
            <CommonBtn color={palette["--color-primary-100"]}
                       hover-color={palette["--color-primary-900"]}>신청 하기 ▶</CommonBtn>}
        {item.accessible &&
            <CommonBtn color={palette["--color-red-500"]}
                       hover-color={palette["--color-red-700"]}>접근 가능</CommonBtn>}
      </S.ApiCard>
  )
}

export default ApiCard;