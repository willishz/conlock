package org.willishz.conlock.service.impl;

import org.springframework.stereotype.Service;
import org.willishz.conlock.service.ConlockService;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author willishz Lu
 */
@Service
public class ConlockServiceImpl implements ConlockService {

    private volatile Map<String, AtomicInteger> atomicIntegerLockKeyMap = new ConcurrentHashMap();
    private volatile Map<String, Long> atomicIntegerLockExpireMap = new ConcurrentHashMap();

    /**
     * first register keys
     * @param key
     * @return
     */
    @Override
    public boolean registerKey(String key, String className) {
        if (className.equals(ConlockService.ReentrantLock)) {
            atomicIntegerLockKeyMap.put(key, new AtomicInteger(0));
            atomicIntegerLockExpireMap.put(key, 0L);
        }
        return true;
    }

    /**
     *
     * @param key
     * @param expire second
     * @return
     */
    @Override
    public boolean tryLock(String key, Long expire) {
        AtomicInteger lock = atomicIntegerLockKeyMap.get(key);
        Long createTime = atomicIntegerLockExpireMap.get(key);
        if (createTime == 0L) {
            atomicIntegerLockExpireMap.put(key, System.currentTimeMillis());
        } else if ((System.currentTimeMillis() - createTime) > expire) {
            lock.set(1);
            atomicIntegerLockExpireMap.put(key, System.currentTimeMillis());
            System.out.println("lock expired");
            return true;
        }
        boolean result = lock.compareAndSet(0, 1);
        if (result) {
            atomicIntegerLockExpireMap.put(key, System.currentTimeMillis());
        }
        return result;
    }

    /**
     *
     * @param key
     * @return
     */
    @Override
    public boolean unlock(String key, Long expire) {
        AtomicInteger lock = atomicIntegerLockKeyMap.get(key);
        Long createTime = atomicIntegerLockExpireMap.get(key);
        if (createTime == 0L) {
            throw new RuntimeException("createTime is null");
        } else if ((System.currentTimeMillis() - createTime) > expire) {
            lock.set(0);
            System.out.println("unlock expired");
            return true;
        }
        return lock.compareAndSet(1, 0);
    }

    /**
     *
     * @param key
     * @param expire second
     * @return
     */
//    @Override
//    public boolean tryLock(String key, Long expire) {
//        System.out.println(Thread.currentThread().getName());
//        ReentrantLock lock = reentrantLockKeyMap.get(key);
//        Long createTime = reentrantLockExpireMap.get(key);
//        if (createTime == 0L) {
//            reentrantLockExpireMap.put(key, System.currentTimeMillis());
//        } else if ((System.currentTimeMillis() - createTime) > expire) {
//            try {
//                lock.unlock();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            reentrantLockExpireMap.put(key, System.currentTimeMillis());
//            System.out.println("lock expired");
//            return true;
//        }
//        boolean result = lock.tryLock();
//        if (result) {
//            reentrantLockExpireMap.put(key, System.currentTimeMillis());
//        }
//        return result;
//    }

    /**
     *
     * @param key
     * @param expire second
     * @return
     */
//    @Override
//    public boolean lock(String key, Long expire) {
//        ReentrantLock lock = reentrantLockKeyMap.get(key);
//        Long createTime = reentrantLockExpireMap.get(key);
//        if (createTime == 0L) {
//            reentrantLockExpireMap.put(key, System.currentTimeMillis());
//        } else if ((System.currentTimeMillis() - createTime) > expire) {
//            try {
//                lock.unlock();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            reentrantLockExpireMap.put(key, System.currentTimeMillis());
//            System.out.println("lock expired");
//            return true;
//        }
//        lock.lock();
//        reentrantLockExpireMap.put(key, System.currentTimeMillis());
//        return true;
//    }

    /**
     *
     * @param key
     * @return
     */
//    @Override
//    public boolean unlock(String key) {
//        System.out.println(Thread.currentThread().getName());
//        ReentrantLock lock = reentrantLockKeyMap.get(key);
//        try {
//            lock.unlock();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//        return true;
//    }

}
