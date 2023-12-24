export interface ApiData {
  apiId: number
  profileUrl: string
  username: string
  apiName: string
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