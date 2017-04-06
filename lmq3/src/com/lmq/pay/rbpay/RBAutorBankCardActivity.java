package com.lmq.pay.rbpay;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.format.Time;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.xyh.R;
import com.lmq.http.BaseHttpClient;
import com.lmq.http.NetWorkStatusBroadcastReceiver;
import com.lmq.main.api.BaseActivity;
import com.lmq.main.api.SystenmApi;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.security.PrivateKey;

/***
 * 我要充值
 *
 */
public class RBAutorBankCardActivity extends BaseActivity implements OnClickListener {

	private EditText[] rbpay_info = new EditText[4];
	/***
	 * 充值提现FLag
	 */

	private String bankcode = "";

	private PrivateKey privateKey = null;

	/**
	 * startActivity(new Intent( getActivity(), HFOutMoneyActivity.class));
	 */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();

		setContentView(R.layout.rb_author_bank_card);

		rbpay_info[0] = (EditText) findViewById(R.id.rb_banck_card);
		rbpay_info[1] = (EditText) findViewById(R.id.rb_real_name);
		rbpay_info[2] = (EditText) findViewById(R.id.rb_id_no);
		rbpay_info[3] = (EditText) findViewById(R.id.rb_bank_phone);

		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {
		case R.id.back:
			finish();

			break;
	
		default:
			break;
		}

	}
	
	private void doGetRb(JSONObject json){
		
		
		Time time = new Time();
		time.setToNow();
		
		String time_str = time.year+""+time.month+""+time.monthDay+""+time.hour+""+time.minute+""+time.second;
		
		String imei = ((TelephonyManager) getSystemService(TELEPHONY_SERVICE))
				.getDeviceId();
		
		String ip = SystenmApi.getIPStr(RBAutorBankCardActivity.this, NetWorkStatusBroadcastReceiver.wifi);
		
		RequestParams  rbParams = new RequestParams();
		try {
			rbParams.add("merchant_id", json.getString("merchant_id"));
			rbParams.add("card_no", rbpay_info[0].getText().toString());
			rbParams.add("owner", rbpay_info[1].getText().toString());
			rbParams.add("cert_type", "01");
			rbParams.add("cert_no",rbpay_info[2].getText().toString());
			rbParams.add("phone", rbpay_info[3].getText().toString());
			rbParams.add("order_no", json.getString("order_no"));
			rbParams.add("transtime", time_str);
			rbParams.add("currency", "cny");
			rbParams.add("total_fee",json.getString("total_fee"));
			rbParams.add("title", json.getString("title"));
			rbParams.add("body", json.getString("body"));
			rbParams.add("member_id", json.getString("member_id"));
			rbParams.add("terminal_type", "mobile");
			rbParams.add("terminal_info", imei);
			rbParams.add("member_ip", ip);
			rbParams.add("seller_email", json.getString("seller_email"));
			rbParams.add("sign", "");
			
			
			
			BaseHttpClient.normalDoPost(RBAutorBankCardActivity.this, "", rbParams, new AsyncHttpResponseHandler() {
				
				@Override
				public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
					// TODO Auto-generated method stub
					
				}
			});
			
			
			
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
		
		
		
		
		
		
		
	}

	
	
}
