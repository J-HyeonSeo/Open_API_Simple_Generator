import Modal from "./Modal";
import * as T from "../../styles/control/Table.styled";
import React, {useEffect, useRef, useState} from "react";
import PageNavBar from "../page-nav-bar/PageNavBar";
import useAxios from "../../hooks/useAxios";
import {useParams} from "react-router-dom";
import {ApiDataFormat, ApiIntroData} from "../../constants/interfaces";

const ApiDataModal: React.FC<{introData: ApiIntroData | undefined, callback: () => void}> = ({introData, callback}) => {

  const id = useParams().id;
  const [isEdit, setIsEdit] = useState(Array(16).fill(false));
  const [dataList, setDataList] = useState<Array<ApiDataFormat>>(Array(16).fill({}));
  const [pageIdx, setPageIdx] = useState(0);

  //input 객체 연결
  const inputRefs = useRef<Array<Array<HTMLInputElement | null>>>([[]]);

  const {res: dataRes, request: requestGetData} = useAxios();
  const {res: modifyRes, request: modifyRequest,
    setRes, isError,
    setIsError, errorMessage} = useAxios();
  const modalKeyword = useRef('');

  const modifyData = (type: string, index: number) => {
    editHandler(index, false);
    let body: Record<any, any>;
    switch (type) {
      case "INSERT":

        for (let col = 0; col < (introData?.schemaStructure.length || 1); col++) {
          if (inputRefs.current[index][col]?.value.trim() === '') {
            return;
          }
        }

        modalKeyword.current = "추가";
        body = {
          insertData: {}
        }
        introData?.schemaStructure.forEach((key, col) => {
          body.insertData[key.field] = inputRefs.current[index][col]?.value;
        });
        modifyRequest(`/api/data/manage/${id}`, "post", body);
        break;
      case "UPDATE":
        modalKeyword.current = "수정";
        body = {
          dataId: dataList[index]._id,
          updateData: {}
        }

        let isUpdate = false;
        introData?.schemaStructure.forEach((key, col) => {

          //기존과 데이터가 달라졌다면, isUpdate를 true로 만들어 데이터 수정 진행, 아니면, 수정하지 않음.
          if (dataList[index][key.field] != inputRefs.current[index][col]?.value.trim()) {
            console.log(`원본 => ${dataList[index][key.field]}, 수정 => ${inputRefs.current[index][col]?.value},`);
            isUpdate = true;
          }
          body.updateData[key.field] = inputRefs.current[index][col]?.value;
        });

        if (isUpdate) {
          modifyRequest(`/api/data/manage/${id}`, "put", body);
        }
        break;
      case "DELETE":
        modalKeyword.current = "삭제";
        body = {
          data: {
            dataId: dataList[index]._id
          }
        }
        modifyRequest(`/api/data/manage/${id}`, "delete", body);
        break;
    }
  }

  const modifiedSuccessHandler = () => {
    setRes(undefined);
    getData();
  }

  const getData = () => {
    requestGetData(`/api/data/${id}/${pageIdx}/16`, "get");
  }

  const editHandler = (line:number, value: boolean) => {
    const newIsEdit = Array(16).fill(false);
    newIsEdit[line] = value;
    setIsEdit(newIsEdit);
  }

  useEffect(() => {
    getData();
  }, [pageIdx]);

  useEffect(() => {
    if (dataRes === undefined) {
      return;
    }
    const content = dataRes.data.content;

    //date format 변경 처리
    introData?.schemaStructure.forEach((key) => {
      if (key.type === "DATE" && content instanceof Array) {
        for(let row = 0; row < content.length; row++) {
          content[row][key.field] = content[row][key.field].split("T")[0];
        }
      }
    });

    const filledArray = Array.from({ length: 16 }, (_, index) => content[index] || {});
    setDataList(filledArray);
  }, [dataRes]);

  return (
      <Modal title={"데이터 관리"}
             w={1200} h={800}
             closeHandler={callback}>
        <T.ContentWrapper>
          <T.TableWrapper>
            <T.Table>
              <thead>
              <tr>
                {introData && introData.schemaStructure.map((item => (
                    <T.TableData>{item.field}</T.TableData>
                )))}
              </tr>
              </thead>
              <T.TableBody>
                {
                  dataList.map((item, index) => (
                      <T.TableRow onDoubleClick={() => editHandler(index, true)}>
                        {isEdit[index] &&
                            introData?.schemaStructure.map((key, col) => (
                              <T.TableData>
                               <T.TableInput ref={(el) => {
                                 inputRefs.current[index] = inputRefs.current[index] || [];
                                 inputRefs.current[index][col] = el;
                               }}
                               defaultValue={item[key.field]}></T.TableInput>
                              </T.TableData>
                            ))}
                        {!isEdit[index] &&
                              introData?.schemaStructure.map(key => (
                                <T.TableData>{item[key.field]}</T.TableData>
                              ))}
                        <T.TableData>
                          <div style={{display: "flex", alignItems: "center"}}>
                            {isEdit[index] && <T.OuterOkBtn
                                onClick={() => dataList[index]._id ?
                                    modifyData("UPDATE", index) : modifyData("INSERT", index)}/>}
                            {!isEdit[index] && dataList[index]._id && <T.OuterSubBtn
                              onClick={() => modifyData("DELETE", index)}
                            />}
                          </div>
                        </T.TableData>
                      </T.TableRow>
                  ))
                }
              </T.TableBody>
            </T.Table>
          </T.TableWrapper>
        </T.ContentWrapper>
        <PageNavBar page={
          {total: (dataRes?.data.totalElements + 1) || 0,
            index: pageIdx + 1, displaySize: 16, navBarSize: 5}}
                    setPageIdx={setPageIdx}
                    margin={30}/>
        {modifyRes && <Modal
            title={"성공"}
            mark={"success"}
            isButton={true}
            text={`성공적으로 해당 데이터를 ${modalKeyword.current}하였습니다.`}
            yesCallback={modifiedSuccessHandler}
            closeHandler={modifiedSuccessHandler} />}
        {isError && <Modal
            title={"실패"}
            mark={"error"}
            text={errorMessage?.message || `문제가 발생하여, ${modalKeyword.current}하지 못했습니다.`}
            isButton={true}
            yesCallback={() => setIsError(false)}
            closeHandler={() => setIsError(false)} />}
      </Modal>
  )
}

export default ApiDataModal;