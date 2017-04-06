/**
 *
 */
package com.lmq.main.activity.user.manager.withdrawal;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.xyh.R;
import com.lmq.http.BaseHttpClient;
import com.lmq.http.JsonHttpResponseHandler;
import com.lmq.main.activity.user.manager.bankinfo.ChoiceBankListActivity;
import com.lmq.main.activity.user.manager.bankinfo.ShowBankCardInfoActivity;
import com.lmq.main.api.BaseActivity;
import com.lmq.main.api.JsonBuilder;
import com.lmq.main.api.MyLog;
import com.lmq.main.api.SystenmApi;
import com.lmq.main.dialog.CommonDialog;
import com.lmq.main.item.BankCardListlItem;
import com.lmq.main.util.Default;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * 个人中心-提现
 */
public class WithDrawalAcitity extends BaseActivity implements
        View.OnClickListener {

    private Button mEnter_money;

    private TextView tv_bank_name;
    private TextView tv_bank_num;
    private ImageView bank_iv;

    /**
     * 免手续费金额
     */
    private TextView sxf_ed_money;
    /**
     * 可提现金额
     */
    private TextView tv_money;
    /**
     * 手续费
     */
    private TextView tv_sxf_money;

    /**
     * 提现金额
     */
    private EditText mEdit_money;
    private EditText mEdit_pass;
    private EditText mEd_code;
    private String mMoney, mPassword, mCcode;



    private String bank_name;
    private String bank_num;
    private int bank_id;
    private int id;
    private int is_idcard = 0;

    /**
     * 超出回款金额费率
     */
    private String cc_hksxfee;
    /**
     * 超出回款金额手续费最大金额
     */
    private String maxfee;
    /**
     * 回款金额费率
     */
    private String hksxfee;
    /**
     * 回款金额手续费最大金额
     */
    private String hk_maxfee;
    /**
     * 手续费最低金额
     */
    private String minfee;

    /**
     * 免手续费金额
     */
    private String back_money;
    /**
     * 可提现金额
     */
    private String all_money;


    private String messageInfo;
    private JSONArray list = null;
    private ArrayList<BankCardListlItem> data = new ArrayList<BankCardListlItem>();

    private Intent intent;
    private ImageView serverCode;
    private RadioGroup tzPayKindGroup;
    private String tzPayKindStr = "1";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.people_info_withdrawal_new);


        findViewById(R.id.back).setOnClickListener(this);
        TextView text = (TextView) findViewById(R.id.title);
        text.setText(R.string.gerenzhongxin_tixian);

        mEnter_money = (Button) findViewById(R.id.btn_enter_money);
        mEnter_money.setOnClickListener(this);

        findViewById(R.id.bank_lv).setOnClickListener(this);
        serverCode = (ImageView) findViewById(R.id.server_code);
        serverCode.setOnClickListener(this);

        tv_bank_name = (TextView) findViewById(R.id.tv_bank_name);
        tv_bank_num = (TextView) findViewById(R.id.tv_bank_num);

        sxf_ed_money = (TextView) findViewById(R.id.sxf_ed_money);
        tv_money = (TextView) findViewById(R.id.money);
        tv_sxf_money = (TextView) findViewById(R.id.sxf_money);

        bank_iv = (ImageView) findViewById(R.id.bank_iv);

        mEdit_money = (EditText) findViewById(R.id.edit_money);
        mEdit_pass = (EditText) findViewById(R.id.edit_pass);
        mEd_code = (EditText) findViewById(R.id.ed_code);

        mEdit_money.addTextChangedListener(textWatcher);

        if(Default.IS_ZFT){
            findViewById(R.id.rl_sxf).setVisibility(View.GONE);//手续费
            findViewById(R.id.pay_password).setVisibility(View.GONE);//支付密码
            findViewById(R.id.lv).setVisibility(View.VISIBLE);
            findViewById(R.id.tv_info).setVisibility(View.GONE);
            findViewById(R.id.tv_info_zft).setVisibility(View.VISIBLE);
        }
        tzPayKindGroup = (RadioGroup) findViewById(R.id.tx_pay_kind);
        tzPayKindGroup
                .setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(RadioGroup group,
                                                 int checkedId) {

                        switch (checkedId) {
                            case R.id.tz_pay_kind1:
                                tzPayKindStr = "1";
                                break;
                            case R.id.tz_pay_kind2:
                                tzPayKindStr = "2";
                                break;

                            default:
                                break;
                        }

                    }
                });

    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        getTiXian();
        getServerCode(false);

    }

    private TextWatcher textWatcher = new TextWatcher() {


        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {


        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {


        }

        @Override
        public void afterTextChanged(Editable s) {
            String temp = mEdit_money.getText().toString();
            if (SystenmApi.isNullOrBlank(temp)) {
                temp = "0";
                tv_sxf_money.setText("0.00");
            }

            double lv = Double.parseDouble(temp);
            if(!Default.IS_ZFT){
               getUncollect(lv);
            }



        }
    };

    private void getUncollect(double money) {

        /**
         * 1.  提现手续费收取说明及提现金额的先后顺序：
         a. 提现金额在回款总金额内，每笔收取0.1%作为手续费（最低不少于2元，最高不超过100元）。
         b. 提现金额超出回款资金总额，每笔收取0.5%作为手续费（最低不少于2元，最高不超过10000元）。
         c. 提现手续费计算公式：手续费 = 回款金额 x 回款手续费 + 超出回款金额 x 超出回款金额手续费。
         */

        double all_ktmoney = Double.parseDouble(all_money);
        double limit_free_money = Double.parseDouble(back_money);
        double cc_hkjefee = Double.parseDouble(cc_hksxfee);
        double cc_hkmaxfee_money = Double.parseDouble(maxfee);
        double hkjefee = Double.parseDouble(hksxfee);
        double hk_maxfee_money = Double.parseDouble(hk_maxfee);
        double minmoney = Double.parseDouble(minfee);

        if (money > all_ktmoney) {
            showCustomToast("提现金额不能超过可用余额哦");
            return;
        }
        if (money <= limit_free_money) {
            if (hkjefee == 0) {
                tv_sxf_money.setText("0");
            } else if (money * hkjefee * 0.001 < minmoney) {
                tv_sxf_money.setText("" + minmoney);
            } else if (money * hkjefee * 0.001 > hk_maxfee_money) {
                tv_sxf_money.setText("" + hk_maxfee_money);
            } else {

                tv_sxf_money.setText("" + new BigDecimal(money * hkjefee * 0.001).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());

            }
        } else {

            if (money * hkjefee * 0.001 + (money - limit_free_money) * cc_hkjefee * 0.001 > cc_hkmaxfee_money) {
                tv_sxf_money.setText("" + cc_hkmaxfee_money);
            } else if (money * hkjefee * 0.001 < minmoney) {
                tv_sxf_money.setText("" + minmoney);
            } else {
                tv_sxf_money.setText("" + new BigDecimal(money * hkjefee * 0.001 + (money - limit_free_money) * cc_hkjefee * 0.001).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
            }
        }

    }

    private void getServerCode(final boolean flash) {

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


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        MyLog.e("MyLog", requestCode + "=====" + resultCode);
        if (requestCode == 10111) {

            if (resultCode == Default.RESULT_BANKCARD) {

                bank_name = data.getExtras().getString("bank_name");
                bank_num = data.getExtras().getString("bank_num");
                bank_id = data.getExtras().getInt("bank_id", 0);
                id = data.getExtras().getInt("id", 0);
                is_idcard = data.getExtras().getInt("is_idcard", 1);

                tv_bank_name.setText(bank_name);
                tv_bank_num.setText(bank_num);

                bank_iv.setBackgroundDrawable(null);
                bank_iv.setImageResource(R.drawable.bankphoto_list);
                bank_iv.setImageLevel(bank_id);
            }
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_enter_money:

               // v.setEnabled(false);
                mMoney = mEdit_money.getText().toString();
                mPassword = mEdit_pass.getText().toString();
                mCcode = mEd_code.getText().toString();

                if (mMoney.equals("")) {
                    showCustomToast(R.string.toast10);
                    return;
                }

                if(!Default.IS_ZFT){
                    if (mPassword.equals("")) {
                        showCustomToast(R.string.toast11);
                        return;
                    }
                }

                if (mCcode.equals("")) {
                    showCustomToast("请输入验证码");
                    return;
                }

                dohttp();
                break;
            case R.id.bank_lv:
                if (Default.PAY_TYPE == Default.PAY_BF_RZ) {
                } else {
                    startActivityForResult(new Intent(WithDrawalAcitity.this, ChoiceBankListActivity.class), 10111);
                }
                break;
            case R.id.server_code:
                getServerCode(true);
                break;
            case R.id.back:
                finish();
                break;
        }
    }


    public void getTiXian() {

        JsonBuilder jsonBuilder = new JsonBuilder();
        jsonBuilder.put("uid", Default.userId);

        BaseHttpClient.post(getBaseContext(), Default.validate_index,
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
                                    MyLog.e(json.toString());
                                    MyLog.e("获取提现的信息", json.toString());
                                    if (is_idcard == 0) {

                                        updateAddInfo(json);
                                    }
                                    if (json.has("all_money")) {
                                        all_money = json.optString("all_money", "0");
                                        tv_money.setText(json.optString("all_money", "0"));
                                    }

                                    if (json.has("back_money")) {

                                        back_money = json.optString("back_money", "0");
                                        sxf_ed_money.setText("免提现手续费额度" + back_money + "元");
                                    }
                                    if (json.has("hk_maxfee")) {

                                        hk_maxfee = json.optString("hk_maxfee", "0");
                                    }
                                    if (json.has("maxfee")) {

                                        maxfee = json.optString("maxfee", "0");
                                    }
                                    if (json.has("hksxfee")) {

                                        hksxfee = json.optString("hksxfee", "0");
                                    }
                                    if (json.has("minfee")) {

                                        minfee = json.optString("minfee", "0");
                                    }
                                    if (json.has("cc_hksxfee")) {

                                        cc_hksxfee = json.optString("cc_hksxfee", "0");
                                    }

                                } else if(json.has("is_jumpmsg")){
                                    String message = json.getString("is_jumpmsg");
                                    SystenmApi.showCommonErrorDialog(WithDrawalAcitity.this, json.getInt("status"), message);
                                } else {
                                    showCustomToast(json.getString("message"));
                                    finish();
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

    public void updateAddInfo(JSONObject json) {
        try {

            if (!json.isNull("list")) {
                list = json.getJSONArray("list");

                if (list != null)
                    for (int i = 0; i < list.length(); i++) {
                        JSONObject templist = list.getJSONObject(0);
                        BankCardListlItem item = new BankCardListlItem();
                        item.init(templist);
                        data.add(item);

                        tv_bank_name.setText(item.getBank_name());
                        tv_bank_num.setText(item.getBank_num());

                        id = item.getId();
                        if(Default.IS_ZFT){

                           /* if(item.getBank_code().equals("ABC")){
                                bank_iv
                                        .setBackgroundResource(R.drawable.bank_05);
                            }else if(item.getBank_code().equals("BOC")){
                                bank_iv
                                        .setBackgroundResource(R.drawable.bank_01);
                            }else if(item.getBank_code().equals("ICBC")){
                                bank_iv
                                        .setBackgroundResource(R.drawable.bank_03);
                            }else if(item.getBank_code().equals("BCCB")){
                                bank_iv
                                        .setBackgroundResource(R.drawable.bank_08);
                            }else if(item.getBank_code().equals("SHYH")){
                                bank_iv
                                        .setBackgroundResource(R.drawable.bank_10);
                            }else if(item.getBank_code().equals("HXB")){
                                bank_iv
                                        .setBackgroundResource(R.drawable.bank_11);
                            }else if(item.getBank_code().equals("GDB")){
                                bank_iv
                                        .setBackgroundResource(R.drawable.bank_13);
                            }else if(item.getBank_code().equals("SDB")){
                                bank_iv
                                        .setBackgroundResource(R.drawable.bank_07);
                            }else if(item.getBank_code().equals("HZYHGFYHGS")){
                                bank_iv
                                        .setBackgroundResource(R.drawable.bank_23);
                            }else if(item.getBank_code().equals("ECITIC")){
                                bank_iv
                                        .setBackgroundResource(R.drawable.bank_14);
                            }else if(item.getBank_code().equals("CCB")){
                                bank_iv
                                        .setBackgroundResource(R.drawable.bank_33);
                            }else if(item.getBank_code().equals("NJYHGFYHGS")){//-------------------------------缺图 南京
                                bank_iv
                                        .setBackgroundResource(R.drawable.bank_04);
                            }else if(item.getBank_code().equals("KLYHGFYHGS")){
                                bank_iv
                                        .setBackgroundResource(R.drawable.bank_26);
                            }else if(item.getBank_code().equals("GUAZYHGFYHGS")){
                                bank_iv
                                        .setBackgroundResource(R.drawable.bank_21);
                            }else if(item.getBank_code().equals("CIB")){
                                bank_iv
                                        .setBackgroundResource(R.drawable.bank_06);
                            }else if(item.getBank_code().equals("NBYHGFYHGS")){//----------------宁波
                                bank_iv
                                        .setBackgroundResource(R.drawable.bank_34);
                            }else if(item.getBank_code().equals("XAYHGFYHGS")){
                                bank_iv
                                        .setBackgroundResource(R.drawable.bank_30);
                            }else if(item.getBank_code().equals("XAYHGFYHGS")){
                                bank_iv
                                        .setBackgroundResource(R.drawable.bank_20);
                            }else if(item.getBank_code().equals("CMBC")){
                                bank_iv
                                        .setBackgroundResource(R.drawable.bank_18);
                            }else if(item.getBank_code().equals("WZYH")){
                                bank_iv
                                        .setBackgroundResource(R.drawable.bank_29);
                            }else if(item.getBank_code().equals("SZNCSYYHGFYHGS")){//-------------深圳农村商业银行
                                bank_iv
                                        .setBackgroundResource(R.drawable.bank_35);
                            }else if(item.getBank_code().equals("CDYH")){
                                bank_iv
                                        .setBackgroundResource(R.drawable.bank_19);
                            } else if(item.getBank_code().equals("BJNCSYYHGFYHGS")){//----------------北京农商银行
                                bank_iv
                                        .setBackgroundResource(R.drawable.bank_36);
                            }else if(item.getBank_code().equals("BOCO")){
                                bank_iv
                                        .setBackgroundResource(R.drawable.bank_16);
                            }else if(item.getBank_code().equals("HKYH")){
                                bank_iv
                                        .setBackgroundResource(R.drawable.bank_22);
                            }else if(item.getBank_code().equals("XMYHGFYHGS")){
                                bank_iv
                                        .setBackgroundResource(R.drawable.bank_28);
                            }else if(item.getBank_code().equals("ZZYH")){
                                bank_iv
                                        .setBackgroundResource(R.drawable.bank_32);
                            }else if(item.getBank_code().equals("NCYH")){
                                bank_iv
                                        .setBackgroundResource(R.drawable.bank_32);
                            }else if(item.getBank_code().equals("JISYHGFYHGS")){
                                bank_iv
                                        .setBackgroundResource(R.drawable.bank_27);
                            }else if(item.getBank_code().equals("POST")){
                                bank_iv
                                        .setBackgroundResource(R.drawable.bank_17);
                            }else if(item.getBank_code().equals("TJYH")){
                                bank_iv
                                        .setBackgroundResource(R.drawable.bank_33);
                            }else if(item.getBank_code().equals("SPDB")){//------------上海浦东
                                bank_iv
                                        .setBackgroundResource(R.drawable.bank_37);
                            }else if(item.getBank_code().equals("CMBCHINA")){
                                bank_iv
                                        .setBackgroundResource(R.drawable.bank_02);
                            }else if(item.getBank_code().equals("HBYHGFYHGS")){
                                bank_iv
                                        .setBackgroundResource(R.drawable.bank_24);
                            }else if(item.getBank_code().equals("CEB")){
                                bank_iv
                                        .setBackgroundResource(R.drawable.bank_12);
                            }else if(item.getBank_code().equals("CSYHGFYHGS")){
                                bank_iv
                                        .setBackgroundResource(R.drawable.bank_31);
                            }*/

                        }else{
                            bank_iv.setBackgroundDrawable(null);
                            bank_iv.setImageResource(R.drawable.bankphoto_list);
                            bank_iv.setImageLevel(item.getBank_id());
                        }


                    }

            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }

    public void showCheckDialog(String messageInfo) {


        CommonDialog.Builder ibuilder = new CommonDialog.Builder(WithDrawalAcitity.this);
        ibuilder.setTitle(R.string.prompt);
        ibuilder.setMessage(messageInfo);
        ibuilder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                WithDrawalAcitity.this.startActivity(
                        new Intent(WithDrawalAcitity.this, ShowBankCardInfoActivity.class));

            }
        });

        ibuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        ibuilder.create().show();
    }





		/*
        AlertDialog.Builder builder = new AlertDialog.Builder(WithDrawalAcitity.this);
		builder.setTitle("友情提示");
		builder.setMessage(messageInfo);

		builder.setNegativeButton("取消",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		builder.setPositiveButton("确认",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						WithDrawalAcitity.this.startActivity(
								new Intent(WithDrawalAcitity.this, ShowBankCardInfoActivity.class));

					}
				});
		builder.create().show();
		*/


    // 我要提现—最后提交
    public void dohttp() {
        JsonBuilder jsonBuilder = new JsonBuilder();

        if(Default.IS_ZFT){
            jsonBuilder.put("money", mMoney);
            jsonBuilder.put("WithdrawType", tzPayKindStr);
            jsonBuilder.put("account_bank_id", id);
            jsonBuilder.put("valicode", mCcode);
        }else {
            jsonBuilder.put("amount", mMoney);
            jsonBuilder.put("pwd", mPassword);
            jsonBuilder.put("bank_id", id);
            jsonBuilder.put("code", mCcode);
        }



        BaseHttpClient.post(getBaseContext(), Default.peoInfoWithdrawal_3,
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
                                    showCustomToast(json.getString("message"));
                                    finish();
                                    getTiXian();
                                    mEdit_money.setText("");
                                    mEdit_pass.setText("");
                                    mEd_code.setText("");


                                } else if(json.has("is_jumpmsg")){
                                    String message = json.getString("is_jumpmsg");
                                    SystenmApi.showCommonErrorDialog(WithDrawalAcitity.this, json.getInt("status"), message);
                                }else {
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