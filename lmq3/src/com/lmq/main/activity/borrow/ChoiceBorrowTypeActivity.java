package com.lmq.main.activity.borrow;

import org.apache.http.Header;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.xyh.R;
import com.lmq.http.BaseHttpClient;
import com.lmq.http.JsonHttpResponseHandler;
import com.lmq.main.activity.person.companyinfo.CompanyInfoActivity;
import com.lmq.main.activity.person.personinfo.PersoninfoActivity;
import com.lmq.main.activity.user.manager.apply.ApplyForMoneyLimitActivity;
import com.lmq.main.api.BaseActivity;
import com.lmq.main.api.JsonBuilder;
import com.lmq.main.api.MyLog;
import com.lmq.main.api.SystenmApi;
import com.lmq.main.dialog.ApplyPersonOrEnterpriseUserDialog;
import com.lmq.main.util.Default;



public class ChoiceBorrowTypeActivity extends BaseActivity implements OnClickListener {

	private String kindStr;

	/**
	 * 可使用额度
	 */
	private TextView use_edu;
	/**
	 * 总额度
	 */
	private TextView total_edu;
	/**
	 * 已使用额度
	 */
	private TextView no_use_edu;
	/**
	 * 额度状态
	 */
	private TextView status_edu;

	/**
	 * 净值可使用额度
	 */
	private TextView jz_use_edu;
	/**
	 * 净值总额度
	 */
	private TextView jz_total_edu;
	/**
	 * 净值已使用额度
	 */
	private TextView jz_ysy_ed;
	/**
	 * 净值额度状态
	 */
	private TextView jz_use_status;

	private int is_transfer;
	private int validate_user_type;
	private int credit_limit;

	private ApplyPersonOrEnterpriseUserDialog dialog;


