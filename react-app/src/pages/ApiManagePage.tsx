import {Fragment} from "react";
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
const ApiManagePage = () => {
  return (
      <Fragment>
        <Header />
        <OwnerProfileArea />
        <S.TitleWrapper>
          <h2>2020 ~ 2023년도 경제 시장 분석 데이터 API</h2>
        </S.TitleWrapper>
        <ApiKeyCard/>
        <ApiManageRequestCardArea />
        <ApiManageInviteCardArea />
        <ApiPermissionCardArea />
        <ApiBlackListCardArea />
        <ApiDataCard />
        <ApiHistoryCardArea />
      </Fragment>
  )
}

export default ApiManagePage;