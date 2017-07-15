package com.brian.common.util;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts.Photo;
import android.text.TextUtils;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 读取联系人相关
 * 参考：http://www.cnblogs.com/zhoujian315/p/3169469.html
 * 
 * PS：扫描速度很快，不需要单独开线程，直接同步调用即可
 */
public class ContactUtil {

    private static final String TAG = ContactUtil.class.getSimpleName();
    
    /**获取库Phon表字段 */ 
    private static final String[] PHONES_PROJECTION = new String[] {
        Phone.DISPLAY_NAME, Phone.NUMBER, Photo.PHOTO_ID, Phone.CONTACT_ID };
    
    /** 联系人显示名称 */ 
    private static final int PHONES_DISPLAY_NAME_INDEX = 0;  
 
    /** 电话号码 */ 
    private static final int PHONES_NUMBER_INDEX = 1;  
 
    /** 头像ID */ 
    private static final int PHONES_PHOTO_ID_INDEX = 2;  
 
    /** 联系人的ID */ 
    private static final int PHONES_CONTACT_ID_INDEX = 3;
    
    /**
     * 定制函数：读取通讯录中的手机号码，统一返回格式 +8613691619511
     * 
     */
    public static HashSet<String> getPhoneContacts(Context context) {
        
        HashSet<String> mobileNumList = new HashSet<String>(); // 用set保证去重
        
        // 获取手机联系人
        ContentResolver resolver = context.getContentResolver();
        Cursor phoneCursor = resolver.query(Phone.CONTENT_URI,PHONES_PROJECTION, null, null, null);  // PS：会弹出权限框
        if (phoneCursor != null) {  
            while (phoneCursor.moveToNext()) {  
          
                String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
                JDLog.log(TAG, "org:" + phoneNumber);
                
                // 如果号码为空则跳过  
                if (TextUtils.isEmpty(phoneNumber)) {
                     continue;
                }
                
                // 格式化号码
                phoneNumber = formatPhoneNumber(phoneNumber);
                if (!TextUtils.isEmpty(phoneNumber)) {
                    mobileNumList.add(phoneNumber);
                }else {
                    // 丢弃此联系人
                }
            }
            
            phoneCursor.close();  
        }  
        
        return mobileNumList;
    }
    
    /**
     * 格式化电话号码： +8613691619511
     * 
     * ！！！坑！！！
     * 本地获取手机号码13025461261，无法判断是大陆，还是香港、美国的号码
     * 因此暂时支持扫描大陆区号 +86的手机号码
     * 最好ios也使用这套算法
     * 
     * 注意：直接从通讯录读出来，存在以下情况
     *  #13682495595
     *  (0755) 26712163
     * +86 755-86636168
     * +8613025461261
     * 8613025461261
     * 02195511
     * 02787541815
     * 0755-21611319
     * 130-2663-1503
     * 13249886745
     * 135 9168 1372
     * 
     */
    public static String formatPhoneNumber(String phoneTmp){
        // 特殊处理：统一为半角数字，有些手机的通讯录很奇葩，有全角的数字，关键是正则表达式会忽略半角、全角
        String phoneNumber = toDBC(phoneTmp);
        
        // 去特殊符号
        phoneNumber = phoneNumber.replace("#", "");
        phoneNumber = phoneNumber.replace(" ", "");
        phoneNumber = phoneNumber.replace("-", "");

        // 去括号内容
        int s = phoneNumber.indexOf("(");
        int e = phoneNumber.indexOf(")");
        if ((s >= 0) && (e > s)){
            String temp = phoneNumber.substring(s, (e+1));
            phoneNumber = phoneNumber.replace(temp, "");
        }

        JDLog.log(TAG, "new:" + phoneNumber);
        
        // ---------- 逐个情况判断 -------------
        
        // 针对不带前缀的手机号码
        if (isPureMobileNum(phoneNumber)) {
            // 加上前缀 +86 ，插入
            phoneNumber = "+86"+phoneNumber;
            JDLog.log(TAG, "insert:" + phoneNumber + " ***");
            JDLog.log(TAG, "..................");
            
            return phoneNumber;
        }
        
        // 针对带+86前缀的手机号码
        if (phoneNumber.startsWith("+86")) {
            // 去掉前缀后判断
            phoneNumber = phoneNumber.substring(3);
            if (isPureMobileNum(phoneNumber)) {
                // 加上前缀 +86 ，插入
                phoneNumber = "+86"+phoneNumber;
                JDLog.log(TAG, "insert:" + phoneNumber + " ***");
                JDLog.log(TAG, "..................");

                return phoneNumber;
            }
        }
        
        // 针对带86前缀的手机号码
        if (phoneNumber.startsWith("86")) {
            // 去掉前缀后判断
            phoneNumber = phoneNumber.substring(2);
            if (isPureMobileNum(phoneNumber)) {
                // 加上前缀 +86 ，插入
                phoneNumber = "+86"+phoneNumber;
                JDLog.log(TAG, "insert:" + phoneNumber + " ***");
                JDLog.log(TAG, "..................");

                return phoneNumber;
            }
        }
        
        // ...  其他情况暂不处理 ...
        JDLog.log(TAG, "..................");
        return null;
    }
    
