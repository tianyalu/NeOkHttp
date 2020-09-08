package com.sty.ne.okhttp.dutychain;

/**
 * 熟悉责任链模式
 * @Author: tian
 * @UpdateDate: 2020/9/7 8:16 PM
 */
public class Test {
    public static void main(String[] args) {
        Task1 task1 = new Task1(false);
        Task2 task2 = new Task2(false);
        Task3 task3 = new Task3(true);
        Task4 task4 = new Task4(false);

        task1.addNextTask(task2);
        task2.addNextTask(task3);
        task3.addNextTask(task4);

        //执行第一个任务
        task1.action();
    }
}
