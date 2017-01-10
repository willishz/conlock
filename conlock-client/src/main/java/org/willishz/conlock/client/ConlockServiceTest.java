package org.willishz.conlock.client;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ContextConfiguration;
import org.willishz.conlock.service.ConlockService;

/**
 * @author willishz Lu
 */
@RunWith(ConlockSpringJUnit4ClassWithLog4jRunner.class)
@ContextConfiguration(locations = { "classpath*:test-spring.xml" })
@Service
public class ConlockServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(ConlockServiceTest.class);

    @Autowired
    private ConlockService conlockService;

    @Test
    public void triggerRule() {
        conlockService.lock();
        conlockService.unlock();
    }

}

