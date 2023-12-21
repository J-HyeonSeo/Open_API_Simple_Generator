import React from "react";
import * as S from "../../styles/api-detail/TypeCard.styled";
import {Line} from "../../styles/line/line.styled";
import {palette} from "../../constants/Styles";
import {TypeCardInfo} from "../../constants/interfaces";


const TypeCard: React.FC<{item: TypeCardInfo, vm?: number, hm?: number}> = ({item, vm, hm}) => {
  return (
    <S.TypeCardWrapper id={"1"} draggable={true} $vm={vm} $hm={hm}>
      <S.TypeCardTitle>{item.fieldName}</S.TypeCardTitle>
      <Line $h={18} $c={palette["--color-gray-500"]}/>
      <S.TypeElement $top-color={item["top-color"]} $bottom-color={item["bottom-color"]}>
        {item.typeString}
      </S.TypeElement>
    </S.TypeCardWrapper>
  )
}

export default TypeCard;