package org.willishz.conlock.service;

/**
 * @author willishz Lu
 */
public interface ConlockService {
    boolean lock();

    boolean unlock();
}
