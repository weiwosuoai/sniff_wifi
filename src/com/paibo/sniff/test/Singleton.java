package com.paibo.sniff.test;

/**
 * The test of single mode
 * 
 * @author jiangbing
 *
 */
public class Singleton {
	
	private static Object obj = new Object();
	
	private static Singleton instance = null;
	
	private Singleton() {}
	
	public static Singleton getInstance() {
		// If already inited, no need to get lock everytime
		if (instance == null) {
            synchronized (obj) {
                if (instance == null) {
                    instance = new Singleton();
                }
            }
        }

        return instance;
	}
}
