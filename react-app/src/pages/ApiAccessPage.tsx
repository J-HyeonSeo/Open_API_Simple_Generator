import {Fragment} from "react";
import Header from "../components/header/Header";
import ApiCardManage from "../components/api-card/ApiCardManage";

const ApiAccessPage = () => {
  return (
      <Fragment>
        <Header />
        <ApiCardManage url={"access"} placeholder={"접근 가능한 OpenAPI를 검색해보세요."} title={"접근 가능 API 목록"}/>
      </Fragment>
  )
}

export default ApiAccessPage;