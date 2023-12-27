import {atom} from "recoil";
import {Profile, TokenDto} from "../constants/interfaces";

//Profile Data
export const profileData = atom<Profile | null>({
  key: "profileData",
  default: null
});

//Token Data
export const tokenData = atom<TokenDto | null>({
  key: "tokenData",
  default: null
});