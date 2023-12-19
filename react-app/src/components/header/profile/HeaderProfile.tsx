import {Profile} from "../../../styles/profile/profile.styled";
import testProfile from "../../../assets/test-profile.png";
import * as S from "../../../styles/header/Header.styled";
import {SmallBtn} from "../../../styles/control/SmallBtn.styled";
import styled from "styled-components";

const ProfileHeader = styled.h3`
      font-weight: 600;
      font-size: 16px;
      margin: 0;
    `;

const HeaderProfile = () => {
  return (
      <S.HeaderProfile>
        <Profile src={testProfile} alt={"profileImage"}/>
        <div>
          <ProfileHeader>Adam Smith</ProfileHeader>
          <SmallBtn>로그아웃</SmallBtn>
        </div>
      </S.HeaderProfile>
  )
}

export default HeaderProfile;