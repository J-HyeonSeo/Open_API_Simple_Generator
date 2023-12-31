import * as S from "../../../styles/common-card/Card.styled";
import {Card} from "../../../styles/common-card/Card.styled";
import {palette} from "../../../constants/Styles";
import ProfileArea from "../../api-card/ProfileArea";
import TestProfileImg from "../../../assets/test-profile.png";
import {CommonBtn} from "../../../styles/control/CommonBtn.styled";
import React from "react";
import {IvReData} from "../../../constants/interfaces";

const ApiManageInviteCard: React.FC<{item: IvReData}> = ({item}) => {
  return (
      <S.CardWrapper $w={550} $m={10}>
        <Card $p={5} $r={10} $h={50} $c={palette["--color-gray-300"]}>
          <ProfileArea item={{profileImage: item.profileUrl, name: item.memberNickname}} isLine={true} w={300}/>
          <div>
            {item.requestStateType === "ASSIGN" && <CommonBtn
                $color={palette["--color-primary-100"]}
                $hover-color={palette["--color-primary-900"]}
                $w={90} $h={30} $m={5}>
              수락됨
            </CommonBtn>}
            {item.requestStateType === "REJECT" && <CommonBtn
                $color={palette["--color-red-500"]}
                $hover-color={palette["--color-red-700"]}
                $w={90} $h={30} $m={5}>
              거절됨
            </CommonBtn>}
            {item.requestStateType === "REQUEST" && <CommonBtn
                $color={palette["--color-gray-500"]}
                $hover-color={palette["--color-gray-700"]}
                $w={90} $h={30} $m={5}>
              대기중
            </CommonBtn>}
          </div>
        </Card>
      </S.CardWrapper>
  )
}

export default ApiManageInviteCard;