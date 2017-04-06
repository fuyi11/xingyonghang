package com.lmq.main.activity.user.manager.pay;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baofoo.sdk.vip.BaofooPayActivity;
import com.xyh.R;
import com.lmq.http.BaseHttpClient;
import com.lmq.http.JsonHttpResponseHandler;
import com.lmq.main.api.BaseActivity;
import com.lmq.main.api.JsonBuilder;
import com.lmq.main.api.MyLog;
import com.lmq.main.api.SystenmApi;
import com.lmq.main.item.UnpayItem;
import com.lmq.main.util.Default;
import com.lmq.pay.ysbpay.GlobalDefine;
import com.lmq.pay.jdpay.JDPayActivity;
import com.lmq.pay.rbpay.RBPayActivity;
import com.lmq.ybpay.YBPayActivity;
import com.unionpay.UPPayAssistEx;
import com.uns.pay.example.CallUnsPay;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.Iterator;

/***
 * 我要充值
 */
public class ThirdPayActivity extends BaseActivity implements OnClickListener {

    private EditText user_amount;
    private Button user_pay_btn;
    private UnpayItem unpayItem;
    private StringBuilder paraBuilder = new StringBuilder();
    private TextView bank_name;

    private boolean processFlag = true; //默认可以点击

    private View bank_select_view;
    private String bfBankid;
    private String bfBanknum;
    /*
    private String[] bank_names;
    private String[] bank_codes;

    private PopDialog popDialog;
    private String bankcode = "";
    private ArrayList<BankItem> bfBankList = new ArrayList<BankItem>();
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.peopleinfo_pay);
        /*
        bank_names = getResources().getStringArray(R.array.bank_name);
        bank_codes = getResources().getStringArray(R.array.bank_id);
        */
        TextView text = (TextView) findViewById(R.id.title);
        text.setText(R.string.chongzhiz);

        bank_select_view = findViewById(R.id.bank_select);
        bank_select_view.setOnClickListener(this);
        bank_name = (TextView) findViewById(R.id.bank_name);
        user_amount = (EditText) findViewById(R.id.user_pay_amount);
        user_pay_btn = (Button) findViewById(R.id.user_pay_btn);
        user_pay_btn.setOnClickListener(this);
        findViewById(R.id.back).setOnClickListener(this);

