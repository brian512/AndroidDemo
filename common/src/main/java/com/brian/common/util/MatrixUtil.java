package com.brian.common.util;

/**
 * Created by yeguangrong on 2017/4/6.
 */

public class MatrixUtil {

    /**
     * 4*4矩阵和1*3的向量相乘（用作顶点坐标的变换）
     * @param result
     * @param lm
     * @param rv
     */
    public static void mulMV(float[] result, float[] lm, float[] rv){
        result[0] = lm[0]*rv[0] + lm[4]*rv[1] + lm[8]*rv[2] + lm[12];
        result[1] = lm[1]*rv[0] + lm[5]*rv[1] + lm[9]*rv[2] + lm[13];
        result[2] = lm[2]*rv[0] + lm[6]*rv[1] + lm[10]*rv[2] +lm[14];
    }
}
