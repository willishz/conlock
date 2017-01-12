package org.willishz.conlock.service;

import java.math.BigDecimal;

/**
 * @author willishz Lu
 */
public interface ConlockService {

    String ReentrantLock = "ReentrantLock";

    String LinkedBlockingQueue = "LinkedBlockingQueue";

    boolean registerKey(String key, String className, BigDecimal limit);

    boolean tryLock(String key, Long expire);

    boolean unlock(String key, Long expire);

    boolean offer(String key, PurchaseAction element);

    PurchaseAction poll(String key);
}