        /*
        if(Default.use_bf_rz){
            popDialog = new PopDialog(ThirdPayActivity.this);
            popDialog.setonClickListener(this);
            popDialog.addItems(bank_names);
            popDialog.setDialogTitle("选择充值银行");
            popDialog.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    // TODO Auto-generated method stub
                    popDialog.setDefaultSelect(position);

                }
            });
        }*/
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            /*case R.id.cButton:
                popDialog.dismiss();
                break;
            case R.id.bank_select:
                popDialog.showAsDropDown(v);
                break;*/
            case R.id.user_pay_btn:
                if (user_amount.getText() != null) {
                    setProcessFlag();
                    String amount = user_amount.getText().toString();
                    if (!amount.equals("")) {

                        if(Default.IS_YB){
                            Intent intent = new Intent(ThirdPayActivity.this, YBPayActivity.class);
                            intent.putExtra("money", user_amount.getText().toString());
                            intent.putExtra("YB_TYPE", Default.TYPE_YB_CHARGE);
                            startActivity(intent);

                        }else if (Default.IS_Qdd){

                            loan();

                        }else{
                            switch (Default.PAY_TYPE) {
                                case Default.PAY_RB://融宝
                                    payRb(amount);
                                    break;
                                case Default.PAY_YSB:
                                    doHttpYsb();
                                    break;
                                case Default.PAY_BF_RZ:
                                    doHttpBaoFu_RZ();
                                    break;
                                case Default.PAY_BF_YL:
                                    doHttpBaoFu_YL();
                                    break;
                                case Default.PAY_JD:
                                    doHttpJD();
                                    break;
                            }
                        }
                        new TimeThread().start();
                    } else {
                        showCustomToast("请输入充值金额");
                    }
                }
                break;
            default:
                break;
        }

    }

    private void loan() {




    }

    /**
     * 设置按钮在短时间内被重复点击的有效标识（true表示点击有效，false表示点击无效）
     */
    private synchronized void setProcessFlag() {
        processFlag = false;
    }

    /**
     * 计时线程（防止在一定时间段内重复点击按钮）
     */
    private class TimeThread extends Thread {
        public void run() {
            try {
                sleep(1000);
                processFlag = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        /*if(Default.use_bf_rz)
            getBFPayBankList();*/
    }


    /*
    //  获取宝付支付支持银行列表
    private void getBFPayBankList() {

        ///Mobile/mcenter/chk_bank_index

        JsonBuilder builder = new JsonBuilder();

        builder.put("amount", user_amount.getText().toString());

        BaseHttpClient.post(getBaseContext(), Default.get_bf_bank_list, builder,
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


                                if (json.has("list")) {


                                    JSONArray array = json.getJSONArray("list");
                                    bfBankList.clear();
                                    popDialog.clearData();
                                    for (int i = 0; i < array.length(); i++) {


                                        BankItem item = new BankItem(array.getJSONObject(i));

                                        bfBankList.add(item);
                                        popDialog.addItem(item.getBankName());


                                    }


                                }


                            } else {
                                showCustomToast(R.string.toast1);
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
                        showCustomToast(responseString);
                    }

                });


    }


    private class BankItem {

        private String bankName;
        private String bankId;
        private String bankNum;


        public BankItem(JSONObject json) {

            if (json.has("bank_name")) {

                bankName = json.optString("bank_name", "");

            }
            if (json.has("bank_id")) {

                bankId = json.optString("bank_id", "");

            }
            if (json.has("bank_num")) {

                bankNum = json.optString("bank_num", "");

            }

        }

        public String getBankName() {
            return bankName;
        }

        public void setBankName(String bankName) {
            this.bankName = bankName;
        }

        public String getBankId() {
            return bankId;
        }

        public void setBankId(String bankId) {
            this.bankId = bankId;
        }

        public String getBankNum() {
            return bankNum;
        }

        public void setBankNum(String bankNum) {
            this.bankNum = bankNum;
        }
    }
*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {

        if (requestCode == GlobalDefine.PAY) {
            String result = "";
            Bundle mbundle = intent.getExtras();
            result = mbundle.getString("para");
            String payresult = mbundle.getString("payresult");

            if (result.equalsIgnoreCase("fail")) {
                Toast.makeText(getApplicationContext(), "支付失败了",
                        Toast.LENGTH_SHORT).show();
            } else if (result.equalsIgnoreCase("cancel")) {
                Toast.makeText(getApplicationContext(), "支付取消了",
                        Toast.LENGTH_SHORT).show();
            } else if (result.equalsIgnoreCase("success")) {
                Toast.makeText(getApplicationContext(), "支付成功了",
                        Toast.LENGTH_SHORT).show();
            } else if (result.equalsIgnoreCase("wait")) {
                Toast.makeText(getApplicationContext(), "手机支付等待",
                        Toast.LENGTH_SHORT).show();
            } else if (result.equalsIgnoreCase(GlobalDefine.R_TRANSID_FAIL)) {
                Toast.makeText(getApplicationContext(), "订单获取失败",
                        Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(getApplicationContext(), "其他错误",
                        Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == 1001011) {
            this.finish();
        } else if (requestCode == 10) {
            String str = intent.getExtras().getString("pay_result");
            String msg = "";
            if (str.equalsIgnoreCase("success")) {
                msg = "支付成功";
            } else if (str.equalsIgnoreCase("fail")) {
                msg = "支付失败";
            } else if (str.equalsIgnoreCase("cancel")) {
                msg = "支付已被取消";
            }
            Toast.makeText(getApplicationContext(), msg,
                    Toast.LENGTH_SHORT).show();
        }else if(requestCode == JDPayActivity.JD_CODE)
        {
            if(resultCode == JDPayActivity.JD_CODE_SUCCESS)
            {
                Toast.makeText(getApplicationContext(), "支付成功",
                        Toast.LENGTH_SHORT).show();
            }
        }

    }

    /**
     * 融宝支付
     */
    public void payRb(String amount)
    {
        Intent intent = new Intent();
        intent.putExtra("amoney", amount);
        intent.setClass(ThirdPayActivity.this,
                RBPayActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 银生宝支付
     */
    public void doHttpYsb() {
        JsonBuilder builder = new JsonBuilder();

        if (SystenmApi.isNullOrBlank(bfBankid) && SystenmApi.isNullOrBlank(bfBanknum)) {

            builder.put("bank_id", bfBankid);
            builder.put("bank_num", bfBanknum);
        }

        builder.put("amount", user_amount.getText().toString());

        BaseHttpClient.post(getBaseContext(), Default.peopleinfoPay, builder,
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
                                MyLog.e("123", "服务端返回银行数据+" + json.toString());
                                if (json.getInt("status") == 1) {

                                    unpayItem = new UnpayItem(json);
                                    requestToTheUNPay();
                                } else {
                                    showCustomToast(json.getString("message"));

                                }

                            } else {
                                showCustomToast(R.string.toast1);
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
                        showCustomToast(responseString);
                    }

                });

    }

    /**
     * 银生宝安装插件
     */
    public void requestToTheUNPay() {

        String min = "mode=" + unpayItem.getMode() + "&merchantId="
                + unpayItem.getMerchantId() + "&merchantUrl="
                + unpayItem.getMerchantUrl() + "&responseMode="
                + unpayItem.getResponseMode() + "&orderId="
                + unpayItem.getOrderId() + "&amount=" + unpayItem.getAmount()
                + "&time=" + unpayItem.getTime() + "&remark=&merchantName="
                + "" + "&commodity=" + "" + "&mac=" + unpayItem.getMac();

        // new MD5().getMD5ofStr(unpayItem.getMac());

        CallUnsPay mcCallUnsPay = new CallUnsPay(ThirdPayActivity.this);
        if (!mcCallUnsPay.isApkInstalled()) {
            boolean isdunp = mcCallUnsPay.dumpApkFromAssets();
            if (isdunp) {
                mcCallUnsPay.InstallAPK();
            } else {
                // apk包不存在assets目录下
                Toast.makeText(ThirdPayActivity.this, "银生宝支付中心不存在。。。",
                        Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            Uri uri = Uri.parse("unspay://pay");
            Intent intent = new Intent("com.uns.pay.PAY", uri);
            intent.putExtra("para", min);
            startActivityForResult(intent, GlobalDefine.PAY);
        }

    }

    /**
     * 宝付认证支付
     */
    public void doHttpBaoFu_RZ() {
        JsonBuilder builder = new JsonBuilder();

        builder.put("amount", user_amount.getText().toString());

        BaseHttpClient.post(getBaseContext(), Default.peopleinfoPayBaofu, builder,
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
                        super.onSuccess(statusCode, headers, json);
                        dismissLoadingDialog();
                        try {

                            if (statusCode == 200) {
                                if (json.getInt("status") == 1) {

                                    Intent payintent = new Intent(ThirdPayActivity.this, BaofooPayActivity.class);
                                    payintent.putExtra(BaofooPayActivity.PAY_TOKEN, json.getString("tradeNo"));
                                    payintent.putExtra(BaofooPayActivity.PAY_BUSINESS, true);//true真是环境，false测试环境
                                    startActivityForResult(payintent, 1001011);
                                } else {
                                    if (json.has("message")) {
                                        showCustomToast(json.getString("message"));
                                    } else {
                                        SystenmApi.showCommonErrorDialog(ThirdPayActivity.this, json.getInt("status"), json.getString("is_jumpmsg"));
                                    }
                                }

                            } else {
                                showCustomToast(R.string.toast1);
                            }

                        } catch (Exception e) {
                            // TODO: handle exception
                        }
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
     * 宝付银联支付
     */
    public void doHttpBaoFu_YL() {
        JsonBuilder builder = new JsonBuilder();

        builder.put("amount", user_amount.getText().toString());

        BaseHttpClient.post(getBaseContext(), Default.peopleinfoPayBaofu2, builder,
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
                        super.onSuccess(statusCode, headers, json);
                        dismissLoadingDialog();
                        try {

                            if (statusCode == 200) {
                                if (json.getInt("status") == 1) {
                                    String orderNo = json.getString("tradeNo");

                                    int ret = UPPayAssistEx.startPay(ThirdPayActivity.this, null, null, orderNo, "00");
                                    // 如果支付插件未安装，则请求安装支付插件
                                    if (ret == UPPayAssistEx.PLUGIN_NOT_FOUND) {
                                        AlertDialog.Builder builer = new AlertDialog.Builder(
                                                ThirdPayActivity.this);
                                        builer.setTitle("安装提示");
                                        builer.setMessage("请先安装支付插件");

                                        builer.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (dialog instanceof AlertDialog) {
                                                    UPPayAssistEx.installUPPayPlugin(ThirdPayActivity.this);
                                                }
                                            }
                                        });
                                        builer.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (dialog instanceof AlertDialog) {
                                                }
                                            }
                                        });

                                        AlertDialog adlg = builer.create();
                                        adlg.show();
                                    }
                                } else {
                                    showCustomToast(json.getString("retMsg"));

                                }

                            } else {
                                showCustomToast(R.string.toast1);
                            }

                        } catch (Exception e) {
                            // TODO: handle exception
                        }
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
     * 京东支付
     */
    public void doHttpJD() {
        JsonBuilder builder = new JsonBuilder();

        builder.put("amount", user_amount.getText().toString());

        BaseHttpClient.post(getBaseContext(), Default.peopleinfoPayJD, builder,
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
                        super.onSuccess(statusCode, headers, json);
                        dismissLoadingDialog();
                        try {

                            if (statusCode == 200) {
                                if (json.getInt("status") == 1) {
                                    String url = json.getString("urlapp");

                                    Intent intent = new Intent(ThirdPayActivity.this, JDPayActivity.class);
                                    intent.putExtra("url",url);
                                    intent.putExtra("info",getParamsFromServer(json));
                                    startActivityForResult(intent, JDPayActivity.JD_CODE);

                                } else {
                                    showCustomToast(json.getString("message"));

                                }

                            } else {
                                showCustomToast(R.string.toast1);
                            }

                        } catch (Exception e) {
                            // TODO: handle exception
                        }
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                        super.onSuccess(statusCode, headers, response);
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

    private String getParamsFromServer(JSONObject json){


        StringBuffer paras = new StringBuffer();
        Iterator<String> iterator = json.keys();

        while (iterator.hasNext()) {


            try {
                String key = iterator.next();
                String value = json.getString(key);

                if(!key.equals("status")&&!key.equals("urlapp")){
                    value = URLEncoder.encode(value, "UTF-8");
                    paras.append(key).append("="+value).append("&");
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        paras.replace(paras.length() - 1, paras.length(), "");
        return paras.toString();
    }

}
