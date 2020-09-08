package com.sty.ne.okhttp.dutychain;

/**
 * @Author: tian
 * @UpdateDate: 2020/9/7 8:04 PM
 */
public class Task1 extends BaseTask{
    public Task1(boolean isTask) {
        super(isTask);
    }

    @Override
    public void doAction() {
        //执行子节点任务
        System.out.println("Task1 任务节点一执行了");
    }
}
