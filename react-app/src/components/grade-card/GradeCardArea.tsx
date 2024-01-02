import * as S from "../../styles/common-card/Card.styled";
import {GradeInfo} from "../../constants/interfaces";
import GradeCard from "./GradeCard";
import {useRecoilState} from "recoil";
import {profileData} from "../../store/RecoilState";
import {useEffect, useState} from "react";
import useAxios from "../../hooks/useAxios";

const GradeCardArea = () => {

  const [profile, _] = useRecoilState(profileData);
  const [gradeInfoList, setGradeInfoList] = useState<Array<GradeInfo>>([]);
  const {res, request} = useAxios();

  useEffect(() => {
    request("/payment/grades", "get");
  }, []);

  useEffect(() => {
    if (res === undefined) {
      return;
    }
    setGradeInfoList(res.data);
  }, [res]);

  return (
      <S.CardWrapper $w={1200} $m={100} $isFlex={true}>
        {gradeInfoList.map(item => (
          <S.CardWrapper $w={250} key={item.id}>
            <GradeCard item={item} gradeId={profile?.gradeId || -1}/>
          </S.CardWrapper>
        ))}
      </S.CardWrapper>
  )
}

export default GradeCardArea;