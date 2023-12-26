import * as S from "../../styles/common-card/Card.styled";
import {Card} from "../../styles/common-card/Card.styled";
import ApiManageRequestCard from "./card/ApiManageRequestCard";
import PageNavBar from "../page-nav-bar/PageNavBar";
import ApiManageInviteCard from "./card/ApiManageInviteCard";
import {CommonBtn} from "../../styles/control/CommonBtn.styled";
import {palette} from "../../constants/Styles";
import {useState} from "react";
import MemberSearchModal from "../modal/MemberSearchModal";

const ApiManageInviteCardArea = () => {
  const [isShowMemberSearchModal, setIsShowMemberSearchModal] = useState(false);

  const modalHandler = (value: boolean) => {
    setIsShowMemberSearchModal(value);
  }

  return (
      <S.CardWrapper $m={80}>
        <S.CardTitle>초대 관리</S.CardTitle>
        <Card $d={"column"} $notAround={true} $p={50}>
          <CommonBtn onClick={() => modalHandler(true)}
                     $color={palette["--color-primary-100"]}
                     $hover-color={palette["--color-primary-900"]}>
            + 추가
          </CommonBtn>
          <ApiManageInviteCard />
          <ApiManageInviteCard />
          <ApiManageInviteCard />
          <ApiManageInviteCard />
          <PageNavBar page={{total: 20, index: 2, displaySize: 4, navBarSize: 5}} margin={1}/>
        </Card>
        {isShowMemberSearchModal && <MemberSearchModal closeHandler={() => modalHandler(false)}/>}
      </S.CardWrapper>
  )
}

export default ApiManageInviteCardArea;