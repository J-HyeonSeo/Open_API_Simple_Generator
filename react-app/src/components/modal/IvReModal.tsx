import * as S from "../../styles/modal/my-page/IvReModal.styled";
import ProfileArea from "../api-card/ProfileArea";
import testProfileImg from "../../assets/test-profile.png";
import {CommonBtn} from "../../styles/control/CommonBtn.styled";
import {palette} from "../../constants/Styles";

const IvReModal = () => {
  const mockIterator = [1, 2, 3, 4, 5, 6];

  return (
      <S.ContentWrapper>
        {mockIterator.map((item) => (
            <S.Content>
              <S.DataOuterArea>
                <S.DataInnerArea>
                  <S.Title>2020 ~ 2023 년도 경제 시장 분석 데이터 OPEN API</S.Title>
                </S.DataInnerArea>
                <S.DataInnerArea>
                  <S.BottomArea>
                    <ProfileArea item={{profileImage: testProfileImg, name: "Adam Smith"}} isLine={true}/>
                    <CommonBtn $w={150} $color={palette["--color-gray-500"]} $hover-color={palette["--color-gray-900"]}>
                      API 보러가기
                    </CommonBtn>
                  </S.BottomArea>
                </S.DataInnerArea>
              </S.DataOuterArea>
              <S.ButtonArea>
                <CommonBtn $color={palette["--color-primary-100"]} $hover-color={palette["--color-primary-900"]}>
                  수락하기
                </CommonBtn>
                <CommonBtn $color={palette["--color-red-500"]} $hover-color={palette["--color-red-700"]}>
                  거절하기
                </CommonBtn>
              </S.ButtonArea>
            </S.Content>
        ))}
      </S.ContentWrapper>
  )
}

export default IvReModal;