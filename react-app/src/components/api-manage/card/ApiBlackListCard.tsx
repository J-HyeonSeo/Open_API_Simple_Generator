import * as S from "../../../styles/common-card/Card.styled";
import {Card} from "../../../styles/common-card/Card.styled";
import {palette} from "../../../constants/Styles";
import ProfileArea from "../../api-card/ProfileArea";
import TestProfileImg from "../../../assets/test-profile.png";
import {CommonBtn} from "../../../styles/control/CommonBtn.styled";
import React from "react";

const ApiBlackListCard = () => {
  return (
      <S.CardWrapper $w={550} $m={10}>
        <Card $p={5} $r={10} $h={50} $c={palette["--color-gray-300"]}>
          <ProfileArea item={{profileImage: TestProfileImg, name: "Adam Smith"}} isLine={true} w={300}/>
          <div>
            <CommonBtn $color={palette["--color-red-500"]} $hover-color={palette["--color-red-700"]} $w={90} $h={30} $m={5}>
              해제하기
            </CommonBtn>
          </div>
        </Card>
      </S.CardWrapper>
  )
}

export default ApiBlackListCard;