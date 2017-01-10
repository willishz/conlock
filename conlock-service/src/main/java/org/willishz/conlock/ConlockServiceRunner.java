package org.willishz.conlock;

/**
 * @author willishz Lu
 */
public class ConlockServiceRunner {

    public static void main(String[] args) {
        try {
            ConlockServiceLocator.getApplicationContext();
            while (System.in.available() == 0) {
                Thread.sleep(5000);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(100);
        }
    }
}
