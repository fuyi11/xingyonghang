package com.lmq.main.activity.investmanager.lhb;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ScrollView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.xyh.R;
import com.lmq.http.BaseHttpClient;
import com.lmq.http.JsonHttpResponseHandler;
import com.lmq.main.activity.tools.LMQWebViewActivity;
import com.lmq.main.api.BaseActivity;
import com.lmq.main.api.JsonBuilder;
import com.lmq.main.api.MyLog;
import com.lmq.main.api.SystenmApi;
import com.lmq.main.dialog.LHBShDialog;
import com.lmq.main.enmu.LHBLogsType;
import com.lmq.main.util.Default;

import org.apache.http.Header;
import org.json.JSONObject;

public class LHBDetailActivity extends BaseActivity implements OnClickListener {


    private TextView lhb_detail_name;
    private TextView lhb_detail_sy;
    private TextView lhb_detail_ztje;
    private TextView lhb_detail_fcqx;
    private TextView lhb_detail_nhsy;
    private TextView lhb_detail_tzrq;
    private TextView lhb_detail_qxrq;
    private TextView lhb_detail_qjrq;
    private TextView lhb_sh_dec;
    private String batch_no;
    private String sh_time;

    private LHBShDialog shDialog;
    private PullToRefreshScrollView scrollView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lhb_detail_layout);

        Intent intent = getIntent();
        if (intent.hasExtra("batch")) {

            batch_no = intent.getStringExtra("batch");
        }

        TextView text = (TextView) findViewById(R.id.title);
        text.setText(R.string.lhb_detail_title);

        scrollView = (PullToRefreshScrollView) findViewById(R.id.mscrollView);
        scrollView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        scrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {

                getListDataFromSever();
            }
        });

        lhb_detail_name = (TextView) findViewById(R.id.lhb_detail_name);
        lhb_detail_sy = (TextView) findViewById(R.id.lhb_detail_sy);
        lhb_detail_ztje = (TextView) findViewById(R.id.lhb_detail_ztje);
        lhb_detail_fcqx = (TextView) findViewById(R.id.lhb_detail_fcqx);
        lhb_detail_nhsy = (TextView) findViewById(R.id.lhb_detail_nhsy);

        lhb_detail_tzrq = (TextView) findViewById(R.id.lhb_detail_tzrq);
        lhb_detail_qxrq = (TextView) findViewById(R.id.lhb_detail_qxrq);
        lhb_detail_qjrq = (TextView) findViewById(R.id.lhb_detail_qjrq);

        lhb_sh_dec = (TextView) findViewById(R.id.lhb_sh_dec);

        findViewById(R.id.back).setOnClickListener(this);
        findViewById(R.id.lhb_detail_sylogs).setOnClickListener(this);
        findViewById(R.id.lhb_detail_shlogs).setOnClickListener(this);
        findViewById(R.id.lhb_detail_shbtn).setOnClickListener(this);
        findViewById(R.id.lhb_detail_xy).setOnClickListener(this);


    }

    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.back:
                finish(0);
                break;
            case R.id.lhb_detail_sylogs:
                goDetailLogsActivity(LHBLogsType.SHOU_YI_LOGS_TYPE,batch_no);
                break;
            case R.id.lhb_detail_shlogs:
                goDetailLogsActivity(LHBLogsType.SHU_HUI_LOGS_TYPE,batch_no);
                break;
            case R.id.lhb_detail_shbtn:
                showSHDialog(v);
                //TODO 赎回功能
                break;
             case R.id.lhb_detail_xy:
                //TODO 赎回功能
                 Intent intent = new Intent(LHBDetailActivity.this,LMQWebViewActivity.class);
                 intent.putExtra("title","灵活宝投资协议");
                 intent.putExtra("url",Default.ip+"/member/agreement/flexible?id="+batch_no);
                 startActivity(intent);

                break;

            case R.id.dialog_cancle:
                shDialog.dismiss();
                break;
            case R.id.dialog_submit:

                if(SystenmApi.isNullOrBlank(shDialog.geteUserInput())){

                    showCustomToast("请输入赎回金额");
                    return;
                }
                shAction();
                break;


        }
    }




    private void showSHDialog(View parent){

        if(null == shDialog){
            shDialog = new LHBShDialog(LHBDetailActivity.this);
            shDialog.setShowMoney(lhb_detail_ztje.getText().toString());
            shDialog.setDialogTitle("提前赎回");
            shDialog.setonClickListener(this);
        }

        if(!shDialog.isShowing()){
            shDialog.showAsDropDown(parent);
        }













    }






























    private void goDetailLogsActivity(Enum<LHBLogsType> type,String bitch_nos) {

        Intent syIntente = new Intent();
        if(type == LHBLogsType.SHOU_YI_LOGS_TYPE){
            syIntente.putExtra("type", true);
        }else{
            syIntente.putExtra("type", false);
        }

        syIntente.putExtra("bitch_no",bitch_nos);
        syIntente.setClass(LHBDetailActivity.this, LHBDetailLogsActivity.class);
        startActivity(syIntente);
    }


    @Override
    protected void onResume() {
        super.onResume();
        getListDataFromSever();
    }

    private void getListDataFromSever() {

        JsonBuilder builder = new JsonBuilder();
        builder.put("batch", batch_no);
        //TODO 请求灵活宝服务器信息

        BaseHttpClient.post(getBaseContext(), Default.iteminfo, builder,
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


                                    MyLog.e("获取灵活宝相信信息", "" + json.toString());

                                    updataViewInfo(json);
                                } else {
                                    String message = json.getString("message");
                                    SystenmApi.showCommonErrorDialog(LHBDetailActivity.this, json.getInt("status"),message);
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
     * 更新显示
     */
    private void updataViewInfo(JSONObject json) {


        /***
         * interest_rate   年化收益
         batch_no   项目编号
         money    在投金额
         term   封存期限
         start_funds   部分赎回后剩余金额不得小于   （这句话后面的字段）
         deadline      号 还本付息（这句话前面的字段）
         archive_time  可随时一次性或部分赎回本息(这句话前面字段)
         add_time   投资日期
         e_time   起息日期
         fenpei_style   收益复投类型
         record_money   已赚收益
         bao_status   赎回状态
         */

        if (json.has("batch_no")) {

            lhb_detail_name.setText(json.optString("batch_no", ""));
        }

        if (json.has("money")) {

            lhb_detail_ztje.setText(json.optString("money", ""));
        }

        if (json.has("term")) {

            lhb_detail_fcqx.setText(json.optString("term", "") + "天");
        }
        if (json.has("deadline")) {

            lhb_detail_qjrq.setText(json.optString("deadline", ""));
        }


        if (json.has("add_time")) {

            lhb_detail_tzrq.setText(json.optString("add_time", ""));
        }
        if (json.has("e_time")) {

            lhb_detail_qxrq.setText(json.optString("e_time", ""));
        }
        if (json.has("record_money")) {

            lhb_detail_sy.setText(json.optString("record_money", ""));
        }

        if (json.has("interest_rate")) {

            lhb_detail_nhsy.setText(json.optString("interest_rate", "") + "%");
        }

        if(json.has("archive_time")){
            sh_time = json.optString("archive_time","");
        }
        if(json.has("tip")){
            lhb_sh_dec.setText(json.optString("tip", ""));

        }

        scrollView.onRefreshComplete();

    }


    /**
     * 赎回接口
     */
    private void shAction() {

        JsonBuilder builder = new JsonBuilder();
        builder.put("batch", batch_no);
        builder.put("fredeemamount", shDialog.geteUserInput());
        //TODO 请求灵活宝服务器信息

        BaseHttpClient.post(getBaseContext(), Default.redeemSave, builder,
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


                                } else {
                                    String message = json.getString("message");
                                    SystenmApi.showCommonErrorDialog(LHBDetailActivity.this, json.getInt("status"),message);
                                }
                            } else {
                                showCustomToast(R.string.toast1);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        dismissLoadingDialog();
                        scrollView.onRefreshComplete();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          String responseString, Throwable throwable) {
                        // TODO Auto-generated method stub
                        super.onFailure(statusCode, headers, responseString,
                                throwable);
                        dismissLoadingDialog();
                        showCustomToast(responseString);
                        scrollView.onRefreshComplete();

                    }

                });


    }

    public void finish() {
        super.finish();
    }

}
