import * as S from "../../../styles/common-card/Card.styled";
import {Card} from "../../../styles/common-card/Card.styled";
import {CommonBtn} from "../../../styles/control/CommonBtn.styled";
import {palette} from "../../../constants/Styles";
import React, {useState} from "react";
import ApiDataModal from "../../modal/ApiDataModal";
import {ApiIntroData} from "../../../constants/interfaces";

const ApiDataCard: React.FC<{item: ApiIntroData | undefined}> = ({item}) => {
  const [isShowDataModal, setIsShowDataModal] = useState(false);
  return (
      <S.CardWrapper $m={80}>
        <S.CardTitle>데이터 관리</S.CardTitle>
        <Card $h={70}>
          <CommonBtn onClick={() => setIsShowDataModal(true)}
              $color={palette["--color-primary-100"]}
              $hover-color={palette["--color-primary-900"]}>데이터 관리</CommonBtn>
        </Card>
        {isShowDataModal && <ApiDataModal introData={item} callback={() => setIsShowDataModal(false)}/>}
      </S.CardWrapper>
  )
}

export default ApiDataCard;