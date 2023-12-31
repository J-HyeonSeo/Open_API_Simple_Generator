import React from "react";
import {Profile} from "../../styles/profile/profile.styled";
import testProfile from "../../assets/test-profile.png";
import * as S from "../../styles/api-detail/ApiDetail.styled";
import {Line} from "../../styles/line/line.styled";
import {CommonBtn} from "../../styles/control/CommonBtn.styled";
import {palette} from "../../constants/Styles";
import {useNavigate} from "react-router-dom";

const OwnerProfileArea: React.FC<{
  profileUrl?: string,
  nickname?: string,
  isShowBtn?: boolean,
  isShowRequest?: boolean,
  isManage?: boolean,
  isUpdate?: boolean,
  apiName?: string,
  apiIntroduce?: string,
  isPublic?: boolean,
  id?: string,
  btnCallBack?: () => void}> = (
      {profileUrl,
          nickname,
          isShowBtn,
          isShowRequest,
          isManage,
          isUpdate,
          apiName,
          apiIntroduce,
          isPublic,
          id,
          btnCallBack}) => {

  const navigate = useNavigate();
  return (
      <div>
        <S.OwnerProfileAreaWrapper>
          <S.OwnerProfileWrapper>
            <Profile src={profileUrl}/>
            <Line $h={30} $m={10}/>
            <S.ProfileNameText>{nickname}</S.ProfileNameText>
          </S.OwnerProfileWrapper>
          {isShowBtn && isShowRequest && !isManage && !isUpdate && <CommonBtn
              $color={palette["--color-primary-100"]}
              $hover-color={palette["--color-primary-900"]}
              onClick={btnCallBack}>
            신청 하기 ▶
          </CommonBtn>}
          {isShowBtn && isManage && <CommonBtn
              $color={palette["--color-gray-500"]}
              $hover-color={palette["--color-gray-700"]}
              onClick={() => navigate(`/api/manage/${id}`)}>
            관리 하기 ▶
          </CommonBtn>}
          {isShowBtn && isUpdate && <CommonBtn
              $color={palette["--color-gray-500"]}
              $hover-color={palette["--color-gray-700"]}
              onClick={() => navigate(`/api/update/${id}`, {state: {
                  apiName: apiName,
                  apiIntroduce: apiIntroduce,
                  isPublic: isPublic
                }})}>
            수정 하기 ▶
          </CommonBtn>}
        </S.OwnerProfileAreaWrapper>
      </div>
  )
}

export default OwnerProfileArea;