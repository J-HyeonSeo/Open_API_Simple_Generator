import React, {Fragment, ReactNode} from "react";
import ReactDOM from "react-dom";
import * as S from "../../styles/modal/Modal.styled";
import InfoMark from "../../assets/modal-mark/info-alert-mark.png";
import QuestionMark from "../../assets/modal-mark/question-alert-mark.png";
import SuccessMark from "../../assets/modal-mark/success-alert-mark.png";
import ErrorMark from "../../assets/modal-mark/error-alert-mark.png";
import {Line} from "../../styles/line/line.styled";
import {palette} from "../../constants/Styles";


const portalElement = document.getElementById("overlays");

interface ModalProps {
  children?: ReactNode;
  mark?: string;
  title: string;
  closeHandler: () => void;
  w?: number;
  h?: number;
  isButton?: boolean;
  yesText?: string;
  yesCallback?: () => void;
  noText?: string;
  noCallback?: () => void;
  text?: string
}

const Modal: React.FC<ModalProps> = (props) => {

  let mark = InfoMark;

  //setting modal mark!
  switch(props.mark) {
    case "info":
      break;
    case "question":
      mark = QuestionMark;
      break;
    case "success":
      mark = SuccessMark;
      break;
    case "error":
      mark = ErrorMark;
      break;
  }

  return (
      <Fragment>
        {portalElement && ReactDOM.createPortal(<S.BackDrop onClick={props.closeHandler}/>, portalElement)}
        {portalElement && ReactDOM.createPortal(
            <S.ModalOverlay $w={props.w} $h={props.h}>
              <S.ModalTitleArea>
                <S.ModalMarkArea>
                  <S.ModalMark src={mark} alt={"mark"}/>
                </S.ModalMarkArea>
                <S.ModalTitle>{props.title}</S.ModalTitle>
                <S.ModalCloseBtn onClick={props.closeHandler}>X</S.ModalCloseBtn>
              </S.ModalTitleArea>
              <S.ModalContentArea $isButton={props.isButton}>
                {props.children}
                {props.text &&
                    <div style={{display: "flex", justifyContent: "center", alignItems: "center", height: "100%"}}>
                      <h2 style={{textAlign: "center"}}>{props.text}</h2>
                    </div>}
              </S.ModalContentArea>
              {props.isButton && 
                  <S.ModalButtonArea>
                    <S.ModalButtonWrapper onClick={props.yesCallback}>
                      <S.ModalButton>{props.yesText || "확인"}</S.ModalButton>
                    </S.ModalButtonWrapper>
                    <Line $c={palette["--color-gray-500"]} $h={40}/>
                    <S.ModalButtonWrapper onClick={props.noCallback || props.closeHandler}>
                      <S.ModalButton>{props.noText || "취소"}</S.ModalButton>
                    </S.ModalButtonWrapper>
                  </S.ModalButtonArea>}
            </S.ModalOverlay>
            , portalElement)}
      </Fragment>
  )
}

export default Modal;