    /**
     * 
     * 注意：以下正则表达式，对全角数字仍然判断为有效，所以导入手机号码的时候，需要先转换
     * 
     * 判断是否为不带前缀的手机号码（针对国内手机号码）
     * 参考：http://blog.csdn.net/centralperk/article/details/7360590
     * 参考：手机号码段：http://baike.baidu.com/link?url=_1l0_QsawOcp_NzBjOzuk-ThptgUNSvnxZjtFPHvrQ5yqf8W-rS0vJfIu2DFA9kL
     * 
     *电信
     *中国电信手机号码开头数字
     *  2G/3G号段（CDMA2000网络）133、153、180、181、189
     *  4G号段 177
     *联通
     *  中国联通手机号码开头数字
     *  2G号段（GSM网络）130、131、132、155、156
     *  3G上网卡145
     *  3G号段（WCDMA网络）185、186
     *  4G号段 176
     *移动
     *  中国移动手机号码开头数字
     *  2G号段（GSM网络）有134x（0-8）、135、136、137、138、139、150、151、152、158、159、182、183、184。
     *  3G号段（TD-SCDMA网络）有157、187、188
     *  3G上网卡 147
     *  4G号段 178
     */
    public static boolean isPureMobileNum(String mobileNum) {
        //Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Pattern p = Pattern.compile("^((13[0-9])|(14[0-9])|(15[0-9])|(17[0-9])|(18[0-9]))\\d{8}$"); // 通杀！
        Matcher m = p.matcher(mobileNum);
        return m.matches();
    }
    
    
    
