import styled from "styled-components";


export const ProfileAreaWrapper = styled.div<{$w?: number}>`
      width: ${(props) => props.$w || 210}px;
      display: flex;
      align-items: center;
    `;

export const Username = styled.h2<{$size?: number}>`
      margin-left: 4px;
      font-size: ${(props) => props.$size || 18}px;
      font-weight: 600;
    `;

export const HistoryWrapper = styled.div`
      display: flex;
      flex-direction: column;
      justify-content: center;
      width: 300px;
    `

export const HistoryTimeWrapper = styled.div`
      width: 100%;
      display: flex;
      align-items: center;
    `;

export const HistoryUsername = styled.h5`
    margin: 0;
    font-weight: 600;
    `;