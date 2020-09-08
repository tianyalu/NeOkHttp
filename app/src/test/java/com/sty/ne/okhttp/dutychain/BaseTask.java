package com.sty.ne.okhttp.dutychain;

/**
 * @Author: tian
 * @UpdateDate: 2020/9/7 8:04 PM
 */
public abstract class BaseTask {
    //判断当前任务节点有没有能力执行
    private boolean isTask;

    public BaseTask(boolean isTask) {
        this.isTask = isTask;
    }

    //执行下一个节点
    private BaseTask nextTask;

    //添加下一个节点任务
    public void addNextTask(BaseTask nextTask) {
        this.nextTask = nextTask;
    }

    //让子节点任务去完成
    public abstract void doAction();

    public void action() {
        if(isTask) {
            doAction(); //执行子节点任务
        } else if(nextTask != null) {
            //继续执行下一个任务节点
            nextTask.action();
        }
    }
}
