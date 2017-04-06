package com.lmq.view;

import java.util.ArrayList;

import android.R.array;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.xyh.R;
import com.lmq.main.api.BaseActivity;

public class ShowWebImageView extends BaseActivity implements
		OnPageChangeListener {

	/**
	 * ViewPager
	 */
	private ViewPager viewPager;

	private TextView tipsView;

	/**
	 * 装点点的ImageView数组
	 */
	private ImageView[] tips;

	/**
	 * 装ImageView数组
	 */
	private CustomImageView[] mImageViews;

	/**
	 * 图片资源id
	 */

	private int currentIndex = 0;

	private ArrayList<String> imageArrayList;

	public ShowWebImageView() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// intent.putStringArrayListExtra("imageArray", imageArray);
		// intent.putExtra("currentIndex", index);

		String imageStr = getIntent().getStringExtra("img");

		imageArrayList = getIntent().getStringArrayListExtra("imageArray");
		for (int i = 0; i < imageArrayList.size(); i++) {

			if (imageArrayList.get(i).trim().equals(imageStr.trim())) {
				currentIndex = i;

			}
		}

		setContentView(R.layout.show_web_image);

		tipsView = (TextView) findViewById(R.id.viewGroup);
		viewPager = (ViewPager) findViewById(R.id.viewPager);

		tipsView.setText(currentIndex + "/" + imageArrayList.size());
		// 将图片装载到数组中
		mImageViews = new CustomImageView[imageArrayList.size()];
		for (int i = 0; i < mImageViews.length; i++) {
			CustomImageView imageView = new CustomImageView(this,
					imageArrayList.get(i));
			imageView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					finish();

				}
			});
			mImageViews[i] = imageView;

		}

		// 设置Adapter
		viewPager.setAdapter(new MyAdapter());
		// 设置监听，主要是显示张数
		viewPager.setOnPageChangeListener(this);
		viewPager.setCurrentItem(currentIndex);

	}

	/**
	 * 
	 * @author xiaanming
	 *
	 */
	public class MyAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return mImageViews.length;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(View container, int position, Object object) {
			((ViewPager) container).removeView(mImageViews[position
					% mImageViews.length]);

		}

		/**
		 * 载入图片进去，用当前的position 除以 图片数组长度取余数是关键
		 */
		@Override
		public Object instantiateItem(View container, int position) {

			((ViewPager) container).addView(mImageViews[position
					% mImageViews.length], 0);

			return mImageViews[position % mImageViews.length];
		}

	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int arg0) {
		tipsView.setText((arg0 + 1) + "/" + imageArrayList.size());
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
	}

}
