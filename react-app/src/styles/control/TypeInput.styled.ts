import styled from "styled-components";
import {palette} from "../../constants/Styles";

export const TypeInput = styled.input`
    font: inherit;
    font-size: 12px;
    font-weight: 600;
    padding: 5px;
    margin-right: 8px;
    height: 20px;
    max-width: 150px;
    border-radius: 5px;
    background-color: ${palette["--color-gray-500"]};
    border: 1px solid ${palette["--color-gray-500"]};
    outline-color: ${palette["--color-gray-500"]};
  
    &::placeholder {
      color: ${palette["--color-gray-300"]};
      font-weight: 400;
      font-size: 12px;
    }
    `;