# Trouble Shooting
프로젝트를 진행하면서 발생한 문제점을 정리하고,
이에 대한 해결책을 정리하는 문서입니다.

---

### 1. MongoDB Index 오류


문제 상황
- MongoDB에 인덱스를 생성하려고 하는데, 인덱스 생성이 되지 않는 오류가 발생함.

문제 원인
- MongoDB의 Collection에는 하나의 text-index(full-text-search-index)만 가질 수 있다고 명시됨.

해결 방안
- https://www.mongodb.com/docs/manual/core/indexes/index-types/index-text/
- 텍스트인덱스는 컬렉션당 하나지만, 하나의 인덱스에 복합인덱스로 구성하면 여러개가 사용가능하다고 함.
- 이를 참조하여, 다중 조건 검색에도 유리하게, 복합인덱스로 생성하도록 구성함.

---

### 2. MongoDB 인덱스 사용 여부


문제 상황
- text를 위해 복합인덱스로 생성하였지만, 해당 조합의 쿼리를 날리는 경우에 사용이 가능하다.
- 해당 프로젝트는 원하는 필드별로 각각 쿼리를 날리는 경우가 발생하므로 적합하지 않다고 판단함.

해결 방안
- INCLUDE를 수행하는 필드만 묶어서 text 인덱스를 사용하도록 설정함.
- text 인덱스끼리 있을 경우에는, 필드 순서에 상관없이 해당 단어만 포함되도 검색대상에 포함되는걸 알 수 있음.
- 단, 특정 필드에 대한 검색일수도 있으므로, 질의시에는 검색 필드명에 대해 or연산으로 다시 한 번 처리가 필요해보임.
- text를 제외한 나머지 필드들은 오름차 정렬 인덱스로 각각 따로 생성하는 것으로 구성하기로 함.

---

### 3. ElasticSearch Join

문제 상황
- 전체 검색은 쉽게 만들 수 있었지만, 특정 조건에 맞는 검색은 쉽지 않았음.
- Access가 가능한 API만 Elastic Search가 수행되어야 했음.

해결 방안
- ElasticSearch 에서는 이를 대비하여, Nested 필드와 Join 필드를 제공해주고 있음.
- 데이터의 일관성을 위해, Join 필드를 사용하여, 데이터를 저장하도록 함.
- 초반에는 JoinField가 RDB처럼 각각의 인덱스에 대한 맵핑을 지정하는 줄 알았음.
- JoinField는 알고보니, 하나의 인덱스에서 부모-자식 관계를 지정하는 것이었음.
- alias(별칭)을 사용하면, 멀티 인덱스에서 조인이 가능하다고 하였지만, 이는 권장 사항이 아닌 것 같았음.
- 그래서, permissionMemberId 필드를 추가하고, mapping 이라는 JoinField를 추가하였음.
- apiInfo에 대한 데이터를 넣을 때는, mapping을 "apiinfo"로 지정하여 넣어주고,
- permissionMemberId를 넣을 때는, mapping에 "permissionMember" 와 parent를 apiInfo에 대한 id를 지정해주었음.
- SpringBoot에서 JoinField을 어떻게 다루어야 하는지에 대한 예제가 거의 없어, 라이브러리 소스코드를 분석하여 해결함.

느낀점
- RDB의 테이블관점에서 ElasticSearch를 생각하였기에, Join에 대한 개념을 익히는데 시간이 걸렸음.
- 하나의 인덱스에서 부모-자식 관계를 지정한다는 것을 깨달았음.
- 인도인 유튜브강의가 이렇게나 반가울 줄은 몰랐음.

### 4. out폴더의 문제

문제 상황
- job configuration 클래스의 기능을 변경하고자, 클래스의 명칭을 rename하였음.
- 애플리케이션을 빌드 후 동작을 수행해보면, rename되기 전과, 변경된 클래스가 동시에 수행되어 에러를 일으킴.

해결 방안
- out폴더에 변경되기 전의 class 파일을 이미 저장하였기 때문에, 발생한 문제였음.
- out폴더를 삭제하고, 다시 실행하여 문제를 해결하였음.