package com.brian.common.util;

import java.util.Random;

/**
 * 获得随机数的工具类
 */
public class RandomUtil {

    /**
     * 获得指定长度的随机数(采用截取系统时间的方式)
     * @param len 随机数的长度，限制小于long的范围
     * @return
     */
    public static String getRandStr(int len) {
        String ts   = String.format("%s", System.currentTimeMillis());
        String rand = ts.substring(ts.length() - len);
        return rand;
    }
    
    /**
     * 获取随机数，采用Random类
     * @return
     */
    public static int getRandInt() {
        return new Random().nextInt();
    }
    
    /**
     * 获取一定范围内随机数，采用Random类
     * 0<= n < max
     * @param max
     * @return
     */
    public static int getRandInt(int max) {
        return new Random().nextInt(max);
    }
    
    
}
