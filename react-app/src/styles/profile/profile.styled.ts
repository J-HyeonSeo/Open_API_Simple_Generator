import styled from "styled-components";
import {palette} from "../../constants/Styles";

export const Profile = styled.img<{$size?: number}>`
      width: ${(props) => props.$size || 45}px;
      height: ${(props) => props.$size || 45}px;
      margin-right: 8px;
      border-radius: ${(props) => props.$size || 45}px;
      border: solid 2px ${palette["--color-gray-200"]};
    `;