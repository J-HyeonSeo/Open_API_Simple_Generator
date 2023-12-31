import * as S from "../../../styles/common-card/Card.styled";
import {Card} from "../../../styles/common-card/Card.styled";
import {palette} from "../../../constants/Styles";
import ProfileArea from "../../api-card/ProfileArea";
import {CommonBtn} from "../../../styles/control/CommonBtn.styled";
import React, {useState} from "react";
import {BlackListData} from "../../../constants/interfaces";
import useAxios from "../../../hooks/useAxios";
import Modal from "../../modal/Modal";

const ApiBlackListCard: React.FC<{item: BlackListData, callback: () => void}> = ({item, callback}) => {

  const {res, request, isError, errorMessage, setIsError, setRes} = useAxios();
  const [isShowUnregisterModal, setIsShowUnregisterModal] = useState(false);

  const unregisterBlackList = () => {
    request(`/api/blacklist/${item.id}`, "delete");
    setIsShowUnregisterModal(false);
  }

  const successHandler = () => {
    setRes(undefined);
    callback();
  }

  return (
      <S.CardWrapper $w={550} $m={10}>
        <Card $p={5} $r={10} $h={50} $c={palette["--color-gray-300"]}>
          <ProfileArea item={{profileImage: item.profileUrl, name: item.memberNickname}} isLine={true} w={300}/>
          <div>
            <CommonBtn
                onClick={() => setIsShowUnregisterModal(true)}
                $color={palette["--color-red-500"]}
                $hover-color={palette["--color-red-700"]} $w={90} $h={30} $m={5}>
              해제하기
            </CommonBtn>
          </div>
        </Card>
        {isShowUnregisterModal && <Modal
            title={"블랙리스트 해제"}
            mark={"question"}
            text={"해당 회원을 블랙리스트에 해제하시겠습니까?"}
            isButton={true}
            yesCallback={unregisterBlackList}
            closeHandler={() => setIsShowUnregisterModal(false)}/>}
        {res && <Modal
            title={"성공"}
            mark={"success"}
            text={"성공적으로 해제되었습니다."}
            isButton={true}
            yesCallback={successHandler}
            closeHandler={successHandler}/>}
        {isError && <Modal
            title={"실패"}
            mark={"error"}
            text={errorMessage?.message || "해제할 수 없는 대상입니다."}
            isButton={true}
            yesCallback={() => setIsError(false)}
            closeHandler={() => setIsError(false)}/>}
      </S.CardWrapper>
  )
}

export default ApiBlackListCard;