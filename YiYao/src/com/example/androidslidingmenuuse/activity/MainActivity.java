package com.example.androidslidingmenuuse.activity;

import android.app.Activity;
import android.app.ActivityGroup;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.androidslidingmenuuse.R;
import com.example.androidslidingmenuuse.R.id;
import com.example.androidslidingmenuuse.R.layout;
import com.example.androidslidingmenuuse.tool.BaseTools;
import com.example.androidslidingmenuuse.view.DrawerView;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
/**
 * slidingMenu的使用
 * @author miaowei
 *
 */
public class MainActivity extends ActivityGroup implements OnClickListener{
	
	/**
	 * 动态显示界面
	 */
	private LinearLayout container;
	/**
	 * 首页
	 */
	private LinearLayout linear_index;
	/**
	 * 商城
	 */
	private LinearLayout linear_shop;
	/**
	 * 社区
	 */
	private LinearLayout linear_sns;
	/**
	 * 联系我们
	 */
	private LinearLayout linear_conctact;
	
	protected SlidingMenu side_drawer;
	/**head 头部 的左侧菜单 按钮 **/
	ImageView top_head;
	/** head 头部 的右侧菜单 按钮*/
	ImageView top_more;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initSlidingMenu();
		
		initView();
	}

	
	protected void initView(){
		container = (LinearLayout) findViewById(R.id.container);
		linear_index = (LinearLayout) findViewById(R.id.linear_index);
		linear_shop = (LinearLayout) findViewById(R.id.linear_shop);
		linear_sns = (LinearLayout) findViewById(R.id.linear_sns);
		linear_conctact = (LinearLayout) findViewById(R.id.linear_conctact);
		top_head = (ImageView) findViewById(R.id.top_head);
		top_more = (ImageView)findViewById(R.id.top_more);
		linear_index.setOnClickListener(this);
		linear_shop.setOnClickListener(this);
		linear_sns.setOnClickListener(this);
		linear_conctact.setOnClickListener(this);
		top_head.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(side_drawer.isMenuShowing()){
					side_drawer.showContent();
				}else{
					side_drawer.showMenu();
				}
			}
		});
		top_more.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(side_drawer.isSecondaryMenuShowing()){
					side_drawer.showContent();
				}else{
					side_drawer.showSecondaryMenu();
				}
			}
		});
	}
	/**
	 * 初始化slidingMenu
	 */
	protected void initSlidingMenu() {
		
		side_drawer = new DrawerView(this).initSlidingMenu();
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.linear_index:
			showView(0);
			break;
		case R.id.linear_shop:
			showView(1);
			break;
		case R.id.linear_sns:
			showView(2);
			break;
		case R.id.linear_conctact:
			showView(3);
			break;
		default:
			break;
		}
	}
	

	/**
	 * 显示不同界面
	 * @param flag
	 */
	public void showView(int flag) {
		switch (flag) {
		case 0:
			container.removeAllViews();
			Intent index = new Intent(MainActivity.this, IndexActivity.class);
			container.addView(getLocalActivityManager().startActivity("index",index).getDecorView());
			break;
		case 1:
			container.removeAllViews();
			Intent shop = new Intent(MainActivity.this, ShopActivity.class);
			container.addView(getLocalActivityManager().startActivity("shop",shop).getDecorView());
			break;
		case 2:
			container.removeAllViews();
			Intent sns = new Intent(MainActivity.this, SNSActivity.class);
			container.addView(getLocalActivityManager().startActivity("sns",sns).getDecorView());
			break;
		case 3:
			container.removeAllViews();
			Intent con = new Intent(MainActivity.this, ContactActivity.class);
			container.addView(getLocalActivityManager().startActivity("con",con).getDecorView());
			break;
		default:
			break;
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		showView(0);
	}
}
