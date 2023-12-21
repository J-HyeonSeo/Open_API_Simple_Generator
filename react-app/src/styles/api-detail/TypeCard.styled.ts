import styled from "styled-components";
import {palette} from "../../constants/Styles";

export const TypeCardWrapper = styled.div<{$vm?: number, $hm?: number}>`
      padding: 5px 10px;
      border-radius: 5px;
      margin: ${(props) => props.$vm || 5}px ${(props) => props.$hm || 5}px;
      display: flex;
      align-items: center;
      background-color: ${palette["--color-gray-300"]};
    `;

export const TypeCardTitle = styled.h5`
      margin: 0;
      margin-right: 8px;
    `;

export const TypeElement = styled.div<{
    '$top-color': string, '$bottom-color': string
}>`
      border-radius: 40px;
      padding: 1px 8px;
      color: white;
      font-weight: 400;
      font-size: 12px;
      margin-left: 8px;
      background: linear-gradient(to bottom, ${(props) => props['$top-color']}, ${(props) => props['$bottom-color']});
    `;