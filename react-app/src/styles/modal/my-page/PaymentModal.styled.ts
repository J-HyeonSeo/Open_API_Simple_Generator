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
      height: 100px;
      border-radius: 20px;
      margin: 20px auto;
      background-color: ${palette["--color-gray-100"]};
      display: flex;
      justify-content: space-evenly;
      align-items: center;
    `;

export const GradeArea = styled.div`
      width: 160px;
      display: flex;
    `;

export const MetaDataOuterArea = styled.div`
      height: 100%;
      width: 250px;
      display: flex;
      flex-direction: column;
      justify-content: center;
    `;

export const MetaDataInnerArea = styled.div`
      display: flex;
      margin: 5px 0;
      align-items: center;
    `;

export const MetaDataFieldText = styled.div`
      margin: 0;
    `;

export const MetaDataValueText = styled.div`
      margin: 0;
      font-weight: 600;
    `;