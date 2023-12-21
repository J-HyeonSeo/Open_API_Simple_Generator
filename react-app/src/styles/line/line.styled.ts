import styled from "styled-components";
import {palette} from "../../constants/Styles";

export const Line = styled.span<{
        $h?: number, $c?: string, $m?: number}>`
      width: 1px;
      margin: 0 ${(props) => props.$m || 0}px;
      height: ${(props) => props.$h || 50}px;
      background-color: ${(props) => props.$c || palette["--color-gray-900"]};
    `;