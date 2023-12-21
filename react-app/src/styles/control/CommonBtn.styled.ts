import styled from "styled-components";

export const CommonBtn = styled.button<{
    $color: string, "$hover-color": string, $w?: number, $h?: number, $m?: number}>`
      font: inherit;
      font-weight: 600;
      font-size: 15px;
      width: ${(props) => props.$w || 100}px;
      height: ${(props) => props.$h || 35}px;
      border-radius: 10px;
      margin: 0 ${(props) => props.$m || 0}px;
      border: none;
      color: white;
      background-color: ${(props) => props.$color};
      
      &:hover {
          background-color: ${(props) => props["$hover-color"]};
          cursor: pointer;
      }
    `;