import * as S from "../../../styles/common-card/Card.styled";
import * as S2 from "../../../styles/api-manage/ApiManage.styled";
import {Card} from "../../../styles/common-card/Card.styled";
import {Line} from "../../../styles/line/line.styled";
import {CommonBtn} from "../../../styles/control/CommonBtn.styled";
import {palette} from "../../../constants/Styles";

const ApiKeyCard = () => {
  return (
      <S.CardWrapper $m={80}>
        <S.CardTitle>API KEY 관리</S.CardTitle>
        <Card $h={70}>
          <S2.ApiKeyTextArea>
            <h3>API KEY</h3>
            <Line $h={30} $m={20}></Line>
            <p>19bf9a1a121c4c33a200bd257ccb412e</p>
          </S2.ApiKeyTextArea>
          <CommonBtn $color={palette["--color-primary-100"]} $hover-color={palette["--color-primary-900"]}>발급하기</CommonBtn>
        </Card>
      </S.CardWrapper>
  )
}

export default ApiKeyCard;