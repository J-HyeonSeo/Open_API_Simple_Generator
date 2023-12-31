import * as S from "../../../styles/common-card/Card.styled";
import * as S2 from "../../../styles/control/CheckBox.styled";
import {Card} from "../../../styles/common-card/Card.styled";
import {palette} from "../../../constants/Styles";
import ProfileArea from "../../api-card/ProfileArea";
import TestProfileImg from "../../../assets/test-profile.png";
import {CommonBtn} from "../../../styles/control/CommonBtn.styled";
import React, {useEffect, useState} from "react";
import {PermissionData} from "../../../constants/interfaces";
import useAxios from "../../../hooks/useAxios";
import Modal from "../../modal/Modal";

const ApiPermissionCard: React.FC<{item: PermissionData, callback: () => void}> = ({item, callback}) => {

  //modal
  const [isShowDeletePermissionModal, setIsShowDeletePermissionModal] = useState(false);

  const insertChecked =
      item.permissionList.some(item => item.type === "INSERT");
  const updateChecked =
      item.permissionList.some(item => item.type === "UPDATE");
  const deleteChecked =
      item.permissionList.some(item => item.type === "DELETE");

  const insertId = item.permissionList.find(item => item.type === "INSERT")?.id;
  const updateId = item.permissionList.find(item => item.type === "UPDATE")?.id;
  const deleteId = item.permissionList.find(item => item.type === "DELETE")?.id;

  const {request: deleteRequest, res: deleteRes,
    setRes, isError,
    setIsError, errorMessage} = useAxios();

  const {request: modifyRequest, res: modifyRes} = useAxios();

  const modifyPermission = (isAdd: boolean, type: string, detailId?: number) => {
    if (type === "DELETE_PERMISSION" && !isAdd) {
        deleteRequest(`/api/permission/${item.permissionId}`, "delete");
    } else {
      if (isAdd) {
        modifyRequest(`/api/permission/add/${item.permissionId}?type=${type}`, "put");
      } else {
        modifyRequest(`/api/permission/sub/${detailId}`, "put");
      }
    }
    setIsShowDeletePermissionModal(false);
  }

  const checkedHandler = (type: string, detailId?: number) => {
    switch (type) {
      case "INSERT":
        modifyPermission(!insertChecked, "INSERT", detailId);
        break;
      case "UPDATE":
        modifyPermission(!updateChecked, "UPDATE", detailId);
        break;
      case "DELETE":
        modifyPermission(!deleteChecked, "DELETE", detailId);
        break;
    }
  }

  const deleteSuccessHandler = () => {
    setRes(undefined);
    callback();
  }

  useEffect(() => {
    callback();
  }, [modifyRes]);


  return (
      <S.CardWrapper $w={550} $m={10}>
        <Card $p={5} $r={10} $h={50} $c={palette["--color-gray-300"]}>
          <ProfileArea item={{profileImage: item.profileUrl, name: item.memberNickname}} isLine={true} w={250}/>
          <S2.CheckBoxWrapper>
            {<S2.CheckBox
                onChange={() => checkedHandler("INSERT", insertId)}
                checked={insertChecked} type={"checkbox"} id={`add${item.permissionId}`}/>}
            <S2.CheckBoxLabel htmlFor={`add${item.permissionId}`}>추가</S2.CheckBoxLabel>
            <S2.CheckBox
                onChange={() => checkedHandler("UPDATE", updateId)}
                checked={updateChecked} type={"checkbox"} id={`update${item.permissionId}`}/>
            <S2.CheckBoxLabel htmlFor={`update${item.permissionId}`}>수정</S2.CheckBoxLabel>
            <S2.CheckBox
                onChange={() => checkedHandler("DELETE", deleteId)}
                checked={deleteChecked} type={"checkbox"} id={`delete${item.permissionId}`}/>
            <S2.CheckBoxLabel htmlFor={`delete${item.permissionId}`}>삭제</S2.CheckBoxLabel>
            <CommonBtn
                onClick={() => setIsShowDeletePermissionModal(true)}
                $color={palette["--color-red-500"]}
                $hover-color={palette["--color-red-700"]}
                $w={25} $h={25} $m={10}>
              X
            </CommonBtn>
          </S2.CheckBoxWrapper>
        </Card>
        {isShowDeletePermissionModal && <Modal
            title={"확인"}
            mark={"question"}
            text={"해당 유저의 접근권한을 삭제하시겠습니까?"}
            isButton={true}
            yesCallback={() => modifyPermission(false, "DELETE_PERMISSION")}
            closeHandler={() => setIsShowDeletePermissionModal(false)} />}
        {deleteRes && <Modal
            title={"성공"}
            mark={"success"}
            text={"해당 유저의 접근권한을 삭제하였습니다."}
            isButton={true}
            yesCallback={deleteSuccessHandler}
            closeHandler={deleteSuccessHandler} />}
        {isError && <Modal
            title={"실패"}
            mark={"error"}
            text={errorMessage?.message || "해당 유저의 접근권한을 삭제하지못했습니다."}
            isButton={true}
            yesCallback={() => setIsError(false)}
            closeHandler={() => setIsError(false)} />}
      </S.CardWrapper>
  )
}

export default ApiPermissionCard;