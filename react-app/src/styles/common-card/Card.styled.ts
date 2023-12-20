import styled from "styled-components";
import {palette} from "../../constants/Styles";

export const CardWrapper = styled.div<{$w?: number}>`
      width: ${(props)=> props.$w || 900}px;
      margin: 0 auto;
    `;

export const Card = styled.div<{$h: number, $m: number, $d?: string}>`
      width: 100%;
      height: ${(props)=> props.$h}px;
      margin-bottom: ${(props)=> props.$m}px;
      border-radius: 30px;
      background-color: ${palette["--color-gray-100"]};
      display: flex;
      flex-direction: ${(props) => props.$d || "row"};
      justify-content: center;
      align-items: center;
      padding: 0 30px;
      box-shadow: 2px 2px 4px rgba(0, 0, 0, 0.15);
    `;