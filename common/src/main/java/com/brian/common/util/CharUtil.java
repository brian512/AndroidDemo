package com.brian.common.util;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 用来处理一些字符相关的工作
 * @author lipeilong
 *
 */
public class CharUtil {

    /**
     *  计算字符个数，中文字符算两个字符
     * @param text
     * @return
     */
    public static int calculateCharNum(String text) {
        if (TextUtils.isEmpty(text)) {
            return 0;
        }
        
        char[] array = text.toCharArray();
        int chineseCount = 0;
        int englishCount = 0;
        for (int i = 0; i < array.length; i++) {
            if ((char) (byte) array[i] != array[i]) {
                chineseCount++;
            } else {
                englishCount++;
            }
        }

        return chineseCount*2 + englishCount;
    }
    
    /**
     * 截取固定长度的字符串
     * @param text
     * @param length
     * @return
     */
    public static String getSubString(String text, int length){
        
        String result = text.substring(0, length);
        char[] array = text.toCharArray();
        char[] newArray = new char[array.length];
        int englishCount = 0;
        int chineseCount = 0;
        int count = 0;
        for (int i = 0; i < array.length; i++) {
            
            if ((char) (byte) array[i] == array[i]) {
                englishCount++;
            }else{
                chineseCount++;
            }
            if(englishCount %2 ==0){
                count = chineseCount + englishCount/2;
            }else{
                count = chineseCount + englishCount/2 +1;
            }
            if(count > length){
                break;
            }
            
            newArray[i] = array[i];
        }
        
        result = String.valueOf(newArray);
        
        return result;
    }
    
    /**
     * 获取字符串的hang数
     * @param text
     * @return
     */
    public static int calulateCharRow(String text){
        if(text == null){
            return 0;
        }
        int rowNum = 0;
        int len = text.length();
        for(int i=0 ; i< len ; i++){
            if(text.charAt(i) == '\n'){
                rowNum++;
            }
        }
        return rowNum +1;
    }
    
    /**
     * 判断输入是否是a、B之类的字母
     * 
     * @param c
     * @return
     */
    public static boolean isLetter(char c){
        if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
            return true;
        }else {
            return false;
        }
    }
    
    /**
     * 
     * 获取首字母
     * @param name
     * @return
     */
    public static String getHeadChar(String name){
        if(TextUtils.isEmpty(name)){
            return "#unknow";
        }
        
        String letter = CharacterParser.getInstance().getSelling(name);
        if(TextUtils.isEmpty(letter)){
            return "#unknow";
        }
        
        return letter.toUpperCase().substring(0, 1);
    }

    //判断email格式是否正确
    public static boolean isEmail(String email) {
        StringBuilder builder = new StringBuilder();
        builder.append("^");
        builder.append("([a-zA-Z0-9_\\-\\.]{1,16})");
        builder.append("@");
        builder.append("((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))");
        builder.append("(aero|arpa|coop|int|jobs|mil|museum|name|nato|org|pro|travel|info|biz|com|edu|gov|net|am|bz|cn|cx|hk|jp|tw|vc|vn|tt|tv|im)");
        builder.append("$");

        Pattern p = Pattern.compile(builder.toString());
        Matcher m = p.matcher(email);

        return m.matches();
    }
}
