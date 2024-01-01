
//================ Authentication Type ====================
export interface TokenDto {
  accessToken: string,
  refreshToken: string
}
export interface Profile {
  memberId: number
  nickname: string
  email: string
  profileUrl: string
}
//==========================================================

//=============== Back-End Response Type ===================
export interface ErrorFormat {
  code: number
  message: string
}

export interface ApiData {
  id: number
  apiName: string
  ownerNickname: string
  profileUrl: string
  apiState: string
  accessible: boolean
}

export interface ApiIntroData {
  id: number
  apiName: string
  apiIntroduce: string
  ownerMemberId: number
  profileUrl: string
  ownerNickname: string
  apiState: string
  schemaStructure: Array<FieldAndType>
  queryParameter: Array<FieldAndType>
  registeredAt: Date
  updatedAt: Date
  disabledAt: Date
  public: boolean
}

export interface FieldAndType {
  field: string,
  type: string
}

export interface IvReData {
  id: number
  apiInfoId: number;
  memberNickname: string;
  profileUrl: string;
  apiName: string;
  registeredAt: Date;
  requestStateType: string;
}

export interface MemberSearchData {
  memberId: number;
  memberNickname: string;
  memberEmail: string;
  profileUrl: string;
}

export interface BlackListData {
  id: number
  memberId: number
  memberNickname: string
  profileUrl: string
  registeredAt: Date
}

export interface PermissionData {
  permissionId: number;
  memberNickname: string;
  profileUrl: string;
  permissionList: Array<PermissionDetail>;
}

export interface PermissionDetail {
  id: number;
  type: string;
}

export interface ApiDataFormat {
  _id: string;
  [key: string]: any
}

//==========================================================


//================ Components Type =========================
export interface PageData {
  total: number
  displaySize: number
  navBarSize: number
  index: number
}

export interface GradeInfo {
  gradeId: number
  gradeName: string;
  price: number;
  apiMaxCount: number;
  fieldMaxCount: number;
  queryMaxCount: number;
  recordMaxCount: number;
  dbMaxSize: number;
  accessorMaxCount: number;
  historyStorageDays: number;
  isPaid: boolean
}

export interface TypeCardSetterInfo {
  id: string
  field: string
  type: string
  displayType: string
  'top-color': string
  'bottom-color': string
  isModifying: boolean
}

export interface TypeCardInfo {
  fieldName: string,
  typeString: string,
  'top-color': string,
  'bottom-color': string
}

export interface TypeData {
  type: string
  display: string
  'top-color': string
  'bottom-color': string
}

export interface ProfileInfo {
  profileImage: string
  name: string
  email?: string
}
//==========================================================