/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
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
