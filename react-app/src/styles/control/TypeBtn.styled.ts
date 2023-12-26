import styled from "styled-components";
import fieldAddBtn from "../../assets/button/add-btn.png";
import fieldHoverAddBtn from "../../assets/button/hover/hover-add-btn.png";
import fieldSubBtn from "../../assets/button/sub-btn.png";
import fieldHoverSubBtn from "../../assets/button/hover/hover-sub-btn.png";
export const TypeAddBtn = styled.div<{$size?: number}>`
      background-image: url(${fieldAddBtn});
      background-size: 100%;
      width: ${(props) => props.$size || 38}px;
      height: ${(props) => props.$size || 38}px;
      margin-left: 10px;
      &:hover {
          background-image: url(${fieldHoverAddBtn});
          cursor: pointer;
      }
    `;

export const TypeSubBtn = styled.div<{$m?: number}>`
      background-image: url(${fieldSubBtn});
      background-size: 100%;
      width: 20px;
      height: 20px;
      margin-left: ${(props) => props.$m || 10}px;
      &:hover {
          background-image: url(${fieldHoverSubBtn});
          cursor: pointer;
      }
    `;