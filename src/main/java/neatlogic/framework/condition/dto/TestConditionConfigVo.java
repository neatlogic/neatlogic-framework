/*
 * Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package neatlogic.framework.condition.dto;

import java.util.List;

public class TestConditionConfigVo extends ConditionConfigBaseVo<TestConditionConfigVo.TestConditionGroupVo<TestConditionConfigVo.TestConditionVo>> {

    public static void main(String[] args) {
        TestConditionConfigVo testConditionConfigVo = new TestConditionConfigVo();
        List<TestConditionGroupVo<TestConditionVo>> conditionGroupList1 = testConditionConfigVo.getConditionGroupList();
        for (TestConditionGroupVo<TestConditionVo> conditionGroupVo : conditionGroupList1) {
            System.out.println("conditionGroupVo.getId() = " + conditionGroupVo.getId());
            List<TestConditionVo> conditionList = conditionGroupVo.getConditionList();
            for (TestConditionVo conditionVo : conditionList) {
                System.out.println("conditionVo.getUuid() = " + conditionVo.getUuid());
                System.out.println("conditionVo.getId() = " + conditionVo.getId());
            }
        }
    }

    public static class TestConditionVo extends ConditionBaseVo {
        private String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
    public static class TestConditionGroupVo<T extends ConditionBaseVo> extends ConditionGroupBaseVo<TestConditionVo> {
        private String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
}
