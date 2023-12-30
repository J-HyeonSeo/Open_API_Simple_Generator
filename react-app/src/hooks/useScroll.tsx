import React, {useEffect, useRef} from "react";
import {AxiosResponse} from "axios";

const useScroll = <T extends {}>(
    requestFunction: (idx: number) => void,
    res: AxiosResponse<any, any> | undefined,
    setDataList: React.Dispatch<React.SetStateAction<Array<T>>>): {
  target: React.RefObject<HTMLDivElement>
} => {

  //페이지 관련 객체
  const pageIdx = useRef(0);
  const hasNextPage = useRef(true);

  //target 객체
  const target = useRef<HTMLDivElement>(null);

  const callback = () => {
    if (hasNextPage.current) {
      requestFunction(pageIdx.current);
      pageIdx.current++;
    }
  }

  //관측자 할당
  useEffect(() => {
    const observer = new IntersectionObserver((entries) => {
      entries.forEach((entry) => {
        if (!entry.isIntersecting) return;
        callback();
      });
    });
    target.current && observer.observe(target.current);
  }, []);

  //결과 할당
  useEffect(() => {
    if (res === undefined){
      return;
    }
    if (res.data.hasNextPage === false) {
      hasNextPage.current = false;
    }
    setDataList((prevState) => {
      return [...prevState, ...res.data.content]
    })
  }, [res]);

  return {target}
}

export default useScroll;