import * as S from "../../../styles/common-card/Card.styled";
import * as S2 from "../../../styles/control/CheckBox.styled";
import {Card} from "../../../styles/common-card/Card.styled";
import {palette} from "../../../constants/Styles";
import ProfileArea from "../../api-card/ProfileArea";
import TestProfileImg from "../../../assets/test-profile.png";
import {CommonBtn} from "../../../styles/control/CommonBtn.styled";
import React from "react";

const ApiPermissionCard = () => {
  return (
      <S.CardWrapper $w={550} $m={10}>
        <Card $p={5} $r={10} $h={50} $c={palette["--color-gray-300"]}>
          <ProfileArea item={{profileImage: TestProfileImg, name: "Adam Smith"}} isLine={true} w={250}/>
          <S2.CheckBoxWrapper>
            <S2.CheckBox type={"checkbox"} id={"add"} disabled={true}/>
            <S2.CheckBoxLabel htmlFor={"add"}>추가</S2.CheckBoxLabel>
            <S2.CheckBox type={"checkbox"} id={"update"}/>
            <S2.CheckBoxLabel htmlFor={"update"}>수정</S2.CheckBoxLabel>
            <S2.CheckBox type={"checkbox"} id={"delete"}/>
            <S2.CheckBoxLabel htmlFor={"delete"}>삭제</S2.CheckBoxLabel>
            <CommonBtn $color={palette["--color-red-500"]} $hover-color={palette["--color-red-700"]} $w={25} $h={25} $m={10}>
              X
            </CommonBtn>
          </S2.CheckBoxWrapper>
        </Card>
      </S.CardWrapper>
  )
}

export default ApiPermissionCard;