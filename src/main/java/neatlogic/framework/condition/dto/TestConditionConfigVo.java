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
