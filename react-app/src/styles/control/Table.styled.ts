import styled from "styled-components";
import {palette} from "../../constants/Styles";
import SubBtn from "../../assets/button/sub-btn.png";
import OkBtn from "../../assets/button/ok-btn.png";
import HoverSubBtn from "../../assets/button/hover/hover-sub-btn.png";
import HoverOkBtn from "../../assets/button/hover/hover-ok-btn.png";

export const ContentWrapper = styled.div`
        width: 1100px;
        display: flex;
        margin: 25px auto 0 auto;
        justify-content: center;
        align-items: end;
    `;

export const TableWrapper = styled.div`
      width: 1000px;
      border-radius: 5px;
      overflow: auto;
    `;

export const Table = styled.table`
      width: 100%;
      font-weight: 600;
      border-collapse: collapse;
      border: 1px solid ${palette["--color-gray-300"]};
      background-color: ${palette["--color-gray-300"]};
      border-radius: 10px;
      overflow: hidden;
    `;

export const TableBody = styled.tbody`
      font-weight: 400;
      background-color: ${palette["--color-gray-100"]};
    `;

export const TableRow = styled.tr`
      white-space: nowrap;
      &:hover {
        background-color: ${palette["--color-gray-300"]};
      }
    `;

export const TableData = styled.td`
      border: 1px solid ${palette["--color-gray-300"]};
      border-right: none;
      height: 35px;
      padding: 5px;
    
      &:hover {
          background-color: ${palette["--color-gray-500"]};
          cursor: pointer;
      }

      &:hover:last-child {
          background-color: ${palette["--color-gray-100"]};
          cursor: auto;
      }
      
      &:last-child {
          border-left: none;
          width: 0;
      }
    `;

export const TableInput = styled.input`
      font: inherit;
      width: 100%;
    `;

export const OuterSubBtn = styled.div<{$m?: number}>`
      position: absolute;
      right: 70px;
      background-image: url(${SubBtn});
      background-size: 100%;
      width: 20px;
      height: 20px;
      margin-left: ${(props) => props.$m || 10}px;
      &:hover {
          background-image: url(${HoverSubBtn});
          cursor: pointer;
      }
    `;

export const OuterOkBtn = styled.div<{$m?: number}>`
      position: absolute;
      right: 70px;
      background-image: url(${OkBtn});
      background-size: 100%;
      width: 20px;
      height: 20px;
      margin-left: ${(props) => props.$m || 10}px;
      &:hover {
          background-image: url(${HoverOkBtn});
          cursor: pointer;
      }
    `;

export const GhostTableData = styled.td`
      padding: 5px;
      height: 35px;
    `;