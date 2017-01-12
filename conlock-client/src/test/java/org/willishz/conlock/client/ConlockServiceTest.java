package org.willishz.conlock.client;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ContextConfiguration;
import org.willishz.conlock.service.ConlockService;
import org.willishz.conlock.service.PurchaseAction;

import java.math.BigDecimal;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

/**
 * client sample test
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
    public void testLinkedBlockingQueue() {
        String key1 = "loan_1_amount";
        String key2 = "loan_2_amount";
        String key3 = "loan_3_amount";
        String key4 = "loan_4_amount";
        String key5 = "loan_5_amount";
        long expire = 1000L;
        int loop = 100;
        int amount = 10;

        conlockService.registerKey(key1, ConlockService.LinkedBlockingQueue, new BigDecimal(amount * loop / 5));
        conlockService.registerKey(key2, ConlockService.LinkedBlockingQueue, new BigDecimal(amount * loop / 5));
        conlockService.registerKey(key3, ConlockService.LinkedBlockingQueue, new BigDecimal(amount * loop / 5));
        conlockService.registerKey(key4, ConlockService.LinkedBlockingQueue, new BigDecimal(amount * loop / 5));
        conlockService.registerKey(key5, ConlockService.LinkedBlockingQueue, new BigDecimal(amount * loop / 5));

        System.out.println();
        Random random = new Random();

//        PurchaseAction qa = new PurchaseAction(1, new BigDecimal(random.nextInt(10) + 1));
//        System.out.println(conlockService.offer(key1, qa));
//        qa = new PurchaseAction(1, new BigDecimal(random.nextInt(10) + 1));
//        System.out.println(conlockService.offer(key1, qa));
//        qa = new PurchaseAction(1, new BigDecimal(random.nextInt(10) + 1));
//        System.out.println(conlockService.offer(key1, qa));
//        qa = new PurchaseAction(1, new BigDecimal(random.nextInt(10) + 1));
//        System.out.println(conlockService.offer(key1, qa));

        ExecutorService executorService = Executors.newFixedThreadPool(loop);
        for (int i = 0; i < loop; i++) {
            executorService.execute(new FutureTask(new LinkedBlockingQueueCallable(key1, new PurchaseAction(1, new BigDecimal(random.nextInt(amount) + 1)), conlockService)));
            executorService.execute(new FutureTask(new LinkedBlockingQueueCallable(key2, new PurchaseAction(1, new BigDecimal(random.nextInt(amount) + 1)), conlockService)));
            executorService.execute(new FutureTask(new LinkedBlockingQueueCallable(key3, new PurchaseAction(1, new BigDecimal(random.nextInt(amount) + 1)), conlockService)));
            executorService.execute(new FutureTask(new LinkedBlockingQueueCallable(key4, new PurchaseAction(1, new BigDecimal(random.nextInt(amount) + 1)), conlockService)));
            executorService.execute(new FutureTask(new LinkedBlockingQueueCallable(key5, new PurchaseAction(1, new BigDecimal(random.nextInt(amount) + 1)), conlockService)));
        }
        executorService.shutdown();

        try {
            while (!executorService.isTerminated()) {
                if (executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                    log.info("thread pool close successfully");
                    PurchaseAction pa = null;
                    do {
                        pa = conlockService.poll(key1);
                        if (pa != null) {
                            System.out.println(key1 + " " + pa.toString());
                        }
                    } while (pa != null);
                    do {
                        pa = conlockService.poll(key2);
                        if (pa != null) {
                            System.out.println(key2 + " " + pa.toString());
                        }
                    } while (pa != null);
                    do {
                        pa = conlockService.poll(key3);
                        if (pa != null) {
                            System.out.println(key3 + " " + pa.toString());
                        }
                    } while (pa != null);
                    do {
                        pa = conlockService.poll(key4);
                        if (pa != null) {
                            System.out.println(key4 + " " + pa.toString());
                        }
                    } while (pa != null);
                    do {
                        pa = conlockService.poll(key5);
                        if (pa != null) {
                            System.out.println(key5 + " " + pa.toString());
                        }
                    } while (pa != null);
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

//    @Test
    public void testReentrantLock() {
        String key1 = "goods_1_inventory";
        String key2 = "goods_2_inventory";
        String key3 = "goods_3_inventory";
        String key4 = "goods_4_inventory";
        String key5 = "goods_5_inventory";
        long expire = 1000L;
        int loop = 100;

        conlockService.registerKey(key1, ConlockService.ReentrantLock, null);
        conlockService.registerKey(key2, ConlockService.ReentrantLock, null);
        conlockService.registerKey(key3, ConlockService.ReentrantLock, null);
        conlockService.registerKey(key4, ConlockService.ReentrantLock, null);
        conlockService.registerKey(key5, ConlockService.ReentrantLock, null);

        System.out.println();

        ExecutorService executorService = Executors.newFixedThreadPool(loop);
        for (int i = 0; i < loop; i++) {
            executorService.execute(new FutureTask(new ReentrantLockCallable(key1, expire, i, false, conlockService)));
            executorService.execute(new FutureTask(new ReentrantLockCallable(key2, expire, i, false, conlockService)));
            executorService.execute(new FutureTask(new ReentrantLockCallable(key3, expire, i, false, conlockService)));
            executorService.execute(new FutureTask(new ReentrantLockCallable(key4, expire, i, false, conlockService)));
            executorService.execute(new FutureTask(new ReentrantLockCallable(key5, expire, i, false, conlockService)));
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

    /**
     * LinkedBlockingQueue test thread class
     */
    class LinkedBlockingQueueCallable implements Callable<Boolean> {

        private ConlockService conlockService;
        private String key;
        private PurchaseAction purchaseAction;

        public LinkedBlockingQueueCallable(String key, PurchaseAction purchaseAction, ConlockService conlockService) {
            this.key = key;
            this.purchaseAction = purchaseAction;
            this.conlockService = conlockService;
        }

        public Boolean call() {
            boolean result = conlockService.offer(key, purchaseAction);
            System.out.println("offer:" + result);
            return result;
        }
    }

    /**
     * ReentrantLock test thread class
     */
    class ReentrantLockCallable implements Callable<Boolean> {

        private ConlockService conlockService;
        private String key;
        private Long expire;
        private Integer action;
        private boolean waitLocking;

        public ReentrantLockCallable(String key, Long expire, Integer action, boolean waitLocking, ConlockService conlockService) {
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

