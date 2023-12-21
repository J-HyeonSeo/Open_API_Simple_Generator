import * as S from "../../../styles/common-card/Card.styled";
import {Card} from "../../../styles/common-card/Card.styled";
import {palette} from "../../../constants/Styles";
import TestProfileImg from "../../../assets/test-profile.png";
import ProfileArea from "../../api-card/ProfileArea";
import React from "react";
import {CommonBtn} from "../../../styles/control/CommonBtn.styled";

const ApiManageRequestCard = () => {
  return (
    <S.CardWrapper $w={550} $m={10}>
      <Card $p={5} $r={10} $h={50} $c={palette["--color-gray-300"]}>
        <ProfileArea item={{profileImage: TestProfileImg, name: "Adam Smith"}} isLine={true}/>
        <div>
          <CommonBtn $color={palette["--color-primary-100"]} $hover-color={palette["--color-primary-900"]} $w={90} $h={30} $m={5}>
            수락하기
          </CommonBtn>
          <CommonBtn $color={palette["--color-red-500"]} $hover-color={palette["--color-red-700"]} $w={90} $h={30}>
            거절하기
          </CommonBtn>
        </div>
      </Card>
    </S.CardWrapper>
  )
}

export default ApiManageRequestCard;