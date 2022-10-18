package codedriver.framework.file.core.util;

/**
 * 如果之前已经设置了中断标志，则允许屏蔽中断标志。不做其他事情。
 *
 * Typical use:
 *
 * <pre>
 * InterruptUtil interruptUtil = new InterruptUtil(context);
 *
 * try {
 *   interruptUtil.maskInterruptFlag();
 *   someOtherThread.join(delay);
 * } catch(InterruptedException e) {
 *   // reachable only if join does not succeed within delay.
 *   // Without the maskInterruptFlag() call, the join() would have returned immediately
 *   // had the current thread been interrupted previously, i.e. before entering the above block
 * } finally {
 *   interruptUtil.unmaskInterruptFlag();
 * }
 * </pre>
 */
public class InterruptUtil {

    final boolean previouslyInterrupted;

    public InterruptUtil() {
        super();
        previouslyInterrupted = Thread.currentThread().isInterrupted();
    }

    public void maskInterruptFlag() {
        if (previouslyInterrupted) {
            Thread.interrupted();
        }
    }

    public void unmaskInterruptFlag() {
        if (previouslyInterrupted) {
            try {
                Thread.currentThread().interrupt();
            } catch (SecurityException se) {
//                addError("Failed to intrreupt current thread", se);
            }
        }
    }

}
