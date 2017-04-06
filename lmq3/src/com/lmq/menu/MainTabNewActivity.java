package com.lmq.menu;

import java.util.List;

import org.apache.http.Header;
import org.json.JSONObject;

import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.xyh.R;
import com.lmq.gesture.UnlockGesturePasswordActivity;
import com.lmq.http.BaseHttpClient;
import com.lmq.http.JsonHttpResponseHandler;
import com.lmq.http.NetWorkStatusBroadcastReceiver;

import com.lmq.main.activity.login.loginActivity;
import com.lmq.main.activity.tools.LMQWebViewActivity;
import com.lmq.main.api.ActivityManager;
import com.lmq.main.api.JsonBuilder;
import com.lmq.main.api.MyLog;
import com.lmq.main.api.SystenmApi;
import com.lmq.main.api.res.SyncImageLoader;
import com.lmq.main.dialog.CommonDialog;
import com.lmq.main.dialog.NewVersionDialog;
import com.lmq.main.util.Default;
import com.lmq.menu.activity.AccountActivityNew;
import com.lmq.menu.activity.HomeActivity;
import com.lmq.menu.activity.IndexActivity;
import com.lmq.menu.activity.MoreActivity;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

@SuppressWarnings("deprecation")
public class MainTabNewActivity extends TabActivity implements
		OnCheckedChangeListener {

	private TabHost mTabHost;
	private Intent mAIntent;
	private Intent mBIntent;
	private Intent mCIntent;
	private Intent mDIntent;
	private Intent mEIntent;
	public static MainTabNewActivity mainTabNewActivity;
	private NetWorkStatusBroadcastReceiver netWorkStatusBroadcastReceiver;
	private SyncImageLoader syncImageLoader;
	//	private TextView titleView;
	private RadioButton oneButton, towButton, threeButton, fourButton,
			fivButton;
	//private View head_view;

	/**
	 * 当前TabBar 选择项
	 */
	private int currentIndex = 1;
	/***
	 * 当前TabBar 选择项 之前的选择项
	 */
	private int lastIndex = 1;

	private boolean changeToHomeFlag = false;
	private int homeType = 0;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.maintabs);
		mainTabNewActivity = this;

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				getApplicationContext())
				.memoryCacheExtraOptions(480, 800)
						// default = device screen dimensions
				.diskCacheExtraOptions(480, 800, null)
				.threadPoolSize(3)
						// default
				.threadPriority(Thread.NORM_PRIORITY - 2)
						// default
				.tasksProcessingOrder(QueueProcessingType.FIFO)
						// default
				.denyCacheImageMultipleSizesInMemory()
				.memoryCache(new LruMemoryCache(2 * 1024 * 1024))
				.memoryCacheSize(2 * 1024 * 1024).memoryCacheSizePercentage(13)
						// default
				.diskCacheSize(50 * 1024 * 1024).diskCacheFileCount(100)
				.diskCacheFileNameGenerator(new HashCodeFileNameGenerator()) // default
				.defaultDisplayImageOptions(DisplayImageOptions.createSimple()) // default
				.writeDebugLogs().build();

		//head_view = findViewById(R.id.head_view);
		ImageLoader.getInstance().init(config);
		netWorkStatusBroadcastReceiver = new NetWorkStatusBroadcastReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(netWorkStatusBroadcastReceiver, filter);
//		titleView = (TextView) findViewById(R.id.title_view);
		this.mAIntent = new Intent(this, IndexActivity.class);
		this.mBIntent = new Intent(this, HomeActivity.class);
		this.mDIntent = new Intent(this, AccountActivityNew.class);
		this.mEIntent = new Intent(this, MoreActivity.class);

       //检测更新的
		getVersion();
		checkNewVersion();
        //手势密码出现的
        showThePasswordView();

