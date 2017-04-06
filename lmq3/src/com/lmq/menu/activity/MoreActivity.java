package com.lmq.menu.activity;

import org.apache.http.Header;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption.LocationMode;
import com.xyh.R;
import com.lmq.gesture.GuideGesturePasswordActivity;
import com.lmq.gesture.UnlockGesturePasswordActivity;
import com.lmq.http.BaseHttpClient;
import com.lmq.http.JsonHttpResponseHandler;
import com.lmq.main.activity.more.FeedbackActivity;
import com.lmq.main.activity.tools.LMQWebViewActivity;
import com.lmq.main.activity.news.NewsListActivity;
import com.lmq.main.activity.news.NoticeActivity;
import com.lmq.main.activity.login.loginActivity;
import com.lmq.main.activity.user.manager.pay.ThirdPayActivity;
import com.lmq.main.api.BaseActivity;
import com.lmq.main.api.LocationApplication;
import com.lmq.main.api.MyLog;
import com.lmq.main.api.SystenmApi;
import com.lmq.main.dialog.CommonDialog;
import com.lmq.main.util.Data;
import com.lmq.main.util.Default;

public class MoreActivity extends BaseActivity implements  OnClickListener{

	
	private View parentView;
	private LocationClient locationClient = null;
	private static final int UPDATE_TIME = 5000;
	private static int LOCATION_COUTNS = 0;
	private LocationMode tempMode = LocationMode.Hight_Accuracy;
	private String tempcoor = "gcj02";
	private SharedPreferences sharedPreferences;
	private ToggleButton t2;
	private Button exit_button;
	private View finger_pwd;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setHasExit(true);
		setContentView(R.layout.fragment_more);
		sharedPreferences = MoreActivity.this.getSharedPreferences("lmq", 0);

		initView();
	}
		
	public void initView() {

		((LocationApplication) MoreActivity.this.getApplication()).resultTextView = (TextView) 
				findViewById(R.id.location_text);
		findViewById(R.id.userinfo).setOnClickListener(
				new ItemTouchListener());
		findViewById(R.id.item_news).setOnClickListener(
				new ItemTouchListener());
		findViewById(R.id.item_notice).setOnClickListener(
				new ItemTouchListener());
		findViewById(R.id.item_newVersion).setOnClickListener(
				new ItemTouchListener());
		findViewById(R.id.item_feedback).setOnClickListener(
				new ItemTouchListener());
		findViewById(R.id.item_about).setOnClickListener(
				new ItemTouchListener());
		findViewById(R.id.company_site).setOnClickListener(
				new ItemTouchListener());

		findViewById(R.id.company_tel).setOnClickListener(
				new ItemTouchListener());
		findViewById(R.id.wechat_btn).setOnClickListener(
				new ItemTouchListener());
		
		finger_pwd = findViewById(R.id.finger_pwd);
		exit_button = (Button) findViewById(R.id.more_exit);
		exit_button.setOnClickListener(new ItemTouchListener());

		ToggleButton t = (ToggleButton) findViewById(R.id.toggle_location);
		t.setOnCheckedChangeListener(new SwitchClickListener());
		t2 = (ToggleButton) findViewById(R.id.toggle_screenLock);
		t2.setOnClickListener(this);

//		showExitBtn();

		boolean local_flag = sharedPreferences.getBoolean("dw", false);
		if (local_flag) {

			t.setChecked(true);
			getLocation();

		} else {

			t.setChecked(false);
		}

		if (!sharedPreferences.getBoolean("sl", false)) {

			t2.setChecked(false);

		}

	}

	public void showDialog() {
		AlertDialog.Builder builder = new Builder(MoreActivity.this);
		builder.setTitle("退出");
		builder.setMessage("是否退出该用户");
		builder.setIcon(R.drawable.ic_launcher);

		builder.setPositiveButton("退出",
				new android.content.DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						doHttpExit();
						dialog.dismiss();

					}

				});
		builder.setNegativeButton("取消",
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.create().show();
	}

	/**
	 * 退出登录按钮控制
	 */
