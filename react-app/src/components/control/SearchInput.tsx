import * as S from "../../styles/control/SearchInput.styled";
import magnifierImage from "../../assets/magnifier.png";
import React from "react";



const SearchInput: React.FC<{searchText: string,
  setSearchText: (text: string) => void,
  setType: (text: string) => void,
  loadFunction: () => void,
  placeholder: string
}> = ({searchText, setSearchText, setType, loadFunction, placeholder}) => {

  const keyHandler = (e: React.KeyboardEvent) => {
    if (e.key === "Enter") {
      loadFunction();
    }
  }

  return (
      <S.SearchInputWrapper>
        <S.SearchInput
            value={searchText}
            onKeyDown={(e) => keyHandler(e)}
            onChange={(e) => setSearchText(e.target.value)}
            placeholder={placeholder}/>
        <S.Selector onChange={(e) => setType(e.target.value)}>
          <option value={"API_NAME"}>이름</option>
          <option value={"API_OWNER_EMAIL"}>이메일</option>
          <option value={"API_INTRODUCE"}>소개</option>
        </S.Selector>
        <S.MagnifierImg
            src={magnifierImage}
            alt={"magnifierImage"}
            onClick={loadFunction}/>
      </S.SearchInputWrapper>
  )
}

export default SearchInput;