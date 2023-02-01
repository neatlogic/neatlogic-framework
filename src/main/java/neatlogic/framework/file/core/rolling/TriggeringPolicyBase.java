/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.file.core.rolling;

abstract public class TriggeringPolicyBase<E> implements TriggeringPolicy<E> {

    private boolean start;

    public void start() {
        start = true;
    }

    public void stop() {
        start = false;
    }

    public boolean isStarted() {
        return start;
    }

}
