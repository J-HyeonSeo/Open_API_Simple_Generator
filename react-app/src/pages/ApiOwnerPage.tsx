import {Fragment} from "react";
import Header from "../components/header/Header";
import ApiCardManage from "../components/api-card/ApiCardManage";

const ApiOwnerPage = () => {
  return (
      <Fragment>
        <Header />
        <ApiCardManage url={"owner"} placeholder={"본인이 등록한 OpenAPI를 검색해보세요."} title={"관리 API 목록"}/>
      </Fragment>
  )
}

export default ApiOwnerPage;