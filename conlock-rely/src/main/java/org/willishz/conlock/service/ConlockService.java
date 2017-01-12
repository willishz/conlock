package org.willishz.conlock.service;

/**
 * @author willishz Lu
 */
public interface ConlockService {

    static final String ReentrantLock = "ReentrantLock";

    boolean registerKey(String key, String className);

    boolean tryLock(String key, Long expire);

    boolean unlock(String key, Long expire);

}
