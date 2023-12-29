import {ApiData} from "../../constants/interfaces";
import React from "react";
import {CommonBtn} from "../../styles/control/CommonBtn.styled";
import {Line} from "../../styles/line/line.styled";
import styled from "styled-components";
import {palette} from "../../constants/Styles";
import {Card} from "../../styles/common-card/Card.styled";
import ProfileArea from "./ProfileArea";
import TestProfileImg from "../../assets/test-profile.png";

const ApiName = styled.h2`
      margin-left: 20px;
      width: 510px;
      font-weight: 600;
      font-size: 20px;
    `;

const ApiCard: React.FC<{ item: ApiData }> = ({ item }) => {
  return (
      <Card $h={85} $m={25}>
        <ProfileArea item={{profileImage: item.profileUrl, name: item.ownerNickname}}/>
        <Line $h={30} $c={palette["--color-gray-500"]}/>
        <ApiName>{item.apiName}</ApiName>
        {!item.accessible &&
            <CommonBtn $color={palette["--color-primary-100"]}
                       $hover-color={palette["--color-primary-900"]}>신청 하기 ▶</CommonBtn>}
        {item.accessible && item.apiState === "ENABLED" &&
            <CommonBtn $color={palette["--color-red-500"]}
                       $hover-color={palette["--color-red-700"]}>접근 가능</CommonBtn>}
        {item.accessible && item.apiState === "FAILED" &&
            <CommonBtn $color={palette["--color-gray-500"]}
                       $hover-color={palette["--color-gray-700"]}>업로드 실패</CommonBtn>}
      </Card>
  )
}

export default ApiCard;