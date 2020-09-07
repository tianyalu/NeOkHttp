package com.sty.ne.okhttp.build.com;

/**
 * 300个员工
 * @Author: tian
 * @UpdateDate: 2020/9/7 6:52 PM
 */
public class Worker {
    //拿到图纸
    private HouseParam houseParam;

    public void setHouseParam(HouseParam houseParam) {
        this.houseParam = houseParam;
    }

    //工作 盖房子
    //交付
    public House buildHouse() {
        //盖房子 实例
        House house = new House();
        house.setColor(houseParam.getColor());
        house.setWidth(houseParam.getWidth());
        house.setHeight(houseParam.getHeight());
        return house;
    }
}
