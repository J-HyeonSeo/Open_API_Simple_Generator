import * as S from "../../styles/common-card/Card.styled";
import {Card} from "../../styles/common-card/Card.styled";
import PageNavBar from "../page-nav-bar/PageNavBar";
import ApiPermissionCard from "./card/ApiPermissionCard";

const ApiPermissionCardArea = () => {
  return (
      <S.CardWrapper $m={80}>
        <h2>권한 관리</h2>
        <Card $d={"column"} $notAround={true} $p={50}>
          <ApiPermissionCard />
          <ApiPermissionCard />
          <ApiPermissionCard />
          <ApiPermissionCard />
          <PageNavBar page={{total: 20, index: 2, displaySize: 4, navBarSize: 5}} margin={1}/>
        </Card>
      </S.CardWrapper>
  )
}

export default ApiPermissionCardArea;