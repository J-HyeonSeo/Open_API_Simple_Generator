import * as S from "../../styles/common-card/Card.styled";
import {Card} from "../../styles/common-card/Card.styled";
import {CommonBtn} from "../../styles/control/CommonBtn.styled";
import {palette} from "../../constants/Styles";
import ApiManageInviteCard from "./card/ApiManageInviteCard";
import PageNavBar from "../page-nav-bar/PageNavBar";
import ApiBlackListCard from "./card/ApiBlackListCard";

const ApiBlackListCardArea = () => {
  return (
      <S.CardWrapper $m={80}>
        <S.CardTitle>블랙리스트 관리</S.CardTitle>
        <Card $d={"column"} $notAround={true} $p={50}>
          <CommonBtn $color={palette["--color-primary-100"]} $hover-color={palette["--color-primary-900"]}>
            + 추가
          </CommonBtn>
          <ApiBlackListCard />
          <ApiBlackListCard />
          <ApiBlackListCard />
          <ApiBlackListCard />
          <PageNavBar page={{total: 20, index: 2, displaySize: 4, navBarSize: 5}} margin={1}/>
        </Card>
      </S.CardWrapper>
  )
}

export default ApiBlackListCardArea;