package com.lmq.pay.rbpay;

import org.apache.http.Header;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.xyh.R;
import com.lmq.http.BaseHttpClient;
import com.lmq.main.api.BaseActivity;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;



/***
 * 
 * @author lmq
 *
 */
public class RBPayPhoneCoderActivity extends BaseActivity implements OnClickListener {

	private EditText phone_code;

	
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.rb_pay_final);
		
		
		phone_code = (EditText) findViewById(R.id.rb_phone_code);
		findViewById(R.id.user_pay_btn).setOnClickListener(this);
		
		
		
		
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
	
	private void payAction(){
		
		RequestParams requestParams = new RequestParams();
		requestParams.add("merchant_id", "");
		requestParams.add("order_no", "");
		requestParams.add("check_code", "");
		requestParams.add("sign", "");
		
		
		
		
		BaseHttpClient.normalDoPost(RBPayPhoneCoderActivity.this, "", requestParams, new AsyncHttpResponseHandler() {
			
			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				// TODO Auto-generated method stub
				
			}
		});
		
		
		
		
		
	}
	
	
	
	
	
	
	private void getPhoneCode(){
		
		
		RequestParams requestParams = new RequestParams();
		requestParams.add("merchant_id", "");
		requestParams.add("order_no", "");
		requestParams.add("sign", "");
		
		
		BaseHttpClient.normalDoPost(RBPayPhoneCoderActivity.this, "", requestParams, new AsyncHttpResponseHandler() {
			
			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				// TODO Auto-generated method stub
				
			}
		});
		
		
	}


}



