package com.lmq.main.activity.invest.investdetail;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xyh.R;
import com.lmq.http.BaseHttpClient;
import com.lmq.http.JsonHttpResponseHandler;
import com.lmq.main.activity.invest.investbuy.Tender_ZR_Activity;
import com.lmq.main.activity.login.loginActivity;
import com.lmq.main.activity.tools.CalculateActivity;
import com.lmq.main.activity.tools.LMQWebViewActivity;
import com.lmq.main.activity.user.manager.tenderlogs.InvestLogsActivity;
import com.lmq.main.api.BaseActivity;
import com.lmq.main.api.JsonBuilder;
import com.lmq.main.api.MyLog;
import com.lmq.main.api.SystenmApi;
import com.lmq.main.item.tzItem;
import com.lmq.main.util.Default;

import org.apache.http.Header;
import org.json.JSONObject;

public class InvestDetailType101 extends BaseActivity implements
		android.view.View.OnClickListener {

	/** 借款金额 */
	private TextView tv_jkje;
	/** 现年化利率 */
	private TextView tv_nhlv;
	/** 原年化利率 */
	private TextView borrow_interest_rate;
	/** 起投金额 */
	private TextView borrow_min;
	/** 剩余金额 */
	private TextView tv_syje;
	/** 标的名字 */
	private TextView tv_name;
	/**剩余天数 */
	private TextView remain_duration;
	/**还款方式 */
	private TextView hkfs;
	/** 截止时间 */
	private TextView debt_et;

	/** ProgressBar的进度 */
	private ProgressBar mProgressBar;
	
	/** 购买按钮 */
	private Button enterBtn;


	/** 投标记录人数 */
	private TextView tv_numberpeople;

	private String itemId;
	private tzItem item;
	private int itemType;
	
	private String type;
	/**查看原项目id*/
	private int borrow_id;
	/**债券id*/
	private String invest_id;

	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.tz_item101_new);

		Intent intent = getIntent();
		if(intent.hasExtra("id")){

			itemId = intent.getExtras().getString("id");
		}


		itemType = intent.getExtras().getInt("type");

		if(intent.hasExtra("borrow_id")){

			itemId = intent.getExtras().getString("borrow_id");
		}

		initView();

	}


	public void initView() {
		TextView title = (TextView) findViewById(R.id.title);
		title.setText("债权详情");

		tv_name = (TextView) findViewById(R.id.title_detail);
		tv_jkje = (TextView) findViewById(R.id.jkje);
		tv_nhlv = (TextView) findViewById(R.id.nhlv);
		borrow_interest_rate = (TextView) findViewById(R.id.borrow_interest_rate);
		tv_syje = (TextView) findViewById(R.id.syje);
		borrow_min = (TextView) findViewById(R.id.borrow_min);
		remain_duration = (TextView) findViewById(R.id.remain_duration);
		hkfs = (TextView) findViewById(R.id.hkfs);
		debt_et = (TextView) findViewById(R.id.debt_et);
		
		enterBtn = (Button) findViewById(R.id.enter_buy);
		enterBtn.setOnClickListener(this);

		mProgressBar = (ProgressBar) findViewById(R.id.progressbar);

		findViewById(R.id.back).setOnClickListener(this);
		findViewById(R.id.enter_buy).setOnClickListener(this);
		findViewById(R.id.sq).setOnClickListener(this);
		findViewById(R.id.tender_people).setOnClickListener(this);
		findViewById(R.id.tender_miaoshu).setOnClickListener(this);
		findViewById(R.id.rl_reward).setOnClickListener(this);

		tv_numberpeople = (TextView) findViewById(R.id.tv_numberpeople);

	}


	public void getPageInfoHttp() {

		JsonBuilder builder = new JsonBuilder();
		builder.put("id", itemId);

		BaseHttpClient.post(getBaseContext(), Default.debt_detail, builder,
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
									
									updateInfo(json);
									
								} else {
									String message = json.getString("message");
									SystenmApi.showCommonErrorDialog(InvestDetailType101.this, json.getInt("status"),message);
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

	public void updateInfo(JSONObject json) {
		
		if (json.has("invest_num")) {

			tv_numberpeople.setText(json.optString("invest_num", "0")+"人");
		}
		if (json.has("borrow_interest_rate")) {
			
			borrow_interest_rate.setText(json.optString("borrow_interest_rate", "0"));
            borrow_interest_rate.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        }
		if (json.has("need")) {
			
			tv_syje.setText(json.optString("need", "0"));
		}
		if (json.has("borrow_name")) {
			
			tv_name.setText(json.optString("borrow_name", "0"));
		}
		
			item = new tzItem();
			item.init(json);
			
			tv_jkje.setText(item.getZq_money() + "");
			tv_nhlv.setText(item.getInterest_rate() + "");
			remain_duration.setText(item.getRemain_duration() + "");
			borrow_min.setText(item.getQitou()+"");
			hkfs.setText(item.getRepayment_type_name() + "");
			debt_et.setText(item.getDebt_et() + "");
			
			mProgressBar.setProgress((int) item.getProgress());
			type=item.getBorrow_type();
			borrow_id=item.getBorrow_id();
			invest_id=item.getInvest_id();
			

//			if (item.getBorrow_status() == 0) {
//				enterBtn.setText("刚发布的标");
//				enterBtn.setBackgroundResource(R.drawable.hui);
//				enterBtn.setClickable(false);
//			} else if (item.getBorrow_status() == 1) {
//				enterBtn.setText("初审未通过");
//				enterBtn.setBackgroundResource(R.drawable.hui);
//				enterBtn.setClickable(false);
//			} else if (item.getBorrow_status() == 2) {
//				enterBtn.setText("立即投标");
//			} else if (item.getBorrow_status() == 3) {
//				enterBtn.setText("已流标");
//				enterBtn.setBackgroundResource(R.drawable.hui);
//				enterBtn.setClickable(false);
//			} else if (item.getBorrow_status() == 4) {
//				enterBtn.setText("复审中");
//				enterBtn.setBackgroundResource(R.drawable.hui);
//				enterBtn.setClickable(false);
//			} else if (item.getBorrow_status() == 5) {
//				enterBtn.setText("复审未通过");
//				enterBtn.setBackgroundResource(R.drawable.hui);
//				enterBtn.setClickable(false);
//			} else if (item.getBorrow_status() == 6) {
//				enterBtn.setText("还款中");
//				enterBtn.setBackgroundResource(R.drawable.hui);
//				enterBtn.setClickable(false);
//			} else if (item.getBorrow_status() == 7) {
//				enterBtn.setText("已完成");
//				enterBtn.setBackgroundResource(R.drawable.hui);
//				enterBtn.setClickable(false);
//			} else if (item.getBorrow_status() == 100) {
//				enterBtn.setText("新手专享");
//				enterBtn.setBackgroundResource(R.drawable.hui);
//				enterBtn.setClickable(false);
//			}

			

		
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
//		case R.id.shareBtn:
//			v.setEnabled(false);
//			SystenmApi.showShareView(this, "分享给朋友",
//					"亲，给您推荐绿麻雀的一个投资项目," + item.getName()
//							+ "  收益高，风险低，快来了解一下吧！会理财，财汇来！！，" + Default.ip
//							+ "/invest/" + itemId + ".html", Default.ip
//							+ "/invest/" + itemId + ".html");
//			v.setEnabled(true);
//			break;
		case R.id.rl_reward:
			
			if(type.equals(7)){
				Intent intent = new Intent(InvestDetailType101.this,
						InvestDetailType201.class);
				intent.putExtra("id", borrow_id+"");
				startActivity(intent);
			}else if(type.equals(6)){
				Intent intent = new Intent(InvestDetailType101.this,
						InvestDetailType11.class);
				intent.putExtra("id", borrow_id+"");
				startActivity(intent);
			}else{
				Intent intent = new Intent(InvestDetailType101.this,
						InvestDetailType11.class);
				intent.putExtra("id", borrow_id+"");
				startActivity(intent);
			}

			break;
		case R.id.tender_people:
			
			Intent intent = new Intent(InvestDetailType101.this,
					InvestLogsActivity.class);
			intent.putExtra("id", invest_id);
			MyLog.e("7777777777777777", invest_id+"");
			intent.putExtra("type", itemType);
			
			startActivity(intent);
			
			break;
		case R.id.tender_miaoshu:
//			if (item != null) {
//				Intent intent1 = new Intent();
//				intent1.setClass(InvestDetailType101.this,
//						CustomDetailView.class);
////				intent1.putExtra("html_str", info2.getText().toString());
//				// intent1.putStringArrayListExtra("imageArray",
//				// item.getImageArray());
//				startActivity(intent1);
//			} else {
//				showCustomToast(R.string.toast1);
//			}
			Intent abIntent = new Intent();
			abIntent.putExtra("title","项目详情");
			abIntent.putExtra("url",Default.ip+"/mobile/zqzrxq?id="+itemId);
			abIntent.setClass(InvestDetailType101.this, LMQWebViewActivity.class);
			startActivity(abIntent);
			break;

		case R.id.enter_buy:

			if (Default.userId == 0) {
				startActivity(new Intent(InvestDetailType101.this,
						loginActivity.class), 1);
			} else {

				if (item.getBorrow_uid() == Default.userId) {
					showCustomToast("不能投自己发布的标！");
				} else if (item.getProgress() == 100) {

					showCustomToast("交易已经结束,请选择其他标");
				} else if (item.getUid() == Default.userId) {
					showCustomToast("不能去投自己的标");
				} else {

					Intent buyInten = new Intent();
					buyInten.putExtra("invest_id", invest_id);
					buyInten.putExtra("id", itemId);
					buyInten.putExtra("qitou", item.getQitou());
					
					buyInten.setClass(InvestDetailType101.this,
							Tender_ZR_Activity.class);
					startActivity(buyInten);
				}

			}

			break;
		case R.id.sq:
			Intent info = new Intent(InvestDetailType101.this,
					CalculateActivity.class);
			info.putExtra("lilv", item.getNhll());
			info.putExtra("qixian", item.getJkqx());
			info.putExtra("fangshi", item.getJkfs());
			info.putExtra("jiangli", item.getJl());
			info.putExtra("guanli", "0");
			info.putExtra("zonge", item.getMoney());
			startActivity(info);
			break;
		

		}
	}
}
