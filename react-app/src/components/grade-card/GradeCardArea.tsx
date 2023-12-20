import * as S from "../../styles/common-card/Card.styled";
import {GradeInfo} from "../../constants/interfaces";
import GradeCard from "./GradeCard";

const GradeCardArea = () => {

  const mockGradeInfoList: Array<GradeInfo> = [
    {
      gradeId: 1,
      gradeName: "브론즈",
      price: 0,
      apiMaxCount: 1,
      fieldMaxCount: 3,
      queryMaxCount: 1,
      recordMaxCount: 100,
      dbMaxSize: 1000000,
      accessorMaxCount: 10,
      historyStorageDays: 3,
      isPaid: true
    },
    {
      gradeId: 2,
      gradeName: "실버",
      price: 2000,
      apiMaxCount: 3,
      fieldMaxCount: 5,
      queryMaxCount: 2,
      recordMaxCount: 500,
      dbMaxSize: 5000000,
      accessorMaxCount: 30,
      historyStorageDays: 10,
      isPaid: false
    },
    {
      gradeId: 3,
      gradeName: "골드",
      price: 3000,
      apiMaxCount: 5,
      fieldMaxCount: 7,
      queryMaxCount: 3,
      recordMaxCount: 1000,
      dbMaxSize: 10000000,
      accessorMaxCount: 50,
      historyStorageDays: 20,
      isPaid: true
    },
    {
      gradeId: 4,
      gradeName: "다이아",
      price: 4000,
      apiMaxCount: 7,
      fieldMaxCount: 9,
      queryMaxCount: 4,
      recordMaxCount: 3000,
      dbMaxSize: 30000000,
      accessorMaxCount: 100,
      historyStorageDays: 30,
      isPaid: false
    },
  ];

  return (
      <S.CardWrapper $w={1200} $m={100} $isFlex={true}>
        {mockGradeInfoList.map(item => (
          <S.CardWrapper $w={250} key={item.gradeId}>
            <GradeCard item={item}/>
          </S.CardWrapper>
        ))}
      </S.CardWrapper>
  )
}

export default GradeCardArea;