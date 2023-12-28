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

export interface ErrorFormat {
  code: number
  message: string
}

export interface ApiData {
  apiId: number
  apiName: string
  ownerNickname: string
  profileUrl: string
  apiState: string
  accessible: boolean
}

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

export interface TypeCardInfo {
  fieldName: string,
  typeString: string,
  'top-color': string,
  'bottom-color': string
}

export interface ProfileInfo {
  profileImage: string
  name: string
  email?: string
}