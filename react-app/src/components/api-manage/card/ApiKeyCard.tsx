import * as S from "../../../styles/common-card/Card.styled";
import * as S2 from "../../../styles/api-manage/ApiManage.styled";
import {Card} from "../../../styles/common-card/Card.styled";
import {Line} from "../../../styles/line/line.styled";
import {CommonBtn} from "../../../styles/control/CommonBtn.styled";
import {palette} from "../../../constants/Styles";
import useAxios from "../../../hooks/useAxios";
import {useEffect, useState} from "react";
import {useParams} from "react-router-dom";
import {AuthKeyData} from "../../../constants/interfaces";
import Modal from "../../modal/Modal";

const ApiKeyCard = () => {

  const id = useParams().id;

  //modal
  const [isShowCreateModal, setIsShowCreateModal] = useState(false);
  const [isShowRefreshModal, setIsShowRefreshModal] = useState(false);

  //communication
  const {res: getRes, request: getRequest} = useAxios();
  const {res: createRes, setRes: setCreateRes, request: createRequest, isError: createIsError,
    setIsError: setCreateIsError, errorMessage: createErrorMessage} = useAxios();

  //authKey Data
  const [authKey, setAuthKey] = useState<AuthKeyData>();

  const getAuthKey = () => {
    getRequest(`/api/permission/authkey/${id}`, "get");
  }

  const createAuthKey = (isRefresh: boolean) => {
    if (isRefresh) {
      createRequest(`/api/permission/authkey/${id}`, "put");
    } else {
      createRequest(`/api/permission/authkey/${id}`, "post");
    }
    setIsShowCreateModal(false);
    setIsShowRefreshModal(false);
  }

  //effects
  useEffect(() => {
    getAuthKey();
  }, []);

  useEffect(() => {
    if (getRes === undefined) {
      return;
    }
    setAuthKey(getRes.data);
  }, [getRes]);

  useEffect(() => {
    if (createRes === undefined) {
      return;
    }
    setAuthKey(createRes.data);
  }, [createRes]);

  return (
      <S.CardWrapper $m={80}>
        <S.CardTitle>API KEY 관리</S.CardTitle>
        <Card $h={70}>
          <S2.ApiKeyTextArea>
            <h3>API KEY</h3>
            <Line $h={30} $m={20}></Line>
            <p>{authKey?.authKey || "Open API를 사용하기 위해서 API KEY를 발급해주세요!"}</p>
          </S2.ApiKeyTextArea>
          {!authKey && <CommonBtn
              onClick={() => setIsShowCreateModal(true)}
              $color={palette["--color-primary-100"]}
              $hover-color={palette["--color-primary-900"]}>발급하기</CommonBtn>}
          {authKey && <CommonBtn
              onClick={() => setIsShowRefreshModal(true)}
              $color={palette["--color-primary-100"]}
              $hover-color={palette["--color-primary-900"]}>재발급하기</CommonBtn>}
          {isShowCreateModal && <Modal
              title={"API KEY 발급"}
              mark={"question"}
              isButton={true}
              text={"API KEY를 발급 받으시겠습니까?"}
              yesCallback={() => createAuthKey(false)}
              closeHandler={() => setIsShowCreateModal(false)} />}
          {isShowRefreshModal && <Modal
              title={"API KEY 재발급"}
              mark={"question"}
              isButton={true}
              text={"기존에 API KEY가 이미 있습니다.\nAPI KEY를 재발급 받으시겠습니까?"}
              yesCallback={() => createAuthKey(true)}
              closeHandler={() => setIsShowRefreshModal(false)} />}
          {createRes && <Modal
              title={"성공"}
              mark={"success"}
              isButton={true}
              text={"성공적으로 API KEY를 발급하였습니다."}
              yesCallback={() => setCreateRes(undefined)}
              closeHandler={() => setCreateRes(undefined)} />}
          {createIsError && <Modal
              title={"실패"}
              mark={"error"}
              isButton={true}
              text={createErrorMessage?.message || "API KEY를 발급하지못했습니다."}
              yesCallback={() => setCreateIsError(false)}
              closeHandler={() => setCreateIsError(false)} />}
        </Card>
      </S.CardWrapper>
  )
}

export default ApiKeyCard;