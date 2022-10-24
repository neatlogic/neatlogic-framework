/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.file.core.encoder;

import codedriver.framework.file.core.IEvent;
import codedriver.framework.file.core.layout.PatternLayout;

public class PatternLayoutEncoder extends PatternLayoutEncoderBase<IEvent> {

    @Override
    public void start() {
        PatternLayout patternLayout = new PatternLayout();
        patternLayout.start();
        this.layout = patternLayout;
        super.start();
    }
}
