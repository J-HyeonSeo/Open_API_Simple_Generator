import * as S from "../../styles/common-card/Card.styled";
import TypeCard from "../api-detail/TypeCard";
import {QUERY_TYPE_LIST, SCHEMA_TYPE_LIST} from "../../constants/Data";
import React, {useRef, useState} from "react";
import {TypeAddBtn, TypeSubBtn} from "../../styles/control/TypeBtn.styled";
import TypeInputCard from "./TypeInputCard";

const INITIAL_FIELDS = [
  {
    id: Math.random().toString(),
    fieldName: "",
    type: "",
    'top-color': SCHEMA_TYPE_LIST[0]["top-color"],
    'bottom-color': SCHEMA_TYPE_LIST[0]["bottom-color"],
    isModifying: true
  }
]

const TypeCardSetter: React.FC<{isSchema: boolean}> = ({isSchema}) => {

  const grapItem = useRef<string>('');
  const putItem = useRef<string>('');
  const [fields, setFields] = useState(INITIAL_FIELDS);

  //################### Drag And Drop Events ########################
  const onDragStart = (index: string) => {
    grapItem.current = index;
  }
  const onDragEnter = (index: string) => {
    if (grapItem.current === index) {
      return;
    }
    putItem.current = index;
  }
  const onDragEnd = () => {
    const grapIndex = fields.findIndex((item) => {return item.id === grapItem.current});
    const putIndex = fields.findIndex((item) => {return item.id === putItem.current});
    const newFields = [...fields];
    newFields[grapIndex] = fields[putIndex];
    newFields[putIndex] = fields[grapIndex];
    setFields(newFields);
  }

  //################### Data Control Handlers ########################
  const addTypeHandler = () => {
    const newType = {
      id: Math.random().toString(),
      fieldName: "",
      type: "",
      'top-color': SCHEMA_TYPE_LIST[1]["top-color"],
      'bottom-color': SCHEMA_TYPE_LIST[1]["bottom-color"],
      isModifying: true
    };
    setFields((prevState) => {
      return [...prevState, newType]
    });
  }

  const subTypeHandler = (index: string) => {
    const subIndex = fields.findIndex((item) => {return item.id === index});
    const newFields = [...fields];
    newFields.splice(subIndex, 1);
    setFields(newFields);
  }

  const enterChangeHandler = (index: string) => {
    const targetIndex = fields.findIndex((item) => {return item.id === index});
    const newFields = [...fields];
    newFields[targetIndex].isModifying = true;
    setFields(newFields);
  }

  const endChangeHandler = (index: string, fieldName: string, type: string) => {
    const targetIndex = fields.findIndex((item) => {return item.id === index});
    const newFields = [...fields];
    newFields[targetIndex].isModifying = false;
    newFields[targetIndex].fieldName = fieldName;
    newFields[targetIndex].type = type;

    const typeList = isSchema ? SCHEMA_TYPE_LIST : QUERY_TYPE_LIST;
    const typeIndex = typeList.findIndex((item) => {return item.display === type});
    newFields[targetIndex]["top-color"] = typeList[typeIndex]["top-color"];
    newFields[targetIndex]["bottom-color"] = typeList[typeIndex]["bottom-color"];
    setFields(newFields);
  }

  return (
      <S.Card $d={"column"} $isLeft={true} $p={0.1} $isNotShadow={true} $c={"white"}>
        {fields.map((item, index) => (
            <div key={item.id} style={{display: "flex", alignItems: "center"}}>
              {!item.isModifying && <TypeCard
                  key={item.id}
                  index={item.id}
                  onDragStart={onDragStart}
                  onDragEnter={onDragEnter}
                  onDragEnd={onDragEnd}
                  enterChange={enterChangeHandler}
                  vm={10} hm={0.1}
                  item={{fieldName: item.fieldName,
                    typeString: item.type,
                    "top-color": item["top-color"],
                    "bottom-color": item["bottom-color"]}}/>}
              {item.isModifying && <TypeInputCard index={item.id} endCallBack={endChangeHandler} isSchema={isSchema} fieldNameParam={item.fieldName} typeParam={item.type}/>}
              <TypeSubBtn onClick={() => subTypeHandler(item.id)}/>
              {index === fields.length-1 && <TypeAddBtn onClick={addTypeHandler}/>}
            </div>
        ))}
        {fields.length === 0 && <TypeAddBtn onClick={addTypeHandler}/>}
      </S.Card>
  )
}

export default TypeCardSetter;