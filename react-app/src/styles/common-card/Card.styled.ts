import styled from "styled-components";
import {palette} from "../../constants/Styles";

export const CardWrapper = styled.div<{$w?: number, $m?: number, $isFlex?: boolean}>`
      width: ${(props)=> props.$w || 900}px;
      display: ${(props) => props.$isFlex ? "flex" : "block"};
      margin: ${(props) => props.$m || 0}px auto;
    `;

export const InnerCardWrapper = styled.div<{$w: number}>`
      width: ${(props) => props.$w}px;
    `;

export const Card = styled.div<{
    $h: number, $m?: number, $p?: number, $d?: string, $c?: string}>`
      width: 100%;
      height: ${(props)=> props.$h}px;
      margin-bottom: ${(props)=> props.$m || 0}px;
      border-radius: 30px;
      background-color: ${(props) => props.$c || palette["--color-gray-100"]};
      display: flex;
      flex-direction: ${(props) => props.$d || "row"};
      justify-content: space-around;
      align-items: center;
      padding: 0 ${(props) => props.$p || 30}px;
      box-shadow: 2px 2px 4px rgba(0, 0, 0, 0.15);
    `;