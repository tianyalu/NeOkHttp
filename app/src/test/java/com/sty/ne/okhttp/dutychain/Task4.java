package com.sty.ne.okhttp.dutychain;

/**
 * @Author: tian
 * @UpdateDate: 2020/9/7 8:14 PM
 */
public class Task4 extends BaseTask {
    public Task4(boolean isTask) {
        super(isTask);
    }

    @Override
    public void doAction() {
        //执行子节点任务
        System.out.println("Task4 任务节点四执行了");
    }
}
