import Header from "../components/header/Header";
import { Fragment } from "react";
import ApiCardManage from "../components/api-card/ApiCardManage";

const MainPage = () => {
  return (
      <Fragment>
        <Header />
        <ApiCardManage url={"public"} placeholder={"공개된 OpenAPI를 검색하고 신청해보세요."} title={"공개 API 목록"}/>
      </Fragment>
  )
}

export default MainPage;