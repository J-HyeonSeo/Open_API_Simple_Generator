import React from "react";
import * as S from "../../styles/api-detail/TypeCard.styled";
import {Line} from "../../styles/line/line.styled";
import {palette} from "../../constants/Styles";
import {TypeCardInfo} from "../../constants/interfaces";
import {CommonBtn} from "../../styles/control/CommonBtn.styled";


const TypeCard: React.FC<{item: TypeCardInfo,vm?: number, hm?: number, index?: string,
  onDragStart?: (index: string) => void,
  onDragEnter?: (index: string) => void,
  onDragEnd?: () => void,
  enterChange?: (index: string) => void}> = (
    {item,
      vm,
      hm,
      index,
      onDragStart,
      onDragEnter,
      onDragEnd,
      enterChange}) => {
  return (
    <S.TypeCardWrapper
        draggable={true}
        onDragStart={() => onDragStart && index && onDragStart(index)}
        onDragEnter={() => onDragEnter && index && onDragEnter(index)}
        onDragEnd={onDragEnd}
        onDragOver={(e) => e.preventDefault()}
        onDoubleClick={() => enterChange && index && enterChange(index)}
        $vm={vm}
        $hm={hm}
    >
      <S.TypeCardTitle>{item.fieldName}</S.TypeCardTitle>
      <Line $h={18} $c={palette["--color-gray-500"]}/>
      <S.TypeElement $top-color={item["top-color"]} $bottom-color={item["bottom-color"]}>
        {item.typeString}
      </S.TypeElement>
    </S.TypeCardWrapper>
  )
}

export default TypeCard;