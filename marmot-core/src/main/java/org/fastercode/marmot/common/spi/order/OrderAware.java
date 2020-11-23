package org.fastercode.marmot.common.spi.order;

/**
 * Order aware.
 *
 * @param <T> type
 */
public interface OrderAware<T> {

    /**
     * Get order of load.
     * 数字越小, 优先级越高
     *
     * @return load order
     */
    default int getOrder() {
        return 100;
    }

    /**
     * Get type.
     *
     * @return type
     */
    default T getType() {
        return null;
    }
}