    /**
     * 对电话号进行格式化，按照大陆手机号码的格式 +86...
     */
    public static String formatMobileNumAt86(String mobileNum){
        if (mobileNum.startsWith("+86")) {
            return mobileNum;
        }else if (mobileNum.startsWith("86")) {
            return "+" + mobileNum;
        }else {
            return "+86" + mobileNum;
        }
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
     * 获取联系人信息
     * 
     * @return ContactInfo结构的链表
     */
    public static ArrayList<ContactInfo> getContactInfosList(Context context){
        ArrayList<ContactInfo> contactInfoList = new ArrayList<ContactInfo>();
        
        String phoneNumber;
        String contactName;
        ContactInfo contactInfo;
        
        // 获取手机联系人  
        ContentResolver resolver = context.getContentResolver();
        Cursor phoneCursor = resolver.query(Phone.CONTENT_URI,PHONES_PROJECTION, null, null, null);
        try {
            if (phoneCursor != null) {  
                while (phoneCursor.moveToNext()) {  
                    
                    //得到手机号码  
                    phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
                    
                    //当手机号码为空的或者为空字段 跳过当前循环  
                    if (TextUtils.isEmpty(phoneNumber)) {
                        continue;
                    } 
                    
                    // 格式化手机号码
                    phoneNumber = formatPhoneNumber(phoneNumber);
                    if (TextUtils.isEmpty(phoneNumber)) {
                        continue;
                    }
                    
                    //得到联系人名称  
                    contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);  
                    
                    contactInfo = new ContactInfo();
                    contactInfo.contactName = contactName;
                    contactInfo.contactNum  = phoneNumber;
                    
                    contactInfoList.add(contactInfo);
                }  
                
                phoneCursor.close();  
            }  
            
        } catch (Exception e) {
            if (phoneCursor != null) {
                phoneCursor.close();
                phoneCursor = null;
            }
            
            contactInfoList.clear();
            contactInfoList = null;
        } finally{
            if (phoneCursor != null) {
                phoneCursor.close();
                phoneCursor = null;
            }
        }
        
        // TODO:是否需要排序
        
        return contactInfoList;
    }
    
    
    // ---------------------- 以下为参考代码 ----------------------
    
    
    /**
     * 示例代码：读取手机通讯录
     */
    private static void __getPhoneContacts(Context context) {
        
        ContentResolver resolver = context.getContentResolver();
        
        // 获取手机联系人  
        Cursor phoneCursor = resolver.query(Phone.CONTENT_URI,PHONES_PROJECTION, null, null, null);
        if (phoneCursor != null) {  
            while (phoneCursor.moveToNext()) {  
          
                //得到手机号码  
                String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
                
                //当手机号码为空的或者为空字段 跳过当前循环  
                if (TextUtils.isEmpty(phoneNumber)) {
                     continue;
                } 
                  
                //得到联系人名称  
                String contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);
                  
                //得到联系人ID  
                Long contactid = phoneCursor.getLong(PHONES_CONTACT_ID_INDEX);
              
                //得到联系人头像ID  
                Long photoid = phoneCursor.getLong(PHONES_PHOTO_ID_INDEX);
                  
                //得到联系人头像Bitamp  
                Bitmap contactPhoto = null;
              
                //photoid 大于0 表示联系人有头像 如果没有给此人设置头像则给他一个默认的  
                if(photoid > 0 ) {  
                    Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI,contactid);
                    InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(resolver, uri);
                    contactPhoto = BitmapFactory.decodeStream(input);
                }else {  
                    ////contactPhoto = BitmapFactory.decodeResource(context.getResources(), R.mipmap.contact_photo);
                } 
                
                // todo : save phoneNumber
                // todo : save contactName
                // todo : save photoid
                // todo : save contactPhoto
            }  
          
            phoneCursor.close();  
        }  
    }
    
    /**
     * 示例代码：读取SIM卡联系人
     * @param context
     */
    private static void __getSIMContacts(Context context) {
        
        ContentResolver resolver = context.getContentResolver();
        
        // 获取Sims卡联系人
        Uri uri = Uri.parse("content://icc/adn");
        Cursor phoneCursor = resolver.query(uri, PHONES_PROJECTION, null, null,null);

        if (phoneCursor != null) {
            while (phoneCursor.moveToNext()) {

                // 得到手机号码
                String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
                
                // 当手机号码为空的或者为空字段 跳过当前循环
                if (TextUtils.isEmpty(phoneNumber)) {
                    continue;
                }
                    
                // 得到联系人名称
                String contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);

                // Sim卡中没有联系人头像

                // todo : save phoneNumber
                // todo : save contactName
            }
            phoneCursor.close();
        }
    }
    
    /**
     * 联系人数据结构
     * 
     * @author ls
     *
     */
    public static class ContactInfo implements Serializable {
        /**
         * 机器生成的UID
         */
        private static final long serialVersionUID = 9072977394526452145L;

        /**
         * 名字
         */
        public String contactName;
        
        /**
         * 号码
         */
        public String contactNum;
        
        /**
         * 名字首字拼音首字母（界面展示需要）
         */
        public String preLetter;
        
    }
}
