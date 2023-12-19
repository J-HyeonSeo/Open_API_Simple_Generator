import Header from "../components/header/Header";
import SearchInput from "../components/control/SearchInput";
import { Fragment } from "react";
import ApiCardArea from "../components/api-card/ApiCardArea";
import PageNavBar from "../components/page-nav-bar/PageNavBar";

const MainPage = () => {
  return (
      <Fragment>
        <Header />
        <SearchInput />
        <ApiCardArea />
        <PageNavBar page={{total: 50, displaySize: 5, navBarSize: 5, index: 8}}/>
      </Fragment>
  )
}

export default MainPage;