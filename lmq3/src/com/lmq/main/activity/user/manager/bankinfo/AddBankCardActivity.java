package com.lmq.main.activity.user.manager.bankinfo;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

//import com.bigkoo.pickerview.OptionsPopupWindow;
import com.bigkoo.pickerview.OptionsPopupWindow;
import com.xyh.R;
import com.lmq.http.BaseHttpClient;
import com.lmq.main.api.BaseActivity;
import com.lmq.main.api.JsonBuilder;
import com.lmq.main.api.MyLog;
import com.lmq.main.api.SystenmApi;
import com.lmq.main.util.Default;
import com.lmq.http.JsonHttpResponseHandler;

/**
 * 第一次绑定银行卡
 * 
 */

public class AddBankCardActivity extends BaseActivity implements
		OnClickListener {
	
	private LayoutInflater mInflater;
//	private Spinner mBank, mProvince, mCity;
	private EditText  mCardNumber, mBankName,ed_vcode;
	private TextView mUser_phone;

	private List<BankItem> mBankList = new ArrayList<BankItem>();
	private List<BankItem> mProvinceList = new ArrayList<BankItem>();
	private List<BankItem> mCityList = new ArrayList<BankItem>();


    private TextView tv_real_name;
	
	private String strBank;// 银行卡
	private String strBankNum;// 卡号
	private String strProvince;// 开户省
	private String strCity;// 城市
	private String strBranch;// 开户行
	private String real_name;
	private String mobile;
	
	/**
	 * 发送验证码
	 * */
	private TextView mSendPhoneNum;
	private TimeCount time;


    /**银行选择**/
    private OptionsPopupWindow bank_select;
    private OptionsPopupWindow province_select;
    private OptionsPopupWindow city_select;

    private ArrayList<String> bank_select_list;
    private  ArrayList<String> province_select_list;


    private  ArrayList<String> city_select_list;

    private TextView bank_name;
    private TextView province_name;
    private TextView city_name;

    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.people_info_add_bankcard);
		mInflater = LayoutInflater.from(this);

		Intent intent = getIntent();
		 if (intent.hasExtra("real_name")) {
	        	
			 real_name = intent.getStringExtra("real_name");
	     }
		 if (intent.hasExtra("mobile")) {
			 
			 mobile = intent.getStringExtra("mobile");
		 }else{

             mobile = null;
         }

		initView();
		time = new TimeCount(60000, 1000);

		
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		dohttpBank();
		tv_real_name.setText(real_name);
		mUser_phone.setText(mobile);
		
		
	}

	public void initView() {

        if(null == mobile){

            findViewById(R.id.show_phone).setVisibility(View.GONE);
            findViewById(R.id.show_phone_code).setVisibility(View.GONE);

        }else {

            findViewById(R.id.show_phone).setVisibility(View.VISIBLE);
            findViewById(R.id.show_phone_code).setVisibility(View.VISIBLE);
        }

		findViewById(R.id.back).setOnClickListener(this);
		findViewById(R.id.btn_tijiao).setOnClickListener(this);

		TextView title = (TextView) findViewById(R.id.title);
		title.setText("添加银行卡");
		
		tv_real_name = (TextView) findViewById(R.id.real_name);
		mSendPhoneNum = (TextView) findViewById(R.id.sendphonenum);
		mSendPhoneNum.setOnClickListener(this);

		mBankName = (EditText) findViewById(R.id.ed_oldbank);
		mCardNumber = (EditText) findViewById(R.id.ed_oldcardnumber);

		mUser_phone = (TextView) findViewById(R.id.user_phone);
        ed_vcode = (EditText) findViewById(R.id.ed_vcode);


        bank_select_list = new ArrayList<String>();
        province_select_list = new ArrayList<String>();
        city_select_list = new ArrayList<String>();


        bank_select = new OptionsPopupWindow(this);

        province_select = new OptionsPopupWindow(this);

        city_select = new OptionsPopupWindow(this);

        bank_select.setOnoptionsSelectListener(new OptionsPopupWindow.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {

                bank_name.setText(bank_select_list.get(options1));
                strBank = mBankList.get(options1).id;
                strCity = "";
                city_name.setText("");


            }
        });

        city_select.setOnoptionsSelectListener(new OptionsPopupWindow.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {

                city_name.setText(city_select_list.get(options1));
                strCity = mCityList.get(options1).id;

            }
        });

        province_select.setOnoptionsSelectListener(new OptionsPopupWindow.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {

                province_name.setText(province_select_list.get(options1));

                strProvince = mProvinceList.get(options1).id;
                dohttpCity(strProvince);

            }
        });

        findViewById(R.id.choice_bank_btn).setOnClickListener(this);
        findViewById(R.id.choice_province_btn).setOnClickListener(this);
        findViewById(R.id.choice_city_btn).setOnClickListener(this);

        bank_name = (TextView) findViewById(R.id.tv_bank_name);
        province_name = (TextView) findViewById(R.id.tv_province_name);
        city_name = (TextView) findViewById(R.id.tv_city_name);






