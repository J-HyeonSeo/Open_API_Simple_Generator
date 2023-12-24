import styled from "styled-components";
import {palette} from "../../../constants/Styles";

export const ContentWrapper = styled.div`
      width: 100%;
      height: 100%;
      flex-direction: column;
      align-items: center;
      padding: 30px;
      overflow: auto;
    `;

export const Content = styled.div`
      width: 650px;
      height: 130px;
      border-radius: 20px;
      margin: 20px auto;
      background-color: ${palette["--color-gray-100"]};
      display: flex;
      justify-content: space-evenly;
      align-items: center;
    `;

export const DataOuterArea = styled.div`
      width: 400px;
      height: 100%;
      display: flex;
      justify-content: center;
      flex-direction: column;
    `;

export const DataInnerArea = styled.div`
      width: 100%;
    `;

export const Title = styled.h2`
      font-size: 18px;
      font-weight: 600;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    `;

export const BottomArea = styled.div`
      display: flex;
      justify-content: space-between;
      align-items: center;
    `;

export const ButtonArea = styled.div`
      height: 100%;
      width: 150px;
      display: flex;
      flex-direction: column;
      justify-content: space-evenly;
      align-items: end;
    `;