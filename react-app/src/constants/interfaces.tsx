import {SCHEMA_TYPE_LIST} from "./Data";

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

export interface createApiInput {
  apiName: string
  apiIntroduce: string
  schemaStructure: Array<number>
  queryParameter: Array<number>
  isPublic: boolean
  file: File
}

export interface FieldAndType {
  field: string,
  type: string
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