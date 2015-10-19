package com.example.androidslidingmenuuse.tool;

import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;
/**
 * 帮助类
 * @author miaowei
 *
 */
public class BaseTools {
	/**
	 * 软件是否在前台（home）
	 * true前台/false后台
	 */
    public static boolean isAppOnForeground;
    
	/** 获取屏幕的宽度 */
	public final static int getWindowsWidth(Activity activity) {
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.widthPixels;
	}
	/**
	 * 是否有效联网
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetworkAvailable(Context context) {
		// 网络连接的管理
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm == null){
			
			return false;
		}
			
		// 网络的状态信息
		NetworkInfo netinfo = cm.getActiveNetworkInfo();
		if (netinfo == null) {
			
			return false;
		}
		// 网络是否已被打开
		if (netinfo.isConnected()) {
			
			return true;
		}
		return false;
	}

	/**
	 * 网络是否是打开的(WIFI\cmwap\cmnet)
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetWorkOpen(Context context) {
		boolean isOpen = false;
		try {
			ConnectivityManager cwjManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			isOpen = cwjManager.getActiveNetworkInfo().isAvailable();
		} catch (Exception ex) {
			// 如果出异常，那么就是电信3G卡
			isOpen = false;
		}

		return isOpen;
	}

	/**
	 * 获取连接类型
	 * 
	 * @param context
	 * @return
	 */
	public static String getApnType(Context context) {
		String result = null;
		try {
			if (isNetWorkOpen(context)) {
				ConnectivityManager mag = (ConnectivityManager) context
						.getSystemService(Context.CONNECTIVITY_SERVICE);
				String type = mag.getActiveNetworkInfo().getTypeName();
				if (type.toLowerCase().equals("wifi")) {
					result = "wifi";
				} else {
					NetworkInfo mobInfo = mag
							.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
					result = mobInfo.getExtraInfo();
				}
			}
		} catch (Exception e) {

			LogPrint.Print(e.getMessage().toString());
		}
		return result.toLowerCase();
	}

	/**
	 * 获得操作系统版本
	 * 
	 * @return
	 */
	public static String getOs_Version() {
		if (null != android.os.Build.VERSION.RELEASE) {
			return android.os.Build.VERSION.RELEASE;
		}
		return "";
	}

	/**
	 * 获取imei
	 * 
	 * @param activity
	 * @return
	 */
	public static String getIMEI(Context activity) {
		try {
			TelephonyManager tm = (TelephonyManager) activity
					.getSystemService(activity.TELEPHONY_SERVICE);
			String mImei = tm.getDeviceId();
			if (mImei != null) {
				mImei = mImei.trim();
				return mImei;
			}
		} catch (Exception e) {
			
			LogPrint.Print(e.getMessage().toString());
		}
		return "";
	}

	/**
	 * 获取imsi
	 * 
	 * @param activity
	 * @return
	 */
	public static String getIMSI(Context activity) {
		try {
			TelephonyManager tm = (TelephonyManager) activity
					.getSystemService(activity.TELEPHONY_SERVICE);
			String mImsi = tm.getSimSerialNumber();// sim卡后面的20位唯一标市
			if (mImsi != null) {
				mImsi = mImsi.trim();
				return mImsi;
			}
		} catch (Exception e) {
			
			LogPrint.Print(e.getMessage().toString());
		}
		return "";
	}

	/**
	 * 获取sim卡类型
	 * 
	 * @param activity
	 * @return
	 */
	public static String getSimType(Context activity) {
		try {
			String mImsi = getIMSI(activity);
			if (mImsi.length() >= 6) {
				return mImsi.substring(4, 6);
			}
		} catch (Exception e) {
			
			LogPrint.Print(e.getMessage().toString());
		}
		return "-1";
	}

	/**
	 * 获得设备名称
	 * 
	 * @return
	 */
	public static String getDeviceName() {
		if (null != android.os.Build.MODEL) {
			return android.os.Build.MODEL;
		}
		return "";
	}
	
	/**
	 * 获取当前设置宽、高
	 * @param context
	 * @return
	 */
	public static String getDeviceWidthAndHeight(Context context){
		StringBuilder sbBuilder = new StringBuilder();
		WindowManager windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
		Display display = windowManager.getDefaultDisplay();
		sbBuilder.append(display.getWidth()+"X"+display.getHeight());
		return sbBuilder.toString();
	}
	
	/**
	 * 判断当前应用程序处于前台还是后台
	 * @param context
	 * @return
	 */
	public static boolean isApplicationBroughtToBackground(final Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
            	LogPrint.Print("后台="+ topActivity.getPackageName());
            	isAppOnForeground = false;
                return true;
                
            }else{
            	
            	LogPrint.Print("前台="+ topActivity.getPackageName());
            	isAppOnForeground = true;
            }
            
        }
        return false;

    }
	/** 
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素) 
     */  
    public static int dip2px(Context context, float dpValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (dpValue * scale + 0.5f);  
    }
    
    /** 
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp 
     */  
    public static int px2dip(Context context, float pxValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (pxValue / scale + 0.5f);  
    }
    public static void ShowToast(Context context,String text){
    	try {
    		if(isAppOnForeground == false){//home时不提示
    			return;
    		}
    		if(text == null||text.length() <= 0){
    			return;
    		}
    		Toast.makeText(context, text, Toast.LENGTH_LONG).show();
		} catch (Exception e) {
		}
    }
    
    public static void ShowToast(Context context,String text,boolean islong){
    	try {
    		if(isAppOnForeground == false){//home时不提示
    			return;
    		}
    		if(text == null||text.length() <= 0){
    			return;
    		}
    		Toast.makeText(context, text, islong?Toast.LENGTH_LONG:Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
		}
    }
    
    /**
     * 检测service是否运行中
     * @param context
     * @param className
     * @return
     */
    public static boolean isServiceRunning(Context context,String className){
    	try {
    		ActivityManager manager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
    		//获取正在运行的服务 1000 maxNum 代表我们希望返回的服务数目大小
    		for (RunningServiceInfo service : manager.getRunningServices(1000)) {
    			if (className.equals(service.service.getClassName())) {
    				return true;
    			}
    		}
		} catch (Exception e) {
			// TODO: handle exception
		}
    	return false;
    }
    
    /**
     * 检测软件进程是否运行中
     * @param context
     * @param packageName
     * @return
     */
    public static boolean isAppRunning(Context context,String packageName){
		try {
			ActivityManager mActivityManager = (ActivityManager) context
					.getSystemService(Context.ACTIVITY_SERVICE);
			List<RunningAppProcessInfo> run = mActivityManager
					.getRunningAppProcesses();
			for (ActivityManager.RunningAppProcessInfo pro : run) {
				if (pro.processName.equals(packageName)) {
					return true;
				}
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		}
    	return false;
    }
}
