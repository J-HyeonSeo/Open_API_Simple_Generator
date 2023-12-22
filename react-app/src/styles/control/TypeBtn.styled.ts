import styled from "styled-components";
import fieldAddBtn from "../../assets/add-btn.png";
import fieldHoverAddBtn from "../../assets/hover-add-btn.png";
import fieldSubBtn from "../../assets/sub-btn.png";
import fieldHoverSubBtn from "../../assets/hover-sub-btn.png";
export const TypeAddBtn = styled.div`
      background-image: url(${fieldAddBtn});
      background-size: 100%;
      width: 38px;
      height: 38px;
      margin-left: 10px;
      &:hover {
          background-image: url(${fieldHoverAddBtn});
          cursor: pointer;
      }
    `;

export const TypeSubBtn = styled.div`
      background-image: url(${fieldSubBtn});
      background-size: 100%;
      width: 20px;
      height: 20px;
      margin-left: 10px;
      &:hover {
          background-image: url(${fieldHoverSubBtn});
          cursor: pointer;
      }
    `;