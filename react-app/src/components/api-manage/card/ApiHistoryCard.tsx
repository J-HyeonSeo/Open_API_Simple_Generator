import * as S from "../../../styles/common-card/Card.styled";
import * as S2 from "../../../styles/api-card/ProfileArea.styled";
import * as S3 from "../../../styles/modal/api-manage/ApiHistoryModal.styled";
import {Card} from "../../../styles/common-card/Card.styled";
import {palette} from "../../../constants/Styles";
import TestProfileImg from "../../../assets/test-profile.png";
import ArrowImg from "../../../assets/arrow.png";
import {CommonBtn} from "../../../styles/control/CommonBtn.styled";
import React, {useState} from "react";
import {Profile} from "../../../styles/profile/profile.styled";
import {Line} from "../../../styles/line/line.styled";
import Modal from "../../modal/Modal";
import ProfileArea from "../../api-card/ProfileArea";
import {ApiIntroData, HistoryData} from "../../../constants/interfaces";

const ApiHistoryCard: React.FC<{item: HistoryData, introData: ApiIntroData | undefined}> = ({item, introData}) => {
  const [isShowDetailModal, setIsShowDetailModal] = useState(false);
  const keyword = item.type === "INSERT" ? "추가" : item.type === "UPDATE" ? "수정" : "삭제";
  return (
      <S.CardWrapper $w={550} $m={10}>
        <Card $p={5} $r={10} $h={50} $c={palette["--color-gray-300"]}>
          <S2.ProfileAreaWrapper $w={500}>
            <Profile src={item.profile_image} alt={"ProfileImg"}/>
            <Line $m={15} $h={30}/>
            <S2.HistoryWrapper>
              <S2.HistoryUsername><strong>'{item.member_name}'</strong> 님이 API 데이터를 {keyword}하였습니다.</S2.HistoryUsername>
              <S2.HistoryTimeWrapper>
                <S2.HistoryUsername>변경시각</S2.HistoryUsername>
                <Line $h={10} $m={10}></Line>
                <S2.HistoryUsername>{item.at}</S2.HistoryUsername>
              </S2.HistoryTimeWrapper>
            </S2.HistoryWrapper>
          </S2.ProfileAreaWrapper>
          <div>
            <CommonBtn
                onClick={() => setIsShowDetailModal(true)}
                $color={palette["--color-primary-100"]}
                $hover-color={palette["--color-primary-900"]}
                $w={90} $h={30} $m={5}>
              상세보기
            </CommonBtn>
          </div>
        </Card>
        {isShowDetailModal && item.type === "UPDATE" &&
            <Modal title={"로그 상세보기"}
                   w={1000} h={600}
                   closeHandler={() => setIsShowDetailModal(false)}>
            <S.CardWrapper $m={20} $w={500}>
              <S.Card $r={10} $h={65}>
                <ProfileArea
                    item={{profileImage: item.profile_image, name: item.member_name, email: item.member_email}}
                    isLine={true} w={500} isEmail={true}/>
              </S.Card>
            </S.CardWrapper>
            <S.CardWrapper $w={900} $isFlex={true}>
              <S.CardWrapper $w={400}>
                <S.Card $h={400} $d={"column"} $notAround={true} $r={10}>
                  <S.CardTitle>변경 전</S.CardTitle>
                  <S.InnerCardWrapper $w={350}>
                    <S.ScrollCard $h={300} $c={palette["--color-gray-300"]} $p={1} $r={10}>
                      <S3.FieldArea>
                        {introData?.schemaStructure.map((key) => (
                            <S3.FieldText key={key.field}>{key.field}</S3.FieldText>
                        ))}
                      </S3.FieldArea>
                      <Line $fullHeight={true} $c={palette["--color-gray-500"]}/>
                      <S3.ValueArea>
                        {introData?.schemaStructure.map((key) => (
                            <S3.ReadInput readOnly={true} value={item.original_data[key.field]}/>
                        ))}
                      </S3.ValueArea>
                    </S.ScrollCard>
                  </S.InnerCardWrapper>
                </S.Card>
              </S.CardWrapper>
              <img src={ArrowImg} alt={"right-arrow"} width={29} height={16}/>
              <S.CardWrapper $w={400}>
                <S.Card $h={400} $d={"column"} $notAround={true} $r={10}>
                  <S.CardTitle>변경 후</S.CardTitle>
                  <S.InnerCardWrapper $w={350}>
                    <S.ScrollCard $h={300} $c={palette["--color-gray-300"]} $p={1} $r={10}>
                      <S3.FieldArea>
                        {introData?.schemaStructure.map((key) => (
                            <S3.FieldText key={key.field}>{key.field}</S3.FieldText>
                        ))}
                      </S3.FieldArea>
                      <Line $fullHeight={true} $c={palette["--color-gray-500"]}/>
                      <S3.ValueArea>
                        {introData?.schemaStructure.map((key) => (
                            <S3.ReadInput readOnly={true} value={item.new_data[key.field]}/>
                        ))}
                      </S3.ValueArea>
                    </S.ScrollCard>
                  </S.InnerCardWrapper>
                </S.Card>
              </S.CardWrapper>
            </S.CardWrapper>
        </Modal>}

        {isShowDetailModal && item.type !== "UPDATE" &&
            <Modal title={"로그 상세보기"}
                   w={500} h={600}
                   closeHandler={() => setIsShowDetailModal(false)}>
              <S.CardWrapper $m={20} $w={400}>
                <S.Card $r={10} $h={65}>
                  <ProfileArea
                      item={{profileImage: item.profile_image, name: item.member_name, email: item.member_email}}
                      isLine={true} w={500} isEmail={true}/>
                </S.Card>
              </S.CardWrapper>
              <S.CardWrapper $w={400}>
                <S.Card $h={400} $d={"column"} $notAround={true} $r={10}>
                  <S.CardTitle>{keyword} 데이터</S.CardTitle>
                  <S.InnerCardWrapper $w={350}>
                    <S.ScrollCard $h={300} $c={palette["--color-gray-300"]} $p={1} $r={10}>
                      <S3.FieldArea>
                        {introData?.schemaStructure.map((key) => (
                            <S3.FieldText key={key.field}>{key.field}</S3.FieldText>
                        ))}
                      </S3.FieldArea>
                      <Line $fullHeight={true} $c={palette["--color-gray-500"]}/>
                      <S3.ValueArea>
                        {introData?.schemaStructure.map((key) => (
                            <S3.ReadInput readOnly={true} value={item.type === "INSERT" ? item.new_data[key.field] : item.original_data[key.field]}/>
                        ))}
                      </S3.ValueArea>
                    </S.ScrollCard>
                  </S.InnerCardWrapper>
                </S.Card>
              </S.CardWrapper>
            </Modal>}
      </S.CardWrapper>
  )
}

export default ApiHistoryCard;
