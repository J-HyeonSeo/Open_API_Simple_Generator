import * as S from "../../../styles/common-card/Card.styled";
import * as T from "../../../styles/control/Table.styled";
import {Card} from "../../../styles/common-card/Card.styled";
import {CommonBtn} from "../../../styles/control/CommonBtn.styled";
import {palette} from "../../../constants/Styles";
import Modal from "../../modal/Modal";
import {Fragment, useState} from "react";
import PageNavBar from "../../page-nav-bar/PageNavBar";
import {TypeAddBtn, TypeSubBtn} from "../../../styles/control/TypeBtn.styled";

const ApiDataCard = () => {
  const [isShowDataModal, setIsShowDataModal] = useState(false);
  const [isEdit, setIsEdit] = useState(Array(16).fill(false));

  const editHandler = (line:number, value: boolean) => {
    const newIsEdit = Array(16).fill(false);
    newIsEdit[line] = value;
    setIsEdit(newIsEdit);
  }

  const modalHandler = (value: boolean) => {
    setIsShowDataModal(value);
  }

  const mockTableData = [1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1];

  return (
      <S.CardWrapper $m={80}>
        <S.CardTitle>데이터 관리</S.CardTitle>
        <Card $h={70}>
          <CommonBtn onClick={() => modalHandler(true)}
              $color={palette["--color-primary-100"]}
              $hover-color={palette["--color-primary-900"]}>데이터 관리</CommonBtn>
        </Card>
        {isShowDataModal &&
            <Modal title={"데이터 관리"}
                   w={1200} h={800}
                   closeHandler={() => modalHandler(false)}>
              <T.ContentWrapper>
                <T.TableWrapper>
                  <T.Table>
                    <thead>
                      <tr>
                        <T.TableData>ID</T.TableData>
                        <T.TableData>와이파이명</T.TableData>
                        <T.TableData>설치년도</T.TableData>
                        <T.TableData>위도</T.TableData>
                        <T.TableData>경도</T.TableData>
                        <T.TableData>경도</T.TableData>
                        <T.TableData>경도</T.TableData>
                      </tr>
                    </thead>
                    <T.TableBody>
                      {
                        mockTableData.map((_, index) => (
                          <T.TableRow onDoubleClick={() => editHandler(index, true)}>
                            {isEdit[index] && <Fragment>
                              <T.TableData>
                                <T.TableInput value={"6abc1x7abv5c2sxz2v"}></T.TableInput>
                              </T.TableData>
                              <T.TableData>
                                <T.TableInput value={"신설 와이파이"}></T.TableInput>
                              </T.TableData>
                              <T.TableData>
                                <T.TableInput value={"2023"}></T.TableInput>
                              </T.TableData>
                              <T.TableData>
                                <T.TableInput value={"37.1234"}></T.TableInput>
                              </T.TableData>
                              <T.TableData>
                                <T.TableInput value={"128.123442"}></T.TableInput>
                              </T.TableData>
                              <T.TableData>
                                <T.TableInput value={"128.123442"}></T.TableInput>
                              </T.TableData>
                              <T.TableData>
                                <T.TableInput value={"128.123442"}></T.TableInput>
                              </T.TableData>
                            </Fragment>}
                            {!isEdit[index] && <Fragment>
                              <T.TableData>6abc1x7abv5c2sxz2v</T.TableData>
                              <T.TableData>신설 와이파이</T.TableData>
                              <T.TableData>2023</T.TableData>
                              <T.TableData>37.1234</T.TableData>
                              <T.TableData>128.123442</T.TableData>
                              <T.TableData>128.156421</T.TableData>
                              <T.TableData>128.123454</T.TableData>
                            </Fragment>}
                            <T.TableData>
                              <div style={{display: "flex", alignItems: "center"}}>
                                {isEdit[index] && <T.OuterOkBtn onClick={() => editHandler(index, false)}/>}
                                {!isEdit[index] && <T.OuterSubBtn/>}
                              </div>
                            </T.TableData>
                          </T.TableRow>
                        ))
                      }
                    </T.TableBody>
                  </T.Table>
                </T.TableWrapper>
                {/*<div>*/}
                {/*  <table style={{borderCollapse: "collapse"}}>*/}
                {/*    <thead>*/}
                {/*      <tr>*/}
                {/*        <T.GhostTableData/>*/}
                {/*      </tr>*/}
                {/*    </thead>*/}
                {/*    <tbody>*/}
                {/*      {*/}
                {/*        mockTableData.map((_, index) => (*/}
                {/*            <tr>*/}
                {/*              <T.GhostTableData><TypeSubBtn $m={10}/></T.GhostTableData>*/}
                {/*              {index === mockTableData.length - 1 && <T.GhostTableData><TypeAddBtn $size={20}/></T.GhostTableData>}*/}
                {/*            </tr>*/}
                {/*        ))*/}
                {/*      }*/}
                {/*    </tbody>*/}
                {/*  </table>*/}
                {/*</div>*/}
              </T.ContentWrapper>
              <PageNavBar page={{total: 25, index:2, displaySize: 5, navBarSize: 5}} margin={30}/>
            </Modal>
        }
      </S.CardWrapper>
  )
}

export default ApiDataCard;