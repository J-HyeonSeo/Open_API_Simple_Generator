/*
    ############# TYPE DATA ##################
 */

import {TypeData} from "./interfaces";

export const SCHEMA_TYPE_LIST: Array<TypeData> = [
  {
    type: "STRING",
    display: "String",
    'top-color': '#B91717',
    'bottom-color': '#620000'
  },
  {
    type: "INTEGER",
    display: "Integer",
    'top-color': '#00B3CB',
    'bottom-color': '#0055A4'
  },
  {
    type: "FLOAT",
    display: "Float",
    'top-color': '#2DA240',
    'bottom-color': '#005D09'
  },
  {
    type: "DATE",
    display: "Date",
    'top-color': '#e89a20',
    'bottom-color': '#a96027'
  }
];

export const QUERY_TYPE_LIST: Array<TypeData> = [
  {
    type: "INCLUDE",
    display: "Include",
    'top-color': '#B91717',
    'bottom-color': '#620000'
  },
  {
    type: "START",
    display: "Start",
    'top-color': '#B91717',
    'bottom-color': '#620000'
  },
  {
    type: "EQUAL",
    display: "Equal",
    'top-color': '#e89a20',
    'bottom-color': '#a96027'
  },
  {
    type: "GT",
    display: "GT",
    'top-color': '#00B3CB',
    'bottom-color': '#0055A4'
  },
  {
    type: "GTE",
    display: "GTE",
    'top-color': '#00B3CB',
    'bottom-color': '#0055A4'
  },
  {
    type: "LT",
    display: "LT",
    'top-color': '#2DA240',
    'bottom-color': '#005D09'
  },
  {
    type: "LTE",
    display: "LTE",
    'top-color': '#2DA240',
    'bottom-color': '#005D09'
  }
]