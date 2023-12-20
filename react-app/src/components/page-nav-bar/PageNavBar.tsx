import * as S from "../../styles/page-nav-bar/PageNavBar.styled";
import {PageData} from "../../constants/interfaces";
import React from "react";

const PageNavBar: React.FC<{page: PageData}> = ({page}) => {

  let pageCount = Math.ceil(page.total / page.displaySize);
  let start = Math.trunc((page.index-1) / page.navBarSize) * page.navBarSize + 1;
  let end = Math.min(pageCount, Math.trunc(((page.index-1) + page.navBarSize) / page.navBarSize) * page.navBarSize);

  const pageLooper = () => {
    const pages = [];
    for(let i = start; i <= end; i++) {
      i === page.index ? pages.push(<S.PageNavBtn key={i} $isFocus={true}>{i}</S.PageNavBtn>) :
          pages.push(<S.PageNavBtn key={i}>{i}</S.PageNavBtn>);
    }
    return pages;
  }

  return (
      <S.PageNavBar>
        <S.PageNavBtn>◀◀</S.PageNavBtn>
        <S.PageNavBtn>◀</S.PageNavBtn>
        {pageLooper()}
        <S.PageNavBtn>▶</S.PageNavBtn>
        <S.PageNavBtn>▶▶</S.PageNavBtn>
      </S.PageNavBar>
  )
}

export default PageNavBar;