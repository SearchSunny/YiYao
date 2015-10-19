package com.example.androidslidingmenuuse.sdcard;

import java.io.File;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.os.EnvironmentCompat;

import com.example.androidslidingmenuuse.tool.StringOperator;

/**
 * SD卡使用
 * @author miaowei
 *
 */
public class SdcardUtil {
	private static IntentFilter mSdcardIntentFilter = new IntentFilter();
	private List<OnSdcardChangedListener> mListenerList = new LinkedList<OnSdcardChangedListener>();
	private BroadcastReceiver mSdcardBroadcastReceiver = new SdcardBroadcastReceiver();
	private static List<String> mSdcard2Paths = new LinkedList<String>();
	private static final String mVirtualHeader = "/mnt";
	private static String mSdcard1Path;
	private static String mSdcard2Path;
	private static SdcardUtil mInstance;
	private static Context mContext;

	/**
	 * 下载软件使用地址
	 */
	public static String dir_download = Environment.getExternalStorageDirectory().getPath() +"/download";
	
	private static File[] files = null;

	static {
		mSdcardIntentFilter.addAction(Intent.ACTION_MEDIA_REMOVED);
		mSdcardIntentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
		mSdcardIntentFilter.addAction(Intent.ACTION_MEDIA_CHECKING);
		mSdcardIntentFilter.addAction(Intent.ACTION_MEDIA_NOFS);
		mSdcardIntentFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
		mSdcardIntentFilter.addAction(Intent.ACTION_MEDIA_SHARED);
		mSdcardIntentFilter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
		mSdcardIntentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTABLE);
		mSdcardIntentFilter.addAction(Intent.ACTION_MEDIA_EJECT);
		mSdcardIntentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_STARTED);
		mSdcardIntentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
		mSdcardIntentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		mSdcardIntentFilter.addAction(Intent.ACTION_MEDIA_BUTTON);
		mSdcardIntentFilter.addDataScheme("file");
	}

	@SuppressLint("SdCardPath")
	private void initSdcard2Paths() {
		// 3.2及以上SDK识别路径
		mSdcard2Paths = getSdcard2Paths();
		mSdcard2Paths.add("/mnt/emmc");
		mSdcard2Paths.add("/mnt/extsdcard");
		mSdcard2Paths.add("/mnt/ext_sdcard");
		mSdcard2Paths.add("/sdcard-ext");
		mSdcard2Paths.add("/mnt/sdcard-ext");
		mSdcard2Paths.add("/sdcard2");
		mSdcard2Paths.add("/sdcard");
		mSdcard2Paths.add("/mnt/sdcard2");
		mSdcard2Paths.add("/mnt/sdcard");
		mSdcard2Paths.add("/sdcard/sd");
		mSdcard2Paths.add("/sdcard/external");
		mSdcard2Paths.add("/flash");
		mSdcard2Paths.add("/mnt/flash");
		mSdcard2Paths.add("/mnt/sdcard/external_sd");

		mSdcard2Paths.add("/mnt/external1");
		mSdcard2Paths.add("/mnt/sdcard/extra_sd");
		mSdcard2Paths.add("/mnt/sdcard/_ExternalSD");
		mSdcard2Paths.add("/mnt/extrasd_bin");
		// 4.1SDK 识别路径
		mSdcard2Paths.add("/storage/extSdCard");
		mSdcard2Paths.add("/storage/sdcard0");
		mSdcard2Paths.add("/storage/sdcard1");
		initSdcard2();
	}

	@SuppressLint({ "InlinedApi", "NewApi", "NewApi" })
	private List<String> getSdcard2Paths() {
		List<String> paths = new LinkedList<String>();
		if (Build.VERSION.SDK_INT < 13) {
			return paths;
		}

		StorageManager sm = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
		try {
			Class<? extends StorageManager> clazz = sm.getClass();
			Method mlist = clazz.getMethod("getVolumeList", (Class[]) null);
			Class<?> cstrvol = Class.forName("android.os.storage.StorageVolume");
			Method mvol = cstrvol.getMethod("getPath", (Class[]) null);
			Object[] objects = (Object[]) mlist.invoke(sm);
			if (objects != null && objects.length > 0) {
				for (Object obj : objects) {
					paths.add((String) mvol.invoke(obj));
				}
			}
		} catch (Exception e) {
		}
		return paths;
	}

	private static void initSdcard2() {
		String sdcard = getSdcardPathNoSlash();
		int count = mSdcard2Paths.size();
		for (int index = 0; index < count; index++) {
			boolean isSame = isSamePath(sdcard, mSdcard2Paths.get(index));
			if (isSame) {
				continue;
			}
			boolean isExsits = isExsitsPath(mSdcard2Paths.get(index));
			if (isExsits && !isSameSdcard(sdcard, mSdcard2Paths.get(index))) {
				mSdcard2Path = mSdcard2Paths.get(index);
				break;
			}
		}
	}

	private static boolean isSameSdcard(String sdcard1, String sdcard2) {
		long sdcard1Size = getSdcardSize(sdcard1);
		long sdcard2Size = getSdcardSize(sdcard2);
		if (sdcard1Size != sdcard2Size) {
			return false;
		}
		sdcard1Size = getSdcardAvailableSize(sdcard1);
		sdcard2Size = getSdcardAvailableSize(sdcard2);
		if (sdcard1Size != sdcard2Size) {
			return false;
		}

		File f1 = new File(sdcard1);
		File f2 = new File(sdcard2);

		String[] fileList1 = f1.list();
		String[] fileList2 = f2.list();

		// 都是空，则认为是同一个目录
		if (fileList1 == null && fileList2 == null) {
			return true;
		}

		// 有一个为空，则认为是不同目录
		if (fileList1 == null || fileList2 == null) {
			return false;
		}

		// 不一样多的文件，则认为不同目录
		if (fileList1.length != fileList2.length) {
			return false;
		}

		return true;
	}

	private static boolean isExsitsPath(String path) {
		File f = new File(path);
		if (f.exists() && f.canWrite()) {
			return true;
		}
		return false;
	}

	private static boolean isSamePath(String path, String path2) {
		// 名称有空则认为一样
		if (StringOperator.isNullOrEmptyOrSpace(path) || StringOperator.isNullOrEmptyOrSpace(path2)) {
			return true;
		}
		// 一样
		if (path.trim().toLowerCase().equals(path2.trim().toLowerCase())) {
			return true;
		}
		// 添加/mnt
		if (path2.trim().toLowerCase().equals((mVirtualHeader + path).trim().toLowerCase())) {
			return true;
		}
		// 添加/mnt
		if (path.trim().toLowerCase().equals((mVirtualHeader + path2).trim().toLowerCase())) {
			return true;
		}

		return false;
	}

	private SdcardUtil(Context context) {
		mContext = context;
	}

	public static void initInstance(Context context) {
		mInstance = new SdcardUtil(context);
		mInstance.registerReceiver();
		if (Build.VERSION.SDK_INT < 1) {
			//获取内置sdcard存储卡路径
			mSdcard1Path = Environment.getExternalStorageDirectory().getAbsolutePath();

			mInstance.initSdcard2Paths();
		} else {
			files = ContextCompat.getExternalFilesDirs(mContext, null);
			if (files != null) {
				if (files.length > 0 && files[0] != null) {
					mSdcard1Path = files[0].getAbsolutePath();
				}
				if (files.length > 1 && files[1] != null) {
					mSdcard2Path = files[1].getAbsolutePath();
				}
			}
		}
	}

	public static void unInitInstance() {
		mInstance.unregisterReceiver();
	}

	/**
	 * 获取单实例
	 * 
	 * @return Sdcard对象
	 */
	public static SdcardUtil getInstance() {
		return mInstance;
	}

	private void registerReceiver() {
		mContext.registerReceiver(mSdcardBroadcastReceiver, mSdcardIntentFilter);
	}

	private void unregisterReceiver() {
		removeAllListener();
		mContext.unregisterReceiver(mSdcardBroadcastReceiver);
	}

	// 自己写一个广播监听函数
	public class SdcardBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			synchronized (mListenerList) {

				for (int index = mListenerList.size() - 1; index > -1; index--) {
					OnSdcardChangedListener listener = mListenerList.get(index);
					if (listener == null) {
						continue;
					}
					listener.onSdcardChanged(SdcardUtil.this);
				}
			}
		}
	};

	/**
	 * 添加SDCARD改变事件监听
	 * 
	 * @param listener
	 *            监听
	 */
	public void addListener(OnSdcardChangedListener listener) {
		mListenerList.add(listener);
	}

	/**
	 * 移除监听，可以不移除在系统关闭的时候会自动移除所有监听
	 * 
	 * @param listener
	 *            监听
	 * @return 是否移除成功
	 */
	public boolean removeListener(OnSdcardChangedListener listener) {
		boolean isContains = false;
		synchronized (mListenerList) {
			isContains = mListenerList.contains(listener);
			if (isContains) {
				mListenerList.remove(listener);
				isContains = true;
			} else {
				isContains = false;
			}
		}
		if (isContains) {
			return true;
		}

		else {
			return false;
		}
	}

	/**
	 * 移除所有监听
	 */
	public void removeAllListener() {
		synchronized (mListenerList) {
			mListenerList.clear();
		}
	}

	/**
	 * SDCARD状态改变接口
	 * 
	 * @author xuzs
	 * 
	 */
	public interface OnSdcardChangedListener {
		public void onSdcardChanged(SdcardUtil sender);
	}

	public static String getSdcardState() {
		return Environment.getExternalStorageState();
	}

	public static String getSdcard2State() {
		if (isExsitsSdcard2()) {
			return Environment.MEDIA_MOUNTED;
		}
		return Environment.MEDIA_UNMOUNTED;
	}

	/**
	 * sdcard是否存在
	 * 
	 * @return true 存在，false不存在
	 */
	public static boolean isExsitsSdcard() {
		if (Build.VERSION.SDK_INT >= 19) {
			if (mSdcard1Path == null) {
				return false;
			}
			boolean result = Environment.MEDIA_MOUNTED.equals(EnvironmentCompat.getStorageState(files[0]));
			return result;
		} else {
			return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
		}
	}

	protected static boolean isExsitsSdcard2() {
		if (StringOperator.isNullOrEmptyOrSpace(getSdcard2PathNoSlash())) {
			return false;
		}
		File f = new File(getSdcard2PathNoSlash());
		if (f.exists() && f.canWrite()) {
			return true;
		}
		return false;
	}

	/**
	 * 获取SDCARD下mapbar的路径，一般是/mnt/sdcard/mapbar
	 * 在Linux系统中，文件和文件夹可以通过使用“.”来隐藏,而在Windows系统中,
	 * 不能建立以“.”开头的文件或文件夹。所以这种隐藏文件夹的方法只能在Android系统手机上操作
	 * 而利用这种方法隐藏的文件夹在手机以U盘模式连接电脑后，在Windows系统中是可见的
	 * 卡2的图片地址(加.可隐藏图片.mapbarinfo/photo/)默认卡2外置
	 * @return 外置SD卡mapbar路径
	 */
	public static String getSdcardCollInfo() {
		return getSdcardPath() + ".mapbarinfo/photo/";
	}
	/**
	 * 外置
	 * @return
	 */
	public static String getSdcardCollInfoNO() {
		return getSdcardPath()+"/";
	}

	/**
	 * 卡1的图片地址(内置)未使用
	 * @return
	 */
	public static String getSdcardCollInfo2() {
		return getSdcard2Path() + ".mapbarinfo/photo/";
	}

	/**
	 * 获取内置卡路径。而有些手机刚好相反
	 */
	static String getSdcard2Path() {
		return getSdcard2PathNoSlash() + "/";
	}

	/**
	 * 
	 * 此方法获取的不一定是外置sd卡，有些三星手机相反
	 */
	protected static String getSdcardPath() {
		return getSdcardPathNoSlash() + "/";
	}

	protected static String getSdcard2PathNoSlash() {
		return mSdcard2Path;
	}

	/**
	 * 获取SDCARD路径，一般是/mnt/sdcard/,现在有些手机sdcard路径不是标准的，希望各位通过这种方法获取SDCARD路径
	 * 
	 * @return SDCARD路径
	 */
	protected static String getSdcardPathNoSlash() {
		return mSdcard1Path;

	}

	/**
	 * 返回SD卡大小，单位是Byte
	 * 
	 * @return
	 */
	protected static long getSdcardSize() {
		long size = 0;
		boolean isOk = isExsitsSdcard();
		if (!isOk) {
			return size;
		}

		try {
			File sdcard = /* Environment.getExternalStorageDirectory() */new File(mSdcard1Path);
			StatFs statFs = new StatFs(sdcard.getPath());
			int blockSize = statFs.getBlockSize();
			int totalBlocks = statFs.getBlockCount();
			size = (long) ((long) totalBlocks * (long) blockSize);
		} catch (Exception e) {
		}
		//return size / 1024 / 1024; 以M单位返回
		return size;
	}

	protected static long getSdcard2Size() {
		long size = 0;
		boolean isOk = isExsitsSdcard2();
		if (!isOk) {
			return size;
		}
		try {

			File sdcard = new File(getSdcard2PathNoSlash());
			StatFs statFs = new StatFs(sdcard.getPath());
			int blockSize = statFs.getBlockSize();
			int totalBlocks = statFs.getBlockCount();
			size = (long) ((long) totalBlocks * (long) blockSize);
		} catch (Exception e) {
		}
		//return size / 1024 / 1024; 以M单位返回
		return size;
	}

	/**
	 * 返回SD卡剩余大小，单位是Byte
	 * 
	 * @return
	 */
	protected static long getSdcardAvailSize() {
		long size = 0;
		boolean isOk = isExsitsSdcard();
		if (!isOk) {
			return size;
		}

		try {
			File sdcard = /* Environment.getExternalStorageDirectory() */new File(mSdcard1Path);
			StatFs statFs = new StatFs(sdcard.getPath());
			int blockSize = statFs.getBlockSize();
			// int totalBlocks = statFs.getBlockCount();
			int availCount = statFs.getAvailableBlocks();
			size = (long) ((long) availCount * (long) blockSize);
		} catch (Exception e) {
		}
		//return size / 1024 / 1024; 以M单位返回
		return size;
	}

	/**
	 * 返回SD卡剩余大小，单位是Byte
	 * 
	 * @return
	 */
	@SuppressLint("NewApi")
	protected static long getSdcard2AvailSize() {
		long size = 0;
		boolean isOk = isExsitsSdcard2();
		if (!isOk) {
			return size;
		}
		try {
			File sdcard = new File(getSdcard2PathNoSlash());
			StatFs statFs = new StatFs(sdcard.getPath());
			int blockSize = statFs.getBlockSize();
			// int totalBlocks = statFs.getBlockCount();
			int availCount = statFs.getAvailableBlocks();
			size = (long) ((long) availCount * (long) blockSize);
		} catch (Exception e) {
		}
		//return size / 1024 / 1024; 以M单位返回
		return size;
	}

	/**
	 * sd是否可以卸载，如果可以卸载则是外置卡
	 * 
	 * @return
	 */
	@TargetApi(9)
	@SuppressLint("NewApi")
	protected static boolean isSdcardStorageRemovable() {
		if (Build.VERSION.SDK_INT >= 9 && Build.VERSION.SDK_INT < 19) {
			// 测试发现4.4该方法返回false
			return Environment.isExternalStorageRemovable();
		} else {
			return true;
		}
	}

	/**
	 * 返回SD卡可用大小，单位是M
	 * 
	 * @return
	 */
	public static long getSdcardAvailableSize() {
		long size = 0;
		boolean isOk = isExsitsSdcard();
		if (!isOk) {
			return size;
		}
		File sdcard = /* Environment.getExternalStorageDirectory() */new File(mSdcard1Path);
		StatFs statFs = new StatFs(sdcard.getPath());
		int blockSize = statFs.getBlockSize();
		int totalBlocks = statFs.getAvailableBlocks();
		size = (long) ((long) totalBlocks * (long) blockSize);
		return size / 1024 / 1024;  //以M单位返回
		//return size;
	}

	protected static long getSdcard2AvailableSize() {
		long size = 0;
		boolean isOk = isExsitsSdcard2();
		if (!isOk) {
			return size;
		}
		File sdcard2 = new File(getSdcard2PathNoSlash());
		StatFs statFs = new StatFs(sdcard2.getPath());
		int blockSize = statFs.getBlockSize();
		int totalBlocks = statFs.getAvailableBlocks();
		size = (long) ((long) totalBlocks * (long) blockSize);
		//return size / 1024 / 1024; 以M单位返回
		return size;
	}

	protected static long getSdcardSize(String sdcardPath) {
		long size = 0;
		try {
			StatFs statFs = new StatFs(sdcardPath);
			int blockSize = statFs.getBlockSize();
			int totalBlocks = statFs.getBlockCount();
			size = (long) ((long) totalBlocks * (long) blockSize);
		} catch (Exception e) {
		}
		//return size / 1024 / 1024; 以M单位返回
		return size;
	}

	protected static long getSdcardAvailableSize(String sdcardPath) {
		long size = 0;
		StatFs statFs = new StatFs(sdcardPath);
		int blockSize = statFs.getBlockSize();
		int totalBlocks = statFs.getAvailableBlocks();
		size = (long) ((long) totalBlocks * (long) blockSize);
		
		//return size / 1024 / 1024; 以M单位返回
		return size;
	}
	

}
