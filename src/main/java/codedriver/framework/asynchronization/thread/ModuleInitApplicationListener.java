package codedriver.framework.asynchronization.thread;

import codedriver.framework.applicationlistener.core.ApplicationListenerBase;
import codedriver.framework.common.RootComponent;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.concurrent.Phaser;

@RootComponent
public class ModuleInitApplicationListener extends ApplicationListenerBase {
    /**
     * 因为root-context.xml，所以初始化计数器值为1
     **/
    private final static Phaser moduleInitPhaser = new Phaser(1);

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        /* 模块加载完成，计数器减一 **/
        moduleInitPhaser.arrive();
    }

    @Override
    protected void myInit() {

    }

    public static Phaser getModuleinitphaser() {
        return moduleInitPhaser;
    }

}
