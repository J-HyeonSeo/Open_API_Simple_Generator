import {Fragment} from "react";
import Header from "../components/header/Header";
import OwnerProfileArea from "../components/api-detail/OwnerProfileArea";
import * as S from "../styles/api-detail/ApiDetail.styled";
import * as S2 from "../styles/common-card/Card.styled";
import {Card} from "../styles/common-card/Card.styled";
import TypeCard from "../components/api-detail/TypeCard";
import {TypeCardInfo} from "../constants/interfaces";
import {SCHEMA_TYPE_LIST} from "../constants/Data";

const ApiIntroducePage = () => {

  const mockFieldData: Array<TypeCardInfo> = [
    {
      fieldName: '연도',
      typeString: SCHEMA_TYPE_LIST[1].display,
      'top-color': SCHEMA_TYPE_LIST[1]["top-color"],
      'bottom-color': SCHEMA_TYPE_LIST[1]["bottom-color"],
    },
    {
      fieldName: '업종',
      typeString: SCHEMA_TYPE_LIST[0].display,
      'top-color': SCHEMA_TYPE_LIST[0]["top-color"],
      'bottom-color': SCHEMA_TYPE_LIST[0]["bottom-color"],
    },
    {
      fieldName: '신설기업갯수',
      typeString: SCHEMA_TYPE_LIST[1].display,
      'top-color': SCHEMA_TYPE_LIST[1]["top-color"],
      'bottom-color': SCHEMA_TYPE_LIST[1]["bottom-color"],
    },
    {
      fieldName: '경제성장률',
      typeString: SCHEMA_TYPE_LIST[2].display,
      'top-color': SCHEMA_TYPE_LIST[2]["top-color"],
      'bottom-color': SCHEMA_TYPE_LIST[2]["bottom-color"],
    },
    {
      fieldName: '자금순환금액',
      typeString: SCHEMA_TYPE_LIST[2].display,
      'top-color': SCHEMA_TYPE_LIST[2]["top-color"],
      'bottom-color': SCHEMA_TYPE_LIST[2]["bottom-color"],
    },
  ];

  return (
      <Fragment>
        <Header />
        <OwnerProfileArea isShowBtn={true}/>
        <S.TitleWrapper>
          <h2>2020 ~ 2023년도 경제 시장 분석 데이터 API</h2>
        </S.TitleWrapper>
        <S2.CardWrapper $m={80}>
          <h2>소개</h2>
          <Card>
            <p>Mr. Adam Smith가 2020 ~ 2023 년도의 경제 시장 데이터를 분석하였습니다. 조선업, 건축업, 반도체업, 자동차업, 농업 등
              총 5가지의 업종에 대해서 매 년도 마다 경제 시장에 어떤 영향을 주는지 확인해볼 수 있는 데이터를 API로 제공하고 있습니다.</p>
          </Card>
        </S2.CardWrapper>
        <S2.CardWrapper $m={80}>
          <h2>데이터 필드</h2>
          <Card $isWrap={true} $notAround={true}>
            {mockFieldData.map((item) => (
              <TypeCard item={item}/>
            ))}
          </Card>
        </S2.CardWrapper>
        <S2.CardWrapper $m={80}>
          <h2>검색시 가능한 질의 인수</h2>
          <Card $isWrap={true} $notAround={true}>
            {mockFieldData.map((item) => (
                <TypeCard item={item}/>
            ))}
          </Card>
        </S2.CardWrapper>
        <S2.CardWrapper $m={80}>
          <h2>사용 예시</h2>
          <Card>
            <p>{"/query/1/{AUTHKEY}/0/10?연도={INTEGER}&업종={STRING}&자금순환금액={FLOAT}"}</p>
          </Card>
        </S2.CardWrapper>
      </Fragment>
  )
}

export default ApiIntroducePage;