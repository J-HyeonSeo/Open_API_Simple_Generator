import styled from "styled-components";
import {palette} from "../../constants/Styles";

export const ApiCreateTitle = styled.h1`
      text-align: center;
      font-weight: 600;
      font-size: 27px;
      margin: 100px 0;
    `;

export const ApiCreateInput = styled.input<{$w?: number}>`
      font: inherit;
      font-size: 20px;
      font-weight: 600;
      padding: 20px;
      width: ${(props) => props.$w || 700}px;
      height: 50px;
      border-radius: 10px;
      border: 1px solid ${palette["--color-gray-300"]};
      outline-color: ${palette["--color-gray-500"]};
      
      &::placeholder {
          color: ${palette["--color-gray-300"]};
          font-weight: 400;
          font-size: 18px;
      }
    `;

export const ApiCreateTextArea = styled.textarea`
      font: inherit;
      font-size: 20px;
      font-weight: 600;
      padding: 20px;
      width: 700px;
      resize: vertical;
      border-radius: 10px;
      border: 1px solid ${palette["--color-gray-300"]};
      outline-color: ${palette["--color-gray-500"]};
      
      &::placeholder {
          color: ${palette["--color-gray-300"]};
          font-weight: 400;
          font-size: 18px;
      }
    `;