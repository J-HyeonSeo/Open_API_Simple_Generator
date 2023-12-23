import {Fragment, useRef, useState} from "react";
import Header from "../components/header/Header";
import * as S from "../styles/api-create/ApiCreate.styled";
import * as S2 from "../styles/common-card/Card.styled";
import {CommonBtn} from "../styles/control/CommonBtn.styled";
import {palette} from "../constants/Styles";
import {Line} from "../styles/line/line.styled";
import {CheckBox, CheckBoxLabel, CheckBoxWrapper} from "../styles/control/CheckBox.styled";
import TypeCardSetter from "../components/api-create/TypeCardSetter";


const ApiCreatePage = () => {
  const selectFile = useRef<HTMLInputElement>(null);
  const [fileName, setFileName] = useState("");

  const fileChangeHandler = () => {
    setFileName(selectFile.current?.files?.item(0)?.name || '');
  }

  return (
      <Fragment>
        <Header />
        <S.ApiCreateTitle>Open API 생성하기</S.ApiCreateTitle>
        <S2.CardWrapper $w={700}>
          <S2.CardTitle>API 이름</S2.CardTitle>
          <S.ApiCreateInput type={"input"} placeholder={"OpenAPI 이름을 입력해주세요."}/>
        </S2.CardWrapper>
        <S2.CardWrapper $w={700} $m={80}>
          <S2.CardTitle>API 설명</S2.CardTitle>
          <S.ApiCreateTextArea rows={6} placeholder={"OpenAPI 설명을 입력해주세요."}/>
        </S2.CardWrapper>
        <S2.CardWrapper $w={700} $m={80}>
          <S2.CardTitle>엑셀 파일 업로드</S2.CardTitle>
          <span>
            <input
                type="file"
                style={{ display: "none" }}
                ref={selectFile} //input에 접근 하기위해 useRef사용
                onChange={fileChangeHandler}
            />
            <CommonBtn
                $color={palette["--color-primary-100"]}
                $hover-color={palette["--color-primary-900"]}
                onClick={() => selectFile.current?.click()}
            >
              파일 선택
            </CommonBtn>
          </span>
          <Line $h={50} $m={20} $c={palette["--color-gray-900"]}/>
          <S.ApiCreateInput value={fileName} disabled={true} type={"input"} $w={300} placeholder={"파일을 선택해주세요."}/>
        </S2.CardWrapper>
        <S2.CardWrapper $w={700} $m={80}>
          <S2.CardTitle $noMargin={true}>자료형 지정 하기</S2.CardTitle>
          <TypeCardSetter isSchema={true} />
        </S2.CardWrapper>
        <S2.CardWrapper $w={700} $m={80}>
          <S2.CardTitle $noMargin={true}>검색 질의 인수 추가하기</S2.CardTitle>
          <TypeCardSetter isSchema={false} />
        </S2.CardWrapper>
        <S2.CardWrapper $w={700} $m={80}>
          <S2.CardTitle>공개 여부</S2.CardTitle>
          <CheckBoxWrapper>
            <CheckBox id={"isPublic"}/>
            <CheckBoxLabel $c={palette["--color-gray-500"]} $m={10} htmlFor={"isPublic"}>해당 OpenAPI에 대해 검색을 허용하고 신청요청을 받겠습니다.</CheckBoxLabel>
          </CheckBoxWrapper>
        </S2.CardWrapper>
        <S2.CardWrapper $w={700} $m={80} $isFlex={true} >
          <S2.Card $isNotShadow={true} $c={"white"} $p={120}>
            <CommonBtn $color={palette["--color-primary-100"]} $hover-color={palette["--color-primary-900"]}>
              생성 하기
            </CommonBtn>
            <CommonBtn $color={palette["--color-gray-500"]} $hover-color={palette["--color-gray-900"]}>
              취소 하기
            </CommonBtn>
          </S2.Card>
        </S2.CardWrapper>
      </Fragment>
  )
}

export default ApiCreatePage;