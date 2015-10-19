package com.example.androidslidingmenuuse.tool;

import java.io.File;
import java.text.SimpleDateFormat;

import com.example.androidslidingmenuuse.crash.CrashDebug;

import android.content.Context;
/**
 * debug模式下收集运行日志
 * @author miaowei
 *
 */
public class DebugManager {
	
	public static boolean bDebug = true;
	
	private final static String LOG_FILE_PATH = "/sdcard/yhl/MapLogs/";
	private static SimpleDateFormat mSdf = new SimpleDateFormat("yyyyMMdd");
	private static SimpleDateFormat mSdfLog = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss S");
	
	/**
	 * DebugManager实例
	 */
	private static DebugManager INSTANCE;

	/** 获取DebugManager实例 ,单例模式 */
	public static DebugManager getInstance() {

		if (INSTANCE == null) {

			syncInit();
		}
		return INSTANCE;
	}
	
	private static synchronized void syncInit() {

		if (INSTANCE == null) {
			INSTANCE = new DebugManager();
		}
	}
	
	/**
	 * 获取全局处理崩溃异常
	 * 
	 * @param context
	 */
	public void getCrashDebug(Context context) {

		CrashDebug crashHandler = CrashDebug.getInstance();
		crashHandler.init(context, LOG_FILE_PATH);
	}
	
	/**
	 * 打印运行日志
	 */
	public static void printlnRunLog(boolean isPrint){
		if (isPrint) {
			
			File dir = new File(LOG_FILE_PATH);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			StringBuilder sbPath = new StringBuilder();
			sbPath.append(dir.getAbsolutePath()).append(File.separator).append("run_log.txt");
			try {
				Runtime.getRuntime().exec("logcat -v long -f " + sbPath);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
