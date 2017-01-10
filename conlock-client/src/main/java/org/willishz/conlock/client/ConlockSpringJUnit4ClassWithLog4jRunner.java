package org.willishz.conlock.client;

import org.junit.runners.model.InitializationError;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Log4jConfigurer;

import java.io.FileNotFoundException;

/**
 * @author willishz Lu
 */
public class ConlockSpringJUnit4ClassWithLog4jRunner extends SpringJUnit4ClassRunner {
    static {
        try {
            Log4jConfigurer.initLogging("classpath:log4j.properties");
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }
    public ConlockSpringJUnit4ClassWithLog4jRunner(Class<?> clazz) throws InitializationError {
        super(clazz);
    }
}
