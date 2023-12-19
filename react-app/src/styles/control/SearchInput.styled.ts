import styled from "styled-components";
import { palette } from "../../constants/Styles";

export const SearchInputWrapper = styled.div`
      width: 900px;
      height: 75px;
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding-left: 50px;
      padding-right: 40px;
      background-color: ${palette["--color-gray-100"]};
      border-radius: 50px;
      margin: 70px auto;
    `;

export const SearchInput = styled.input`
      font: inherit;
      width: 750px;
      background: inherit;
      font-size: 1.3rem;
      font-weight: 400;
      border: none;
      outline: none;
    `;

export const MagnifierImg = styled.img`
      width: 35px;
      height: 38px;
    `;