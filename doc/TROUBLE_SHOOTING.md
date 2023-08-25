# Trouble Shooting
프로젝트를 진행하면서 발생한 문제점을 정리하고,
이에 대한 해결책을 정리하는 문서입니다.

---

1. MongoDB Index 오류

문제 상황
- MongoDB에 인덱스를 생성하려고 하는데, 인덱스 생성이 되지 않는 오류가 발생함.

문제 원인
- MongoDB의 Collection에는 하나의 text-index(full-text-search-index)만 가질 수 있다고 명시됨.

해결 방안
- https://www.mongodb.com/docs/manual/core/indexes/index-types/index-text/
- 텍스트인덱스는 컬렉션당 하나지만, 하나의 인덱스에 복합인덱스로 구성하면 여러개가 사용가능하다고 함.
- 이를 참조하여, 다중 조건 검색에도 유리하게, 복합인덱스로 생성하도록 구성함.