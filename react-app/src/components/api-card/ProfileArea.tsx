import {ProfileInfo} from "../../constants/interfaces";
import {Profile} from "../../styles/profile/profile.styled";
import React from "react";
import * as S from "../../styles/api-card/ProfileArea.styled";
import {Line} from "../../styles/line/line.styled";

const ProfileArea: React.FC<{item: ProfileInfo, isLine?:
      boolean, w?: number, isEmail?: boolean}> = ({item, isLine, w, isEmail}) => {
  return (
      <S.ProfileAreaWrapper $w={w}>
        <Profile src={item.profileImage} alt={"ProfileImg"}/>
        {isLine && <Line $m={15} $h={30}/>}
        <S.Username>{item.name}</S.Username>
        {isEmail && isLine && <Line $m={15} $h={30}/>}
        {isEmail && <S.Username>{item.email}</S.Username>}
      </S.ProfileAreaWrapper>
  )
}

export default ProfileArea;