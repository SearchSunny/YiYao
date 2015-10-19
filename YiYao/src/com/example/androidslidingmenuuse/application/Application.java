package com.example.androidslidingmenuuse.application;

import com.example.androidslidingmenuuse.tool.DebugManager;
/**
 * 全局应用
 * @author miaowei
 *
 */
public class Application extends android.app.Application {

	private static Application instance = null;
	@Override
	public void onCreate() {
		super.onCreate();
		
		instance = this;
		DebugManager.getInstance().getCrashDebug(getApplicationContext());
	}
	
	public static Application getInstance() {
		
		return instance;
	}
}
