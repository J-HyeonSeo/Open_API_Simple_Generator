import * as S from "../../styles/page-nav-bar/PageNavBar.styled";
import {PageData} from "../../constants/interfaces";
import React from "react";

const PageNavBar: React.FC<{
  page: PageData,
  setPageIdx?: (idx: number) => void
  margin?: number}> = ({page, setPageIdx, margin}) => {

  const pageCount = Math.ceil(page.total / page.displaySize);
  const startPage = Math.trunc((page.index-1) / page.navBarSize) * page.navBarSize + 1;
  const endPage = Math.min(pageCount, Math.trunc(((page.index-1) + page.navBarSize) / page.navBarSize) * page.navBarSize);

  const pageLooper = () => {
    const pages = [];
    for(let i = startPage; i <= endPage; i++) {
      i === page.index ? pages.push(<S.PageNavBtn key={i} $isFocus={true}>{i}</S.PageNavBtn>) :
          pages.push(<S.PageNavBtn onClick={() => setPageIdx && setPageIdx(i-1)} key={i}>{i}</S.PageNavBtn>);
    }
    return pages;
  }

  return (
      <S.PageNavBar $m={margin}>
        <S.PageNavBtn onClick={() => setPageIdx && setPageIdx(0)}>◀◀</S.PageNavBtn>
        <S.PageNavBtn onClick={() => setPageIdx && setPageIdx(Math.max(0, startPage-2))}>◀</S.PageNavBtn>
        {pageLooper()}
        <S.PageNavBtn onClick={() => setPageIdx && setPageIdx(Math.min(endPage, pageCount-1))}>▶</S.PageNavBtn>
        <S.PageNavBtn onClick={() => setPageIdx && setPageIdx(pageCount-1)}>▶▶</S.PageNavBtn>
      </S.PageNavBar>
  )
}

export default PageNavBar;