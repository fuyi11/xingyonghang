package com.lmq.main.api;

import android.app.Application;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.lmq.main.util.LockPatternUtils;

import cn.jpush.android.api.JPushInterface;

public class LocationApplication extends Application {

	public LocationClient locationClient;
	public MyLocationListener myLocationListener;
	public TextView resultTextView;
	private static LocationApplication mInstance;
	private LockPatternUtils mLockPatternUtils;
	public TextView titleTextView;

	public static LocationApplication getInstance() {
		return mInstance;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		// Toast.makeText(getApplicationContext(), "application",
		// Toast.LENGTH_SHORT).show();
		mInstance = this;
		locationClient = new LocationClient(this.getApplicationContext());
		myLocationListener = new MyLocationListener();
		locationClient.registerLocationListener(myLocationListener);
		mLockPatternUtils = new LockPatternUtils(this);



		JPushInterface.setDebugMode(true);
		JPushInterface.init(this);

	}



	public LockPatternUtils getLockPatternUtils() {
		return mLockPatternUtils;
	}

	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// Receive Location
			StringBuffer sb = new StringBuffer(256);
			sb.append(location.getCity());

			logMsg(sb.toString());
			MyLog.i("BaiduLocationApiDem", sb.toString());
		}

	}

	/**
	 * 显示请求字符串
	 * 
	 * @param str
	 */
	public void logMsg(String str) {
		try {
			if (resultTextView != null)
				resultTextView.setText("已定位:" + str);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



}
