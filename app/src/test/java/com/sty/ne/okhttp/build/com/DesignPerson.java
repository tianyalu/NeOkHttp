package com.sty.ne.okhttp.build.com;

/**
 * 设计师
 * @Author: tian
 * @UpdateDate: 2020/9/7 6:40 PM
 */
public class DesignPerson {
    //画图纸
    private HouseParam houseParam;

    //员工
    private Worker worker;

    public DesignPerson() {
        houseParam = new HouseParam();
        worker = new Worker();
    }

    /**
     * 增加楼层 -- 画图纸的过程
     * @param height
     */
    public DesignPerson addHeight(double height) {
        houseParam.setHeight(height);
        return this;
    }

    /**
     * 增加面积 -- 画图纸的过程
     * @param width
     */
    public DesignPerson addWidth(double width) {
        houseParam.setWidth(width);
        return this;
    }

    /**
     * 增加颜色 -- 画图纸的过程
     * @param color
     */
    public DesignPerson addColor(String color) {
        houseParam.setColor(color);
        return this;
    }

    /**
     * 把图纸给工人
     * @return 员工说房子盖好了
     */
    public House build() {
        worker.setHouseParam(houseParam);
        return worker.buildHouse();
    }
}
