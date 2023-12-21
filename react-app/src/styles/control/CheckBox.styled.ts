import styled from "styled-components";

export const CheckBox = styled.input.attrs({type: "checkbox"})`
      width: 20px;
      height: 20px;
    `;

export const CheckBoxLabel = styled.label`
      font-weight: 600;
      font-size: 15px;
      margin-right: 5px;
    `;

export const CheckBoxWrapper = styled.div`
      display: flex;
      align-items: center;
    `;