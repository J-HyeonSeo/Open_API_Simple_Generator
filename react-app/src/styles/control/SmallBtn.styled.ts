import styled from "styled-components";
import {palette} from "../../constants/Styles";

export const SmallBtn = styled.button`
      font: inherit;
      color: white;
      font-size: 12px;
      width: 65px;
      border-radius: 20px;
      border: none;
      background-color: ${palette["--color-primary-900"]};

      &:hover {
          background-color: ${palette["--color-primary-100"]};
          cursor: pointer;
      }
    `;