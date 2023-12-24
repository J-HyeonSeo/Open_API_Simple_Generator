import styled from "styled-components";
import {palette} from "../../constants/Styles";

export const ModalInput = styled.input<{$w: number}>`
      font: inherit;
      font-size: 20px;
      font-weight: 600;
      padding: 20px;
      width: ${(props) => props.$w}px;
      height: 50px;
      border-radius: 10px;
      border: 1px solid ${palette["--color-gray-300"]};
      outline-color: ${palette["--color-gray-500"]};
    
      &::placeholder {
        color: ${palette["--color-gray-300"]};
        font-weight: 400;
        font-size: 18px;
      }
    `;