import {Fragment, JSX, useEffect, useState} from "react";
import Header from "../components/header/Header";
import OwnerProfileArea from "../components/api-detail/OwnerProfileArea";
import * as S from "../styles/api-detail/ApiDetail.styled";
import * as S2 from "../styles/common-card/Card.styled";
import {Card} from "../styles/common-card/Card.styled";
import TypeCard from "../components/api-detail/TypeCard";
import {ApiIntroData, FieldAndType, TypeCardInfo, TypeData} from "../constants/interfaces";
import {QUERY_TYPE_LIST, SCHEMA_TYPE_LIST} from "../constants/Data";
import Modal from "../components/modal/Modal";
import useAxios from "../hooks/useAxios";
import {useParams} from "react-router-dom";
import {BASE_URL} from "../utils/CustomAxios";

const ApiIntroducePage = () => {

  const params = useParams();
  const id = params.id;
  const manageable = params.manageable;
  const {res, isError, errorMessage, request} = useAxios();
  const [introData, setIntroData] = useState<ApiIntroData>();
  const [isShowRequestModal, setIsShowRequestModal] = useState(false);
  const [isShowErrorModal, setIsShowErrorModal] = useState(false);

  useEffect(() => {
    request(`/api/public/${id}`, "get");
  }, []);

  useEffect(() => {
    setIntroData(res?.data);
  }, [res]);

  const modalHandler = (type: string, value: boolean) => {
    switch (type) {
      case "request":
        setIsShowRequestModal(value);
        break;
      case "error":
        setIsShowErrorModal(value);
        break;
    }
  }
  
  const createTypeCardInfoArr = (arr: Array<FieldAndType>, typeArr: Array<TypeData>) => {
    const typeCards: Array<JSX.Element> = [];
    
    arr.forEach((item) => {
      //타입을 찾아와야 함.
      const index = typeArr.findIndex((x) => {return x.type === item.type});
      const typeData = typeArr[index];

      //타입 카드 정보 생성
      const typeCardInfo: TypeCardInfo = {
        fieldName: item.field,
        typeString: typeData.display,
        "top-color": typeData["top-color"],
        "bottom-color": typeData["bottom-color"]
      }

      //JSX 추가
      typeCards.push(
          <TypeCard item={typeCardInfo}/>
      );
    });

    return typeCards;
  }

  const exampleUrlMaker = () => {
    if (introData === null) {
      return 'ERROR';
    }

    let url = `${BASE_URL}/query/${id}/{AUTHKEY}/{idx}/{size}?`;

    introData?.schemaStructure.forEach((item, index) => {
      if (index === 0) {
        url += `${item.field}={${item.type}}`;
      } else {
        url += `&${item.field}={${item.type}}`;
      }
    });

    return url;
  }

  return (
      <Fragment>
        <Header />
        <OwnerProfileArea
            profileUrl={introData?.profileUrl}
            nickname={introData?.ownerNickname}
            isShowBtn={true}
            isManage={manageable === '1'}
            id={id}
            btnCallBack={() => modalHandler("request", true)}/>
        <S.TitleWrapper>
          <h2>{introData?.apiName}</h2>
        </S.TitleWrapper>
        <S2.CardWrapper $m={80}>
          <h2>소개</h2>
          <Card>
            <p>{introData?.apiIntroduce}</p>
          </Card>
        </S2.CardWrapper>
        <S2.CardWrapper $m={80}>
          <h2>데이터 필드</h2>
          <Card $isWrap={true} $notAround={true}>
            {createTypeCardInfoArr(introData?.schemaStructure || [], SCHEMA_TYPE_LIST)}
          </Card>
        </S2.CardWrapper>
        <S2.CardWrapper $m={80}>
          <h2>검색시 가능한 질의 인수</h2>
          <Card $isWrap={true} $notAround={true}>
            {createTypeCardInfoArr(introData?.queryParameter || [], QUERY_TYPE_LIST)}
          </Card>
        </S2.CardWrapper>
        <S2.CardWrapper $m={80}>
          <h2>사용 예시</h2>
          <Card>
            <p>{exampleUrlMaker()}</p>
          </Card>
        </S2.CardWrapper>
        {isShowRequestModal && <Modal mark={"question"}
               title={"OpenAPI 신청하기"}
               isButton={true}
               text={"해당 OpenAPI를 신청하시겠습니까?"}
               closeHandler={() => modalHandler("request", false)}/>}
      </Fragment>
  )
}

export default ApiIntroducePage;