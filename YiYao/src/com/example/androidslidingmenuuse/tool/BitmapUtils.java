package com.example.androidslidingmenuuse.tool;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import com.example.androidslidingmenuuse.sdcard.SdcardUtil;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.location.Location;
import android.media.ExifInterface;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
/**
 * bitmap图片处理
 * @author miaowei
 *
 */
public class BitmapUtils {
	/**
	 * 读取图片:旋转的角度
	 * 
	 * @param path
	 *            图片绝对路径
	 * @return degree旋转的角度
	 */
	public static int readPictureDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			// exifInterface.setAttribute(ExifInterface.TAG_GPS_LATITUDE, lat);
			// exifInterface.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, lon);
			exifInterface.saveAttributes();
			int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation)
			{
				case ExifInterface.ORIENTATION_ROTATE_90:
					degree = 90;
				break;
				case ExifInterface.ORIENTATION_ROTATE_180:
					degree = 180;
				break;
				case ExifInterface.ORIENTATION_ROTATE_270:
					degree = 270;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}

	/**
	 * 旋转图片
	 * 
	 * @param angle
	 *            旋转的角度
	 * @param bitmap
	 *            要旋转的对象
	 * @return 旋转过后的 bitmap
	 */
	public static Bitmap rorateBitamp(int angle, Bitmap bitmap) {
		// 旋转图片 动作
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		// 创建新的图片
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		return resizedBitmap;
	}

	/**
	 * 获取任务图片的路径
	 * 
	 * @param name
	 * @return
	 */
	public static ArrayList<String> getPhotoById(String name) {
		ArrayList<String> pathList = new ArrayList<String>();

		File file = new File(SdcardUtil.getSdcardCollInfo() + name);
		if (!file.exists()) {
			return null;
		}
		if (!file.isDirectory()) {
			return null;
		}
		String[] tempList = file.list();
		for (int i = 0; i < tempList.length; i++) {
			String path = tempList[i];
			pathList.add(path);
		}
		return pathList;

	}
	/**
	 * 获取每个任务点的图片数量
	 * @param url 需要传入的文件路径
	 * @return
	 */
	public static int getPhotoCount(String url) {
		int count = 0;
		File file = new File(SdcardUtil.getSdcardCollInfo() + url);
		if (!file.exists()) {
			return 0;
		}
		if (!file.isDirectory()) {
			return 0;
		}
		String[] tempList = file.list();
		for (int i = 0; i < tempList.length; i++) {
			
			  count++;
		}
		return count;
	}

	/**
	 * 
	 * @param url
	 *            图片的路径url
	 * @return 返回当前的图片，没有返回null
	 */
	public static ArrayList<Bitmap> getBitmapFromSDCard(String url) {
		ArrayList<String> pathList = getPhotoById(url);
		ArrayList<Bitmap> listBitmap = new ArrayList<Bitmap>();
		if (pathList != null)
			for (int i = 0; i < pathList.size(); i++) {
				File fileDir = new File(SdcardUtil.getSdcardCollInfo() + url + "/" + pathList.get(i));
				Bitmap bitmap = null;

				if (fileDir != null && fileDir.exists()) {
					bitmap = decodeFile(fileDir);
				}
				listBitmap.add(bitmap);
			}
		return listBitmap;
	}

	/**
	 * 
	 * @param file
	 *            要转为bitmap的文件，根据file得到输入流，然后options.
	 *            inJustDecodeBounds来得到bitmap的信息，进行压缩
	 * @return bitmap
	 * 
	 */
	public static Bitmap decodeFile(File file) {

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true; // 设置后不会返回bitmap的实例但是可以得到起 宽高等象素信息

		try {
			BitmapFactory.decodeStream(new FileInputStream(file), null, options);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		int scale = 1;
		int width = options.outWidth;
		int height = options.outHeight;
		while (true) {
			if (width > Configs.WIDTH * 2 || height > Configs.HEIGHT * 2) {
				width = width / 10;
				height = height / 10;
				scale = scale * 2;
			} else {
				break;
			}

		}
		BitmapFactory.Options option = new BitmapFactory.Options();
		option.inSampleSize = scale;

		int degree = BitmapUtils.readPictureDegree(file.getAbsolutePath());

		try {
			Bitmap cameraBitmap = BitmapFactory.decodeStream(new FileInputStream(file), null, option);
			Bitmap bitmap = BitmapUtils.rorateBitamp(degree, cameraBitmap);
			return bitmap;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 保存拍摄到的图片
	 * @param fileName
	 * @param mBitmap
	 * @param location
	 */
	public static void saveBitmapToSD(String fileName, Bitmap mBitmap, Location location) {
		File f = new File(fileName);
		FileOutputStream fOut = null;
		try {
			if (f.exists()) {
			}
			fOut = new FileOutputStream(f);
		} catch (IOException e) {
			e.printStackTrace();
		}
		mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
		try {
			fOut.flush();
			fOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
     * 为图片加水印
     * @param src 原图片
     * @param watermark  要打的水印图片
     * @param title  要打的水印文字
     * @param densityDpi  屏幕位深密度
     * @return Bitmap 打好水印的图片
     */
	public static Bitmap createBitmap(String file, Bitmap watermark, String title,int densityDpi) {
		int dpi = densityDpi;
		LogPrint.Print("watermark===createBitmap====dpi"+dpi);
		// 读取原图片信息  
        File srcImgFile = new File(file); 
		Bitmap roratesrc = BitmapFactory.decodeFile(file);
		Bitmap  src = rorateBitamp(90, roratesrc);
		if (src == null) {
			
			return null;
		}
		int srcWidth = src.getWidth();
		int srcHeight = src.getHeight();

		Paint paint = new Paint();
		// ARGB_8888
		Bitmap newb = Bitmap
				.createBitmap(srcWidth, srcHeight, Config.ARGB_8888);// 创建一个新的和src长度宽度一样的位图
		// 把创建的位图作为画板
		Canvas cv = new Canvas(newb);
		// 在0,0坐标开始画入src
		cv.drawBitmap(src, 0, 0, paint);
		if (watermark != null) {
			int ww = watermark.getWidth();
			int wh = watermark.getHeight();
			paint.setAlpha(50);
			// cv.drawBitmap(watermark, srcWidth - ww + 1, srcHeight - wh +1,paint);// 在src的右下角画入水印
			cv.drawBitmap(watermark, 1, 1, paint);// 在src的左上角画入水印
		}
		// 加入文字
		if (title != null) {
			String familyName = "宋体";
			Typeface font = Typeface.create(familyName, Typeface.NORMAL);
			TextPaint textPaint = new TextPaint();
			textPaint.setColor(Color.WHITE);
			textPaint.setTypeface(font);
			if(dpi <= 120){//qvga 240X400
				textPaint.setTextSize(5);
			}else if(dpi <= 160){//hvga 320X480
				textPaint.setTextSize(8);
			}else if(dpi <= 240){//wvga 480X800
				textPaint.setTextSize(10);
				if (Configs.WIDTH > 700) {
					
					textPaint.setTextSize(15);
				}
			}else if(dpi <= 320){// 1280*720
				
				textPaint.setTextSize(15);
				if (Configs.WIDTH > 2000 ) {
					
					textPaint.setTextSize(72);
				}
				
			}else { //更大屏幕分辨率
				
				textPaint.setTextSize(72);
			}
			// 这里是自动换行的
			StaticLayout layout = new StaticLayout(title, textPaint, srcWidth,
					Alignment.ALIGN_NORMAL, 1.0F, 0.0F, true);
			layout.draw(cv);
			// 文字加在左上角
			//cv.drawText(title,40,40,paint);
		} else {

			paint.setColor(Color.WHITE);
			paint.setTextSize(20);
			cv.drawText("测试", 1, 1, paint);
		}
		cv.save(Canvas.ALL_SAVE_FLAG);// 保存
		cv.restore();// 存储
	
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		newb.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] bytes = baos.toByteArray();
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(srcImgFile);
			fileOutputStream.write(bytes);
			fileOutputStream.flush();
			fileOutputStream.close();
		} catch (IOException e) {

			e.printStackTrace();
		}
		return newb;
	}
	
	
	
	
	/**
	 * 根据传入的宽和高，计算出合适的inSampleSize值
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// 源图片的高度和宽度
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		if (height > reqHeight || width > reqWidth) {
			// 计算出实际宽高和目标宽高的比率
			if (width > height) {
				
				inSampleSize = Math.round((float) width / (float) reqWidth);
				
			}else {
				
				//round 返回最接近参数，意思也就是四舍五入math.round(-8.1)=-8 math.round(8.9)=9
				inSampleSize = Math.round((float) height / (float) reqHeight);
			}
			
			
			// 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
			// 一定都会大于等于目标的宽和高。
			//inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}
		return inSampleSize;
	}
	/**
	 * 压缩好的bitmap
	 * @param pathName
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static Bitmap decodeSampledBitmapFromResource(String pathName, 
			int reqWidth, int reqHeight) { 
		// 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(pathName,options);
		// 调用上面定义的方法计算inSampleSize值
		options.inSampleSize = calculateInSampleSize(options, reqWidth,reqHeight);
		// 使用获取到的inSampleSize值再次解析图片
		options.inJustDecodeBounds = false;
		//options.inSampleSize = 5;
		return BitmapFactory.decodeFile(pathName,options);
	}
	/**
	 * 处理圆角的图标
	 * @param background
	 * @param src
	 * @param r 弧度
	 * @param x
	 * @param y
	 * @return
	 */
    public static Bitmap mergerIcon(Bitmap background,Bitmap src,int r,int x,int y){
    	Bitmap output = Bitmap.createBitmap(background.getWidth(), background.getHeight(), Config.ARGB_8888);  
        Canvas canvas = new Canvas(output);  
  
        final int color = 0xff000000;  
        final Paint paint = new Paint();  
        final Rect rect = new Rect(0, 0, background.getWidth(), background.getHeight());  
        final RectF rectF = new RectF(rect);  
        final float roundPx = r;  
  
        paint.setAntiAlias(true);  
        canvas.drawARGB(0, 0, 0, 0);  
        paint.setColor(color);  
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);  
  
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));  
        canvas.drawBitmap(background, rect, rect, paint);
//        canvas.drawBitmap(src, rect, rect, paint);
        canvas.drawBitmap(src, x, y, paint);
        canvas.save();
        
        return output;
    }
    
    /**缩放图片*/
    public static Bitmap resizeImage(Bitmap src, int destW, int destH){
    	if(src == null)return null;
        int srcW = src.getWidth();
        int srcH = src.getHeight();
        float scaleWidth = 1;
        float scaleHeight = 1;
        if(srcW == destW&&srcH == destH){
        	return src;
        }
        //计算出这次要缩小的比例
        scaleWidth=(float)destW/(float)srcW;
        scaleHeight=(float)destH/(float)srcH;
        //产生resize后的Bitmap对象
        Matrix matrix=new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        
        Bitmap resizeBmp=Bitmap.createBitmap(src, 0, 0, srcW, srcH, matrix, true);
        
        return resizeBmp;
    }
    
    public static byte[] compressBitmap(byte[] data, float size) {
		/* 取得相片 */
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0,data.length);
        //Bitmap bitmap = BitmapFactory.decodeFile(imageFile);
		if (bitmap == null || getSizeOfBitmap(bitmap) <= size) {
			return null;// 如果图片本身的大小已经小于这个大小了，就没必要进行压缩
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 如果签名是png的话，则不管quality是多少，都不会进行质量的压缩
		int quality = 100;
		int compressSize = baos.toByteArray().length / 1024;
		while (compressSize > size && quality > 0) {
			LogPrint.Print("fileupload","------quality--------" + quality);
			baos.reset();// 重置baos即清空baos
			quality -= 5;// 每次都减少5(如果这里-=10，有时候循环次数会提前结束)
			if (quality <= 0) {
				break;
			}
			bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
			compressSize = baos.toByteArray().length / 1024;
			LogPrint.Print("fileupload","------compressSize--------" + compressSize);
		}
		byte[] byteData = baos.toByteArray();
		return byteData;
	}
	
	private static long getSizeOfBitmap(Bitmap bitmap){
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);//这里100的话表示不压缩质量
	    long length=baos.toByteArray().length/1024;//读出图片的kb大小
	    return length;
	}

	/**
	 * 根据图片路径进行压缩图片
	 * @param srcPath
	 * @return
	 */
	public static Bitmap getimage(String srcPath,int size) {  
        BitmapFactory.Options newOpts = new BitmapFactory.Options();  
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了  
        newOpts.inJustDecodeBounds = true;  
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath,newOpts);//此时返回bm为空  
          
        newOpts.inJustDecodeBounds = false;
        //当前图片宽高
        float w = newOpts.outWidth;  
        float h = newOpts.outHeight;  
        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为  
        float hh = 800f;//这里设置高度为800f  
        float ww = 480f;//这里设置宽度为480f  
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可  
        int be = 2;//be=1表示不缩放  
       /* if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
        	LogPrint.Print("fileupload","------原始缩放比例 --------" + (newOpts.outWidth / ww));
            be = (int)(newOpts.outWidth / ww);
            //有时会出现be=3.2或5.2现象，如果不做处理压缩还会失败
            if ((newOpts.outWidth / ww) > be) {
				
            	be += 1;
			}
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放  
        	LogPrint.Print("fileupload","------原始缩放比例 --------" + (newOpts.outHeight / hh));
            be = (int)(newOpts.outHeight / hh);
            if ((newOpts.outHeight / hh) > be) {
				
            	be += 1;
			}
        }*/  
        if (be <= 0){
        	
        	be = 1; 
        }  
        newOpts.inSampleSize = be;//设置缩放比例  
        LogPrint.Print("fileupload","------设置缩放比例 --------" + newOpts.inSampleSize);
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了  
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);  
        return compressImage(bitmap,size);//压缩好比例大小后再进行质量压缩  
    } 
		
	/**
	 * 压缩图片
	 * @param image
	 * @param size
	 * @return
	 */
	private static Bitmap compressImage(Bitmap image,int size) {  
		  
        ByteArrayOutputStream baos = new ByteArrayOutputStream();  
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中  
        int options = 100;  
        
        while ((baos.toByteArray().length / 1024) >= size) {  //循环判断如果压缩后图片是否大于等于size,大于等于继续压缩         
        	LogPrint.Print("fileupload","------ByteArray--------" + baos.toByteArray().length / 1024);
            baos.reset();//重置baos即清空baos  
            options -= 5;//每次都减少5 
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中  
            LogPrint.Print("fileupload","------压缩质量--------" + options);
            LogPrint.Print("fileupload","------ByteArray--------" + baos.toByteArray().length / 1024);
        }  
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中  
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片  
        return bitmap;  
    } 
	
	/**
	 * Bitmap转byte数组
	 * @param bitmap
	 * @return
	 */
	public static byte[] compressBitmap(Bitmap bitmap) {
		if (bitmap == null ) {
			return null;// 如果图片本身的大小已经小于这个大小了，就没必要进行压缩
		}
		/* 取得相片 */
        Bitmap tempBitmap = bitmap;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		tempBitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);// 如果签名是png的话，则不管quality是多少，都不会进行质量的压缩
		byte[] byteData = baos.toByteArray();
		return byteData;
	}
}
