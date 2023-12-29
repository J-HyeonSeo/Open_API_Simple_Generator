import React, {Fragment, useEffect, useRef, useState} from "react";
import Header from "../components/header/Header";
import * as S from "../styles/api-create/ApiCreate.styled";
import * as S2 from "../styles/common-card/Card.styled";
import {CommonBtn} from "../styles/control/CommonBtn.styled";
import {palette} from "../constants/Styles";
import {Line} from "../styles/line/line.styled";
import {CheckBox, CheckBoxLabel, CheckBoxWrapper} from "../styles/control/CheckBox.styled";
import TypeCardSetter from "../components/api-create/TypeCardSetter";
import {useRecoilState} from "recoil";
import {profileData, tokenData} from "../store/RecoilState";
import {useNavigate} from "react-router-dom";
import Modal from "../components/modal/Modal";
import {FieldAndType} from "../constants/interfaces";
import useAxios from "../hooks/useAxios";


const ApiCreatePage = () => {
  const navigate = useNavigate();
  const [profile, _] = useRecoilState(profileData);

  const {res, isError, setIsError, errorMessage, request} = useAxios();

  //for display states..
  const [isShowErrorModal, setIsShowErrorModal] = useState(false);
  const [fileName, setFileName] = useState("");

  //for upload form-data states..
  const [apiName, setApiName] = useState("");
  const [apiIntro, setApiIntro] = useState("");
  const selectFile = useRef<HTMLInputElement>(null);
  const [schemaStructure, setSchemaStructure] = useState(Array<FieldAndType>);
  const [queryParameter, setQueryParameter] = useState(Array<FieldAndType>);
  const [isPublic, setIsPublic] = useState(false);


  useEffect(() => {
    if (profile === null) {
      setIsShowErrorModal(true);
    } else {
      setIsShowErrorModal(false);
    }
  }, [profile]);

  const fileChangeHandler = () => {
    setFileName(selectFile.current?.files?.item(0)?.name || '');
  }

  const uploadHandler = async () => {
    const formData = new FormData();
    formData.append("apiName", apiName);
    formData.append("apiIntroduce", apiIntro);
    const file = selectFile.current?.files?.item(0);
    if (file) {
      formData.append("file", file);
    }
    schemaStructure.forEach((item, index) => {
      formData.append(`schemaStructure[${index}].field`, item?.field);
      formData.append(`schemaStructure[${index}].type`, item?.type);
    });
    queryParameter.forEach((item, index) => {
      formData.append(`queryParameter[${index}].field`, item?.field);
      formData.append(`queryParameter[${index}].type`, item?.type);
    });
    formData.append("isPublic", isPublic + '');

    await request("/api", "post", formData);
  }

  return (
      <Fragment>
        <Header />
        <S.ApiCreateTitle>Open API 생성하기</S.ApiCreateTitle>
        <S2.CardWrapper $w={700}>
          <S2.CardTitle>API 이름</S2.CardTitle>
          <S.ApiCreateInput
              onChange={(e) => setApiName(e.target.value)}
              type={"input"} placeholder={"OpenAPI 이름을 입력해주세요."}/>
        </S2.CardWrapper>
        <S2.CardWrapper $w={700} $m={80}>
          <S2.CardTitle>API 설명</S2.CardTitle>
          <S.ApiCreateTextArea
              onChange={(e) => setApiIntro(e.target.value)}
              rows={6} placeholder={"OpenAPI 설명을 입력해주세요."}/>
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
          <TypeCardSetter isSchema={true} setFieldAndType={setSchemaStructure}/>
        </S2.CardWrapper>
        <S2.CardWrapper $w={700} $m={80}>
          <S2.CardTitle $noMargin={true}>검색 질의 인수 추가하기</S2.CardTitle>
          <TypeCardSetter isSchema={false} setFieldAndType={setQueryParameter}/>
        </S2.CardWrapper>
        <S2.CardWrapper $w={700} $m={80}>
          <S2.CardTitle>공개 여부</S2.CardTitle>
          <CheckBoxWrapper>
            <CheckBox id={"isPublic"} onChange={(e) => setIsPublic(e.target.checked)}/>
            <CheckBoxLabel $c={palette["--color-gray-500"]} $m={10} htmlFor={"isPublic"}>해당 OpenAPI에 대해 검색을 허용하고 신청요청을 받겠습니다.</CheckBoxLabel>
          </CheckBoxWrapper>
        </S2.CardWrapper>
        <S2.CardWrapper $w={700} $m={80} $isFlex={true} >
          <S2.Card $isNotShadow={true} $c={"white"} $p={120}>
            <CommonBtn
                onClick={uploadHandler}
                $color={palette["--color-primary-100"]}
                $hover-color={palette["--color-primary-900"]}>
              생성 하기
            </CommonBtn>
            <CommonBtn
                onClick={() => navigate("/")}
                $color={palette["--color-gray-500"]}
                $hover-color={palette["--color-gray-900"]}>
              취소 하기
            </CommonBtn>
          </S2.Card>
        </S2.CardWrapper>
        {isShowErrorModal && <Modal
            title={"접근 불가"}
            mark={"error"}
            text={"로그인이 필요한 서비스입니다."}
            yesText={"로그인"}
            yesCallback={() => navigate("/login/0")}
            noCallback={() => navigate("/")}
            isButton={true}
            closeHandler={() => setIsShowErrorModal(false)}/>}
        {isError && <Modal
            title={"오류"}
            mark={"error"}
            text={errorMessage?.message || "업로드 중에 오류가 발생하였습니다."}
            isButton={true}
            yesCallback={() => setIsError(false)}
            closeHandler={() => setIsError(false)}/>}
        {res && <Modal
            isButton={true}
            mark={"success"}
            title={"성공"}
            text={"서버에 OpenAPI를 업로드하였습니다. \n 일정 시간이 지나면 활성화 상태를 확인할 수 있습니다."}
            yesCallback={() => navigate("/api/owner")}
            closeHandler={() => navigate("/api/owner")}/>}
      </Fragment>
  )
}

export default ApiCreatePage;