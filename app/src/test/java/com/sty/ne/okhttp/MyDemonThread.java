package com.sty.ne.okhttp;

/**
 * @Author: tian
 * @Date: 2020/9/6 19:40
 */
public class MyDemonThread {
    public static void main(String[] args) {
        Thread thread = new Thread(){
            @Override
            public void run() {
                super.run();
                while (true) {
                    try {
                        Thread.sleep(0);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        System.out.println("run ...");
                    }
                }
            }
        };
        //守护线程
        thread.setDaemon(true);
        thread.start();
    }

    // run ...
    // run ...
    // run ...
    // Class transformation time: 0.0119051s for 113 classes or 1.0535486725663717E-4s per class
    // run ...
    // run ...
    // Process finished with exit code 0
}
