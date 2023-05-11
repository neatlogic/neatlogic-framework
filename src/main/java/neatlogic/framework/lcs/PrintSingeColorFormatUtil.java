/*
Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package neatlogic.framework.lcs;

public class PrintSingeColorFormatUtil {
    private static boolean SWITCH = false;
    public static void print(char c){
        if(SWITCH){
            System.out.format("\33[39;2m%c", c);
        }
    }
    public static void print(String s){
        if(SWITCH) {
            System.out.format("\33[39;2m%s", s);
        }
    }
    public static void println(String s){
        if(SWITCH) {
            System.out.format("\33[39;2m%s%n", s);
        }
    }
    public static void println(){
        if(SWITCH) {
            System.out.format("\33[39;2m%n");
        }
    }
    public static void printt(int count){
        if(SWITCH) {
            for (int i = 0; i < count; i++) {
                System.out.format("\33[39;2m\t");
            }
        }
    }
    public static void print(char c, String startMark){
        if(SWITCH) {
            if (startMark.equals(LCSUtil.SPAN_CLASS_INSERT)) {
                System.out.format("\33[32;2m%c", c);
            } else if (startMark.equals(LCSUtil.SPAN_CLASS_DELETE)) {
                System.out.format("\33[31;2m%c", c);
            } else {
                System.out.format("\33[39;2m%c", c);
            }
        }
    }
    public static void print(String s, String startMark){
        if(SWITCH) {
            if (startMark.equals(LCSUtil.SPAN_CLASS_INSERT)) {
                System.out.format("\33[32;2m%s", s);
            } else if (startMark.equals(LCSUtil.SPAN_CLASS_DELETE)) {
                System.out.format("\33[31;2m%s", s);
            } else {
                System.out.format("\33[39;2m%s", s);
            }
        }
    }
    public static void println(String s, String startMark){
        if(SWITCH) {
            print(s, startMark);
            System.out.format("\33[39;2m%n");
        }
    }

    public static void main(String[] args){
        System.out.format("\33[32;42;4mddddddd%n");
        int font = 31;
        int background = 41;
        for (int i = 0; i <= 50; i++) {
            for(int j = 0; j <= 50; j++){
                System.out.format("\33[%d;%d;4m前景色是%d,背景色是%d------我是博主%n", font + i, background + j, font + i, background + j);
            }
        }
    }
}
