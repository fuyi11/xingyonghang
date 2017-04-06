package com.lmq.menu.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.xyh.R;
import com.lmq.http.BaseHttpClient;
import com.lmq.http.JsonHttpResponseHandler;
import com.lmq.main.activity.borrow.ChoiceBorrowTypeActivity;
import com.lmq.main.activity.invest.investdetail.InvestDetailType11;
import com.lmq.main.activity.invest.investdetail.InvestDetailType201;
import com.lmq.main.activity.invest.investdetail.InvestDetailType301;
import com.lmq.main.activity.login.loginActivity;
import com.lmq.main.activity.tools.LMQWebViewActivity;
import com.lmq.main.api.BaseActivity;
import com.lmq.main.api.JsonBuilder;
import com.lmq.main.api.MyLog;
import com.lmq.main.api.SystenmApi;
import com.lmq.main.item.BannerItem;
import com.lmq.main.item.tzItem;
import com.lmq.main.util.Default;
import com.lmq.main.util.ViewPagerAdapter;
import com.lmq.menu.MainTabNewActivity;
import com.lmq.view.ShowBannerViewDetail;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class IndexActivity extends BaseActivity implements OnClickListener,OnPageChangeListener{
	


	/**标名*/
	private TextView tz_title;
	/**期限*/
	private TextView tz_deadline;
	/**借款金额*/
	private TextView tz_money;
	/**还款方式*/
	private TextView tz_huankuan;
	/**年利率*/
	private TextView tz_rate;
	private int curPage = 1, pageCount = 1;
	private tzItem item;
	private JSONArray list = null;
	private long id;
	private int type;
	
	private ViewPagerAdapter vpAdapter;
	private List<View> views;
	private ArrayList<View> footPointViews;
	private List<BannerItem> bannerItems;
	private LinearLayout footpointview;
	private static final int[] pics = { R.drawable.slide1 };
	private ViewPager flipper;
	private int index;
	private boolean change;
	private PullToRefreshScrollView refreshView;

//	private int refresh = 0;

	private Handler mHandler = new Handler();
	private Runnable runable = new Runnable() {

		public void run() {
			changePic();
		}
	};


	
	public void initBannerData(JSONObject json) {

		if (json != null) {
			views.clear();
		//	flipper.removeAllViews();
			flipper.setBackgroundDrawable(null);
			footPointViews.clear();
			footpointview.removeAllViews();
			bannerItems = new ArrayList<BannerItem>();
			try {
				JSONArray array = json.getJSONArray("list");
				for (int i = 0; i < array.length(); i++) {
					bannerItems.add(new BannerItem(array.getJSONObject(i)));

				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			for (int i = 0; i < bannerItems.size(); i++) {
				ImageView footView = new ImageView(IndexActivity.this);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				params.leftMargin = 10;
				footView.setLayoutParams(params);

				if (i == 0) {
					footView.setImageResource(R.drawable.b_dx1);
				} else {
					footView.setImageResource(R.drawable.b_dx2);
				}
				footpointview.addView(footView);
				footPointViews.add(footView);

				final ImageView iv = new ImageView(IndexActivity.this);
				iv.setScaleType(ScaleType.FIT_XY);
				iv.setTag(i);

				iv.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Integer tag = (Integer) v.getTag();
//						Intent toDetailIntent = new Intent(IndexActivity.this,
//								ShowBannerViewDetail.class);
//						toDetailIntent.putExtra("id", bannerItems.get(tag)
//								.getId());
//
//						startActivity(toDetailIntent);
						Intent intent = new Intent(IndexActivity.this,LMQWebViewActivity.class);
						intent.putExtra("title","详细信息");
						intent.putExtra("url", Default.ip + "/mobile/AppHuanDeng?id=" + bannerItems.get(tag).getId());
						startActivity(intent);

					}
				});
				DisplayImageOptions options = new DisplayImageOptions.Builder()
						.showImageForEmptyUri(pics[0]).showImageOnFail(pics[0])
						.resetViewBeforeLoading(true).cacheOnDisk(true)
						.imageScaleType(ImageScaleType.EXACTLY)
						.bitmapConfig(Bitmap.Config.RGB_565)
						.considerExifParams(true)
						.displayer(new FadeInBitmapDisplayer(300)).build();

				ImageLoader.getInstance().displayImage(
						bannerItems.get(i).getPicPath(), iv, options,
						new SimpleImageLoadingListener() {
							@Override
							public void onLoadingStarted(String imageUri,
									View view) {
							}

							@Override
							public void onLoadingFailed(String imageUri,
									View view, FailReason failReason) {
								String message = null;
								switch (failReason.getType()) {
								case IO_ERROR:
									message = "Input/Output error";
									break;
								case DECODING_ERROR:
									message = "解析错误";
									break;
								case NETWORK_DENIED:
									message = "网络错误";
									break;
								case OUT_OF_MEMORY:
									message = "内存错误";
									break;
								case UNKNOWN:
									message = "位置错误";
									break;
								}
								//Toast.makeText(IndexActivity.this, message,
									//	Toast.LENGTH_SHORT).show();

							}

							@Override
							public void onLoadingComplete(String imageUri,
									View view, Bitmap loadedImage) {
								if (loadedImage != null) {

									iv.setImageBitmap(loadedImage);
								}

							}
						});
				views.add(iv);
			}
			

		}
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
//		if (refresh == 0){
			doHttp();
			indexdoHttp();
//			refresh=1;
//		}

		
	}
	
	/**
	 * 首页推荐标
	 */
	public void indexdoHttp() {

		JsonBuilder builder = new JsonBuilder();

		builder.put("type",301);
		builder.put("page", curPage);
		builder.put("limit", pageCount);
		BaseHttpClient.NO_RAS=true;
		// 填充参数
		BaseHttpClient.post(IndexActivity.this, Default.tzList, builder,
				new JsonHttpResponseHandler() {
					@Override
					public void onStart() {
						// TODO Auto-generated method stub
						super.onStart();
//						showLoadingDialogNoCancle(getResources().getString(
//								R.string.toast2));
					}

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject json) {
						// TODO Auto-generated method stub
						super.onSuccess(statusCode, headers, json);

						try {
							if (statusCode == 200) {
								if (json.getInt("status") == 1) {
									btn_tz.setEnabled(true);
									tz_lend_btn.setEnabled(true);
									tz_inxest_btn.setEnabled(true);
									updateAddInfo(json);

								} else {
									String message = json.getString("message");
									SystenmApi.showCommonErrorDialog(IndexActivity.this, json.getInt("status"), message);

								}
							} else {
								showCustomToast(R.string.toast1);
							}

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						dismissLoadingDialog();
                        refreshView.onRefreshComplete();
				}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							Throwable throwable, JSONObject errorResponse) {
						// TODO Auto-generated method stub
						super.onFailure(statusCode, headers, throwable,
								errorResponse);
						dismissLoadingDialog();
                        refreshView.onRefreshComplete();
					}
				});


	}
	
	public void updateAddInfo(JSONObject json) {

		try {

			if (!json.isNull("list")) {
				list = json.getJSONArray("list");
			}else{
//		        layout_btn.setVisibility(View.GONE);
		    }

			ArrayList<tzItem> temp_list = new ArrayList<tzItem>();
			if (list != null) {
				if (list.length()!=0){
				  JSONObject templist = list.getJSONObject(0);
					MyLog.e("首页推荐产品获取到的值", templist + "");
				    item = new tzItem();
				    item.init(templist);
				    temp_list.add(item);
				  
				    tz_title.setText(item.getName() + "");
				    tz_money.setText(SystenmApi.getMoneyInfo(item.getMoney()) + "元");
				    tz_rate.setText(item.getNhll() + "");
		            tz_deadline.setText(item.getJkqx());
		            tz_huankuan.setText(item.getJkfs());
		            
			   }
			}else{
//		        layout_btn.setVisibility(View.GONE);
		    }
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 获取幻灯片
	 */
	public void doHttp() {
		// RequestParams params = new RequestParams();
		JsonBuilder builder = new JsonBuilder();

		// 填充参数
		BaseHttpClient.post(IndexActivity.this, Default.bannerPic, builder,
				new JsonHttpResponseHandler() {

					@Override
					public void onStart() {
						super.onStart();

						//showLoadingDialogNoCancle("请稍后努力加载中...");
					}

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject json) {
						// TODO Auto-generated method stub
						super.onSuccess(statusCode, headers, json);
						dismissLoadingDialog();
						MyLog.e("statusCode=" + statusCode + "--headers="
								+ headers.toString() + "--json="
								+ json.toString());

						if (statusCode == 200) {
							try {
								if (json.getInt("status") == 1) {
									initBannerData(json);
									vpAdapter.notifyDataSetChanged();
								} else {
									String message = json.getString("message");
									SystenmApi.showCommonErrorDialog(IndexActivity.this, json.getInt("status"), message);

								}

								dismissLoadingDialog();
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							Throwable throwable, JSONObject errorResponse) {
						// TODO Auto-generated method stub
						super.onFailure(statusCode, headers, throwable,
								errorResponse);

						dismissLoadingDialog();
					}
				});

	}


	public void stop() {
		try {
			mHandler.wait();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void changePic() {
		if (getApplicationContext() == null)
			return;
		if (!change) {

			if (index < views.size() - 1) {
				index++;
				setImage(index);
				flipper.setCurrentItem(index);
				if (index == views.size() - 1) {
					change = true;
				}
			}

		} else {
			if (index > 0) {
				index--;
				setImage(index);
				flipper.setCurrentItem(index);
				if (index == 0) {
					change = false;
				}
			}
		}
		mHandler.postDelayed(runable, 5000);
	}

	void setImage(int i) {
		for (int j = 0; j < footPointViews.size(); j++) {
			ImageView iv = (ImageView) footPointViews.get(j);
			if (j != i)
				iv.setImageResource(R.drawable.b_dx2);
			else
				iv.setImageResource(R.drawable.b_dx1);
		}
	}

	public int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	public void initView() {
		
		views = new ArrayList<View>();
		footPointViews = new ArrayList<View>();
		flipper = (ViewPager) findViewById(R.id.flipper);
		ImageView footView = new ImageView(IndexActivity.this);

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.leftMargin = 10;
		footView.setLayoutParams(params);

		footView.setImageResource(R.drawable.b_dx1);

		footPointViews.add(footView);
		footpointview.addView(footView);

		flipper = (ViewPager) findViewById(R.id.flipper);

		vpAdapter = new ViewPagerAdapter(views);

		flipper.setAdapter(vpAdapter);
		// 绑定回调
		flipper.setOnPageChangeListener(this);
		
		tz_title = (TextView)findViewById(R.id.tz_title);
		tz_deadline = (TextView)findViewById(R.id.tz_deadline);
		tz_money = (TextView) findViewById(R.id.tz_money);
		tz_huankuan = (TextView) findViewById(R.id.tz_huankuan);
		tz_rate = (TextView) findViewById(R.id.tz_rate);

	}
	private TextView btn_tz;

	private LinearLayout tz_lend_btn;

	private LinearLayout tz_inxest_btn;

	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setHasExit(true);
		setContentView(R.layout.layout_index);
		refreshView = (PullToRefreshScrollView) findViewById(R.id.refreshView);
		refreshView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
		refreshView.setOnRefreshListener(new OnRefreshListener<ScrollView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
				// TODO Auto-generated method stub
				indexdoHttp();
				doHttp();


			}
		});
		tz_inxest_btn = (LinearLayout) findViewById(R.id.tz_inxest_btn);//投资项目
		tz_inxest_btn.setOnClickListener(this);
		tz_inxest_btn.setEnabled(false);//不能再点击了

		tz_lend_btn = (LinearLayout) findViewById(R.id.tz_lend_btn);//我要募捐
		tz_lend_btn.setOnClickListener(this);
		tz_lend_btn.setEnabled(false);

		btn_tz = (TextView) findViewById(R.id.btn_tz);//立即投资
		btn_tz.setOnClickListener(this);
		btn_tz.setEnabled(false);
//		mainView.findViewById(R.id.tz_four_btn).setOnClickListener(this);
		footpointview = (LinearLayout) findViewById(R.id.footer_point);
		initView();
		 mHandler.postDelayed(runable, 5000);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int flag = 0;
		switch (v.getId()) {
		case R.id.tz_inxest_btn:
			flag = 0;
			chenageHomeFragment(flag);
			break;
		case R.id.tz_lend_btn:
			if (Default.userId == 0) {
				startActivity(new Intent(IndexActivity.this, loginActivity.class));
			} else {
			IndexActivity.this.startActivity(
					new Intent(IndexActivity.this, ChoiceBorrowTypeActivity.class));
			}
			break;
		case R.id.btn_tz:
			type = item.getType();
			id = item.getId();
			if (Default.userId == 0) {
				startActivity(new Intent(IndexActivity.this, loginActivity.class));
			} else {

				if (item.getBorrow_uid() == Default.userId) {
					showCustomToast("不能投自己发布的标！");
				} else if (item.getProgress() == 100) {

					showCustomToast("交易已经结束,请选择其他标");
				} else if (item.getUid() == Default.userId) {
					showCustomToast("不能去投自己的标");
				} else {

					Intent buyInten = new Intent();
					buyInten.putExtra("id", id+"");

//					buyInten.putExtra("type", type);
					if (type==7){
						buyInten.setClass(IndexActivity.this, InvestDetailType201.class);
					}else if(type==6){
						buyInten.setClass(IndexActivity.this, InvestDetailType301.class);
					}else{
						buyInten.setClass(IndexActivity.this, InvestDetailType11.class);
					}


					startActivity(buyInten);

				}
			}
//		case R.id.tz_four_btn:
//			flag = 3;
//			chenageHomeFragment(flag);
//			break;

		default:
			break;
		}

	}

	private void chenageHomeFragment(int flag) {
		switch (Default.style) {
		case 0:
			MainTabNewActivity.mainTabNewActivity.changeHomeFragment(flag);
			break;
//		case 1:
//			MainTabNewActivity.mainTabNewActivity.changeHomeFragment(flag);
//			break;

		default:
			break;
		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageSelected(int arg0) {
		index = arg0;
		setImage(arg0);

	}
}
