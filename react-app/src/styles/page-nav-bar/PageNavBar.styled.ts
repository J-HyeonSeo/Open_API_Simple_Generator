import styled from "styled-components";
import {palette} from "../../constants/Styles";

export const PageNavBar = styled.div<{$m?: number}>`
      margin: ${(props) => props.$m || 50}px auto;
      display: flex;
      justify-content: center;
    `;

export const PageNavBtn = styled.button<{$isFocus?: boolean}>`
      padding: 5px 10px;
      font: inherit;
      font-weight: 600;
      border: none;
      margin: 0 10px;
      border-radius: 5px;
      background-color: ${(props) => props.$isFocus ? palette["--color-gray-300"] : palette["--color-gray-100"]};
      box-shadow: 1px 1px 1px rgba(0, 0, 0, 0.1);  
    
      &:hover {
          background-color: ${palette["--color-gray-300"]};
          cursor: pointer;
      }
    `;