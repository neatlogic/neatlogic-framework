/*
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
 */

package neatlogic.framework.file.core.layout;

import ch.qos.logback.core.CoreConstants;
import neatlogic.framework.file.core.IEvent;

public class PatternLayout extends PatternLayoutBase<IEvent> {

    public String doLayout(IEvent event) {
//        System.out.println("b2");
        if (!isStarted()) {
            return CoreConstants.EMPTY_STRING;
        }
        return event.getFormattedMessage();
    }
}
