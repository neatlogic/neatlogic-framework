/*Copyright (C) $today.year  深圳极向量科技有限公司 All Rights Reserved.

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

package neatlogic.framework.file.core.recovery;

/**
 * 恢复协调员
 */
public class RecoveryCoordinator {

    public final static long BACKOFF_COEFFICIENT_MIN = 20;
    public final static long BACKOFF_MULTIPLIER = 4;
    static long BACKOFF_COEFFICIENT_MAX = 327680; // BACKOFF_COEFFICIENT_MIN * 4^7

    private long backOffCoefficient = BACKOFF_COEFFICIENT_MIN;

    private static long UNSET = -1;
    // 测试可以独立于系统时钟直接设置时间
    private long currentTime = UNSET;
    private long next;

    public RecoveryCoordinator() {
        next = getCurrentTime() + getBackoffCoefficient();
    }

    public RecoveryCoordinator(long currentTime) {
        this.currentTime = currentTime;
        next = getCurrentTime() + getBackoffCoefficient();
    }

    /**
     * 是不是太早了
     * @return
     */
    public boolean isTooSoon() {
        long now = getCurrentTime();
        if (now > next) {
            next = now + getBackoffCoefficient();
            return false;
        } else {
            return true;
        }
    }

    void setCurrentTime(long forcedTime) {
        currentTime = forcedTime;
    }

    private long getCurrentTime() {
        if (currentTime != UNSET) {
            return currentTime;
        }
        return System.currentTimeMillis();
    }

    private long getBackoffCoefficient() {
        long currentCoeff = backOffCoefficient;
        if (backOffCoefficient < BACKOFF_COEFFICIENT_MAX) {
            backOffCoefficient *= BACKOFF_MULTIPLIER;
        }
        return currentCoeff;
    }
}
