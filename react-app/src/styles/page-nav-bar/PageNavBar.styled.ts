import styled from "styled-components";
import {palette} from "../../constants/Styles";

export const PageNavBar = styled.div<{$m?: number}>`
      margin: ${(props) => props.$m || 50}px auto;
      display: flex;
      justify-content: center;
    `;

export const PageNavBtn = styled.button<{$isFocus?: boolean}>`
      padding: 10px;
      font: inherit;
      border: none;
      margin: 0 10px;
      border-radius: 30px;
      background-color: ${(props) => props.$isFocus ? palette["--color-gray-500"] : palette["--color-gray-100"]};
      
      &:hover {
          background-color: ${palette["--color-gray-500"]};
          cursor: pointer;
      }
    `;