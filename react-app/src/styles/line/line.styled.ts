import styled from "styled-components";
import {palette} from "../../constants/Styles";

export const Line = styled.span<{
        $h?: number, $fullHeight?: boolean, $c?: string, $m?: number}>`
      width: 1px;
      margin: 0 ${(props) => props.$m || 0}px;
      height: ${(props) => props.$fullHeight ? "90%" : props.$h + "px" || "50px"};
      background-color: ${(props) => props.$c || palette["--color-gray-900"]};
    `;