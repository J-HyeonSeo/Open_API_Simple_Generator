import * as S from "../../styles/modal/my-page/PaymentModal.styled";
import testGradeImg from "../../assets/grade-image/gold-mark.png";
import {Line} from "../../styles/line/line.styled";
import {palette} from "../../constants/Styles";
import {CommonBtn} from "../../styles/control/CommonBtn.styled";

const PaymentModal = () => {

  const mockIterator = [1, 2, 3, 4, 5, 6];

  return (
      <S.ContentWrapper>
        {mockIterator.map((item) => (
          <S.Content>
            <S.GradeArea>
              <img src={testGradeImg} alt={"gradeImg"}/>
              <h2 style={{marginLeft: "20px"}}>골드 등급</h2>
            </S.GradeArea>
            <Line $h={50} $c={palette["--color-gray-300"]}/>
            <S.MetaDataOuterArea>
              <S.MetaDataInnerArea>
                <S.MetaDataFieldText>결제일</S.MetaDataFieldText>
                <Line $h={15} $c={palette["--color-gray-500"]} $m={15}/>
                <S.MetaDataValueText>2023-12-01T18:00:01</S.MetaDataValueText>
              </S.MetaDataInnerArea>
              <S.MetaDataInnerArea>
                <S.MetaDataFieldText>결제금액</S.MetaDataFieldText>
                <Line $h={15} $c={palette["--color-gray-500"]} $m={15}/>
                <S.MetaDataValueText>4,000</S.MetaDataValueText>
              </S.MetaDataInnerArea>
            </S.MetaDataOuterArea>
            <CommonBtn $color={palette["--color-primary-100"]}
                       $hover-color={palette["--color-primary-900"]}>
              환불하기
            </CommonBtn>
          </S.Content>
        ))}
      </S.ContentWrapper>
  )
}

export default PaymentModal;