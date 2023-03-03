package neatlogic.framework.lrcode.constvalue;

/**
 * @Title: MoveType
 * @Package neatlogic.framework.lrcode.constvalue
 * @Description: 移动类型
 * @Author: linbq
 * @Date: 2021/3/17 17:24
Copyright(c) $today.year NeatLogic Co., Ltd. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 **/
public enum MoveType {
    INNER("inner", "移动到目标节点里面"),
    PREV("prev", "移动到目标节点前面"),
    NEXT("next", "移动到目标节点后面")
    ;
    private String value;
    private String text;

    MoveType(String value, String text) {
        this.value = value;
        this.text = text;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public static MoveType getMoveType(String _value){
        for(MoveType e : values()){
            if(e.getValue().equals(_value)){
                return e;
            }
        }
        return null;
    }
}
