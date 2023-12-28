import styled from "styled-components";
import {palette} from "../../constants/Styles";

export const BackDrop = styled.div`
      position: fixed;
      top: 0;
      left: 0;
      width: 100%;
      height: 100vh;
      z-index: 20;
      background-color: rgba(0, 0, 0, 0.5);
    `;

export const ModalOverlay = styled.div<{$w?: number, $h?: number}>`
      position: fixed;
      left: 0;
      right: 0;
      top: 0;
      bottom: 0;
      margin: auto auto;
      width: ${(props) => props.$w || 600}px;
      height: ${(props) => props.$h || 400}px;
      background-color: white;
      border-radius: 10px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.25);
      z-index: 30;

    animation: fadeInUp 0.3s;
    @keyframes fadeInUp {
        0% {
            opacity: 0;
            transform: translate3d(0, 100%, 0);
        }
        to {
            opacity: 1;
            transform: translateZ(0);
        }
    }
    `;

export const ModalTitleArea = styled.div`
      width: 100%;
      height: 70px;
      border-radius: 10px 10px 0 0;
      background-color: ${palette["--color-gray-300"]};
      display: flex;
      align-items: center;
      justify-content: space-between;
    `;

export const ModalMarkArea = styled.div`
      width: 20%;
    `;

export const ModalMark = styled.img`
      margin-left: 15px;
    `;

export const ModalTitle = styled.h2`
      width: 80%;
      text-align: center;
    `;

export const ModalCloseBtn = styled.h2`
      width: 20%;
      text-align: right;
      padding-right: 15px;
    
      &:hover {
          color: ${palette["--color-gray-500"]};
          cursor: pointer;
      }
    `;

export const ModalContentArea = styled.div<{$isButton?: boolean}>`
      width: 100%;
      height: calc(100% - ${(props) => props.$isButton ? 140 : 70}px);
      overflow: hidden;
    `;

export const ModalButtonArea = styled.div`
      width: 100%;
      height: 70px;
      display: flex;
      align-items: center;
      justify-content: space-around;
    `;

export const ModalButtonWrapper = styled.div`
      width: 50%;
      height: 100%;
      border-radius: 10px;
      
      &:hover {
          background-color: ${palette["--color-gray-300"]};
          cursor: pointer;
      }
    `;

export const ModalButton = styled.h2`
      text-align: center;
    `;

