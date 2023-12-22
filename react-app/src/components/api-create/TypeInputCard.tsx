import * as S from "../../styles/api-detail/TypeCard.styled";
import {Line} from "../../styles/line/line.styled";
import {palette} from "../../constants/Styles";
import React, {ChangeEvent, ChangeEventHandler, useState} from "react";
import {QUERY_TYPE_LIST, SCHEMA_TYPE_LIST} from "../../constants/Data";
import {TypeInput} from "../../styles/control/TypeInput.styled";

const TypeInputCard: React.FC<{
  index: string,
  endCallBack: (index: string, fieldName: string, type: string) => void,
  fieldNameParam: string,
  typeParam: string,
  isSchema: boolean }> = ({index, endCallBack, fieldNameParam, typeParam, isSchema}) => {
  const type_list = isSchema ? SCHEMA_TYPE_LIST : QUERY_TYPE_LIST;
  const [fieldName, setFieldName] = useState(fieldNameParam);

  const onChangeHandler = (e: ChangeEvent<HTMLInputElement>) => {
    setFieldName(e.target.value);
  }

  const setTypeHandler = (inputType: string) => {
    if (fieldName.trim() === '') {
      alert("필드명을 입력해주세요.");
      return;
    }
    endCallBack(index, fieldName, inputType);
  }

  return (
      <S.TypeCardWrapper $vm={10} $hm={0.1}>
        <TypeInput onChange={onChangeHandler} type={"input"} placeholder={"필드명을 입력해주세요."} value={fieldName}></TypeInput>
        <Line $h={18} $c={palette["--color-gray-500"]}/>
        {type_list.map((item) => (
            <S.Clickable onClick={() => setTypeHandler(item.display)} key={item.type}>
              <S.TypeElement $isHover={true} $top-color={item["top-color"]} $bottom-color={item["bottom-color"]}>
                {item.display}
              </S.TypeElement>
            </S.Clickable>
        ))}
      </S.TypeCardWrapper>
  )
}

export default TypeInputCard;