import styled from "styled-components";
import {palette} from "../../constants/Styles";

export const CardWrapper = styled.div<{$w?: number, $m?: number, $isFlex?: boolean}>`
      width: ${(props)=> props.$w || 900}px;
      display: ${(props) => props.$isFlex ? "flex" : "block"};
      margin: ${(props) => props.$m || 0}px auto;
      align-items: center;
    `;

export const InnerCardWrapper = styled.div<{$w: number}>`
      width: ${(props) => props.$w}px;
      overflow: auto;
    `;

export const Card = styled.div<{
    $h?: number, $m?: number, $p?: number, $d?: string, $c?: string,
    $isWrap?: boolean, $notAround?: boolean, $r?: number, $isNotShadow?: boolean,
    $isLeft?: boolean}>`
      width: 100%;
      min-height: 50px;
      height: ${(props)=> props.$h || "auto"}px;
      margin-bottom: ${(props)=> props.$m || 0}px;
      border-radius: ${(props) => props.$r || 30}px;
      background-color: ${(props) => props.$c || palette["--color-gray-100"]};
      display: flex;
      flex-direction: ${(props) => props.$d || "row"};
      justify-content: ${(props) => props.$notAround ? "normal" : "space-around"};
      align-items: ${(props) => props.$isLeft ? "flex-start" : "center"};;
      flex-wrap: ${(props) => props.$isWrap ? "wrap" : "no-wrap"};
      padding: ${(props) => !props.$h ? 15 : 0}px ${(props) => props.$p || 30}px;
      box-shadow: ${(props) => props.$isNotShadow ? "" : "2px 2px 4px rgba(0, 0, 0, 0.15)"};
    `;

export const ScrollCard = styled.div<{
    $h?: number, $m?: number, $p?: number, $d?: string, $c?: string,
    $isWrap?: boolean, $notAround?: boolean, $r?: number, $isNotShadow?: boolean,
    $isLeft?: boolean}>`
      width: 100%;
      min-height: 50px;
      height: ${(props)=> props.$h || "auto"}px;
      margin-bottom: ${(props)=> props.$m || 0}px;
      border-radius: ${(props) => props.$r || 30}px;
      background-color: ${(props) => props.$c || palette["--color-gray-100"]};
      display: flex;
      flex-direction: ${(props) => props.$d || "row"};
      justify-content: ${(props) => props.$notAround ? "normal" : "space-around"};
      align-items: ${(props) => props.$isLeft ? "flex-start" : "center"};;
      flex-wrap: ${(props) => props.$isWrap ? "wrap" : "no-wrap"};
      padding: ${(props) => !props.$h ? 15 : 0}px ${(props) => props.$p || 30}px;
      box-shadow: ${(props) => props.$isNotShadow ? "" : "2px 2px 4px rgba(0, 0, 0, 0.15)"};
      overflow: auto;
    `;

export const CardTitle = styled.h2<{$noMargin?: boolean}>`
      font-weight: 600;
      margin: ${(props) => props.$noMargin ? 0 : '' };
    `;