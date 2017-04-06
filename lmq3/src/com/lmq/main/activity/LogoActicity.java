package com.lmq.main.activity;

import java.io.File;
import java.security.PrivateKey;
import java.security.PublicKey;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.widget.Toast;

import com.xyh.R;
import com.lmq.main.api.MyLog;
import com.lmq.main.util.Default;
import com.lmq.main.util.RSAUtils;
import com.lmq.menu.MainTabNewActivity;
import com.lmq.push.Utils;
import com.loopj.android.http.Base64;

public class LogoActicity extends Activity {

	private SharedPreferences sharedPreferences;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.logo);
		sharedPreferences = getSharedPreferences("lmq", 0);

		if (!sharedPreferences.contains("fist_into")) {
			Editor editor = sharedPreferences.edit();
			editor.putBoolean("fist_into", true);
			editor.commit();
		}

		MyLog.e("手机型号:", android.os.Build.MODEL);
		// 获取手机系统信息
		Default.PHONE_MODEL = android.os.Build.MODEL;
		Default.OS_VERSION = android.os.Build.VERSION.RELEASE + "";
		MyLog.e("手机型号:", "" + Default.PHONE_MODEL + "Android版本:"
				+ Default.OS_VERSION);


			


		

		// 获取分辨率
		Default.dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(Default.dm);

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				stop();
			}
		}, 800);
		File f = new File(Environment.getExternalStorageDirectory()
				+ Default.PIC_PATH);
		if (!f.exists()) {
			f.mkdir();
		}

		Default.isAlive = true;

        SharedPreferences sp = getApplicationContext().getSharedPreferences(
                Default.userPreferences, 0);

//        edit.putString("userid", json.optString("uid"));
        if(sp.contains("userid")){

            Default.userId = Long.parseLong(sp.getString("userid","0"));
            MyLog.e("用户登录ID",Default.userId+"");

        }




        Default.needRelogin = true;
		Default.user_exit = sp.getBoolean("user_exit",false);
	}

	public void stop() {
		// time.cancel();
		// time.purge();

		// 判断用户是否开启了手势密码功能
		Intent intent = null;
		if (sharedPreferences.getBoolean("fist_into", false)) {
			intent = new Intent(LogoActicity.this, LoadIntroduceActivity.class);
		} else {
			switch (Default.style) {
			case 0:
				intent = new Intent(LogoActicity.this, MainTabNewActivity.class);
				break;
//			case 1:
//				intent = new Intent(LogoActicity.this, MainMenuActivity.class);
//				break;

			default:
				break;
			}

		}

		// Intent ;
		startActivity(intent);
		LogoActicity.this.finish();
		overridePendingTransition(R.anim.light_to_dark, R.anim.dark_to_light);
		// overridePendingTransition(android.R.anim.fade_in,
		// android.R.anim.fade_out);

		finish();
	}
}
