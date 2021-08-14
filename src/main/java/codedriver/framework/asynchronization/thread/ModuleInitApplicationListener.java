/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.asynchronization.thread;

import java.util.concurrent.Phaser;

public class ModuleInitApplicationListener {
    /**
     * 因为root-context.xml，所以初始化计数器值为1
     **/
    private final static Phaser moduleInitPhaser = new Phaser(1);

    public static Phaser getModuleinitphaser() {
        return moduleInitPhaser;
    }

}
