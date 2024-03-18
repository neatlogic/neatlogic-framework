/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

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