	// 奖励金额
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.borrow_list);
		findViewById(R.id.back).setOnClickListener(this);
		TextView title = (TextView) findViewById(R.id.title);
		title.setText("我要借款");
		initViews();

	}

	protected void initViews() {

		use_edu = (TextView) findViewById(R.id.use_edu);
		total_edu = (TextView) findViewById(R.id.total_edu);
		no_use_edu = (TextView) findViewById(R.id.no_use_edu);
		status_edu = (TextView) findViewById(R.id.status_edu);

		jz_use_edu = (TextView) findViewById(R.id.jz_use_edu);
		jz_total_edu = (TextView) findViewById(R.id.jz_total_edu);
		jz_ysy_ed = (TextView) findViewById(R.id.jz_ysy_ed);
		jz_use_status = (TextView) findViewById(R.id.jz_use_status);


		findViewById(R.id.edu_application).setOnClickListener(this);
		findViewById(R.id.btn_borrow_xy).setOnClickListener(this);
		findViewById(R.id.btn_borrow_db).setOnClickListener(this);
		findViewById(R.id.btn_borrow_jz).setOnClickListener(this);
		findViewById(R.id.btn_borrow_mh).setOnClickListener(this);
		findViewById(R.id.btn_borrow_dy).setOnClickListener(this);


	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getListDataBorrowType();

	}

	@Override
	public void onClick(View arg0){
	// TODO Auto-generated method stub
	switch(arg0.getId())

	{
		case R.id.edu_application:
			if (is_transfer == 0) {
				checkStatus(new Intent(ChoiceBorrowTypeActivity.this, ApplyBorrowMoneyActivity.class));
			} else {
				ChoiceBorrowTypeActivity.this.startActivity(
						new Intent(ChoiceBorrowTypeActivity.this, ApplyForMoneyLimitActivity.class));
			}

			break;
		case R.id.btn_borrow_xy:
			if (is_transfer == 0) {
				checkStatus(new Intent(ChoiceBorrowTypeActivity.this, ApplyBorrowMoneyActivity.class));
			} else if (credit_limit < 50) {
				showCustomToast("您的信用额度小于50，请申请信用额度");
			} else {
				Intent intent = new Intent();
				intent.putExtra("type", 1);
				intent.setClass(ChoiceBorrowTypeActivity.this,
						ApplyBorrowMoneyActivity.class);
				startActivity(intent);
			}

			break;
		case R.id.btn_borrow_db:
			if (is_transfer == 0) {
				checkStatus(new Intent(ChoiceBorrowTypeActivity.this, ApplyBorrowMoneyActivity.class));
			} else {
				Intent intent = new Intent();
				intent.putExtra("type", 2);
				intent.setClass(ChoiceBorrowTypeActivity.this,
						ApplyBorrowMoneyActivity.class);
				startActivity(intent);
			}

			break;
		case R.id.btn_borrow_jz:
			if (is_transfer == 0) {
				checkStatus(new Intent(ChoiceBorrowTypeActivity.this, ApplyBorrowMoneyActivity.class));
			} else {
				Intent intent = new Intent();
				intent.putExtra("type", 4);
				intent.setClass(ChoiceBorrowTypeActivity.this,
						ApplyBorrowMoneyActivity.class);
				startActivity(intent);
			}

			break;
		case R.id.btn_borrow_mh:
			if (is_transfer == 0) {
				checkStatus(new Intent(ChoiceBorrowTypeActivity.this, ApplyBorrowMoneyActivity.class));
			} else {
				Intent intent = new Intent();
				intent.putExtra("type", 3);
				intent.setClass(ChoiceBorrowTypeActivity.this,
						ApplyBorrowMoneyActivity.class);
				startActivity(intent);
			}

			break;
		case R.id.btn_borrow_dy:
			if (is_transfer == 0) {
				checkStatus(new Intent(ChoiceBorrowTypeActivity.this, ApplyBorrowMoneyActivity.class));
			} else {
				Intent intent = new Intent();
				intent.putExtra("type", 5);
				intent.setClass(ChoiceBorrowTypeActivity.this,
						ApplyBorrowMoneyActivity.class);
				startActivity(intent);
			}

			break;
		case R.id.submit_lendmoney:

			break;

		case R.id.back:

			finish();

			break;

		case R.id.dialog_cancle:

			finish();

			break;

		case R.id.apply_person:

			startActivity(new Intent(ChoiceBorrowTypeActivity.this, PersoninfoActivity.class));

			dialog.dismiss();

			break;

		case R.id.apply_enterprise:

			startActivity(new Intent(ChoiceBorrowTypeActivity.this, CompanyInfoActivity.class));

			dialog.dismiss();

			break;

		case R.id.dialog_submit:

			dialog.dismiss();

			break;

		default:

			break;
	}

}
	
	private void getListDataBorrowType() {

        JsonBuilder builder = new JsonBuilder();
        //TODO 请求灵活宝服务器信息

        BaseHttpClient.post(getBaseContext(), Default.borrow_index, null,
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
                                // 没有新版本
                                if (json.getInt("status") == 1) {


                                    MyLog.e("获取借款类型详细信息", "" + json.toString());

                                    updataViewInfo(json);
                                } else {
									String message = json.getString("message");
									SystenmApi.showCommonErrorDialog(ChoiceBorrowTypeActivity.this, json.getInt("status"),message);
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

	public void checkStatus(Intent tyIntent){

		Intent intent = null;

		if(is_transfer==0){

			if(null == dialog){

				dialog = new ApplyPersonOrEnterpriseUserDialog(ChoiceBorrowTypeActivity.this);
				dialog.setDialogTitle("借款人类型");
				dialog.setonClickListener(this);
			}

            //填出填写资料
			dialog.showAsDropDown(total_edu);


			//TODO 进入申请借款类型
		}else if(is_transfer == 1){

			if(validate_user_type == 0){

				intent = new Intent(ChoiceBorrowTypeActivity.this, CompanyInfoActivity.class);

				startActivity(intent);
			}else if(validate_user_type == 1){

				showCustomToast("您的资料还在审核中...");

			}else if(validate_user_type == 2){

				showCustomToast("企业借款请联系网站客服人员！");


			}


			//TODO 企业判断
		}else if(is_transfer == 2){

			//TODO 个人判断


			if(validate_user_type == 0){

				intent = new Intent(ChoiceBorrowTypeActivity.this, PersoninfoActivity.class);

				startActivity(intent);
			}else if(validate_user_type == 1){

				showCustomToast("您的资料还在审核中...");

			}else if(validate_user_type == 2){

				intent = tyIntent;
				startActivity(intent);


			}


		}










	}

    private void updataViewInfo(JSONObject json) {

        if (json.has("credit_limit")) {

//        	use_edu.setText(json.optString("credit_limit", ""));
			credit_limit=json.optInt("credit_limit", 0);
			use_edu.setText(credit_limit+"");

        }

        if (json.has("credit_cuse")) {

        	total_edu.setText(json.optString("credit_cuse", ""));
        }

        if (json.has("credit_use")) {

        	no_use_edu.setText(json.optString("credit_use", ""));
        }
        if (json.has("credit_stuats")) {

        	status_edu.setText(json.optString("credit_stuats", ""));
        }

        if (json.has("netmoney")) {

        	jz_use_edu.setText(json.optString("netmoney", ""));
        }
        if (json.has("allnetMoney")) {

        	jz_total_edu.setText(json.optString("allnetMoney", ""));
        }
        if (json.has("allnetMoney_use")) {

        	jz_ysy_ed.setText(json.optString("allnetMoney_use", ""));
        }

        if (json.has("netMoney_stuats")) {

        	jz_use_status.setText(json.optString("netMoney_stuats", ""));
        }
        
        /**
         * 会员借款状态   0未确定 1企业借款  2 个人借款
         */
        if (json.has("is_transfer")) {
        	
        	is_transfer=json.optInt("is_transfer", 0);
        }
        
        if (json.has("phone_status")) {
        	
        	json.optInt("phone_status", 0);
        }
        if (json.has("id_status")) {
        	
        	json.optInt("id_status", 0);
        }
        /**
         * 会员借款类型申请状态   0未提交/审核未通过 1审核中 2通过
         * */
        if (json.has("validate_user_type")) {
        	
        	validate_user_type=json.optInt("validate_user_type", 0);
        }
 
        

    }


}
