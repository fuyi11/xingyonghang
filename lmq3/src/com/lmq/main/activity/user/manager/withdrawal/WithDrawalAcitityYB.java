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

import com.lmq.ybpay.YBPayActivity;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * 易宝个人中心-提现
 */
public class WithDrawalAcitityYB extends BaseActivity implements
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
    private TextView tx_show1;
    private TextView tx_show2;

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

    private String fee_mode;
    private String withdrawfee;
    private Double withdrawrates;
    private Double withdrawmin;


    private String messageInfo;
    private JSONArray list = null;
    private ArrayList<BankCardListlItem> data = new ArrayList<BankCardListlItem>();

    private Intent intent;
    private ImageView serverCode;
    private RadioGroup tzPayKindGroup;
    private String txPayKindStr = "2";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.people_info_withdrawal_new_yb);


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
        tx_show1 = (TextView) findViewById(R.id.tx_show1);
        tx_show2 = (TextView) findViewById(R.id.tx_show2);

        bank_iv = (ImageView) findViewById(R.id.bank_iv);

        mEdit_money = (EditText) findViewById(R.id.edit_money);
        mEdit_pass = (EditText) findViewById(R.id.edit_pass);
        mEd_code = (EditText) findViewById(R.id.ed_code);

        mEdit_money.addTextChangedListener(textWatcher);


//        getTiXian();
//        getServerCode(false);

        tzPayKindGroup = (RadioGroup) findViewById(R.id.tx_pay_kind);
        tzPayKindGroup.setVisibility(View.VISIBLE);
        tzPayKindGroup
                .setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(RadioGroup group,
                                                 int checkedId) {

                        switch (checkedId) {
                            case R.id.tz_pay_kind1:
                                txPayKindStr = "1";
                                break;
                            case R.id.tz_pay_kind2:
                                txPayKindStr = "2";

                                break;

                            default:
                                break;
                        }

                    }
                });

    };


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
            getUncollect(lv);


        }
    };

    private void getUncollect(double money) {



        /**
         * 首先如果提现金额小于back_money，为0。如果大于，（提现金额-back_money)*0.005。最大不超过一万。

         */

        double limit_free_money = Double.parseDouble(back_money);


        if (money > limit_free_money) {
            double tips_free = new BigDecimal((money - limit_free_money) * 0.005).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            tv_sxf_money.setText("" + (tips_free > 10000 ? 10000 : tips_free));
        } else {
            tv_sxf_money.setText("0");

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
        if (requestCode ==10111) {

            if(resultCode == Default.RESULT_BANKCARD){

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


                mMoney = mEdit_money.getText().toString();

                if (mMoney.equals("")) {
                    showCustomToast(R.string.toast10);
                    return;
                }

                        Intent intent = new Intent(WithDrawalAcitityYB.this, YBPayActivity.class);
                        intent.putExtra("amount", mEdit_money.getText().toString());
                        intent.putExtra("YB_TYPE", Default.TYPE_YB_WITHDRAW);
                        intent.putExtra("withdraw_type", txPayKindStr);
                        startActivity(intent);



                //	dohttp();
                break;
            case R.id.bank_lv:

                startActivityForResult(new Intent(WithDrawalAcitityYB.this, ChoiceBankListActivity.class), 10111);

                break;
            case R.id.server_code:
                getServerCode(true);
                break;
            case R.id.back:
                finish();
                break;
        }
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
                        tv_bank_num.setText("尾号" + item.getBank_num());

                        id=item.getId();

                        bank_iv.setBackgroundDrawable(null);
                        bank_iv.setImageResource(R.drawable.bankphoto_list);
                        bank_iv.setImageLevel(item.getBank_id());

                    }


            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
    public void showCheckDialog(String messageInfo) {


        CommonDialog.Builder ibuilder  = new CommonDialog.Builder(WithDrawalAcitityYB.this);
        ibuilder.setTitle(R.string.prompt);
        ibuilder.setMessage(messageInfo);
        ibuilder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                WithDrawalAcitityYB.this.startActivity(
                        new Intent(WithDrawalAcitityYB.this, ShowBankCardInfoActivity.class));

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
                                    if (json.has("withdrawfee")) {

                                        withdrawfee = json.optString("withdrawfee", "0");
                                    }
                                    if (json.has("withdrawrates")) {

                                        withdrawrates = json.optDouble("withdrawrates", 0);
                                    }
                                    if (json.has("withdrawmin")) {

                                        withdrawmin = json.optDouble("withdrawmin", 0);
                                    }

                                    if (json.has("fee_mode")) {

                                        fee_mode = json.optString("fee_mode", "用户");
                                        tx_show1.setText("1.将用户的账户余额提现至绑定的银行卡，会收取一定的提现手续费，手续费由" + fee_mode + "承担。");
                                    }
                                    tx_show2.setText("2.次日到账："+withdrawfee+"元/笔，即时到账：提现金额*"+withdrawrates +"%/笔，最低不能低于"+withdrawmin+"" +
                                            "元/笔，如果提现金额*"+withdrawrates+"%<"+withdrawfee+"元，手续费则按"+withdrawmin+"元计算。");


                                } else if (json.getInt("status") == 1005) {
                                    showCheckDialog(json.getString("is_jumpmsg"));


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
        jsonBuilder.put("amount", mMoney);
        jsonBuilder.put("pwd", mPassword);
        jsonBuilder.put("bank_id", bank_id);
        jsonBuilder.put("code", mCcode);


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
                                    getTiXian();
                                    mEdit_money.setText("");
                                    mEdit_pass.setText("");
                                    mEd_code.setText("");


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

