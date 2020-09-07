package com.sty.ne.okhttp.build;

import com.sty.ne.okhttp.build.com.DesignPerson;
import com.sty.ne.okhttp.build.com.House;

/**
 * 用户
 * @Author: tian
 * @UpdateDate: 2020/9/7 6:39 PM
 */
public class UserClient {
    public static void main(String[] args) {
//        buildVersion1();

        buildChainRevoke();
    }

    public static void buildVersion1() {
        //用户 --》 找到建筑公司 --》 DesignPerson
        DesignPerson designPerson = new DesignPerson();
        //  -- 画图纸的过程
        designPerson.addColor("白色");
        designPerson.addWidth(120.0);
        designPerson.addHeight(4);
        // 修改一次
        designPerson.addColor("绿色");
        designPerson.addWidth(100.0);
        designPerson.addHeight(2);
        // 再修改一次
        designPerson.addColor("红色");
        designPerson.addWidth(90.0);
        designPerson.addHeight(3);

        // 盖房子的过程
        House house = designPerson.build();
        System.out.println(house);
    }

    public static void buildChainRevoke() {
        //用户 --》 找到建筑公司 --》 DesignPerson
        House house = new DesignPerson().addColor("白色").addWidth(100).addHeight(6).addHeight(8).build();
        System.out.println(house);
    }
}
