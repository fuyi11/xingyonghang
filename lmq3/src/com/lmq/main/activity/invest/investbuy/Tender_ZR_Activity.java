package com.lmq.main.activity.invest.investbuy;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.xyh.R;
import com.lmq.http.BaseHttpClient;
import com.lmq.http.JsonHttpResponseHandler;
import com.lmq.main.api.BaseActivity;
import com.lmq.main.api.JsonBuilder;
import com.lmq.main.api.MyLog;
import com.lmq.main.api.SystenmApi;
import com.lmq.main.item.TB_YHQ_Item;
import com.lmq.main.util.Default;
import com.lmq.ybpay.YBPayActivity;
import com.money.more.activity.ControllerActivity;
import com.money.more.basil.Conts;
import com.money.more.bean.TransferData;
import com.money.more.utils.StringUtil;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tender_ZR_Activity extends BaseActivity implements
        android.view.View.OnClickListener {

    /**
     * 支付密码
     */
    private EditText mEditPassword;
    /**
     * 投资金额
     */
    private EditText mEditNum;

    /**
     * 账户余额
     */
    private TextView tv_account_money;
    /**
     * 预计收益
     */
    private TextView tv_yjsy;
    /**
     * 起投金额
     */
    private TextView tv_borrow_min;
    /**
     * 限投金额
     */
    private TextView tv_borrow_max;
    /**
     * 无限制"元"
     */
    private TextView xz_yuan;
    /**
     * 可减金额
     */
    private TextView tv_money;

    private String id;
    private int type;
    private ArrayList<TB_YHQ_Item> data = new ArrayList<TB_YHQ_Item>();


    /**
     * 预计收益
     */
    private String amount;
    /**
     * 详情页债券id，作为最终投标使用
     **/
    private String invest_id;
    /**
     * 起投金额
     **/
    private String qitou;


    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.tender_zr_layout);

        if (Default.IS_Qdd) {
            findViewById(R.id.zfmm).setVisibility(View.GONE);
        }

        Intent intent = getIntent();
        if (intent.hasExtra("id")) {
            id = intent.getStringExtra("id");
        }
        if (intent.hasExtra("invest_id")) {
            invest_id = intent.getStringExtra("invest_id");
        }
        if (intent.hasExtra("qitou")) {
            qitou = intent.getStringExtra("qitou");
        }

        initView();
        mEditNum.addTextChangedListener(textWatcher);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        doHttpBuy();
        tv_borrow_min.setText(qitou);
    }

    public void initView() {
        TextView title = (TextView) findViewById(R.id.title);
        title.setText("立即投标");

        findViewById(R.id.back).setOnClickListener(this);
        findViewById(R.id.enter_money).setOnClickListener(this);

        tv_account_money = (TextView) findViewById(R.id.tv_account_money);
        tv_yjsy = (TextView) findViewById(R.id.tv_yjsy);
        tv_borrow_min = (TextView) findViewById(R.id.tv_borrow_min);
        tv_borrow_max = (TextView) findViewById(R.id.tv_borrow_max);
        tv_money = (TextView) findViewById(R.id.tv_money);
        xz_yuan = (TextView) findViewById(R.id.xz_yuan);

        mEditNum = (EditText) findViewById(R.id.ed_money);
        mEditPassword = (EditText) findViewById(R.id.ed_pin);
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
            String temp = mEditNum.getText().toString();
            if (SystenmApi.isNullOrBlank(temp)) {
                temp = "0";
            }
            double lv = Double.parseDouble(temp);
            quickcountrate();
        }
    };

    /**
     * 计算金额
     **/
    public void quickcountrate() {

        JsonBuilder builder = new JsonBuilder();
        builder.put("id", id);
        builder.put("amount", mEditNum.getText().toString());

        BaseHttpClient.post(getBaseContext(), Default.quickcountrate, builder,
                new JsonHttpResponseHandler() {

                    @Override
                    public void onStart() {
                        // TODO Auto-generated method stub
                        super.onStart();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          JSONObject json) {
                        // TODO Auto-generated method stub
                        super.onSuccess(statusCode, headers, json);
                        try {

                            if (statusCode == 200) {
                                if (json.getInt("status") == 1) {
                                    if (json.has("amount")) {
                                        amount = json.optString("amount", "0");
                                    }
                                    tv_yjsy.setText(amount);
                                } else {
                                    String message = json.getString("message");
                                    SystenmApi.showCommonErrorDialog(Tender_ZR_Activity.this, json.getInt("status"), message);
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

    public void updateInfo(JSONObject json) {

        if (json.has("user_money")) {
            tv_account_money.setText(json.optString("user_money", "0"));
        }
        if (json.has("invest_id")) {
            invest_id = json.optString("invest_id", "0");
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.enter_money:

                if (SystenmApi.isNullOrBlank(mEditNum.getText().toString())) {
                    showCustomToast("请输入投资金额");
                    return;
                }


                if (Default.IS_YB) {
                    Intent intent = new Intent(Tender_ZR_Activity.this, YBPayActivity.class);
                    intent.putExtra("YB_TYPE", 6);
                    intent.putExtra("id", invest_id);
                    intent.putExtra("money", mEditNum.getText().toString());
                    startActivity(intent);
                    finish();
                } else if (Default.IS_Qdd){
                    doQddHttpMoney();
                }else{

                    doHttpMoney();

                }
                break;
        }
    }


    public void doHttpBuy() {
        JsonBuilder builder = new JsonBuilder();
        builder.put("id", id);

        BaseHttpClient.post(getBaseContext(), Default.debt_ajax_invest, builder,
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
                                    dismissLoadingDialog();
                                    updateInfo(json);
                                } else {
                                    String message = json.getString("message");
                                    SystenmApi.showCommonErrorDialog(Tender_ZR_Activity.this, json.getInt("status"), message);
                                }
                            } else {
                                showCustomToast(R.string.toast1);
                                finish();
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
     * 最终购买
     */
    private void doHttpMoney() {
        JsonBuilder builder = new JsonBuilder();
        builder.put("id", invest_id);
        builder.put("pin", mEditPassword.getText().toString());
        builder.put("money", mEditNum.getText().toString());

        BaseHttpClient.post(getBaseContext(), Default.debt_investmoney, builder,
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
                                    showCustomToast(json.getString("message"));
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

    /**
     * 钱多多债权转让最终购买
     */
    private void doQddHttpMoney() {
        JsonBuilder builder = new JsonBuilder();
        builder.put("id", invest_id);
        builder.put("money", mEditNum.getText().toString());

        BaseHttpClient.post(getBaseContext(), Default.双钱债权转让, builder,
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


                                    submitTransterData(json);//本地签名


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


    /**
     * =========================使用本地签名
     * @param json
     * @throws JSONException
     */
    private void submitTransterData(JSONObject json) throws JSONException {

        JSONArray array = json.getJSONArray("LoanJsonList");
        JSONObject object = array.getJSONObject(0);

        TransferData tfd = new TransferData();

        Conts.setServiceUrl(json.optString("url"));
        Conts.setMddPrivateKey(Default.privateKey);



        List<Map<String,String>> list = new ArrayList<Map<String,String>>();
        Map<String,String> map1 = new HashMap<String, String>();

        map1.put("LoanOutMoneymoremore", object.getString("LoanOutMoneymoremore"));
        map1.put("LoanInMoneymoremore", object.getString("LoanInMoneymoremore"));
        map1.put("OrderNo", object.getString("OrderNo"));
        map1.put("BatchNo", object.getString("BatchNo"));
        map1.put("Amount", object.getString("Amount"));
        map1.put("TransferName",object.getString("TransferName"));
        map1.put("AdvanceBatchNo", object.getString("AdvanceBatchNo"));
        map1.put("Remark", object.getString("Remark"));
        map1.put("FullAmount", object.getString("FullAmount"));

        if (object.getString("SecondaryJsonList").equals("")){

            map1.put("SecondaryJsonList","");

        }else {

            //判断有误二次分配列表
            if (SystenmApi.isNullOrBlank(object.getString("SecondaryJsonList"))){

                map1.put("SecondaryJsonList", "");

            }else {//有二次分配列表

                JSONArray array1 = object.getJSONArray("SecondaryJsonList");

                //添加二次分配列表
                List<Map<String,String>> secondslist = new ArrayList<Map<String, String>>();
                for(int i=0;i<array1.length();i++){
                    JSONObject object1 = array1.getJSONObject(i);
                    //二次分配列表1
                    Map<String,String> smap = new HashMap<String, String>();
                    smap.put("Remark",object1.getString("Remark"));
                    smap.put("TransferName",object1.getString("TransferName"));
                    smap.put("Amount",object1.getString("Amount"));
                    smap.put("LoanInMoneymoremore",object1.getString("LoanInMoneymoremore"));

                    secondslist.add(smap);
                }
                String secondaryJsonList = new Gson().toJson(secondslist);

                map1.put("SecondaryJsonList",secondaryJsonList);

            }

            }

        list.add(map1);

        String loanJsonlist = new Gson().toJson(list);


        tfd.setLoanJsonList(loanJsonlist);
        tfd.setPlatformdd(json.getString("PlatformMoneymoremore"));
        tfd.setTransferAction(json.getInt("TransferAction"));
        tfd.setAction(json.getInt("Action"));
        tfd.setTransferType(json.getInt("TransferType"));
        tfd.setNeedAudit(json.getString("NeedAudit"));
        tfd.setRemark1(json.getString("Remark1"));
        tfd.setNotifyurl(json.getString("NotifyURL"));

        tfd.setSignData(tfd.signData());


        Intent intent = new Intent(this, ControllerActivity.class);
        //设置操作类型为4，表示转账
        intent.putExtra("type", 4);
        intent.putExtra("data", tfd);
        startActivityForResult(intent, 400);

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == 1) {
                if (data != null) {
                    String plat = data.getStringExtra("plat");
                    if (!StringUtil.isEmpty(plat)) {

                        showCustomToast(plat);
                    }

                    MyLog.e("URL===="+Conts.getServiceUrl());
                }
            } else {
                StringBuilder sb = new StringBuilder("");
                int code1 = data.getIntExtra("code1", 0);
                int code2 = data.getIntExtra("code2", 0);
                String message1 = data.getStringExtra("message1");
                String message2 = data.getStringExtra("message2");
                if (code1 == 88 ||code2 == 88) {
                    // 成功
                    sb.append("投标成功！");
                    Toast.makeText(this, sb.toString(), Toast.LENGTH_SHORT).show();
                    finish();
                }else{
                    sb.append("操作结果标识:").append(code1).append("操作结果:")
                            .append(message1).append("操作结果标识:").append(code2).append("操作结果:")
                            .append(message2);
                    Toast.makeText(this, sb.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }
}