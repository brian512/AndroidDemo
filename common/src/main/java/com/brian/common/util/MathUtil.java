package com.brian.common.util;

/**
 * Created by yeguangrong on 2017/4/13.
 */

public class MathUtil {

    /**
     * 求取两个数的最大公约数
     * @param m
     * @param n
     * @return
     */
    public static int getMaxCommonDivider(int m, int n) {

        int k,y;
        if(m<n) {
            k=m;
            m=n;
            n=k;
        }
        while(m%n!=0) {
            y=m%n;
            m=n;
            n=y;
        }
        return n;

    }
}