//
//		if (!Default.isActive) {
//			// app 从后台唤醒，进入前台
//			if(!Default.isgestures){
//
//
//			}else{
//				Default.isgestures = false;
//			}
//		}


		/***
		 * 处理推送消息
		 */
		SharedPreferences sharedPreferences = getApplicationContext()
				.getSharedPreferences("lmq", 0);



		if(sharedPreferences.getBoolean("has_pus",false)){

			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putBoolean("has_pus",false);
			editor.commit();

			Intent intent = new Intent();
			intent.putExtra("title","信息详情");
			intent.putExtra("url", Default.ip + "/Mobile/appnewale?id=" + sharedPreferences.getFloat("id",0));
			intent.setClass(MainTabNewActivity.this, LMQWebViewActivity.class);
			startActivity(intent);



		}





		oneButton = ((RadioButton) findViewById(R.id.radio_button0));
		oneButton.setOnCheckedChangeListener(this);

		towButton = ((RadioButton) findViewById(R.id.radio_button1));
		towButton.setOnCheckedChangeListener(this);

		threeButton = ((RadioButton) findViewById(R.id.radio_button2));
		threeButton.setOnCheckedChangeListener(this);

		fourButton = ((RadioButton) findViewById(R.id.radio_button3));
		fourButton.setOnCheckedChangeListener(this);

		fivButton = ((RadioButton) findViewById(R.id.radio_button4));
		fivButton.setOnCheckedChangeListener(this);

		setupIntent();
		((RadioButton) findViewById(R.id.radio_button0)).setChecked(true);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (isChecked) {
			int selectedId = 0;
			switch (buttonView.getId()) {
				case R.id.radio_button0:
					selectedId = 1;
					lastIndex = currentIndex;
					currentIndex = selectedId;
					//titleView.setText(R.string.app_name);
					this.mTabHost.setCurrentTabByTag("A_TAB");
					break;
				case R.id.radio_button1:
					selectedId = 2;
					lastIndex = currentIndex;
					currentIndex = selectedId;
					//titleView.setText("投资");
					this.mTabHost.setCurrentTabByTag("B_TAB");
					break;
				case R.id.radio_button2:
					selectedId = 3;
					lastIndex = currentIndex;
					currentIndex = selectedId;
					if(Default.userId == 0){
						Intent lendMoneyLoginIntent = new Intent();
						lendMoneyLoginIntent.setClass(this, loginActivity.class);
						startActivityForResult(lendMoneyLoginIntent, 4000);

					}else{
						//titleView.setText("我要借款");
						this.mTabHost.setCurrentTabByTag("C_TAB");
					}
					break;
				case R.id.radio_button3:
					selectedId = 4;
					lastIndex = currentIndex;
					currentIndex = selectedId;

					if (Default.userId == 0) {
						Intent userInfoLoginIntent = new Intent();
						userInfoLoginIntent.setClass(this, loginActivity.class);
						startActivityForResult(userInfoLoginIntent, 5000);
					} else {
//				titleView.setText("我的账户");
						this.mTabHost.setCurrentTabByTag("D_TAB");

					}
					break;
				case R.id.radio_button4:
					selectedId = 5;
					lastIndex = currentIndex;
					currentIndex = selectedId;
					//titleView.setText("更多");
					this.mTabHost.setCurrentTabByTag("MORE_TAB");
					break;
			}
			if (selectedId == 4) {
				//head_view.setVisibility(View.GONE);
			} else {
				//head_view.setVisibility(View.VISIBLE);
			}
		}

	}

	public SyncImageLoader getImageLodader() {
		return syncImageLoader;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
			case 4000:
				if (resultCode == Default.LOGIN_TYPE_2) {
					//	titleView.setText("我要借款");
					this.mTabHost.setCurrentTabByTag("C_TAB");
				} else {
					changeTOFirst(lastIndex);
				}

				break;
			case 5000:

				if (resultCode == Default.LOGIN_TYPE_2) {
					this.mTabHost.setCurrentTabByTag("D_TAB");
				} else {
					changeTOFirst(lastIndex);
				}
				break;
			case 6000:
				// 设置头型
				if (resultCode == Default.LOGIN_TYPE_2) {

					// sm.toggle(true);
				} else {

				}
				break;

			default:
				break;
		}
	}

	/**
	 * 跳转到上一页
	 */
	private void changeTOFirst(int index) {
		switch (index) {
			case 1:
				oneButton.setChecked(true);
				break;
			case 2:
				towButton.setChecked(true);
				break;
			case 3:
				threeButton.setChecked(true);
				break;
			case 4:
				fourButton.setChecked(true);
				break;
			case 5:
				fivButton.setChecked(true);
				break;

			default:
				break;
		}
		changeTitle(index);

	}

	/**
	 * 更改标题
	 *
	 * @param index
	 *            Tab 选项
	 */
	private void changeTitle(int index) {




		switch (index) {
			case 1:
				//titleView.setText(getResources().getString(R.string.app_name));
				break;
			case 2:
				//titleView.setText("我要投资");
				break;
			case 3:
				if (Default.userId == 0) {
					Intent lendMoneyLoginIntent = new Intent();
					lendMoneyLoginIntent.setClass(this, loginActivity.class);
					startActivityForResult(lendMoneyLoginIntent, 4000);

				} else {

					//titleView.setText("我要借款");
					threeButton.setChecked(true);
				}
				break;
			case 4:
				if (Default.userId == 0) {
					Intent userInfoLoginIntent = new Intent();
					userInfoLoginIntent.setClass(this, loginActivity.class);
					startActivityForResult(userInfoLoginIntent, 5000);
				} else {
					//titleView.setText("我的账户");
					fourButton.setChecked(true);
				}
				break;
			case 5:
				//titleView.setText("设置");
				break;

			default:
				break;
		}
	}

	/***
	 * 跳转到首页
	 *
	 * @return
	 */
	public void IndexView() {

		oneButton.setChecked(true);

	}

	/**
	 * 跳转到我要投资
	 */
	public void changeHomeFragment(int type) {
		homeType = type;
		changeToHomeFlag = true;
		towButton.setChecked(true);

	}


	//点击返回
	private long lastTime;
	private int closeNum;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub

		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
				closeNum++;
				if (System.currentTimeMillis() - lastTime > 5000) {
					closeNum = 0;
				}
				lastTime = System.currentTimeMillis();
				if (closeNum == 1) {
					doHttp();
					finish();
				} else {

					showCustomToast(R.string.toast6);
				}
			}
			return false;
		}

		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 判断手势密码进入
	 */

	protected void onStop() {
		// TODO Auto-generated method stub
		// showCustomToast("ON STOP ");
		super.onStop();
		if (!isAppOnForeground()) {
			// app 进入后台

			// 全局变量isActive = false 记录当前已经进入后台
			Default.isActive = false;

		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		// showCustomToast("ON RESUME ");
		super.onResume();



		if (!Default.isActive) {
			// app 从后台唤醒，进入前台
			if (!Default.isgestures) {

				showThePasswordView();
			} else {
				Default.isgestures = false;
			}
			Default.isActive = true;
		}
	}

	/**
	 * 程序是否在前台运行
	 *
	 * @return
	 */
	public boolean isAppOnForeground() {
		// Returns a list of application processes that are running on the
		// device

		android.app.ActivityManager activityManager = (android.app.ActivityManager) getApplicationContext()
				.getSystemService(Context.ACTIVITY_SERVICE);
		String packageName = getApplicationContext().getPackageName();

		List<RunningAppProcessInfo> appProcesses = activityManager
				.getRunningAppProcesses();
		if (appProcesses == null)
			return false;

		for (RunningAppProcessInfo appProcess : appProcesses) {
			// The name of the process that this object is associated with.
			if (appProcess.processName.equals(packageName)
					&& appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
				return true;
			}
		}

		return false;
	}

	public void showThePasswordView() {
		SharedPreferences sharedPreferences = getApplicationContext()
				.getSharedPreferences("lmq", 0);

		if (sharedPreferences.getBoolean("sl", false)) {
			Intent intent = new Intent(getApplicationContext(),
					UnlockGesturePasswordActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			getApplicationContext().startActivity(intent);

		}

	}

	private NewVersionDialog dialog;
	private String downloadURL = "";

	// 提交用户反馈到服务器
	public void checkNewVersion() {

		dialog = new NewVersionDialog(MainTabNewActivity.this, "");
		dialog.setListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				switch (v.getId()) {
					// 下载更新
					case R.id.download_newapp:

						// dialog.updateBtn.setEnabled(false);
						//
						// dialog.exitbtn.setEnabled(false);
						// dialog.showProgress();
						// downloadNewVersionApp(dialog.getPath());

						ActivityInfo activityInfo = SystenmApi
								.getBrowserApp(getApplicationContext());
						if (activityInfo != null) {
							Uri uri = Uri.parse(dialog.getPath());
							Intent intent = new Intent(Intent.ACTION_VIEW, uri);
							intent.setClassName(activityInfo.packageName,
									activityInfo.name);
							startActivity(intent);
						} else {
							showCustomToast("手机没有浏览器，无法更新...");
						}

						break;
					// 退出程序
					case R.id.newversion_exit:
						doHttp();
						finish();

						break;

					default:
						break;
				}

			}

		});

		JsonBuilder jsonBuilder = new JsonBuilder();

		jsonBuilder.put("version", Default.curVersion);// SystenmApi.getVersion(MainMenuActivity.this)

		BaseHttpClient.post(getBaseContext(), Default.version, jsonBuilder,
				new JsonHttpResponseHandler() {

					@Override
					public void onStart() {
						// TODO Auto-generated method stub
						super.onStart();

					}

					@Override
					public void onSuccess(int statusCode, Header[] headers,
										  JSONObject response) {
						// TODO Auto-generated method stub
						super.onSuccess(statusCode, headers, response);
						try {

							if (statusCode == 200) {


								MyLog.e(response.toString());
								if (response.getInt("status") == 1) {

								} else if (response.getInt("status") == 0) {

									String temp = dialog.versionTextView
											.getText().toString();
									dialog.versionTextView.setText(temp + "("
											+ response.getString("version")
											+ ")");
									dialog.show();
									dialog.setPath(response.getString("path")
											.replace("\\/", ""));
								} else {

								}
							} else {
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
										  String responseString, Throwable throwable) {
						// TODO Auto-generated method stub
						super.onFailure(statusCode, headers, responseString,
								throwable);
					}

				});

	}


	private void doHttp() {

		BaseHttpClient.post(getBaseContext(), Default.exit, null,
				new JsonHttpResponseHandler() {

					@Override
					public void onStart() {
						// TODO Auto-generated method stub
						super.onStart();

					}

					@Override
					public void onSuccess(int statusCode, Header[] headers,
										  JSONObject response) {
						// TODO Auto-generated method stub
						super.onSuccess(statusCode, headers, response);

						try {

							if (statusCode == 200) {

								if (response.getInt("status") == 1) {
									MyLog.d("zzx", "exit成功");
									ActivityManager.closeAllActivity();
								} else {
									MyLog.d("zzx", "exit失败");
								}
							} else {
								MyLog.d("zzx", "exit失败");
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
										  String responseString, Throwable throwable) {
						// TODO Auto-generated method stub
						super.onFailure(statusCode, headers, responseString,
								throwable);
					}

				});

	}

	private void setupIntent() {
		this.mTabHost = getTabHost();
		TabHost localTabHost = this.mTabHost;

		localTabHost.addTab(buildTabSpec("A_TAB", R.string.tab_index_str,
				R.drawable.b_page_1_1, this.mAIntent));

		localTabHost.addTab(buildTabSpec("B_TAB", R.string.tab_invest_str,
				R.drawable.b_page_3_1, this.mBIntent));

		localTabHost.addTab(buildTabSpec("C_TAB", R.string.tab_lend_str,
				R.drawable.b_page_5_1, this.mCIntent));

		localTabHost.addTab(buildTabSpec("D_TAB", R.string.tab_account_str,
				R.drawable.b_page_2_1, this.mDIntent));

		localTabHost.addTab(buildTabSpec("MORE_TAB", R.string.tab_more_str,
				R.drawable.b_page_4_1, this.mEIntent));

	}

	private TabHost.TabSpec buildTabSpec(String tag, int resLabel, int resIcon,
										 final Intent content) {
		return this.mTabHost
				.newTabSpec(tag)
				.setIndicator(getString(resLabel),
						getResources().getDrawable(resIcon))
				.setContent(content);
	}

	public int getPercent(int x, int total) {
		int result = 0;// 接受百分比的值
		double x_double = Double.parseDouble(x + "");
		int tempresult = (int) ((x / Double.parseDouble(total + "")) * 100);

		return tempresult;
	}

	/**
	 * 获取版本号
	 *
	 * @return 当前应用的版本号
	 */
	public void getVersion() {
		try {
			Default.curVersion = (getPackageManager().getPackageInfo(
					getPackageName(), 0)).versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(netWorkStatusBroadcastReceiver); // 取消监听
		super.onDestroy();
	}

	public void showCustomToast(String text) {
		View toastRoot = LayoutInflater.from(MainTabNewActivity.this).inflate(
				R.layout.common_toast, null);
		((TextView) toastRoot.findViewById(R.id.toast_text)).setText(text);
		Toast toast = new Toast(MainTabNewActivity.this);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(toastRoot);
		toast.show();
	}

	public void showCustomToast(int id) {
		showCustomToast(getResources().getString(id).toString());
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			// sm.toggle(true);

			return false;
		}
		return super.onKeyUp(keyCode, event);
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
		Default.isAlive = false;
	}
}