import styled from "styled-components";
import {palette} from "../../constants/Styles";

export const Header = styled.header`
      width: 900px;
      margin: 50px auto 0 auto;
      display: flex;
      align-items: center;
      justify-content: space-between;
    `;

export const ListStyle = styled.ul`
      display: flex;
      justify-content: space-between;
    `;

export const ListDetailStyle = styled.li`
    margin: 0 30px;
    font-size: 1.2rem;

    &:hover {
        cursor: pointer;
        color: ${palette["--color-gray-500"]};
    }

    &:last-child {
        margin-right: 0;
    }
`;

export const HeaderProfile = styled.div`
      position: absolute;
      display: flex;
      align-items: center;
      width: 200px;
      top: 60px;
      right: 1%;
      @media screen and (max-width: 1400px) {  
          display: none;
      }
    `;