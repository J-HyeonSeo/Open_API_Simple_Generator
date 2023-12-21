import * as S from "../../styles/common-card/Card.styled";
import {Card} from "../../styles/common-card/Card.styled";
import ApiManageRequestCard from "./card/ApiManageRequestCard";
import PageNavBar from "../page-nav-bar/PageNavBar";

const ApiManageRequestCardArea = () => {
  return (
      <S.CardWrapper $m={80}>
        <h2>신청 관리</h2>
        <Card $d={"column"} $notAround={true} $p={50}>
          <ApiManageRequestCard />
          <ApiManageRequestCard />
          <ApiManageRequestCard />
          <ApiManageRequestCard />
          <PageNavBar page={{total: 20, index: 2, displaySize: 4, navBarSize: 5}} margin={1}/>
        </Card>
      </S.CardWrapper>
  )
}

export default ApiManageRequestCardArea;