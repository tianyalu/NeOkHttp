package com.sty.ne.okhttp.dutychain;

/**
 * @Author: tian
 * @UpdateDate: 2020/9/7 8:14 PM
 */
public class Task3 extends BaseTask {
    public Task3(boolean isTask) {
        super(isTask);
    }

    @Override
    public void doAction() {
        //执行子节点任务
        System.out.println("Task3 任务节点三执行了");
    }
}
