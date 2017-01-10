package org.willishz.conlock.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.willishz.conlock.service.ConlockService;

/**
 * @author willishz Lu
 */
@Service
public class ConlockServiceImpl implements ConlockService {

    private static final Logger logger = LoggerFactory.getLogger(ConlockServiceImpl.class);

    public boolean lock() {
        logger.info("lock");
        return false;
    }

    public boolean unlock() {
        logger.info("unlock");
        return false;
    }
}
