package com.lmq.main.activity.invest.investdetail;

import org.apache.http.Header;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xyh.R;
import com.lmq.http.BaseHttpClient;
import com.lmq.http.JsonHttpResponseHandler;
import com.lmq.main.activity.tools.LMQWebViewActivity;
import com.lmq.main.activity.invest.investbuy.Tender_PT_Activity;
import com.lmq.main.activity.tools.CalculateActivity;
import com.lmq.main.activity.login.loginActivity;
import com.lmq.main.activity.user.manager.tenderlogs.InvestLogsActivity;
import com.lmq.main.api.BaseActivity;
import com.lmq.main.api.JsonBuilder;
import com.lmq.main.api.SystenmApi;
import com.lmq.main.item.tzItem;
import com.lmq.main.util.Data;
import com.lmq.main.util.Default;

@SuppressLint("NewApi")
public class InvestDetailType11 extends BaseActivity implements
		OnClickListener {

	private tzItem item;
	/**标名称*/
	private TextView titTextView;
	/**发标时间*/
	private TextView dateTextView;
	/**借款年化利率*/
	private TextView lvTextView;
	/**借款期限*/
	private TextView borrowTimeTextView2;
	/**还款方式*/
	private TextView boorowTypeTextView;
	/**最小投资金额*/
	private TextView lowerMoneyTextView;
	/**最大投资金额*/
	private TextView borrow_max;
	/**借款金额*/
	private TextView allMoneyTextView;
	/**奖励*/
	private TextView rewardTextView;
	/**剩余金额*/
	private TextView hasLendyMoneyTextView;
	private ProgressBar progress;
	/**已融资进度*/
	private TextView progressText;
	/**借款用途*/
	private TextView borrow_use;

	/** 购买按钮 */
	private Button enterBtn;

	// 标其他信息
	private TextView info1;

	private String  itemId;
//	private int itemType;
	private boolean mShowDx;

	private String messageInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tz_item11_new);
		TextView title = (TextView) findViewById(R.id.title);
		title.setText("投标详情");
		findViewById(R.id.back).setOnClickListener(this);
		findViewById(R.id.sq).setOnClickListener(this);



		Intent intent = getIntent();

		if(intent.hasExtra("id")){

			itemId = intent.getExtras().getString("id");
		}