//
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.btn_tijiao:
			if(SystenmApi.isNullOrBlank(bank_name.getText().toString())){//  strBank

				showCustomToast("请选择银行卡");
				return;
			}

			if(SystenmApi.isNullOrBlank(mCardNumber.getText().toString())){//strBankNum

				showCustomToast("请输入银行卡号");
				return;
			}

			if(SystenmApi.isNullOrBlank(province_name.getText().toString())){//  strProvince

				showCustomToast("请输入银行所在省");
				return;
			}

			if(SystenmApi.isNullOrBlank(city_name.getText().toString())){//  strCity

				showCustomToast("请输入银行所在市");
				return;
			}
			if(SystenmApi.isNullOrBlank(mBankName.getText().toString())){//strBranch

				showCustomToast("请输入开户行");
				return;
			}
			if(null != mobile){

				if(SystenmApi.isNullOrBlank(ed_vcode.getText().toString())){

					showCustomToast("请输入手机验证码");
                    return;
				}

			}

			doHttp();
			break;
			case R.id.back:
				finish();
			break;
            case R.id.choice_bank_btn:

                if(!bank_select.isShowing()){
                    bank_select.showAtLocation(v, Gravity.BOTTOM,0,0);
                }

                break;
            case R.id.choice_province_btn:

                if(!province_select.isShowing()){
                    province_select.showAtLocation(v, Gravity.BOTTOM,0,0);
                }

                break;
            case R.id.choice_city_btn:

                if(!city_select.isShowing()){

                    city_select.showAtLocation(v, Gravity.BOTTOM,0,0);
                }

                break;
		case R.id.sendphonenum:

//			mSendPhoneNum.setEnabled(false);
			
