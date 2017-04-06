package com.lmq.main.activity.register;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import android.view.Gravity;
import android.view.View;

import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.xyh.R;
import com.lmq.http.BaseHttpClient;
import com.lmq.http.JsonHttpResponseHandler;
import com.lmq.main.api.BaseActivity;
import com.lmq.main.api.JsonBuilder;
import com.lmq.main.api.MyLog;
import com.lmq.main.api.SystenmApi;
import com.lmq.main.util.Data;
import com.lmq.main.util.Default;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

import java.text.DecimalFormat;

public class RegisterPhotoActivity extends BaseActivity implements
        View.OnClickListener {


	private TextView titleView;
	private ImageView serverCode;
	private EditText mEd_code;
	private String name;
	private String phone;
	private int tag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.register_photo_layout);
        getWindow().setGravity(Gravity.CENTER);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

		Intent intent = getIntent();
		if(intent.hasExtra("tag")){

			tag = intent.getExtras().getInt("tag");

		}else{

			name = intent.getExtras().getString("name");
			phone = intent.getExtras().getString("phone");
		}

        findViewById(R.id.dialog_cancle).setOnClickListener(this);
        findViewById(R.id.dialog_submit).setOnClickListener(this);
        
	    titleView = (TextView) findViewById(R.id.dialog_title);
	    titleView.setText("图片验证码");
		
		mEd_code = (EditText) findViewById(R.id.ed_code);
		serverCode = (ImageView) findViewById(R.id.server_code);
		serverCode.setOnClickListener(this);


    }

    @Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getServerCode(false);
		
	}
    
    private void getServerCode(final boolean flash){

        BaseHttpClient.getFileFromServer(getBaseContext(), "/Member/common/verify", new AsyncHttpResponseHandler() {

			@Override
			public void onStart() {
				super.onStart();

				if (flash) {

					showLoadingDialog("正在加载验证码");
				}

			}

			@Override
			public void onSuccess(int i, Header[] headers, byte[] bytes) {

				dismissLoadingDialog();
				serverCode.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));

			}

			@Override
			public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
				showCustomToast("验证码加载失败，请重试！");
				dismissLoadingDialog();

			}
		});

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.dialog_cancle:
                finish();
                break;
            case R.id.server_code:
                getServerCode(true);
                break;
            case R.id.dialog_submit:

				if(SystenmApi.isNullOrBlank(mEd_code.getText().toString())){

					showCustomToast("请输入验证码");
					return ;
				}


				if(tag==1){
					getCpdeInfoHttp();
				}else{

					showCustomToast("程序走了这里");
					getPageInfoHttp();
				}

            	break;

			default:
				break;

        }
    }

    public void getPageInfoHttp() {

		JsonBuilder builder = new JsonBuilder();
		builder.put("code", mEd_code.getText().toString());

		BaseHttpClient.post(getApplicationContext(), Default.verify_code, builder,
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
								
//									Intent intent = new Intent();
//									setResult(Default.RESULT_BANKCARD,intent);
									Intent intent = new Intent();
									intent.putExtra("phone", phone);
									intent.putExtra("name",name);
									intent.setClass(RegisterPhotoActivity.this,RegisterStepSecondActivity.class);
									startActivity(intent);

									showCustomToast("444444444444444444444444444444");
//									finish();
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
						showCustomToast(responseString);
					}

				});
	}

	public void getCpdeInfoHttp() {

		JsonBuilder builder = new JsonBuilder();
		builder.put("code", mEd_code.getText().toString());

		BaseHttpClient.post(getApplicationContext(), Default.verify_code, builder,
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

									Intent intent = new Intent();
									setResult(Default.RESULT_BANKCARD,intent);
									showCustomToast("555555555555555555555555");
									finish();
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
						showCustomToast(responseString);
					}

				});
	}

}
