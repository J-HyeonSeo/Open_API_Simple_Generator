import styled from "styled-components";
import {palette} from "../../constants/Styles";

export const ProfileWrapper = styled.div`
      margin-top: 50px;
      margin-bottom: 30px;
      display: flex;
      flex-direction: column;
      align-items: center;
    `;

export const EmailText = styled.h3`
      font-weight: 400;
      color: ${palette["--color-gray-700"]};
    `;

export const NicknameTextWrapper = styled.div`
      margin: 10px 0;
    `;

export const NicknameText = styled.h2`
      margin: 5px 0;
      text-align: center;
      font-weight: 600;
    `;


export const GradeMargin = styled.div`
      margin: 20px 0;
    `;