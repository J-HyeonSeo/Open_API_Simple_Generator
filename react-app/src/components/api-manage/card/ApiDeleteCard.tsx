import * as S from "../../../styles/common-card/Card.styled";
import {Card} from "../../../styles/common-card/Card.styled";
import {CommonBtn} from "../../../styles/control/CommonBtn.styled";
import {palette} from "../../../constants/Styles";
import useAxios from "../../../hooks/useAxios";
import Modal from "../../modal/Modal";
import React, {useState} from "react";
import {useNavigate} from "react-router-dom";

const ApiDeleteCard: React.FC<{id: string | undefined}> = ({id}) => {

  const navigate = useNavigate();

  const {res, isError, setIsError, errorMessage, request} = useAxios();
  const [isShowDeleteModal, setIsShowDeleteModal] = useState(false);

  const deleteRequest = async () => {
    await request(`/api/${id}`, "delete");
    setIsShowDeleteModal(false);
  }

  return (
      <S.CardWrapper $m={80}>
        <S.CardTitle>Open API 삭제</S.CardTitle>
        <Card $h={70}>
          <CommonBtn onClick={() => setIsShowDeleteModal(true)}
                     $color={palette["--color-red-500"]}
                     $hover-color={palette["--color-red-700"]}>API 삭제</CommonBtn>
        </Card>
        {isShowDeleteModal && <Modal
            title={"확인"}
            mark={"question"}
            isButton={true}
            yesCallback={() => deleteRequest()}
            text={"삭제하면 되돌릴 수 없습니다.\n정말로 해당 OpenAPI를 삭제하시겠습니까?"}
            closeHandler={() => setIsShowDeleteModal(false)}/>}
        {res && <Modal
            title={"확인"}
            mark={"success"}
            isButton={true}
            yesCallback={() => navigate("/api/owner")}
            text={"성공적으로 해당 OpenAPI를 삭제하였습니다!"}
            closeHandler={() => () => navigate("/api/owner")}/>}
        {isError && <Modal
            title={"확인"}
            mark={"error"}
            isButton={true}
            yesCallback={() => deleteRequest()}
            text={errorMessage?.message || "해당 OpenAPI를 삭제할 수 없습니다!"}
            closeHandler={() => setIsError(false)}/>}
      </S.CardWrapper>
  )
}

export default ApiDeleteCard;