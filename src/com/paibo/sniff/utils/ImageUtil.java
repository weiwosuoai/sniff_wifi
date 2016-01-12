package com.paibo.sniff.utils;

import java.io.File;
import java.io.InputStream;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;

/**
 * 图片工具类
 * @author jiangbing
 *
 */
public class ImageUtil {
	
	/**
	 * 根据组件指定的宽高来压缩图片
	 * 
	 * <p>
	 * For Example：</br>
	 * mImageView.setImageBitmap(decodeSampleBitmapFromResource(getResource(), R.id.myImage, 100, 100));
	 * 
	 * 
	 * @param res 资源有三种类型：
	 * 					1.Resources（从项目中加载） 2.String文件路径(从磁盘中加载,) 3.InputStream(从网络中加载)，请指定合适的类型
	 * @param resId 组件id
	 * @param reqWidth 期望的宽度(依照组件的宽度)
	 * @param reqHeight 期望的高度(依照组件的高度)
	 * @return bitmap
	 */
	public static Bitmap decodeSampleBitmap(
			Object res, int resId, int reqWidth, int reqHeight) {
		
		// First decode width inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		
		if (res instanceof Resources) {
			BitmapFactory.decodeResource((Resources) res, resId, options);
		} else if (res instanceof String) {
			BitmapFactory.decodeFile((String) res);
		} else if (res instanceof InputStream) {
			BitmapFactory.decodeStream((InputStream) res);
		} else {
			throw new IllegalArgumentException("The type of the first parameter was wrong!");
		}
		
		
		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
		
		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		if (res instanceof Resources) {
			return BitmapFactory.decodeResource((Resources) res, resId, options);
		} else if (res instanceof String) {
			return BitmapFactory.decodeFile((String) res);
		} else if (res instanceof InputStream) {
			return BitmapFactory.decodeStream((InputStream) res);
		}
		return null;
	}
	
	/**
	 * 返回一个压缩图片需要的合适的inSampleSize
	 * 
	 * <p>
	 * 注意：使用此方法，首先要将inJustDecodeBounds为true,把options值传过来，然后</br>
	 * 使用inSampleSize的值并设置inJustDecodeBounds为false,来重新decode一遍,参考
	 * {@link #decodeSampleBitmap}
	 * 里面的做法
	 * </p>
	 * 
	 * @param options
	 * @param reqWidth 你所要求的宽度
	 * @param reqHeight 你所要求的高度
	 * @return
	 */
	public static int calculateInSampleSize(
			BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		
		if (height > reqHeight || width > reqWidth) {
			
			final int halfHeight = height / 2;
			final int halfWidth = width / 2;
			
			// Calculate the largest inSampleSize value that is a power of 2
			// and keeps both height and width larger than the requested height and width
			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}
		
		return inSampleSize;
	}

}
