package org.willishz.conlock.client;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ContextConfiguration;
import org.willishz.conlock.service.ConlockService;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author willishz Lu
 */
@RunWith(ConlockSpringJUnit4ClassWithLog4jRunner.class)
@ContextConfiguration(locations = {"classpath:test-spring.xml"})
@Service
public class ConlockServiceTest {

    private static final Logger log = LoggerFactory.getLogger(ConlockServiceTest.class);

    private volatile int flag1 = 0;
    private volatile int flag2 = 0;
    private volatile int flag3 = 0;
    private volatile int flag4 = 0;
    private volatile int flag5 = 0;

    @Autowired
    private ConlockService conlockService;

    @Test
    public void test() {
        String key1 = "goods_1_inventory";
        String key2 = "goods_2_inventory";
        String key3 = "goods_3_inventory";
        String key4 = "goods_4_inventory";
        String key5 = "goods_5_inventory";
        long expire = 1000L;
        int loop = 100;

        conlockService.registerKey(key1, ConlockService.ReentrantLock);
        conlockService.registerKey(key2, ConlockService.ReentrantLock);
        conlockService.registerKey(key3, ConlockService.ReentrantLock);
        conlockService.registerKey(key4, ConlockService.ReentrantLock);
        conlockService.registerKey(key5, ConlockService.ReentrantLock);

        System.out.println();

        ExecutorService executorService = Executors.newFixedThreadPool(loop);        // 创建线程池并返回ExecutorService实例
        for (int i = 0; i < loop; i++) {
//            new ConlockClientCallable(key1, expire, i, false, conlockService).call();
            executorService.execute(new FutureTask(new ConlockClientCallable(key1, expire, i, false, conlockService)));
            executorService.execute(new FutureTask(new ConlockClientCallable(key2, expire, i, false, conlockService)));
            executorService.execute(new FutureTask(new ConlockClientCallable(key3, expire, i, false, conlockService)));
            executorService.execute(new FutureTask(new ConlockClientCallable(key4, expire, i, false, conlockService)));
            executorService.execute(new FutureTask(new ConlockClientCallable(key5, expire, i, false, conlockService)));
        }
        executorService.shutdown();

        try {
            while (!executorService.isTerminated()) {
                if (executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                    log.info("thread pool close successfully, flag:" + flag1 + " " + flag2 + " " + flag3 + " " + flag4 + " " + flag5);
                    System.exit(0);
                } else {
                    log.info("Waiting thread pool close...");
                }
            }
        } catch (InterruptedException ex) { // should not reach here
            ex.printStackTrace();
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        } catch (Exception ex) { // should not reach here
            ex.printStackTrace();
            executorService.shutdownNow();
        }
    }

    class ConlockClientCallable implements Callable<Boolean> {

        private ConlockService conlockService;
        private String key;
        private Long expire;
        private Integer action;
        private boolean waitLocking;

        public ConlockClientCallable(String key, Long expire, Integer action, boolean waitLocking, ConlockService conlockService) {
            this.key = key;
            this.expire = expire;
            this.action = action;
            this.waitLocking = waitLocking;
            this.conlockService = conlockService;
        }

        public Boolean business() {
            // busniess logic
            for (int i = 0; i < action; i++) {
                switch (Integer.parseInt(key.split("_")[1])) {
                    case 1 : flag1++; flag1--; break;
                    case 2 : flag2++; flag2--; break;
                    case 3 : flag3++; flag3--; break;
                    case 4 : flag4++; flag4--; break;
                    case 5 : flag5++; flag5--; break;
                }
            }
            return true;
        }

        public Boolean call() {
            if (waitLocking) {
                boolean isLockSuccess = false;
                int lockCounter = 0;
                for (int i = 0; i < 100; i++) { // tryLock retry 100 times
                    boolean trylock = conlockService.tryLock(key, expire);
                    System.out.println(Thread.currentThread().getId() + " trylock:" + trylock);
                    if (!trylock) {
                        try {
                            System.out.println(Thread.currentThread().getId() + " lock failed:" + ++lockCounter);
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        isLockSuccess = true;
                        break;
                    }
                }
                if (!isLockSuccess) {
                    return false;
                }
            } else {
                boolean resultLock = conlockService.tryLock(key, expire);
                if (!resultLock) {
                    return false;
                }
                System.out.println(Thread.currentThread().getId() + " lock:" + resultLock);
            }
            try {
                // busniess logic
                business();
            } finally {
                boolean resultUnlock = conlockService.unlock(key, expire);
                if (resultUnlock) {
                    System.out.println(Thread.currentThread().getId() + " unlock success, flag:" + flag1 + " " + flag2 + " " + flag3 + " " + flag4 + " " + flag5);
                    return true;
                } else {
                    System.out.println(Thread.currentThread().getId() + " unlock failed");
                    return false;
                }
            }
        }
    }
}
