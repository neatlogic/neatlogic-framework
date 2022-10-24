package codedriver.framework.file.core.util;

/**
 * 调用门
 */
public interface InvocationGate {

    /**
     * 如果返回值为true，此方法的调用方可以决定跳过进一步的工作。
     * 实现应该能够给出合理的答案，即使当前时间日期不可用。
     *
     * @param currentTime 可以是TIME_UNAVAILABLE（-1），表示时间不可用
     * @return 如果为true，调用方应跳过进一步的工作
     */
    boolean isTooSoon(long currentTime);

}