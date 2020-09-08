package com.sty.ne.okhttp.dutychain2;

/**
 * @Author: tian
 * @UpdateDate: 2020/9/7 9:42 PM
 */
public class Task1 implements IBaseTask {
    @Override
    public void doRunAction(String isTask, IBaseTask iBaseTask) {
        if("no".equals(isTask)) {
            System.out.println("拦截器 任务节点一处理了 ...");
            return;
        }else {
            //继续执行下一个链条的任务节点   chainManager.doRunAction("ok", chainManager)
            iBaseTask.doRunAction(isTask, iBaseTask);
        }
    }
}
