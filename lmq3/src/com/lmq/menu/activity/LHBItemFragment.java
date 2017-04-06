package com.lmq.menu.activity;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.xyh.R;
import com.lmq.http.BaseHttpClient;
import com.lmq.http.JsonHttpResponseHandler;

import com.lmq.main.api.BaseFragment;
import com.lmq.main.api.JsonBuilder;
import com.lmq.main.api.MyLog;
import com.lmq.main.api.SystenmApi;

import com.lmq.main.dialog.LHBHintDialog;
import com.lmq.main.item.LHB_Tz_Item;

import com.lmq.main.util.Default;


public class LHBItemFragment extends BaseFragment implements OnClickListener,
		OnPageChangeListener {

	private  int lhbmoney;
	/**剩余可投*/
	private TextView tv_shkt;
	/**年化收益*/
	private TextView tv_nhsy;
	/**计划金额*/
	private TextView tv_funds;
	/**封存期限*/
	private TextView tv_term;
	/**起投金额*/
	private TextView tv_start_funds;
	/**剩余时间*/
	private TextView lefttime;
	/**投资金额*/
	private EditText ed_money;
	
	private String money;
	/**纯收益金额*/
	private String shouyi;
	/**会员金额*/
	private String user_money;
	/**项目编号id*/
	private String bao_id;

	private String start_funds;
	
	

	private LHB_Tz_Item item;
	private JSONArray list = null;
	private long id;
	private int type;
	private LHBHintDialog dialog;

	private int status;

	private TextView dayText;
	private TextView timeText;
	private TextView minutText;
	private TextView secondTime;
	private Button buy_brn;

	private long hasTime;

	private boolean stop = true;

	private android.os.Handler handler;


	private Runnable runnable = new Runnable() {
		@Override
		public void run() {

			//TODO 修改时间


			if(stop){


				//long nowTime = System.currentTimeMillis();
				hasTime+=1;



				try {

					long day = ((hasTime) / (3600 * 24));


					long hour = ((hasTime) - day * 86400) / 3600;


					long minutes = ((hasTime) - day * 86400 - hour * 3600) / 60;


					long seconds = (hasTime) - day * 86400 - hour * 3600

							- minutes * 60;

					if(item.getBao_status()==1 || item.getBao_status()==4){
						dayText.setText("00");
						timeText.setText("00");
						minutText.setText("00");
						secondTime.setText("00");
						buy_brn.setText("已经抢光");
						ed_money.setText("来晚了,已经抢光了");
						ed_money.setEnabled(false);
						buy_brn.setEnabled(false);
					}else {

						dayText.setText(day + "");
						timeText.setText(hour + "");
						minutText.setText(minutes + "");
						secondTime.setText(seconds + "");
					}

				} catch (Exception e) {
					e.printStackTrace();
				}


				handler.postDelayed(runnable,1000);



			}
		}
	};

	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View mainView = inflater.inflate(R.layout.lhb_invest_layout, null);
//		mainView.findViewById(R.id.buy_brn).setOnClickListener(this);

		initView(mainView);


		return mainView;
	}
	
	public void initView(View view) {

		timeText = (TextView) view.findViewById(R.id.time_text);
		dayText = (TextView) view.findViewById(R.id.day_text);
		minutText = (TextView) view.findViewById(R.id.minut_time);
		secondTime = (TextView) view.findViewById(R.id.second_text);


		tv_shkt = (TextView) view.findViewById(R.id.tv_shkt);
		tv_nhsy = (TextView) view.findViewById(R.id.tv_nhsy);
		tv_funds = (TextView) view.findViewById(R.id.tv_funds);
		tv_term = (TextView) view.findViewById(R.id.tv_term);
		tv_start_funds = (TextView) view.findViewById(R.id.tv_start_funds);

		ed_money = (EditText) view.findViewById(R.id.ed_money);

		buy_brn = (Button) view.findViewById(R.id.buy_brn);
		buy_brn.setOnClickListener(this);


	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(handler != null){

			Message message = new Message();
			message.what = 1;
			handler.sendMessage(message);
			handler.removeCallbacks(runnable);
		}
		LHBdoHttp();
		
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if(handler != null){

			handler.removeMessages(0);
			handler.removeCallbacks(runnable);
		}
	}

	/**
	 * 灵活宝列表页
	 */
	public void LHBdoHttp() {

		// 填充参数
		BaseHttpClient.post(getActivity(), Default.flexible_index, null,
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
									updateAddInfo(json);

								}else {
									String message = json.getString("is_jumpmsg");
									SystenmApi.showCommonErrorDialog(getActivity(), json.getInt("status"),message);
								}
							} else {
								showCustomToast(R.string.toast1);
							}

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						dismissLoadingDialog();
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
	
	public void updateAddInfo(JSONObject json) {

		item = new LHB_Tz_Item();
		item.init(json);
		bao_id =item.getBao_id();
		tv_shkt.setText(item.getRemain_money() + "");
		tv_nhsy.setText(item.getInterest_rate() + "");
		tv_funds.setText(item.getFunds() + "");
		tv_term.setText(item.getTerm());
		start_funds= item.getStart_funds();
		tv_start_funds.setText("起投金额" + item.getStart_funds() + "元");

//		if(item.getBao_status()==1 || item.getBao_status()==4){
//			dayText.setText("0");
//			timeText.setText("0");
//			minutText.setText("0");
//			secondTime.setText("0");
//		}
		if(handler == null){

            handler = new android.os.Handler(){


                @Override
                public void handleMessage(Message msg) {

                    switch (msg.what){

                        case 0:
                            stop = false;
                            break;
                        case 1:
                            stop = true;
                            break;

                    }
                    super.handleMessage(msg);

                }
            };

        }

		hasTime = Long.parseLong(item.getLefttime());
		Message message = new Message();
		message.what = 1;
		handler.sendMessage(message);
		handler.postDelayed(runnable, 1000);


	}


	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		
		case R.id.buy_brn:

			if (TextUtils.isEmpty(ed_money.getText().toString().trim())) {
				  showCustomToast("请输入金额");

				return ;
			 }

			if (Long.parseLong(ed_money.getText().toString().trim())%Long.parseLong(start_funds)==0) {
				//显示dialog（）
				doHttpMOney(v);
			}else {
				showCustomToast("请输入起投金额"+start_funds+"的整数倍");
			}

			break;
		case R.id.dialog_submit:

            	if(SystenmApi.isNullOrBlank(dialog.getPassword())){
                    showCustomToast("请输入支付密码");
                    return;
                }
            	getListDataLHB();

            break;
        case R.id.dialog_cancle:

            dialog.dismiss();

            break;

		default:
			break;
		}

	}
	
	 public void showLHBDialog(final View view,String money,String zf_money,String fcrq,String yjsy){

	        if(null == dialog){


	            dialog = new LHBHintDialog(getActivity());
	            dialog.setDialogTitle("温馨提示");
	            dialog.setonClickListener(this);


	        }


	        dialog.setInfo(money, zf_money, yjsy,fcrq);
		    dialog.clearPassword();

	        if(!dialog.isShowing()){
	            dialog.showAsDropDown(view);
	        }



	    }
	 
	 private void getListDataLHB() {

			JsonBuilder builder = new JsonBuilder();
			builder.put("pay_pass",dialog.getPassword());
			builder.put("bao_id",bao_id);
			builder.put("money",ed_money.getText().toString());
			
			BaseHttpClient.post(getActivity(), Default.flexible_save, builder, new JsonHttpResponseHandler() {

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


								 MyLog.e("获取购买灵活宝最终提交信息", "" + json.toString());
	                             showCustomToast(json.getString("message"));
	                             dialog.dismiss();
	                             ed_money.getText().clear();

								 LHBdoHttp();

	                         } else {
								 String message = json.getString("message");
								 SystenmApi.showCommonErrorDialog(getActivity(), json.getInt("status"),message);
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
					super.onFailure(statusCode, headers, responseString, throwable);
					dismissLoadingDialog();
					showCustomToast(responseString);

				}

			});

		}
	
	private void doHttpMOney(final View v) {

		JsonBuilder builder = new JsonBuilder();
		builder.put("money", money);

		BaseHttpClient.post(getActivity(), Default.flexible_ajax_index, builder,
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
							MyLog.e("123", json.toString());
							if (statusCode == 200) {
								if (json.getInt("status") == 1) {
									
									if (json.has("shouyi")) {

										shouyi=(json.optString("shouyi", "0") + "");
									}
									
									if (json.has("user_money")) {
										
										user_money=(json.optString("user_money", "0") + "");
									}

									showLHBDialog(v,user_money,ed_money.getText().toString(),item.getTerm(),shouyi);

								} else {
									showCustomToast(json.getString("message"));
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
					}

				});

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
		// TODO Auto-generated method stub
		
	}

	



}
