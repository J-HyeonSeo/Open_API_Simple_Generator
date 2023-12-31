import React, {Fragment, useEffect, useState} from "react";
import SearchInput from "../control/SearchInput";
import ApiCardArea from "./ApiCardArea";
import PageNavBar from "../page-nav-bar/PageNavBar";
import {ApiData} from "../../constants/interfaces";
import useAxios from "../../hooks/useAxios";

const ApiCardManage: React.FC<{url: string, placeholder: string, title: string}> = ({url, placeholder, title}) => {

  const [pageIdx, setPageIdx] = useState(0);
  const [searchText, setSearchText] = useState("");
  const [type, setType] = useState("API_NAME");
  const {res, isError, request} = useAxios();
  const [apiList, setApiList] = useState<Array<ApiData>>([]);

  const loadApiList = (pageIdx: number) => {
      setPageIdx(pageIdx);
      request("/api/" + url + "/"+ pageIdx +"/5?searchText=" + searchText + "&type=" + type, "get");
  }

  useEffect(() => {
    loadApiList(pageIdx);
  }, [pageIdx]);

  useEffect(() => {
    setApiList(res?.data.content || []);
  }, [res]);

  return (
      <Fragment>
        <SearchInput
            placeholder={placeholder}
            searchText={searchText}
            setType={setType}
            setSearchText={setSearchText} loadFunction={() => loadApiList(0)}/>
        <ApiCardArea item={apiList} total={res?.data.totalElements || 0} title={title}/>
        <PageNavBar
            setPageIdx={setPageIdx}
            page={{total: res?.data.totalElements || 1, displaySize: 5, navBarSize: 5, index: pageIdx + 1}}/>
      </Fragment>
  )
}

export default ApiCardManage;