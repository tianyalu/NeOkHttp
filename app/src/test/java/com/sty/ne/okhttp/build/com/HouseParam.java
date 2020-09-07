package com.sty.ne.okhttp.build.com;

/**
 * 房子的图纸
 * @Author: tian
 * @UpdateDate: 2020/9/7 6:40 PM
 */
public class HouseParam {
    private double height;
    private double width;
    private String color = "白色";

    public HouseParam() {
    }

    public HouseParam(double height, double width, String color) {
        this.height = height;
        this.width = width;
        this.color = color;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return "HouseParam{" +
                "height=" + height +
                ", width=" + width +
                ", color='" + color + '\'' +
                '}';
    }
}
