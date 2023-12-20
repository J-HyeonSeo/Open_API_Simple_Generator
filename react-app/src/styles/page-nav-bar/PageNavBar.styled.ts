import styled from "styled-components";
import {palette} from "../../constants/Styles";

export const PageNavBar = styled.div`
      margin: 50px auto;
      display: flex;
      justify-content: center;
    `;

export const PageNavBtn = styled.button<{$isFocus?: boolean}>`
      padding: 10px;
      font: inherit;
      border: none;
      margin: 0 10px;
      border-radius: 30px;
      background-color: ${(props) => props.$isFocus ? palette["--color-gray-200"] : palette["--color-gray-100"]};
      
      &:hover {
          background-color: ${palette["--color-gray-200"]};
          cursor: pointer;
      }
    `;