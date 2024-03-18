/*Copyright (C) $today.year  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

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
