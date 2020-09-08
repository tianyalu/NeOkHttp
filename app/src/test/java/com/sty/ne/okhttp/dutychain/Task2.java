package com.sty.ne.okhttp.dutychain;

/**
 * @Author: tian
 * @UpdateDate: 2020/9/7 8:13 PM
 */
public class Task2 extends BaseTask {
    public Task2(boolean isTask) {
        super(isTask);
    }

    @Override
    public void doAction() {
        //执行子节点任务
        System.out.println("Task2 任务节点二执行了");
    }
}
