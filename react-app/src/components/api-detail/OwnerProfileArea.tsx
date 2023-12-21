import React from "react";
import {Profile} from "../../styles/profile/profile.styled";
import testProfile from "../../assets/test-profile.png";
import * as S from "../../styles/api-detail/ApiDetail.styled";
import {Line} from "../../styles/line/line.styled";
import {CommonBtn} from "../../styles/control/CommonBtn.styled";
import {palette} from "../../constants/Styles";

const OwnerProfileArea: React.FC<{isShowBtn?: boolean}> = ({isShowBtn}) => {
  return (
      <div>
        <S.OwnerProfileAreaWrapper>
          <S.OwnerProfileWrapper>
            <Profile src={testProfile}/>
            <Line $h={30} $m={10}/>
            <S.ProfileNameText>Adam Smith</S.ProfileNameText>
          </S.OwnerProfileWrapper>
          {isShowBtn && <CommonBtn $color={palette["--color-primary-100"]} $hover-color={palette["--color-primary-900"]}>
            신청 하기 ▶
          </CommonBtn>}
        </S.OwnerProfileAreaWrapper>
      </div>
  )
}

export default OwnerProfileArea;