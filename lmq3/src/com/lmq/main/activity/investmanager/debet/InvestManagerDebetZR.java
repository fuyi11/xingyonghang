package com.lmq.main.activity.investmanager.debet;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.xyh.R;
import com.lmq.http.BaseHttpClient;
import com.lmq.http.JsonHttpResponseHandler;
import com.lmq.main.api.BaseActivity;
import com.lmq.main.api.JsonBuilder;
import com.lmq.main.api.SystenmApi;
import com.lmq.main.util.Default;

import org.apache.http.Header;
import org.json.JSONObject;

import java.math.BigDecimal;

public class InvestManagerDebetZR extends BaseActivity implements OnClickListener {


    private TextView zr_bj;
    private TextView zr_syf;
    private TextView zr_dzje;
    //    private TextView zr_zrl;
    private EditText zr_pwd;
    private EditText zrl_input;
    private String debetId = "";
    private int hasPin = 0;
    private double zr_free;
    private double  zr_price;
    private double  zr_bqlx;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.debet_zr_layout);

        TextView text = (TextView) findViewById(R.id.title);
        text.setText("立即转让");

        Intent intent = getIntent();

        if(null != intent){

            debetId = intent.getStringExtra("debet_id");
        }
        zr_bj = (TextView) findViewById(R.id.zr_je);
        zr_syf = (TextView) findViewById(R.id.zr_sxf);
        zr_dzje = (TextView) findViewById(R.id.zr_yqdzje);
//        zr_zrl = (TextView) findViewById(R.id.zr_zrl);
        zr_pwd = (EditText) findViewById(R.id.zr_pwd);
        zrl_input = (EditText) findViewById(R.id.zrl_input);
        zrl_input.setText("0.00");

        findViewById(R.id.zr_action).setOnClickListener(this);
        findViewById(R.id.back).setOnClickListener(this);

        zrl_input.addTextChangedListener(textWatcher);
        if(Default.IS_YB && Default.IS_ZFT){
            findViewById(R.id.rl_pay_password).setVisibility(View.GONE);

        }


    }

    private TextWatcher textWatcher = new TextWatcher() {



        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
//

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {


        }

        @Override
        public void afterTextChanged(Editable s) {
            String temp =zrl_input.getText().toString();
            if(SystenmApi.isNullOrBlank(temp)){
                temp = "0";
            }
            double lv = Double.parseDouble(temp);

            if(lv>7.5){

                showCustomToast("折让率在0%-7.5%之间");
            }else{

                getUncollect(lv/100);

            }


//

        }
    };

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish(0);
                break;


            case R.id.zr_action:
                buyDebetAction();
                break;

        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        getDebetInfo();
    }

    // 提交用户反馈到服务器
    public void getDebetInfo() {



        JsonBuilder builder = new JsonBuilder();
        builder.put("id", debetId);




        BaseHttpClient.post(getBaseContext(), Default.sellhtml, builder,
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

                                    if (json.has("capital")) {

                                        zr_bj.setText(json.optString("capital", ""));
                                        zr_price = json.optDouble("capital", 0);

                                    }


//                                    if (json.has("price")) {
//
//                                        zr_syf.setText(json.optString("price", ""));
//
//                                    }

                                    if (json.has("debt_fee_rate")) {

                                        zr_free = json.optDouble("debt_fee_rate", 0);
                                    }

                                    if (json.has("uncollect")) {

                                        zr_bqlx = json.optDouble("uncollect", 0);
                                    }
                                    if (json.has("has_pin")) {

                                        hasPin = json.optInt("has_pin", 0);
                                    }

                                    getUncollect(0);


                                } else {
                                    String message = json.getString("message");
                                    SystenmApi.showCommonErrorDialog(InvestManagerDebetZR.this, json.getInt("status"), message);
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

    private  void getUncollect(double zr_zrlv){



        double retInt = 0;

        double all = zr_price + zr_bqlx - (zr_price * zr_zrlv);
        retInt = all * (1 - zr_free / 100);




        ;

        //设置手续费
        zr_syf.setText(new BigDecimal((all * zr_free / 100)).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() + "");
        //设置预期到账金额
        zr_dzje.setText(new BigDecimal(retInt).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() + "");



    }

    /**
     * 购买债权
     */
    private void buyDebetAction() {


        if(!Default.IS_YB &&!Default.IS_ZFT ){

            if(hasPin == 0){

                showCustomToast("请设置交易密码");
                return;
            }

            if(SystenmApi.isNullOrBlank(zr_pwd.getText().toString())){

                showCustomToast("请输入交易密码");
                return;
            }
        }

        if(SystenmApi.isNullOrBlank(zrl_input.getText().toString())){

            showCustomToast("请输入折让率");
            return;
        }

        JsonBuilder builder = new JsonBuilder();
        builder.put("id",debetId);
        String discount_gold = zrl_input.getText().toString();
        builder.put("discount_gold",discount_gold);
        builder.put("money",zr_bj.getText().toString());
        builder.put("paypass",zr_pwd.getText().toString());



        BaseHttpClient.post(getBaseContext(), Default.sell_debt, builder,
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


                                    showCustomToast(json.getString("message"));
                                    finish();

                                } else {
                                    String message = json.getString("message");
                                    SystenmApi.showCommonErrorDialog(InvestManagerDebetZR.this, json.getInt("status"),message);
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

    public void finish() {
        super.finish();
    }

}
