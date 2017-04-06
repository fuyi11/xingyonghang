package com.lmq.main.activity.invest.investdetail;

import org.apache.http.Header;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xyh.R;
import com.lmq.http.BaseHttpClient;
import com.lmq.http.JsonHttpResponseHandler;
import com.lmq.main.activity.tools.LMQWebViewActivity;
import com.lmq.main.activity.invest.investbuy.Tender_LZ_Activity;
import com.lmq.main.activity.tools.CalculateActivity;
import com.lmq.main.activity.login.loginActivity;
import com.lmq.main.activity.user.manager.tenderlogs.InvestLogsActivity;
import com.lmq.main.api.BaseActivity;
import com.lmq.main.api.JsonBuilder;
import com.lmq.main.api.SystenmApi;
import com.lmq.main.item.tzItem;
import com.lmq.main.util.Data;
import com.lmq.main.util.Default;

public class InvestDetailType301 extends BaseActivity implements
		android.view.View.OnClickListener {

	/** 借款金额 */
	private TextView tv_jkje;
	/** 预期年化利率 */
	private TextView tv_nhlv;
	/** 起投金额 */
	private TextView borrow_min;
	/** 剩余金额 */
	private TextView tv_syje;
	/** 标的名字 */
	private TextView tv_name;
	/**限投金额 */
	private TextView tv_limit_money;
	/** 借款期限 */
	private TextView tv_jkqx;
	/**投标奖励 */
	private TextView reward;
	/**还款方式 */
	private TextView hkfs;
	/**担保机构 */
	private TextView tv_danbao;
	/** 发布时间 */
	private TextView tv_addtime;

	/** ProgressBar的进度 */
	private TextView tv_progresstext;
	private ProgressBar mProgressBar;
	
	/** 购买按钮 */
	private Button enterBtn;

	private int itemType;;
	private long id;

	/** 投标记录人数 */
	private TextView tv_numberpeople;

	private String itemId;
//	private int itemType;
	private tzItem item;

	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.tz_item301_new);

		Intent intent = getIntent();
		itemType = intent.getExtras().getInt("type");
		if(intent.hasExtra("id")){

			itemId = intent.getExtras().getString("id");
		}

		if(intent.hasExtra("borrow_id")){

			itemId = intent.getExtras().getString("borrow_id");
		}

		initView();

	}

	public void initView() {
		TextView title = (TextView) findViewById(R.id.title);
		title.setText(R.string.tz_item_0);

		tv_name = (TextView) findViewById(R.id.title_detail);
		tv_nhlv = (TextView) findViewById(R.id.nhlv);
		borrow_min = (TextView) findViewById(R.id.borrow_min);
		tv_syje = (TextView) findViewById(R.id.syje);
		tv_jkje = (TextView) findViewById(R.id.jkje);
		tv_jkqx = (TextView) findViewById(R.id.tv_jkqx);
		tv_progresstext = (TextView) findViewById(R.id.progresstext);
		tv_addtime = (TextView) findViewById(R.id.addtime);
		hkfs = (TextView) findViewById(R.id.hkfs);
		reward = (TextView) findViewById(R.id.reward);
		tv_limit_money = (TextView) findViewById(R.id.tv_limit_money);
		tv_danbao = (TextView) findViewById(R.id.tv_danbao);
		
		enterBtn = (Button) findViewById(R.id.enter_buy);
		enterBtn.setOnClickListener(this);
		findViewById(R.id.shareBtn).setOnClickListener(this);

		mProgressBar = (ProgressBar) findViewById(R.id.progressbar);

		findViewById(R.id.back).setOnClickListener(this);
		findViewById(R.id.enter_buy).setOnClickListener(this);
		findViewById(R.id.sq).setOnClickListener(this);
		findViewById(R.id.tender_people).setOnClickListener(this);
		findViewById(R.id.tender_miaoshu).setOnClickListener(this);

		tv_numberpeople = (TextView) findViewById(R.id.tv_numberpeople);


	}

	private Handler handler2 = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.arg1 == 3) {
				updateInfo(Data.tzListItem3_1Json);
			} else
				showCustomToast(msg.getData().getString("info"));
		}

	};

	public void getPageInfoHttp() {

		JsonBuilder builder = new JsonBuilder();
		builder.put("id", itemId);
//		builder.put("type", itemType);

		BaseHttpClient.post(getBaseContext(), Default.tztListItem, builder,
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
						dismissLoadingDialog();
						super.onSuccess(statusCode, headers, json);

						try {

							if (statusCode == 200) {
								if (json.getInt("status") == 1) {
									Data.tzListItem3_1Json = json;
									Message msg = new Message();
									msg.arg1 = 3;
									handler2.sendMessage(msg);
								} else {
									String message = json.getString("message");
									SystenmApi.showCommonErrorDialog(InvestDetailType301.this, json.getInt("status"),message);
								}
							} else {
								showCustomToast(R.string.toast1);
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
		
			item = new tzItem();
			item.init(json);
			tv_name.setText(item.getName());
			tv_jkje.setText(item.getMoney() + "");
			tv_nhlv.setText(item.getNhll() + "");
			tv_jkqx.setText(item.getJkqx());
			tv_syje.setText(SystenmApi.getMoneyInfo(item.getNeed())+"");
			borrow_min.setText(item.getBorrowmini()+"");
			reward.setText(item.getJl() + "");
			tv_danbao.setText(item.getDanbao() + "");
			hkfs.setText(item.getJkfs() + "");
			tv_addtime.setText(item.getTime());
			mProgressBar.setProgress((int) item.getProgress());
			tv_progresstext.setText(item.getProgress() + "");
			tv_limit_money.setText(item.getZdtbje() + "");


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

			tv_numberpeople.setText(item.getTzcs() + "人");
//			if (Default.NEW_VERSION) {
//				borrow_min.setText(item.getBorrowmini() + "");// 每份金额
//				tv_syje.setText(SystenmApi.getMoneyInfo(Long.parseLong(item
//						.getNeed() + "")));// 已投
//
//			}

		
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.shareBtn:
			v.setEnabled(false);
			SystenmApi.showShareView(this, "分享给朋友",
					"亲，给您推荐"+getString(R.string.app_name)+"的一个投资项目," + item.getName()
							+ "  收益高，风险低，快来了解一下吧！会理财，财汇来！！，" + Default.ip
							+ "/tinvest/" + itemId + ".html", Default.ip
							+ "/tinvest/" + itemId + ".html");
			v.setEnabled(true);
			break;
		case R.id.tender_people:

			Intent intent = new Intent(InvestDetailType301.this,
					InvestLogsActivity.class);
			intent.putExtra("id", itemId);
//			intent.putExtra("type", item.getType());
			startActivity(intent);

			break;
		case R.id.tender_miaoshu:
//			if (item != null) {
//				Intent intent1 = new Intent();
//				intent1.setClass(InvestDetailType301.this,
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
			abIntent.putExtra("url",Default.ip+"/mobile/Des?id="+itemId);
			abIntent.setClass(InvestDetailType301.this, LMQWebViewActivity.class);
			startActivity(abIntent);
			break;

		case R.id.enter_buy:

			if (Default.userId == 0) {
				startActivity(new Intent(InvestDetailType301.this,
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
					buyInten.putExtra("id", itemId);
//					buyInten.putExtra("type", itemType);
					buyInten.setClass(InvestDetailType301.this,
							Tender_LZ_Activity.class);
					startActivity(buyInten);
				}

			}

			break;
		case R.id.sq:
			Intent info = new Intent(InvestDetailType301.this,
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
