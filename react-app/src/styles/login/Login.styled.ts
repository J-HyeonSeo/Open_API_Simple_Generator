import styled from "styled-components";
import {palette} from "../../constants/Styles";

export const PageWrapper = styled.div`
      margin: 100px auto;
      width: 700px;
      text-align: center;
    `;

export const MainLogo = styled.img`
      width: 560px;
      height: 171px;
      
      &:hover {
        cursor: pointer;
      }
    `;

export const LoginWrapper = styled.div`
      margin: 100px auto;
      width: 560px;
      height: 150px;
      border-radius: 10px;
      background-color: ${palette["--color-gray-100"]};
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: center;
      box-shadow: 2px 2px 4px rgba(0, 0, 0, 0.1);
    `;

export const LoginButton = styled.img`
      width: 500px;
      height: 75px;
      box-shadow: 2px 2px 4px rgba(0, 0, 0, 0.1);
      border-radius: 10px;
      &:hover {
        cursor: pointer;
      }
    `;

export const CustomParagraph = styled.p`
      color: ${palette["--color-gray-300"]};
      font-weight: 600;
    `;