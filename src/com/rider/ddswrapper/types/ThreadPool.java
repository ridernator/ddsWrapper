/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rider.ddswrapper.types;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import org.apache.log4j.Logger;

/**
 *
 * @author ridernator
 */
public class ThreadPool {

    private static ExecutorService executorService;

    private static int numThreads = 0;

    private static boolean initComplete = false;

    private static Logger threadPoolLogger;

    private ThreadPool() {
    }

    public static synchronized void init(final int numberOfThreads,
                                         final Logger logger) {
        if (initComplete) {
            threadPoolLogger.error("ThreadPool can only be initialised once");
        } else {
            threadPoolLogger = logger;

            final ThreadFactory threadFactory = new ThreadFactory() {
                private int threadCounter = 0;

                private final ThreadGroup threadGroup = new ThreadGroup("DDSWrapper ThreadGroup");

                @Override
                public Thread newThread(final Runnable runnable) {
                    return new Thread(threadGroup, runnable, "DDSWrapper Receive Thread " + threadCounter++);
                }
            };

            if (numberOfThreads <= 0) {
                executorService = Executors.newCachedThreadPool(threadFactory);

                threadPoolLogger.info("Unbounded ThreadPool created");
            } else {
                executorService = Executors.newFixedThreadPool(numberOfThreads, threadFactory);

                threadPoolLogger.info("ThreadPool created with " + numberOfThreads + " threads");
            }

            numThreads = numberOfThreads;
            initComplete = true;
        }
    }

    public static void addTask(final Runnable runnable) {
        if (initComplete) {
            executorService.execute(runnable);
        } else {
            threadPoolLogger.error("Cannot add task to ReceiveThreadPool before it is initialised");
        }
    }

    public static int getNumberOfThreads() {
        return numThreads;
    }
}
