package com.sty.ne.okhttp.dutychain2;

/**
 * 对应OkHttp源码 --> getResponseWithInterceptorChain
 * @Author: tian
 * @UpdateDate: 2020/9/7 9:41 PM
 */
public class Test {

    public static void main(String[] args) {
        ChainManager chainManager = new ChainManager();
        chainManager.addTask(new Task1());
        chainManager.addTask(new Task2());
        chainManager.addTask(new Task3());

        chainManager.doRunAction("ok", chainManager);
    }
}
