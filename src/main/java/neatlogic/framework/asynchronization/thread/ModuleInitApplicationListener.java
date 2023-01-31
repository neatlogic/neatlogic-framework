/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.asynchronization.thread;

import java.util.concurrent.Phaser;

public class ModuleInitApplicationListener {
    private final static Phaser moduleInitPhaser = new Phaser(0);

    public static Phaser getModuleinitphaser() {
        return moduleInitPhaser;
    }

}
