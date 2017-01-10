package org.willishz.conlock.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.willishz.conlock.service.ConlockService;

/**
 * @author willishz Lu
 */
public class ConlockClientRunner {

    @Autowired
    private ConlockService conlockService;

    class ConlockClientThread implements Runnable {

        private ConlockService conlockService;

        public ConlockClientThread(ConlockService conlockService) {
            this.conlockService = conlockService;
        }

        @Override
        public void run() {
            conlockService.lock();
            conlockService.unlock();
        }
    }
}