//			phone = user_phone.getText().toString();
//			if(phone.equals(""))
//			{
//				showCustomToast(R.string.toastphone);
//				return;
//			}
			
			dohttpGetcode();
			break;
		}
	}

	
	/**
	 *发送短信验证码
	 */
	private void dohttpGetcode() {

		JsonBuilder builder = new JsonBuilder();
//		builder.put("phone", phone);
		BaseHttpClient.post(getBaseContext(), Default.getcode, builder,
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
							JSONObject response) {
						// TODO Auto-generated method stub
						super.onSuccess(statusCode, headers, response);
						try {

							if (statusCode == 200) {
								if (response != null) {
									if (response.has("status")) {
										if (response.getInt("status") == 1) {
											time.start();
											showCustomToast(response
													.getString("message"));

//											mhandle.postDelayed(runnbale, 1000);
										} else {
											String message = response.getString("message");
											SystenmApi.showCommonErrorDialog(AddBankCardActivity.this, response.getInt("status"),message);
										}
									}
								} else {

									showCustomToast(R.string.toast1);
								}
							}
						} catch (Exception e) {
							// TODO: handle exception
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
	
	/**
	 * 获取银行列表 获取省份列表
	 */
	private void dohttpBank() {

		BaseHttpClient.post(getBaseContext(), Default.getBankInfo, null,
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
									getBankInfo(json);
								} else {
									showCustomToast(json.getString("message"));
								}
							} else {
								String message = json.getString("message");
								SystenmApi.showCommonErrorDialog(AddBankCardActivity.this, json.getInt("status"),message);
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

	/**
	 * 根据省份id获取 城市列表
	 * 
	 * @param id
	 */
	private void dohttpCity(String id) {

		JsonBuilder jsonBuilder = new JsonBuilder();
		jsonBuilder.put("id", id);

		BaseHttpClient.post(getBaseContext(), Default.getCity, jsonBuilder,
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
									getCityInfo(json);
								} else {
									String message = json.getString("message");
									SystenmApi.showCommonErrorDialog(AddBankCardActivity.this, json.getInt("status"),message);
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

	/**
	 * 解析获得数据
	 * 
	 * @param json
	 */
	public void getCityInfo(JSONObject json) {
		try {
			mCityList.clear();
            city_select_list.clear();
			JSONArray citys = json.getJSONArray("city");

			for (int i = 0; i < citys.length(); i++) {
                MyLog.e("12");
				JSONObject temp = citys.getJSONObject(i);
				BankItem item = new BankItem(temp);
				mCityList.add(item);
                city_select_list.add(item.name);
			}

           city_select.setPicker(city_select_list);


		} catch (Exception e) {
            e.printStackTrace();

		}
	}

	class BankItem {
		String id;
		String name;

		public BankItem(JSONObject json) {
			try {
				id = json.getString("id");
				name = json.getString("value");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void getBankInfo(JSONObject json) {
        mBankList.clear();
        mProvinceList.clear();
        bank_select_list.clear();
        province_select_list.clear();
		try {
			JSONArray banks = json.getJSONArray("bank");
			JSONArray province = json.getJSONArray("province");


			for (int i = 0; i < banks.length(); i++) {
				JSONObject temp = banks.getJSONObject(i);
				BankItem item = new BankItem(temp);
				mBankList.add(item);
                bank_select_list.add(item.name);


			}

            bank_select.setPicker(bank_select_list);

//            bank_select.setTitle("选择银行");

			for (int i = 0; i < province.length(); i++) {
				JSONObject temp = province.getJSONObject(i);
				BankItem item = new BankItem(temp);
				mProvinceList.add(item);
                province_select_list.add(item.name);
			}

            province_select.setPicker(province_select_list);

		} catch (Exception e) {
            e.printStackTrace();

		}
	}

	class bankAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mBankList.size();
		}

		@Override
		public Object getItem(int position) {

			BankItem item = mBankList.get(position);

				return item.name;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.adapter_item_bank,
						null);
			}
			TextView name = (TextView) convertView.findViewById(R.id.name);
			BankItem item = mBankList.get(position);
			name.setText(item.name);
			return convertView;
		}

	}

	class provinceAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mProvinceList.size();
		}

		@Override
		public Object getItem(int position) {
			BankItem item = mProvinceList.get(position);

				return item.name;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.adapter_item_bank,
						null);
			}
			TextView name = (TextView) convertView.findViewById(R.id.name);
			BankItem item = mProvinceList.get(position);
			name.setText(item.name);
			return convertView;
		}

	}

	class cityAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mCityList.size();
		}

		@Override
		public Object getItem(int position) {
			BankItem item = mCityList.get(position);


				return item.name;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.adapter_item_bank,
						null);
			}
			TextView name = (TextView) convertView.findViewById(R.id.name);
			BankItem item = mCityList.get(position);
			name.setText(item.name);
			return convertView;
		}

	}

	public void doHttp() {


        JsonBuilder jsonBuilder = new JsonBuilder();

		jsonBuilder.put("bank_name", strBank);// 银行卡
		jsonBuilder.put("bank_num", mCardNumber.getText().toString());// 卡号
		jsonBuilder.put("bank_province", strProvince);// 开户省
		jsonBuilder.put("bank_city", strCity);// 城市
		jsonBuilder.put("bank_address", mBankName.getText().toString());// 开户行
        if(null != mobile){
            jsonBuilder.put("vcode",ed_vcode.getText().toString());
        }

		BaseHttpClient.post(getBaseContext(), Default.addbank,
				jsonBuilder, new JsonHttpResponseHandler() {

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
									showCustomToast("信息绑定成功");
									finish();
								} else {
									String message = json.getString("message");
									SystenmApi.showCommonErrorDialog(AddBankCardActivity.this, json.getInt("status"),message);
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
	

	/* 倒计时类 */
	class TimeCount extends CountDownTimer {
		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
		}

		@Override
		public void onFinish() {
			mSendPhoneNum.setText("发送");
			mSendPhoneNum.setClickable(true);
		}

		@Override
		public void onTick(long millisUntilFinished) {// 计时过程显示
			mSendPhoneNum.setClickable(false);
			mSendPhoneNum.setText(millisUntilFinished / 1000 + "秒");
		}
	}
}
