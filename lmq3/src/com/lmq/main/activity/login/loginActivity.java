package com.lmq.main.activity.login;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.xyh.R;
import com.lmq.gesture.CreateGesturePasswordActivity;
import com.lmq.gesture.UnlockGesturePasswordActivity;
import com.lmq.http.BaseHttpClient;
import com.lmq.http.JsonHttpResponseHandler;
import com.lmq.main.activity.register.YbRegisterActivity;
import com.lmq.main.activity.user.manager.password.ForgotPasswordActivity;
import com.lmq.main.activity.register.RegisterStepFirstActivity;
import com.lmq.main.api.BaseActivity;
import com.lmq.main.api.JsonBuilder;
import com.lmq.main.api.MyLog;
import com.lmq.main.api.SystenmApi;
import com.lmq.main.util.Default;
import com.lmq.main.util.DesUtil;
import com.lmq.menu.MainTabNewActivity;

public class loginActivity extends BaseActivity implements OnClickListener {

	private EditText mEditName, mEditPassword;
	private boolean mRemember = true;

	private String mName;
	private String name;
	private String mPassword;
	private boolean modify_password_flag = false;
	private boolean flag;
	private boolean user_offline_flag = false;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		if (intent.getStringExtra("check_pass") != null
				&& intent.getStringExtra("check_pass").equals("1")) {
			modify_password_flag = true;
		} else {
			modify_password_flag = false;
		}
		if (intent.getBooleanExtra("exit", false)) {
			user_offline_flag = intent.getBooleanExtra("exit", false);
			handler.sendEmptyMessage(1);
		}

		setContentView(R.layout.login_new);
		mEditName = (EditText) findViewById(R.id.editname);
		mEditPassword = (EditText) findViewById(R.id.editpassw);

		TextView text = (TextView) findViewById(R.id.title);
		text.setText(R.string.peo_info8);
		
		
		findViewById(R.id.login).setOnClickListener(this);
		findViewById(R.id.register).setOnClickListener(this);
		findViewById(R.id.back).setOnClickListener(this);
		findViewById(R.id.forget_pwd).setOnClickListener(this);

