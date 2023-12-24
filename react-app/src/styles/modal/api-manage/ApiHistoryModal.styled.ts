import styled from "styled-components";
import {palette} from "../../../constants/Styles";

export const FieldArea = styled.div`
        max-width: 120px;
    `;

export const ValueArea = styled.div`
        max-width: 185px;
    `;

export const FieldText = styled.h4`
      margin: 13px 0;
      font-weight: 600;
      overflow: hidden;
      white-space: nowrap;
      text-overflow: ellipsis;
    `;

export const ReadInput = styled.input`
      font: inherit;
      margin: 6px 0;
      font-weight: 600;
      border-radius: 5px;
      width: 185px;
      height: 25px;
      border: none;
      background-color: ${palette["--color-gray-400"]};
    `;