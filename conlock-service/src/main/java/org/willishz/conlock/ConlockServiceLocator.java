package org.willishz.conlock;

import com.site.spring.DefaultServiceLocator;
import org.springframework.context.ApplicationContext;

/**
 * @author willishz Lu
 */
public class ConlockServiceLocator extends DefaultServiceLocator {

    /**
     * The context.
     */
    private static ApplicationContext context;

    static {
        try {
            context = getApplicationContext(ConlockServiceLocator.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the context.
     *
     * @return the context
     */
    public static ApplicationContext getApplicationContext() {
        if (context == null) {
            throw new RuntimeException("Spring loading error!");
        }
        return context;
    }

}
