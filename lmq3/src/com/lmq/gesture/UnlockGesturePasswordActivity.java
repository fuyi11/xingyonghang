package com.lmq.gesture;

import java.util.List;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.xyh.R;

import com.lmq.main.activity.login.loginActivity;
import com.lmq.main.api.ActivityManager;
import com.lmq.main.api.BaseActivity;
import com.lmq.main.api.LocationApplication;
import com.lmq.main.api.SystenmApi;
import com.lmq.main.util.Default;
import com.lmq.main.util.LockPatternUtils;
import com.lmq.main.util.LockPatternView;
import com.lmq.main.util.LockPatternView.Cell;

public class UnlockGesturePasswordActivity extends BaseActivity {
	private LockPatternView mLockPatternView;
	private int mFailedPatternAttemptsSinceLastTimeout = 0;
	private CountDownTimer mCountdownTimer = null;
	private Handler mHandler = new Handler();
	private TextView mHeadTextView;
	private Animation mShakeAnim;

	private Toast mToast;
	private boolean clolsePwdFlag = false;
	private TextView username;

	private void showToast(CharSequence message) {
		if (null == mToast) {
			mToast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
			mToast.setGravity(Gravity.CENTER, 0, 0);
		} else {
			mToast.setText(message);
		}

		mToast.show();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			ActivityManager.closeAllActivity();
			return false;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gesturepassword_unlock);

		mLockPatternView = (LockPatternView) this
				.findViewById(R.id.gesturepwd_unlock_lockview);
		mLockPatternView.setOnPatternListener(mChooseNewLockPatternListener);
		mLockPatternView.setTactileFeedbackEnabled(true);
		mHeadTextView = (TextView) findViewById(R.id.gesturepwd_unlock_text);
		username = (TextView) findViewById(R.id.gesturepwd_unlock_text2);
		mShakeAnim = AnimationUtils.loadAnimation(this, R.anim.shake_x);

		clolsePwdFlag = getIntent().getBooleanExtra("closePwd", false);
		findViewById(R.id.gesturepwd_unlock_forget).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(
								UnlockGesturePasswordActivity.this,
								loginActivity.class);
						intent.putExtra("check_pass", "1");
						UnlockGesturePasswordActivity.this
								.startActivity(intent);
						finish();

					}
				});
		if(SystenmApi.getUserSavedUserNameAndPwd(UnlockGesturePasswordActivity.this)!=null)
		username.setText(SystenmApi.getUserSavedUserNameAndPwd(UnlockGesturePasswordActivity.this).get(0));
        findViewById(R.id.gesturepwd_login_other_user).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(
                        UnlockGesturePasswordActivity.this,
                        loginActivity.class);
                intent.putExtra("check_pass", "1");
                UnlockGesturePasswordActivity.this
                        .startActivity(intent);
                finish();

            }
        });
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (!LocationApplication.getInstance().getLockPatternUtils()
				.savedPatternExists()) {
			startActivity(new Intent(this, GuideGesturePasswordActivity.class));
			finish();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mCountdownTimer != null)
			mCountdownTimer.cancel();
	}

	private Runnable mClearPatternRunnable = new Runnable() {
		public void run() {
			mLockPatternView.clearPattern();
		}
	};

	protected LockPatternView.OnPatternListener mChooseNewLockPatternListener = new LockPatternView.OnPatternListener() {

		public void onPatternStart() {
			mLockPatternView.removeCallbacks(mClearPatternRunnable);
			patternInProgress();
		}

		public void onPatternCleared() {
			mLockPatternView.removeCallbacks(mClearPatternRunnable);
		}

		public void onPatternDetected(List<LockPatternView.Cell> pattern) {
			if (pattern == null)
				return;
			if (LocationApplication.getInstance().getLockPatternUtils()
					.checkPattern(pattern)) {
				mLockPatternView
						.setDisplayMode(LockPatternView.DisplayMode.Correct);

				if (clolsePwdFlag) {
					Default.passwordSwitchChanged = false;
					SharedPreferences sharedPreferences = getSharedPreferences(
							"lmq", 0);
					Editor editor = sharedPreferences.edit();
					editor.putBoolean("sl", false);
					editor.commit();
					// MainMenuActivity.settingFragment.setpwdSwithClose();
					showToast("关闭手势密码");
				}else{
					/**
					 * 提示登录成功，自动登录
					 */
					showToast("解锁成功");
					SystenmApi.autoLogin(getApplicationContext(),null,null,null);

				}

				// 实现自动登录
				// Setp1 检查是否记住密码
				if (SystenmApi.getUserSavedUserNameAndPwd(getBaseContext()) != null) {
					// 自动登录

				}

				finish();
			} else {
				mLockPatternView
						.setDisplayMode(LockPatternView.DisplayMode.Wrong);
				if (pattern.size() >= LockPatternUtils.MIN_PATTERN_REGISTER_FAIL) {
					mFailedPatternAttemptsSinceLastTimeout++;
					int retry = LockPatternUtils.FAILED_ATTEMPTS_BEFORE_TIMEOUT
							- mFailedPatternAttemptsSinceLastTimeout;
					// 为零的时候直接设置 解锁屏幕不能用
					if (retry == 0) {
						mLockPatternView.clearPattern();
						mLockPatternView.setEnabled(false);
						showToast("您已5次输错密码，请30秒后再试");

					}
					if (retry > 0) {

						mHeadTextView.setText("密码错误，还可以再输入" + retry + "次");
						mHeadTextView.setTextColor(Color.RED);
						mHeadTextView.startAnimation(mShakeAnim);
					}

				} else {
					showToast("输入长度不够，请重试");
				}

				if (mFailedPatternAttemptsSinceLastTimeout >= LockPatternUtils.FAILED_ATTEMPTS_BEFORE_TIMEOUT) {
					mHandler.postDelayed(attemptLockout, 2000);
				} else {
					mLockPatternView.postDelayed(mClearPatternRunnable, 2000);
				}
			}
		}

		public void onPatternCellAdded(List<Cell> pattern) {

		}

		private void patternInProgress() {
		}
	};
	Runnable attemptLockout = new Runnable() {

		@Override
		public void run() {
			// mLockPatternView.clearPattern();
			// mLockPatternView.setEnabled(false);
			mCountdownTimer = new CountDownTimer(
					LockPatternUtils.FAILED_ATTEMPT_TIMEOUT_MS + 1, 1000) {

				@Override
				public void onTick(long millisUntilFinished) {
					int secondsRemaining = (int) (millisUntilFinished / 1000) - 1;
					if (secondsRemaining > 0) {
						mHeadTextView.setText(secondsRemaining + " 秒后重试");
					} else {
						mHeadTextView.setText("请绘制手势密码");
						mHeadTextView.setTextColor(Color.WHITE);
					}

				}

				@Override
				public void onFinish() {
					mLockPatternView.setEnabled(true);
					mFailedPatternAttemptsSinceLastTimeout = 0;
				}
			}.start();
		}
	};

}