		if (getNP() && !modify_password_flag) {
			mEditName.setText(mName);
			mEditPassword.setText(mPassword);
		}
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				showCustomToast("已掉线，请重新登录");
				doHttpExit();
				break;
			}
		}

	};

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == Default.RESULT_REGISTER_TO_LOGIN) {
			mEditName.setText(data.getExtras().getString("name"));
			mEditPassword.setText(data.getExtras().getString("password"));
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			if (modify_password_flag) {

				Intent intent = new Intent(loginActivity.this,
						UnlockGesturePasswordActivity.class);
				startActivity(intent);
			}
			
			/**掉线返回*/
			if(user_offline_flag){
				userOfflineBackAction();
			}
			finish();
			break;
		case R.id.login:
			mName = mEditName.getText().toString();
			mPassword = mEditPassword.getText().toString();

			if (mName.equals("") || mPassword.equals("")) {
				showCustomToast(R.string.erro_login1);
				return;
			}
			// 是否保存密码
			if (mRemember)
				saveNP();
			else {
				clearNP();
			}
			doHttpLogin();
			break;
		case R.id.register:

			if(Default.IS_YB){
				startActivityForResult(new Intent(loginActivity.this,
						YbRegisterActivity.class), 0);
			}else{

				startActivityForResult(new Intent(loginActivity.this,
						RegisterStepFirstActivity.class), 0);
			}

			break;
		case R.id.forget_pwd:
			startActivity(new Intent(loginActivity.this,
					ForgotPasswordActivity.class));
			break;
		}
	};

	public void clearInfo() {
		mEditName.setText("");
		mEditPassword.setText("");
		mRemember = false;
	}
	
	/**
	 * 用户掉线返回
	 */
	private void userOfflineBackAction(){
		MainTabNewActivity.mainTabNewActivity.IndexView();
		finish();
	}

	/**
	 * 存储用户信息
	 */
	public void saveNP() {
		try {
			DesUtil des = new DesUtil();
			SystenmApi.saveUserLoginInfo(getApplicationContext(), mName, mPassword);
		} catch (Exception e) {
			e.printStackTrace();
		}

		SharedPreferences sp = getSharedPreferences(Default.userPreferences, 0);
		Editor edit = sp.edit();
		edit.putBoolean(Default.userRemember, mRemember);
		edit.commit();
	}

	public void clearNP() {
		SharedPreferences sp = getSharedPreferences(Default.userPreferences, 0);
		Editor edit = sp.edit();
		edit.putString(Default.userName, "");
		edit.putString(Default.userPassword, "");
		edit.putBoolean(Default.userRemember, false);
		edit.commit();
	}

	public boolean getNP() {
		SharedPreferences sp = getSharedPreferences(Default.userPreferences, 0);
		mName = sp.getString(Default.userName, "");
		mPassword = sp.getString(Default.userPassword, "");
		mRemember = sp.getBoolean(Default.userRemember, false);

		if (mName.equals("") || mPassword.equals("")) {
			return false;
		} else {
			try {
				DesUtil des = new DesUtil();
				mName = des.decrypt(mName);
				mPassword =des.decrypt(mPassword);
			} catch (Exception e) {
				e.printStackTrace();
			}

			return true;
		}
	}

	/**
	 * 用户登陆
	 */

	public void doHttpLogin() {
		JsonBuilder builder = new JsonBuilder();

		builder.put("sUserName", mName);
		builder.put("sPassword", mPassword);

		BaseHttpClient.post(getBaseContext(), Default.login, builder,
				new JsonHttpResponseHandler() {

					@Override
					public void onStart() {
						// TODO Auto-generated method stub
						super.onStart();
						showLoadingDialogNoCancle(R.string.toast2);
					}

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject json) {
						// TODO Auto-generated method stub
						super.onSuccess(statusCode, headers, json);
 
						dismissLoadingDialog();
						if (statusCode == 200) {
							try {
								if (json.has("status")) {
									if (json.getInt("status") == 1) {
										getJsonInfo(json);
										if (modify_password_flag) {

											Intent intent = new Intent(
													loginActivity.this,
													CreateGesturePasswordActivity.class);

											intent.putExtra("modify_pass", true);
											loginActivity.this
													.startActivity(intent);
										}

                                        SharedPreferences sp = getApplicationContext().getSharedPreferences(
                                                Default.userPreferences, 0);
                                        Editor edit = sp.edit();
                                        edit.putLong("lastLoginTime", System.currentTimeMillis());
                                        edit.putBoolean("user_exit", false);
                                        edit.putString("userid", json.optString("uid"));

										boolean showSl = sp.getBoolean("sl",false);
										MyLog.i("asd","lastsl"+sp.getBoolean(Default.userLastSl,false));
										MyLog.i("asd","sl"+showSl);
										if(!sp.getString(Default.userLastUid,"").equals(""+Default.userId)){
//                                        	if(!showSl)
											{
                                            	startActivity(new Intent(loginActivity.this, CreateGesturePasswordActivity.class));
                                        	}
										}else{
											edit.putBoolean("sl", sp.getBoolean(Default.userLastSl,false));

										}
										edit.commit();
										SystenmApi.saveUserLoginInfo(getApplicationContext(), mName, mPassword);

										if(Default.IS_YB){


											checkoutUserHasBindYBAccountStatus();

										}else{
											finish();
										}



									} else {
										showCustomToast(json .getString("message"));
									}
								} else {
									showCustomToast(json.getString("message"));
								}
							} catch (Exception e) {

								e.printStackTrace();
								showCustomToast("登录异常");
							}
						} else {
							showCustomToast(R.string.toast1);
						}
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
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (modify_password_flag) {

				Intent intent = new Intent(loginActivity.this,
						UnlockGesturePasswordActivity.class);
				startActivity(intent);
				finish();
			}
			
			if(user_offline_flag){
				userOfflineBackAction();
			}

		}
		return super.onKeyDown(keyCode, event);
	}

	public void getJsonInfo(JSONObject json) {
		try {
			int type = json.getInt("status");
			if (type == 1) {
				Default.userId = json.getLong("uid");
				Default.username = json.getString("username");
				if (json.has("phoneverify_manual"))
					Default.phoneverif = json.getInt("phoneverify_manual");
				Intent intent = new Intent();

				try {
					SharedPreferences sp = getSharedPreferences("user", 0);
					Editor edit = sp.edit();

					// 用户名保存
					edit.putString("username", json.getString("username"));
					// 用户ID 保存
					edit.putString("userid", json.getString("uid"));
					// 用户头像保存
					edit.putString("phoneverif", json.has("head")?json.getString("head"):"");

					edit.commit();

				} catch (JSONException e) {
					e.printStackTrace();
				}

				setResult(Default.LOGIN_TYPE_2, intent);

				Default.layout_type = Default.pageStyleInfo;

				overridePendingTransition(R.anim.left_to_right, R.anim.to_right);
			} else {
				showCustomToast(json.getString("message"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void doHttpExit() {
		JsonBuilder builder = new JsonBuilder();

		BaseHttpClient.post(getBaseContext(), Default.exit, null,
				new JsonHttpResponseHandler() {

					@Override
					public void onStart() {
						// TODO Auto-generated method stub
						super.onStart();
					}

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject json) {
						// TODO Auto-generated method stub
						super.onSuccess(statusCode, headers, json);
						try {

							if (statusCode == 200) {
								if (json.getInt("status") == 1) {
									MyLog.d("login", "exit成功");
								} else {
									MyLog.d("login", "exit失败");
								}
							} else {
								MyLog.d("login", "exit失败");
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
						dismissLoadingDialog();
					}

				});
	}











	//检测是否绑定托管
	public void checkoutUserHasBindYBAccountStatus() {
		JsonBuilder builder = new JsonBuilder();

		BaseHttpClient.post(getBaseContext(), Default.Yb_isbind, builder,
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
						dismissLoadingDialog();
						// TODO Auto-generated method stub
						super.onSuccess(statusCode, headers, json);


						if (statusCode == 200) {
							try {
								if (json.has("status")) {
									if (json.getInt("status") == 1) {

										Default.has_Ybbind = true;
										finish();
									} else {
										Default.has_Ybbind = false;
										//								showCustomToast(json
										//										.getString("message"));
										finish();
									}
								} else {
									showCustomToast(json.getString("message"));
								}

							} catch (Exception e) {
								e.printStackTrace();
							}

						} else {
							showCustomToast(R.string.toast1);
						}

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


}
