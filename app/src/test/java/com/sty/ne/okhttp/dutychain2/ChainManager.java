package com.sty.ne.okhttp.dutychain2;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: tian
 * @UpdateDate: 2020/9/7 9:37 PM
 */
public class ChainManager implements IBaseTask {
    private List<IBaseTask> iBaseTaskList = new ArrayList<>();

    public void addTask(IBaseTask iBaseTask) {
        iBaseTaskList.add(iBaseTask);
    }
    private int index = 0;

    @Override
    public void doRunAction(String isTask, IBaseTask iBaseTask) {
        if(iBaseTaskList.isEmpty()) {
            //抛出异常
            return;
        }
        if(index >= iBaseTaskList.size()) {
            return;
        }

        IBaseTask iBaseTaskResult = iBaseTaskList.get(index); //index=0  --> t1     index=1 --> t2

        index++;

        // t1.doRunAction()  t2.doRunAction()
        iBaseTaskResult.doRunAction(isTask, iBaseTask);
    }
}
