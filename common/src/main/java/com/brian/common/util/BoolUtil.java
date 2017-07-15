package com.brian.common.util;

public class BoolUtil {

    
    /**
     * boolean型转成int
     */
    public static int bool2int(boolean b){
        if (b) {
            return 1;
        } else {
            return 0;
        }
    }

    
    /**
     * int型转成bool
     */
    public static boolean int2bool(int i){
        if (i == 0) {
            return false;
        } else {
            return true;
        }
    }    

    
}
