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

  //communication state
  const {res: introRes, request: introRequest} = useAxios();
  const {res: requestRes, isError, setIsError, errorMessage, request: requestRequest, setRes } = useAxios();

  //data state
  const [introData, setIntroData] = useState<ApiIntroData>();
  const [isShowRequestModal, setIsShowRequestModal] = useState(false);

  //페이지 로딩시, 최초로 한 번 불러오기.
  useEffect(() => {
    introRequest(`/api/public/${id}`, "get");
  }, []);

  //페이지 데이터가 셋팅되었다면, 화면에 데이터 표시해주기.
  useEffect(() => {
    setIntroData(introRes?.data);
  }, [introRes]);

  //받아온 페이지 데이터에서, TypeCard를 표시해주기 위한 JSX를 생성.
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

  //사용예시 URL을 만들어주는 함수.
  const exampleUrlMaker = () => {
    if (introData === null) {
      return 'ERROR';
    }

    let url = `${BASE_URL}/query/${id}/{AUTHKEY}/{idx}/{size}?`;

    introData?.queryParameter.forEach((item, index) => {
      if (index === 0) {
        url += `${item.field}={${item.type}}`;
      } else {
        url += `&${item.field}={${item.type}}`;
      }
    });

    return url;
  }

  //OpenAPI 신청 요청을 보내는 함수
  const reuqestOpenApi = async () => {
    await requestRequest(`/api/request/${id}`, "post");
    setIsShowRequestModal(false);
  }

  return (
      <Fragment>
        <Header />
        <OwnerProfileArea
            profileUrl={introData?.profileUrl}
            nickname={introData?.ownerNickname}
            isShowBtn={true}
            isShowRequest={true}
            isManage={manageable === '1'}
            id={id}
            btnCallBack={() => setIsShowRequestModal(true)}/>
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
        {isShowRequestModal && <Modal
            mark={"question"}
            title={"OpenAPI 신청하기"}
            isButton={true}
            text={"해당 OpenAPI를 신청하시겠습니까?"}
            yesCallback={reuqestOpenApi}
            closeHandler={() => setIsShowRequestModal(false)}/>}
        {requestRes && <Modal
            mark={"success"}
            title={"OpenAPI 신청 성공"}
            isButton={true}
            text={"성공적으로 해당 OpenAPI를 신청하였습니다.\n관리자가 승인하면 이용이 가능합니다."}
            yesCallback={() => setRes(undefined)}
            closeHandler={() => setRes(undefined)}/>}
        {isError && <Modal
            mark={"error"}
            title={"OpenAPI 신청 실패"}
            isButton={true}
            text={errorMessage?.message || "해당 OpenAPI를 신청할 수 없습니다."}
            yesCallback={() => setIsError(false)}
            closeHandler={() => setIsError(false)}/>}
      </Fragment>
  )
}

export default ApiIntroducePage;