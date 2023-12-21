import * as S from "../../../styles/common-card/Card.styled";
import {Card} from "../../../styles/common-card/Card.styled";
import {CommonBtn} from "../../../styles/control/CommonBtn.styled";
import {palette} from "../../../constants/Styles";

const ApiDataCard = () => {
  return (
      <S.CardWrapper $m={80}>
        <h2>데이터 관리</h2>
        <Card $h={70}>
          <CommonBtn $color={palette["--color-primary-100"]} $hover-color={palette["--color-primary-900"]}>데이터 관리</CommonBtn>
        </Card>
      </S.CardWrapper>
  )
}

export default ApiDataCard;