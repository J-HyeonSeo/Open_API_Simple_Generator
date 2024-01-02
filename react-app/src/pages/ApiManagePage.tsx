import {Fragment, useEffect, useState} from "react";
import Header from "../components/header/Header";
import OwnerProfileArea from "../components/api-detail/OwnerProfileArea";
import * as S from "../styles/api-detail/ApiDetail.styled";
import ApiKeyCard from "../components/api-manage/card/ApiKeyCard";
import ApiManageRequestCardArea from "../components/api-manage/ApiManageRequestCardArea";
import ApiManageInviteCardArea from "../components/api-manage/ApiManageInviteCardArea";
import ApiPermissionCardArea from "../components/api-manage/ApiPermissionCardArea";
import ApiBlackListCardArea from "../components/api-manage/ApiBlackListCardArea";
import ApiDataCard from "../components/api-manage/card/ApiDataCard";
import ApiHistoryCardArea from "../components/api-manage/ApiHistoryCardArea";
import ApiDeleteCard from "../components/api-manage/card/ApiDeleteCard";
import useAxios from "../hooks/useAxios";
import {ApiIntroData} from "../constants/interfaces";
import {useParams} from "react-router-dom";
import {useRecoilState} from "recoil";
import {profileData} from "../store/RecoilState";
import ApiEnableCard from "../components/api-manage/card/ApiEnableCard";
const ApiManagePage = () => {

  const id = useParams().id;
  const {res, request} = useAxios();
  const [introData, setIntroData] = useState<ApiIntroData>();
  const [profile, _] = useRecoilState(profileData);

  useEffect(() => {
    request(`/api/public/${id}`, "get");
  }, []);

  useEffect(() => {
    setIntroData(res?.data);
  }, [res]);

  return (
      <Fragment>
        <Header />
        <OwnerProfileArea profileUrl={introData?.profileUrl}
                          isShowBtn={true}
                          isUpdate={profile?.memberId === introData?.ownerMemberId}
                          apiName={introData?.apiName}
                          apiIntroduce={introData?.apiIntroduce}
                          isPublic={introData?.public}
                          id={id}
                          nickname={introData?.ownerNickname}/>
        <S.TitleWrapper>
          <h2>{introData?.apiName}</h2>
        </S.TitleWrapper>
        <ApiKeyCard/>
        {introData?.ownerMemberId === profile?.memberId && <ApiManageRequestCardArea/>}
        {introData?.ownerMemberId === profile?.memberId && <ApiManageInviteCardArea />}
        {introData?.ownerMemberId === profile?.memberId && <ApiPermissionCardArea />}
        {introData?.ownerMemberId === profile?.memberId && <ApiBlackListCardArea />}
        <ApiDataCard item={introData}/>
        {introData?.ownerMemberId === profile?.memberId && <ApiHistoryCardArea introData={introData} />}
        {introData?.ownerMemberId === profile?.memberId && <ApiDeleteCard />}
        {introData?.ownerMemberId === profile?.memberId && introData?.apiState === "DISABLED" && <ApiEnableCard />}
      </Fragment>
  )
}

export default ApiManagePage;