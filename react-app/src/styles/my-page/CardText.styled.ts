import styled from "styled-components";
import {palette} from "../../constants/Styles";

export const CardText = styled.h2`
      margin: 25px 0;
      font-size: 20px;
  
      &:hover {
        color: ${palette["--color-gray-500"]};
        cursor: pointer;
      }
    `;