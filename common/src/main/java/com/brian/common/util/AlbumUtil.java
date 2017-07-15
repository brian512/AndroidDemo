package com.brian.common.util;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.brian.common.Env;

import java.util.Locale;

/**
 * 相册的工具类
 * @author wuwenhua
 *
 */
public class AlbumUtil {

	/**
	 * 获得相册中最近的一张照片路径
	 */
	public static String getLatestPhoto() {
		
		String photoPath = null;
		
		// 获取系统相册路径
		String albumDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath();
		
		// 如果系统相册目录获取不成功，则使用常规目录
		if (TextUtils.isEmpty(albumDir)) {
			String sdcardDir = Environment.getExternalStorageDirectory().toString();
			albumDir = sdcardDir + "/DCIM";
		}
		
		// 遍历图片，比较路径
		ContentResolver cr = Env.getContext().getContentResolver();
		Cursor cursor = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
			new String[]{MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA},
			MediaStore.Images.Media.MIME_TYPE + "=? OR " + MediaStore.Images.Media.MIME_TYPE + "=?",
			new String[] { "image/jpeg", "image/png" }, MediaStore.Images.Media._ID + " DESC"); // 按图片ID降序排列

		// PS: 这里可能存在没有图片的情况，cursor为null
		if(cursor != null){
			while (cursor.moveToNext()) {
				
				long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media._ID));
				String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
				// path可能是以/../dcim开头,转成小写来比较
				if (path.toLowerCase(Locale.getDefault()).startsWith(albumDir.toLowerCase())) {
					photoPath = path;
					break;
				}
			}
			cursor.close();
		}
		
		return photoPath;

	 }  
	

	
}
