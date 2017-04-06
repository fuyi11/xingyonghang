package com.lmq.main.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.xyh.R;
import com.lmq.main.util.Default;
import com.lmq.main.util.ViewPagerAdapter;
import com.lmq.menu.MainTabNewActivity;

public class LoadIntroduceActivity extends Activity implements OnClickListener,
		OnPageChangeListener {

	private ViewPager vp;
	private ViewPagerAdapter vpAdapter;
	private List<View> views;
	private SharedPreferences sharedPreferences;

	// 引导图片资源
	private static final int[] pics = { R.drawable.load_intr_1,
			R.drawable.load_intr_2, R.drawable.load_intr_3 };

	// 记录当前选中位置
	private int currentIndex;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.load_introduce);
		sharedPreferences = getSharedPreferences("lmq", 0);
		views = new ArrayList<View>();

		LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);

		// 初始化引导图片列表
		for (int i = 0; i < pics.length; i++) {
			ImageView iv = new ImageView(this);
			iv.setLayoutParams(mParams);
			iv.setImageResource(pics[i]);
			if (i == 2) {
				iv.setOnClickListener(this);
			}
			views.add(iv);
		}
		vp = (ViewPager) findViewById(R.id.viewpager);
		// 初始化Adapter
		vpAdapter = new ViewPagerAdapter(views);
		vp.setAdapter(vpAdapter);
		// 绑定回调
		vp.setOnPageChangeListener(this);

		// 初始化底部小点

	}

	/**
	 * 设置当前的引导页
	 */
	private void setCurView(int position) {
		if (position < 0 || position >= pics.length) {
			return;
		}

		vp.setCurrentItem(position);
	}

	// 当滑动状态改变时调用
	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub

	}

	// 当当前页面被滑动时调用
	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	// 当新的页面被选中时调用
	@Override
	public void onPageSelected(int arg0) {
		// 设置底部小点选中状态
	}

	@Override
	public void onClick(View v) {

		// showCustomToast("123");
		Editor editor = sharedPreferences.edit();
		editor.putBoolean("fist_into", false);
		editor.commit();
		intoAPP();
	}

	public void intoAPP() {
		// time.cancel();
		// time.purge();

		// 判断用户是否开启了手势密码功能
		Intent intent = null;

		switch (Default.style) {
		case 0:
			intent = new Intent(LoadIntroduceActivity.this,
					MainTabNewActivity.class);
			break;
//		case 1:
//			intent = new Intent(LoadIntroduceActivity.this,
//					MainMenuActivity.class);
//			break;

		default:
			break;
		}
		startActivity(intent);
		LoadIntroduceActivity.this.finish();
		overridePendingTransition(R.anim.light_to_dark, R.anim.dark_to_light);

		finish();

	}

}