//		itemType = intent.getExtras().getInt("type");

		if(intent.hasExtra("borrow_id")){

			itemId = intent.getExtras().getString("borrow_id");
		}




		initViews();



	}

	public void initViews() {

		titTextView = (TextView) findViewById(R.id.title_detail);
		dateTextView = (TextView) findViewById(R.id.addtime);
		lvTextView = (TextView) findViewById(R.id.nhlv);
		boorowTypeTextView = (TextView) findViewById(R.id.hkfs);
		lowerMoneyTextView = (TextView) findViewById(R.id.borrow_min);
		allMoneyTextView = (TextView) findViewById(R.id.jkje);
		hasLendyMoneyTextView = (TextView) findViewById(R.id.syje);
		borrowTimeTextView2 = (TextView) findViewById(R.id.jkqx);
		progress = (ProgressBar) findViewById(R.id.progressbar);
		progressText = (TextView) findViewById(R.id.progresstext);
		rewardTextView = (TextView) findViewById(R.id.reward);
		borrow_use = (TextView)findViewById(R.id.borrow_use);
		borrow_max = (TextView)findViewById(R.id.borrow_max);

		// 详情其他信息
		info1 = (TextView) findViewById(R.id.tv_numberpeople);

		enterBtn = (Button) findViewById(R.id.enter);
		enterBtn.setOnClickListener(this);

		findViewById(R.id.shareBtn).setOnClickListener(this);

		findViewById(R.id.tender_people).setOnClickListener(this);
		// tender_miaoshu
		findViewById(R.id.tender_miaoshu).setOnClickListener(this);

	}

	public void getPageInfoHttp() {

		JsonBuilder builder = new JsonBuilder();
		builder.put("id", itemId);
//		builder.put("type", itemType);

		BaseHttpClient.post(getBaseContext(), Default.tzListItem, builder,
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
						try {
							if (statusCode == 200) {
								if (json.getInt("status") == 1) {
									Data.tzListItemJson = json;

									Message msg = new Message();
									msg.arg1 = 3;
									handler.sendMessage(msg);
								} else {
									String message = json.getString("message");
									SystenmApi.showCommonErrorDialog(InvestDetailType11.this, json.getInt("status"),message);
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

	@Override
	protected void onResume() {
		super.onResume();
		getPageInfoHttp();
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.arg1 == 2) {
				showCustomToast(msg.getData().getString("info"));
			} else if (msg.arg1 == 3) {
				updateInfo(Data.tzListItemJson);
			}
		}

	};

	public void updateInfo(JSONObject json) {
		item = new tzItem();
		item.init(json);

		titTextView.setText(item.getName());
		allMoneyTextView.setText(item.getMoney() + "");
		lvTextView.setText(item.getNhll() + "");
		boorowTypeTextView.setText(item.getJkfs() + "");
		borrow_use.setText(item.getBorrowUse());
		hasLendyMoneyTextView.setText(SystenmApi.getMoneyInfo(item.getHxje()));
		dateTextView.setText(item.getTime());
		lowerMoneyTextView.setText(item.getBorrowmini()+"");
		borrowTimeTextView2.setText(item.getJkqx() + "");
		borrow_max.setText(item.getZdtbje() + "");
		
		// 设置奖励
		if (item.getJl().equals("0")) {
			rewardTextView.setText("无");
		} else {
			rewardTextView.setText(item.getJl());
		}

		progress.setProgress((int) item.getProgress());
		progressText.setText(item.getProgress() + "");

		info1.setText(item.getTzcs() + "人");

		if (item.getBorrow_status() == 0) {
			enterBtn.setText("刚发布的标");
			enterBtn.setBackgroundResource(R.drawable.hui);
			enterBtn.setClickable(false);
		} else if (item.getBorrow_status() == 1) {
			enterBtn.setText("初审未通过");
			enterBtn.setBackgroundResource(R.drawable.hui);
			enterBtn.setClickable(false);
		} else if (item.getBorrow_status() == 2) {
			enterBtn.setText("立即投标");
		} else if (item.getBorrow_status() == 3) {
			enterBtn.setText("已流标");
			enterBtn.setBackgroundResource(R.drawable.hui);
			enterBtn.setClickable(false);
		} else if (item.getBorrow_status() == 4) {
			enterBtn.setText("复审中");
			enterBtn.setBackgroundResource(R.drawable.hui);
			enterBtn.setClickable(false);
		} else if (item.getBorrow_status() == 5) {
			enterBtn.setText("复审未通过");
			enterBtn.setBackgroundResource(R.drawable.hui);
			enterBtn.setClickable(false);
		} else if (item.getBorrow_status() == 6) {
			enterBtn.setText("还款中");
			enterBtn.setBackgroundResource(R.drawable.hui);
			enterBtn.setClickable(false);
		} else if (item.getBorrow_status() == 7) {
			enterBtn.setText("已完成");
			enterBtn.setBackgroundResource(R.drawable.hui);
			enterBtn.setClickable(false);
		} else if (item.getBorrow_status() == 100) {
			enterBtn.setText("新手专享");
			enterBtn.setBackgroundResource(R.drawable.hui);
			enterBtn.setClickable(false);
		}

	}

	@Override
	public void finish() {

		super.finish();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {

		case R.id.tender_miaoshu:
//			if (item != null) {
//				Intent intent1 = new Intent();
//				intent1.setClass(InvestDetailType11.this,
//						CustomDetailView.class);
//				intent1.putStringArrayListExtra("imageArray",
//						item.getImageArray());
//				startActivity(intent1);
//			} else {
//				showCustomToast(R.string.toast1);
//
//			}

			Intent abIntent = new Intent();
			abIntent.putExtra("title","项目详情");
			abIntent.putExtra("url",Default.ip+"/mobile/sbxq?id="+itemId);
			abIntent.setClass(InvestDetailType11.this, LMQWebViewActivity.class);
			startActivity(abIntent);
			break;
		case R.id.back:
			finish();
			break;
		case R.id.sq:

			if (item != null) {
				Intent info = new Intent(InvestDetailType11.this,
						CalculateActivity.class);
				info.putExtra("lilv", item.getNhll());
				info.putExtra("qixian", item.getJkqx());
				info.putExtra("fangshi", item.getJkfs());
				info.putExtra("jiangli", item.getJl());
				info.putExtra("guanli", "0");
				info.putExtra("zonge", item.getMoney());
				startActivity(info);
			} else {
				showCustomToast(R.string.toast1);
			}

			break;
		case R.id.enter:
			if (Default.userId == 0) {
				startActivity(new Intent(InvestDetailType11.this,
						loginActivity.class));
			} else {

				if (item.getBorrow_uid() == Default.userId) {
					showCustomToast("不能投自己发布的标！");
				} else if (item.getProgress() == 100) {

					showCustomToast("交易已经结束,请选择其他标");
				} else if (item.getUid() == Default.userId) {
					showCustomToast("不能去投自己的标");
				} else {

					Intent buyInten = new Intent();
					buyInten.putExtra("id", itemId);
//					buyInten.putExtra("type", itemType);
					buyInten.setClass(InvestDetailType11.this,
							Tender_PT_Activity.class);
					startActivity(buyInten);
				}
			}

			break;

		case R.id.tender_people:
			if (item != null) {
				Intent intent = new Intent(InvestDetailType11.this,
						InvestLogsActivity.class);
				intent.putExtra("id", itemId);
				startActivity(intent);
			} else {
				showCustomToast(R.string.toast1);
			}
			

			break;
		case R.id.shareBtn:
			v.setEnabled(false);
			SystenmApi.showShareView(this, "分享给朋友",
					"亲，给您推荐"+getString(R.string.app_name)+"的一个投资项目," + item.getName()
							+ "  收益高，风险低，快来了解一下吧！会理财，财汇来！！，" + Default.ip
							+ "/invest/" + itemId + ".html", Default.ip
							+ "/invest/" + itemId + ".html");
			v.setEnabled(true);
			break;

		default:
			break;
		}

	}

}
