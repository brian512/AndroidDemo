
package com.brian.common.util;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串的工具类
 */
public class StringUtil {

    /**
     * 半角转全角
     * 
     * @param input String.
     * @return 全角字符串.
     */
    public static String toSBC(String input) {
        char c[] = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == ' ') {
                c[i] = '\u3000';
            } else if (c[i] < '\177') {
                c[i] = (char) (c[i] + 65248);

            }
        }
        return new String(c);
    }

    /**
     * 全角转半角
     * 
     * @param input String.
     * @return 半角字符串
     */
    public static String toDBC(String input) {

        char c[] = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == '\u3000') {
                c[i] = ' ';
            } else if (c[i] > '\uFF00' && c[i] < '\uFF5F') {
                c[i] = (char) (c[i] - 65248);

            }
        }
        String returnString = new String(c);
        return returnString;
    }

    /**
     * 将换行符替换为""
     * 
     * @param str
     * @return
     */
    public static String getStringNoBlank(String str) {
        if (str != null && !"".equals(str)) {
            Pattern p = Pattern.compile("\\t|\\r|\\n");
            Matcher m = p.matcher(str);
            String strNoBlank = m.replaceAll("");
            return strNoBlank;
        } else {
            return str;
        }
    }
    
    /**
     * 判断是否为中文
     * 
     * @param c
     * @return
     */
    public static boolean isChinese(char codePoint) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(codePoint);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return true;
        }
        return false;
    }
    
    /**
     * 判断是否为emoji表情
     * @param codePoint
     * @return
     */
    public static boolean isEmoji(char codePoint) {
        return !((codePoint == 0x0) ||
                (codePoint == 0x9) ||
                (codePoint == 0xA) ||
                (codePoint == 0xD) ||
                ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) ||
                ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) || 
                ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF)));
    }
    
    
    /**
     * 获取字符串的字节长度, 汉字和emoji占用两个单位，字母和数字各占用一个单位
     * 区别于 ：str.getBytes().length，单个汉字长度为3
     * 区别于 ：str.length()，单个汉字长度为1
     * @param str
     * @return
     */
    public static int getStringLength(CharSequence str) {
        if (TextUtils.isEmpty(str)) {
            return 0;
        }
        char[] ch = str.toString().toCharArray();
        int length = 0;
        for (int i = 0; i < ch.length; i++) {
            if (ch[i] >= 0 && ch[i] <= 255) {
                length++;
            } else if (isChinese(ch[i])) {
                length += 2;
            } else if (isEmoji(ch[i])) {
                length++;
            } else {
                length++;
            }
        }
        return length;
    }
    
    /**
     * 按长度截取字符串，汉字占用两个单位，字母和数字各占用一个单位
     * ps:适用含通用emoji
     * @param str
     * @param length
     * @param needEnd 若有截取,且需要在结尾添加...，则需要少截取两个单位，即（length-2）
     * @return
     */
    public static CharSequence getSubStringByLength(CharSequence str, int length, boolean needEnd) {
        if (TextUtils.isEmpty(str)) {
            return "";
        }
        if (getStringLength(str) <= length) {
            return str;
        }
        StringBuilder builder = new StringBuilder();
        int tempLen = 0;
        if (needEnd) {
            length -= 2;
        }
        boolean needOneMore = false;
        char[] ch = str.toString().toCharArray();
        for (int i = 0; (i < ch.length && tempLen < length) || needOneMore; i++) {
            if (ch[i] >= 0 && ch[i] <= 255) {
                tempLen++;
            } else if (isChinese(ch[i])) {
                tempLen += 2;
            } else if (isEmoji(ch[i])) { // 两个char合成一个emoji
                needOneMore = !needOneMore;
                tempLen += 1;
            } else {
                tempLen++;
            }
            builder.append(ch[i]);
        }
        if (needEnd) {
            builder.append("...");
        }
        return builder.toString();
    }

    /**
     * 字符串中是否含有中文
     * @param str
     * @return
     */
    public static boolean isContainChinese(String str) {

        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }

    /**
     * 计算两点距离
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    public static float getTwoPointsDistance(float x1, float y1, float x2, float y2){
        return (float) Math.sqrt((x1 - x2) * (x1 - x2) + ((y1 - y2) * (y1 - y2)));
    }
}
