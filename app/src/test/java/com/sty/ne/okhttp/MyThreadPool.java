package com.sty.ne.okhttp;

import org.junit.Test;

import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MyThreadPool {
    public static void main(String[] args) {
        threadPool();
    }

    /**
     * 传统的创建线程的方法
     */
    @Test
    public void traditionalNewThread() {
        new Thread(){
            @Override
            public void run() {
                super.run();
            }
        }.start();
    }

    /**
     * Java1.5 线程池(如何让那么多线程复习，实现线程池管理工作) 复用
     * Executor
     *     --- ExecutorService
     *         --- AbstractExecutorService
     *             --- ThreadPoolExecutor（学习此类）
     */
    @Test
    public static void threadPool() {
        /**
         * 线程池中只有一个核心线程在工作
         * corePoolSize: 核心线程数
         * maximumPoolSize:最大线程池数量，线程池规定大小
         * keepAliveTime: 线程存活时间（如果线程池当前拥有超过corePoolSize的线程，那么多余的线程在空闲时间超过keepAliveTime时会被终止）
         * TimeUnit: 时间单位：秒
         * BlockingQueue<Runnable>: workQueue队列（用于存放提交的任务，队列的实际容量与线程池大小相关联）
         *                          如果当前线程池任务线程数量小于核心线程池数量，执行器总是优先创建一个任务线程，而不是从线程队列中取一个空闲线程
         *                          如果当前线程池任务线程数量大于核心线程池数量，执行器总是优先从线程队列中取一个空闲线程，而不是创建一个任务线程。
         *                          如果当前线程池任务线程数量大于核心线程池数量，且队列中无空闲任务线程，将会创建一个任务线程，直到超出maximumPoolSize，如果超时maximumPoolSize，则任务将会被拒绝
         */
        //单例线程池
//        ExecutorService executorService = new ThreadPoolExecutor(1, 1,
//                 0, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>());
        // 当前线程执行耗时任务，线程是：pool-1-thread-1
        // 当前线程执行耗时任务，线程是：pool-1-thread-1
        // 当前线程执行耗时任务，线程是：pool-1-thread-1
        // 当前线程执行耗时任务，线程是：pool-1-thread-1
        // ...

//        ExecutorService executorService = new ThreadPoolExecutor(5, 1,
//                60, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>());
        // Exception in thread "main" java.lang.IllegalArgumentException
        // at java.util.concurrent.ThreadPoolExecutor.<init>(ThreadPoolExecutor.java:1314)
        // at java.util.concurrent.ThreadPoolExecutor.<init>(ThreadPoolExecutor.java:1202)
        // at com.sty.ne.okhttp.MyThreadPool.threadPool(MyThreadPool.java:53)
        // at com.sty.ne.okhttp.MyThreadPool.main(MyThreadPool.java:13)

//        ExecutorService executorService = new ThreadPoolExecutor(5, 10,
//                60, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>());
        // 当前线程执行耗时任务，线程是：pool-1-thread-2
        // 当前线程执行耗时任务，线程是：pool-1-thread-5
        // 当前线程执行耗时任务，线程是：pool-1-thread-4
        // 当前线程执行耗时任务，线程是：pool-1-thread-3
        // 当前线程执行耗时任务，线程是：pool-1-thread-1
        // 当前线程执行耗时任务，线程是：pool-1-thread-4
        // ...

        //缓存线程池方案（复用）
//        ExecutorService executorService = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
//                60, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>());
        // 当前线程执行耗时任务，线程是：pool-1-thread-1
        // 当前线程执行耗时任务，线程是：pool-1-thread-1
        // 当前线程执行耗时任务，线程是：pool-1-thread-1
        // 当前线程执行耗时任务，线程是：pool-1-thread-1

        //线程池工厂
        ExecutorService executorService = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                60, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(),
                new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable runnable) {
                        Thread thread = new Thread();
                        thread.setName("My OkHttp Dispatcher");
                        thread.setDaemon(false);
                        return thread;
                    }
                });

        System.out.println("当前线程执行耗时任务，线程是：" + Thread.currentThread().getName());
        for (int i = 0; i < 20; i++) {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                        System.out.println("当前线程执行耗时任务，线程是：" + Thread.currentThread().getName());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    /**
     * 缓存线程池（复用）
     */
    public static void cachedThreadPool() {
        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.execute(new Runnable() {
            @Override
            public void run() {

            }
        });
    }

    /**
     * 单例线程池
     */
    public static void singleThreadPool() {
        final ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {

            }
        });
    }

    /**
     * 固定大小的线程池
     */
    public static void fixedThreadPool() {
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        executorService.execute(new Runnable() {
            @Override
            public void run() {

            }
        });
    }
}
