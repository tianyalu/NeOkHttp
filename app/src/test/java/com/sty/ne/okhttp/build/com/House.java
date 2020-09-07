package com.sty.ne.okhttp.build.com;

/**
 * 真实存在的房子
 * @Author: tian
 * @UpdateDate: 2020/9/7 6:43 PM
 */
public class House {
    private double height;
    private double width;
    private String color;

    public House() {
    }

    public House(double height, double width, String color) {
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
        return "具体建造出来的房子 --> House{" +
                "height=" + height +
                ", width=" + width +
                ", color='" + color + '\'' +
                '}';
    }
}
