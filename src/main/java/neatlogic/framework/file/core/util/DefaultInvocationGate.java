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

package neatlogic.framework.file.core.util;

/**
 * 此类充当在关键执行路径上调用“代价高昂”操作的网关。
 */
public class DefaultInvocationGate implements InvocationGate {

    static final int MASK_DECREASE_RIGHT_SHIFT_COUNT = 2;

    // 实验表明，即使对于使用200个或更多线程的CPU密集型应用程序，MASK值的顺序为0xFFFF也是合适的
    private static final int MAX_MASK = 0xFFFF;
    static final int DEFAULT_MASK = 0xF;

    private volatile long mask = DEFAULT_MASK;
    //private volatile long lastMaskCheck = System.currentTimeMillis();

    // 重要提示：此字段可以由多个线程更新。因此，其值可能*不*按顺序递增。然而，除了表达式（invocationCounter++&mask）==mask）有时应该为true之外，我们不关心字段的实际值。
    private long invocationCounter = 0;

    // 如果调用updateMaskIfNecessary()方法之间的间隔时间小于thresholdForMaskIncrease毫秒，则应增加掩码
    private static final long MASK_INCREASE_THRESHOLD = 100;

    // 如果updateMaskIfNecessary()方法的调用间隔超过thresholdForMaskDecrease毫秒，则应减少掩码
    private static final long MASK_DECREASE_THRESHOLD = MASK_INCREASE_THRESHOLD * 8;


    public DefaultInvocationGate() {
        this(MASK_INCREASE_THRESHOLD, MASK_DECREASE_THRESHOLD, System.currentTimeMillis());
    }

    public DefaultInvocationGate(long minDelayThreshold, long maxDelayThreshold, long currentTime) {
        this.minDelayThreshold = minDelayThreshold;
        this.maxDelayThreshold = maxDelayThreshold;
        this.lowerLimitForMaskMatch = currentTime + minDelayThreshold;
        this.upperLimitForNoMaskMatch = currentTime + maxDelayThreshold;
    }

    private long minDelayThreshold;
    private long maxDelayThreshold;

    long lowerLimitForMaskMatch;
    long upperLimitForNoMaskMatch;

    @Override
    final public boolean isTooSoon(long currentTime) {
        boolean maskMatch = ((invocationCounter++) & mask) == mask;

        if (maskMatch) {
            if (currentTime < this.lowerLimitForMaskMatch) {
                increaseMask();
            }
            updateLimits(currentTime);
        } else {
            if (currentTime > this.upperLimitForNoMaskMatch) {
                decreaseMask();
                updateLimits(currentTime);
                return false;
            }
        }
        return !maskMatch;
    }

    private void updateLimits(long currentTime) {
        this.lowerLimitForMaskMatch = currentTime + minDelayThreshold;
        this.upperLimitForNoMaskMatch = currentTime + maxDelayThreshold;
    }


    // 包私有，仅用于测试目的
    long getMask() {
        return mask;
    }

    private void increaseMask() {
        if (mask >= MAX_MASK)
            return;
        mask = (mask << 1) | 1;
    }

    private void decreaseMask() {
        mask = mask >>> MASK_DECREASE_RIGHT_SHIFT_COUNT;
    }

    public long getInvocationCounter() {
        return invocationCounter;
    }
}
