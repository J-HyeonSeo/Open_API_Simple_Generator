import styled from "styled-components";

export const CommonBtn = styled.button<{$color: string, "$hover-color": string}>`
      font: inherit;
      font-weight: 600;
      font-size: 15px;
      width: 100px;
      height: 35px;
      border-radius: 10px;
      border: none;
      color: white;
      background-color: ${(props) => props.$color};
      
      &:hover {
          background-color: ${(props) => props["$hover-color"]};
          cursor: pointer;
      }
    `;