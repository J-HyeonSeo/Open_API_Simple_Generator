import styled from "styled-components";
import {palette} from "../../constants/Styles";

export const ApiCardAreaWrapper = styled.div`
      width: 900px;
      margin: 0 auto;
    `;

export const ApiCardTitleArea = styled.div`
      display: flex;
      justify-content: space-between;
      margin-bottom: 30px;
    `;

export const ApiCard = styled.div`
      width: 100%;
      height: 85px;
      margin-bottom: 25px;
      border-radius: 30px;
      background-color: ${palette["--color-gray-100"]};
      display: flex;
      align-items: center;
      padding: 0 30px;
      box-shadow: 2px 2px 4px rgba(0, 0, 0, 0.15);
    `;