package com.lmq.main.api;

import java.util.List;

import android.app.Activity;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.xyh.R;
import com.lmq.gesture.UnlockGesturePasswordActivity;
import com.lmq.http.BaseHttpClient;
import com.lmq.http.JsonHttpResponseHandler;
import com.lmq.main.dialog.ProgressDialog;
import com.lmq.main.util.Default;

import org.apache.http.Header;
import org.json.JSONObject;

public class BaseActivity extends Activity {

	// FlippingLoadingDialog mLoadingDialog;
			ProgressDialog mLoadingDialog;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActivityManager.addLiveActivity(this);
		// mLoadingDialog = new FlippingLoadingDialog(this, "请求提交中");
		mLoadingDialog = new ProgressDialog(this, "请求提交中，请稍候！");
	}

	@Override
	protected void onStart() {
		super.onStart();
		ActivityManager.addVisibleActivity(this);
	}

	protected void onStop() {
		// TODO Auto-generated method stub
		// showCustomToast("ON STOP ");
		super.onStop();
		ActivityManager.delVisibleActivity(this);
		if (!isAppOnForeground()) {
			// app 进入后台

			// 全局变量isActive = false 记录当前已经进入后台
			Default.isActive = false;

		}
	}


	//点击返回
	private long lastTime;
	private int closeNum;
	//是否出现点击返回提示退出程序功能
	private boolean hasExit = false;

	public void setHasExit(boolean hasExit){

		this.hasExit = hasExit;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub


		if(hasExit){

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

		}



		return super.onKeyDown(keyCode, event);
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

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		// showCustomToast("ON RESUME ");
		super.onResume();

        SharedPreferences sp = getApplicationContext().getSharedPreferences(
                Default.userPreferences, 0);

        Default.user_exit = sp.getBoolean("user_exit",false);
		ActivityManager.addForegroundActivity(this);

		if (!Default.isActive) {
			// app 从后台唤醒，进入前台
			if(!Default.isgestures){
				
				showThePasswordView();
			}else{
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
			Default.click_home_key_flag = false;
			//
			Intent intent = new Intent(getApplicationContext(),
					UnlockGesturePasswordActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			getApplicationContext().startActivity(intent);

		}

	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		ActivityManager.delForegroundActivity(this);
		// showThePasswordView();
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.left_to_right, R.anim.to_right);
	}

	public void finish(int type) {

		dismissLoadingDialog();

		if (type == 0) {
			this.finish();
		} else {
			super.finish();
			overridePendingTransition(R.anim.up_to_down, R.anim.to_down);
		}
	}

	public void startActivity(Intent intent, int type) {
		super.startActivity(intent);
		if (type == 0)
			overridePendingTransition(R.anim.right_to_left, R.anim.to_left);
		else
			overridePendingTransition(R.anim.down_to_up, R.anim.to_up);
	}

	public void startActivity(Intent intent) {
		super.startActivity(intent);
		overridePendingTransition(R.anim.right_to_left, R.anim.to_left);
	}

	public void startActivityForResult(Intent intent, int requestCode) {
		super.startActivityForResult(intent, requestCode);
		overridePendingTransition(R.anim.right_to_left, R.anim.to_left);
	}

	// --------------------------------------------

	// --------------------------------------------------------
	/** 短暂显示Toast提示(来自res) **/
	protected void showShortToast(int resId) {
		Toast.makeText(this, getString(resId), Toast.LENGTH_SHORT).show();
	}

	/** 短暂显示Toast提示(来自String) **/
	protected void showShortToast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}

	/** 长时间显示Toast提示(来自res) **/
	protected void showLongToast(int resId) {
		Toast.makeText(this, getString(resId), Toast.LENGTH_LONG).show();
	}

	/** 长时间显示Toast提示(来自String) **/
	protected void showLongToast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_LONG).show();
	}

	/** 显示自定义Toast提示(来自res) **/
	// protected void showCustomToast(int resId) {
	// View toastRoot = LayoutInflater.from(BaseActivity.this).inflate(
	// R.layout.common_toast, null);
	// ((HandyTextView) toastRoot.findViewById(R.id.toast_text))
	// .setText(getString(resId));
	// Toast toast = new Toast(BaseActivity.this);
	// toast.setGravity(Gravity.CENTER, 0, 0);
	// toast.setDuration(Toast.LENGTH_SHORT);
	// toast.setView(toastRoot);
	// toast.show();
	// }

	/** 显示自定义Toast提示(来自String) **/
	protected void showCustomToast(String text) {
		View toastRoot = LayoutInflater.from(BaseActivity.this).inflate(
				R.layout.common_toast, null);
		((TextView) toastRoot.findViewById(R.id.toast_text)).setText(text);
		Toast toast = new Toast(BaseActivity.this);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(toastRoot);
		toast.show();
	}

	protected void showCustomToast(int id) {
		if (Default.hdf_show_error_info)
			showCustomToast(getResources().getString(id).toString());
	}

	protected void showCustomToast(int id, boolean show) {
		if (show)
			showCustomToast(getResources().getString(id).toString());
	}

	// ---------------------------------------------------------------
	protected void showLoadingDialog( int id) {
		showLoadingDialog(getResources().getString(id));
	}
	protected void showLoadingDialog(String text) {
		if(mLoadingDialog.isShowing()&& !this.isFinishing())
			return;
		if (text != null) {
			mLoadingDialog.setText(text);
		}
		mLoadingDialog.show();
	}

	protected void showLoadingDialogNoCancle(int id)
	{
		showLoadingDialogNoCancle(getResources().getString(id));
	}

	protected void showLoadingDialogNoCancle(String text) {
		if(mLoadingDialog == null)
			return;
		if(mLoadingDialog.isShowing()&& !this.isFinishing())
			return;
		if (text != null && !this.isFinishing()) {
			mLoadingDialog.setText(text);
			// mLoadingDialog.setCancelable(false);
		}
		mLoadingDialog.show();
	}

	protected void dismissLoadingDialog() {
		if(mLoadingDialog == null && this.isFinishing())
			return;
		if (mLoadingDialog.isShowing()&& !this.isFinishing()) {
			mLoadingDialog.dismiss();
		}
	}


}
