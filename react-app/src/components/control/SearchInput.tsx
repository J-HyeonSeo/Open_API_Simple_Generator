import * as S from "../../styles/control/SearchInput.styled";
import magnifierImage from "../../assets/magnifier.png";



const SearchInput = () => {
  return (
      <S.SearchInputWrapper>
        <S.SearchInput placeholder={"공개된 OpenAPI를 검색하고 신청해보세요."}/>
        <S.MagnifierImg src={magnifierImage} alt={"magnifierImage"}/>
      </S.SearchInputWrapper>
  )
}

export default SearchInput;