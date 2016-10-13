
package com.brian.testandroid.common;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程池管理类 
 * 使用:ThreadManager.getPoolProxy().execute(runnable);
 * 
 * @author huamm
 */
public class ThreadPoolManager {
    private static final String TAG = ThreadPoolManager.class.getSimpleName();
    private static ThreadPoolProxy poolProxy;

    public static ThreadPoolProxy getPoolProxy() {
        if (poolProxy == null) {
            synchronized (TAG) {
                if (poolProxy == null) {
                    int processorCount = Runtime.getRuntime().availableProcessors();
                    int maxAvailable = Math.max(processorCount * 3, 10);
                    poolProxy = new ThreadPoolProxy(processorCount, maxAvailable, 15000);
                }
            }
        }
        return poolProxy;
    }

    public static class ThreadPoolProxy {

        private ThreadPoolExecutor  threadPoolExecutor;     // 线程池

        private int                 corePoolSize;           //线程池中核心线程数

        private int                 maximumPoolSize;        //线程池中最大线程数，若并发数高于该数，后面的任务则会等待

        private int                 keepAliveTime;          // 超出核心线程数的线程在执行完后保持alive时长

        /**
         * @param keepAliveTime time in milliseconds
         */
        public ThreadPoolProxy(int corePoolSize, int maximumPoolSize,
                int keepAliveTime) {
            this.corePoolSize       = corePoolSize;
            this.maximumPoolSize    = maximumPoolSize;
            this.keepAliveTime      = keepAliveTime;
        }

        public void execute(Runnable runnable) {
            if (runnable == null) {
                return;
            } else {
                if (threadPoolExecutor == null || threadPoolExecutor.isShutdown()) {
                    synchronized (TAG) {
                        if (threadPoolExecutor == null || threadPoolExecutor.isShutdown()) {
                            threadPoolExecutor = createExecutor();
                            threadPoolExecutor.allowCoreThreadTimeOut(false); // 核心线程始终不消失
                        }
                    }
                }
                threadPoolExecutor.execute(runnable);
            }
        }
        
        private ThreadPoolExecutor createExecutor() {
            return new ThreadPoolExecutor(corePoolSize,
                    maximumPoolSize, keepAliveTime,
                    TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<Runnable>(),
                    new DefaultThreadFactory(Thread.NORM_PRIORITY, "pingo-pool-"), 
                    new AbortPolicy());
        }
    }
    
    /**
     * 创建线程的工厂，设置线程的优先级，group，以及命名
     */
    private static class DefaultThreadFactory implements ThreadFactory {

        private static final AtomicInteger poolNumber   = new AtomicInteger(1); // 线程池的计数

        private final AtomicInteger threadNumber        = new AtomicInteger(1); // 线程的计数
        
        private final ThreadGroup   group;
        private final String        namePrefix;
        private final int           threadPriority;

        DefaultThreadFactory(int threadPriority, String threadNamePrefix) {
            this.threadPriority = threadPriority;
            this.group = Thread.currentThread().getThreadGroup();
            namePrefix = threadNamePrefix + poolNumber.getAndIncrement() + "-thread-";
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
            if (t.isDaemon()) {
                t.setDaemon(false);
            }
            t.setPriority(threadPriority);
            return t;
        }
    }
}
