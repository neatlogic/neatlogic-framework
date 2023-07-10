/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package neatlogic.framework.util;

import java.util.Random;

public class PassWordUtil {
    /*There are three levels of password validation policy enforced when Validate Password plugin is enabled:
           LOW    Length >= 8 characters.
           MEDIUM Length >= 8, numeric, mixed case, and special characters.
           STRONG Length >= 8, numeric, mixed case, special characters and dictionary file.
           default is MEDIUM
       */
    public static String createRandomPassWord() {
        Random rand = new Random();
        StringBuilder password = new StringBuilder();
        String[] chars = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
        String[] specialChars = new String[]{"#", ".", "*", "$"};
        String[] nums = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
        for (int i = 0; i < 10; i++) {
            int randNumber = rand.nextInt(chars.length);
            password.append(chars[randNumber]);
        }
        password.append(specialChars[rand.nextInt(specialChars.length)]);
        password.append(nums[rand.nextInt(nums.length)]);
        return password.toString();
    }
}