//	private void showExitBtn() {
//
//		switch (Default.style) {
//
//		case 0:
//			if (Default.userId == 0) {
//				exit_button.setVisibility(View.GONE);
//			} else {
//				exit_button.setVisibility(View.VISIBLE);
//			}
//
//		default:
//			break;
//
//		}
//	}

	private void doHttpExit() {
		BaseHttpClient.post(MoreActivity.this, Default.exit, null,
				new JsonHttpResponseHandler() {

					@Override
					public void onStart() {
						// TODO Auto-generated method stub
						super.onStart();

						showLoadingDialogNoCancle(getResources().getString(
								R.string.toast2));
					}

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject json) {
						// TODO Auto-generated method stub
						super.onSuccess(statusCode, headers, json);
						MyLog.d("zzx", "exit成功" + json.toString());
						try {

							if (statusCode == 200) {
								if (json.getInt("status") == 1) {
									MyLog.d("zzx", "exit成功");
									Default.layout_type = Default.pageStyleLogin;
									Default.userId = 0;
//									showExitBtn();
									// setDefaultImage();
									Data.clearInfo();
									// Intent intent = new Intent();
								} else {
									String message = json.getString("message");
									SystenmApi.showCommonErrorDialog(MoreActivity.this, json.getInt("status"), message);

								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						dismissLoadingDialog();
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							String responseString, Throwable throwable) {
						// TODO Auto-generated method stub
						super.onFailure(statusCode, headers, responseString,
								throwable);
						dismissLoadingDialog();
					}

				});
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		// 打开开关
		if (Default.passwordSwitchChanged) {

			if (t2 != null) {
				t2.setChecked(true);
			}
		} else if (!Default.passwordSwitchChanged
				&& !sharedPreferences.getBoolean("sl", false)) {
			// 关闭开关
			if (t2 != null) {
				t2.setChecked(false);
			}

		}
//		showExitBtn();
		// showFingerPWDView();
	}

	// private void showFingerPWDView() {
	//
	// if (Default.userId == 0) {
	//
	// finger_pwd.setVisibility(View.GONE);
	// } else {
	//
	// finger_pwd.setVisibility(View.VISIBLE);
	//
	// }
	// }

	// 开关监听事件
	class SwitchClickListener implements OnCheckedChangeListener {

		@Override
		public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
			// TODO Auto-generated method stub

			switch (arg0.getId()) {
			case R.id.toggle_location:
				if (arg1) {

					Editor editor = sharedPreferences.edit();
					editor.putBoolean("dw", true);
					editor.commit();
					getLocation();
				} else {
					Editor editor = sharedPreferences.edit();
					editor.putBoolean("dw", false);
					editor.commit();
					((LocationApplication) MoreActivity.this.getApplication()).locationClient
							.stop();
					((LocationApplication) MoreActivity.this.getApplication()).resultTextView
							.setText("未定位");

				}

				break;

			case R.id.toggle_screenLock:

				// if (arg1) {
				// MyLog.e("手势解锁", "------------>");
				// if (!sharedPreferences.getBoolean("sl", false)) {
				// startActivityForResult(new Intent(MoreActivity.this,
				// GuideGesturePasswordActivity.class), 1212);
				// }
				//
				// } else {
				//
				// Intent closeIntent = new Intent(MoreActivity.this,
				// UnlockGesturePasswordActivity.class);
				// closeIntent.putExtra("closePwd", true);
				// startActivity(closeIntent);
				//
				// }
				break;

			default:
				break;
			}

		}

	}

	public void setpwdSwithClose() {

		if (t2 != null) {

			t2.setChecked(false);
		}
		Editor editor = sharedPreferences.edit();
		editor.putBoolean("sl", false);
		editor.commit();
	}

	public void setpwdSwitch() {
		if (t2 != null) {

			t2.setChecked(true);
		}

	}

	// 实现Item 点击监听事件
	class ItemTouchListener implements OnClickListener {

		@Override
		public void onClick(View arg0) {

			switch (arg0.getId()) {
			case R.id.more_exit:
				showDialog();
				break;
			case R.id.item_news:

				MoreActivity.this.startActivity(
						new Intent(MoreActivity.this, NewsListActivity.class));

				break;
			case R.id.item_notice:

				MoreActivity.this.startActivity(
						new Intent(MoreActivity.this, NoticeActivity.class));

				break;

			case R.id.item_newVersion:
				// getLocation();
				// locationClient.start();
				checkNewVersion();

				break;
			case R.id.item_feedback:

				// 检测用户是否登录
				if (Default.userId != 0) {

					MoreActivity.this.startActivity(
							new Intent(MoreActivity.this, FeedbackActivity.class));

				} else {

					MoreActivity.this.startActivity(
							new Intent(MoreActivity.this, loginActivity.class));
				}

				break;
			case R.id.item_about:

//				MoreActivity.this.startActivity(
//						new Intent(MoreActivity.this, AboutActivity.class));

				Intent abIntent = new Intent();
				abIntent.putExtra("title","关于我们");
				abIntent.putExtra("url",Default.ip+"/Mobile/appnewale?type_id=10");
				abIntent.setClass(MoreActivity.this, LMQWebViewActivity.class);
				startActivity(abIntent);


				break;
			case R.id.company_site:

				ActivityInfo activityInfo = SystenmApi
						.getBrowserApp(getApplicationContext());
				if (activityInfo != null) {
					Uri uri = Uri.parse(Default.ip);
					Intent intent = new Intent(Intent.ACTION_VIEW, uri);
					intent.setClassName(activityInfo.packageName, activityInfo.name);
					startActivity(intent);
				} else {
					showCustomToast("手机没有浏览器，无法访问");
				}

				break;
			case R.id.company_tel:
				gocallphone();
				break;
			case R.id.wechat_btn:

				if (android.os.Build.VERSION.SDK_INT > 11) {
					android.content.ClipboardManager c = (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
					c.setText(getResources().getString(R.string.company_weichat_str));

				} else {
					android.text.ClipboardManager c = (android.text.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

					c.setText(getResources().getString(R.string.company_weichat_str));
				}

				showCustomToast("官方微信已复制，请到微信搜索");


				break;

			default:

				break;
			}

		}

	}

	@Override
	public void onClick(View arg0) {

		switch (arg0.getId()) {
		case R.id.toggle_screenLock:

			if (Default.userId == 0) {
				t2.setChecked(false);
				showCustomToast("请登录后再设置手势密码！");

			} else {
				if (!sharedPreferences.getBoolean("sl", false)) {
					t2.setChecked(false);
					startActivityForResult(new Intent(MoreActivity.this,
							GuideGesturePasswordActivity.class), 1212);
				} else {
					t2.setChecked(true);
					Intent closeIntent = new Intent(MoreActivity.this,
							UnlockGesturePasswordActivity.class);
					closeIntent.putExtra("closePwd", true);
					startActivity(closeIntent);

				}
			}

			break;

		default:
			break;
		}
	}

	public void gocallphone() {

		CommonDialog.Builder ibuilder  = new CommonDialog.Builder(this);
		ibuilder.setTitle(R.string.prompt);
		ibuilder.setMessage(R.string.callphone);
		ibuilder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

				dialog.dismiss();
				Intent intent2 = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
						+MoreActivity.this.getString(R.string.about_info3)));
				// intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent2);

			}
		});

		ibuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

				dialog.dismiss();
			}
		});
		ibuilder.create().show();



	}

	// 提交用户反馈到服务器
	public void checkNewVersion() {

		BaseHttpClient.post(MoreActivity.this, Default.version, null,
				new JsonHttpResponseHandler() {

					@Override
					public void onStart() {
						// TODO Auto-generated method stub
						super.onStart();

						showLoadingDialogNoCancle(getResources().getString(
								R.string.toast2));
					}

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						// TODO Auto-generated method stub
						super.onSuccess(statusCode, headers, response);
						try {

							if (statusCode == 200) {
								// 没有新版本
								if (response.getInt("status") == 1) {
									// initData(json);
									// 获取新版本
									showCustomToast(response
											.getString("message"));
								} else {
									String message = response.getString("message");
									SystenmApi.showCommonErrorDialog(MoreActivity.this, response.getInt("status"), message);


								}
							} else {
								showCustomToast(R.string.toast1);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						dismissLoadingDialog();
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							String responseString, Throwable throwable) {
						// TODO Auto-generated method stub
						super.onFailure(statusCode, headers, responseString,
								throwable);
						dismissLoadingDialog();
						showCustomToast(responseString);
					}

				});

	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);
		MyLog.e("123", "123");
		if (requestCode == 1212) {
			if (!sharedPreferences.getBoolean("sl", false)) {

				t2.setChecked(false);

			} else {

				t2.setChecked(true);
			}

		}
		if (requestCode == 101) {
			if (resultCode == 102) {
				showCustomToast(data.getExtras().getString("msg"));
			}
		}
	}

	/***
	 * 基于百度定位SDK获取地理位置
	 * 
	 */
	public void getLocation() {

//		locationClient = ((LocationApplication) MoreActivity.this.getApplication()).locationClient;
//		LocationClientOption option = new LocationClientOption();
//		option.setLocationMode(tempMode);// 设置定位模式
//		option.setCoorType(tempcoor);// 返回的定位结果是百度经纬度，默认值gcj02
//		int span = 1000;
//		option.setScanSpan(span);// 设置发起定位请求的间隔时间为5000ms
//		option.setIsNeedAddress(true);
//		locationClient.setLocOption(option);
//		locationClient
//				.registerLocationListener(((LocationApplication) 
//						.getApplication()).myLocationListener);
//		locationClient.start();

	}

}
