package com.lmq.main.activity.investmanager.auto;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.bigkoo.pickerview.TimePopupWindow;
import com.xyh.R;
import com.lmq.http.BaseHttpClient;
import com.lmq.http.JsonHttpResponseHandler;
import com.lmq.main.api.BaseActivity;
import com.lmq.main.api.JsonBuilder;
import com.lmq.main.api.MyLog;
import com.lmq.main.api.SystenmApi;
import com.lmq.main.dialog.InvestPopList;
import com.lmq.main.util.Default;

import org.apache.http.Header;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

public class AutoTenderActivity extends BaseActivity implements OnClickListener {


	/**最大投资金额*/
    private EditText invest_money;
    /**最小投资金额*/
    private EditText min_invest;
    /**年化利率*/
    private EditText interest_rate;
    /**借款期限开始月*/
    private EditText duration_from;
    /**借款期限结束月*/
    private EditText duration_to;
    /**账户保留余额*/
    private EditText account_money;
    /**结束时间*/
    private TextView end_time;
    private CheckBox check_box_rate;
    private CheckBox check_box_date;
    private CheckBox check_box_tianbiao;
    private CheckBox check_box_cancledate;
    private CheckBox check_box_account;

    private ToggleButton is_use_auto;

    private TimePopupWindow end_time_pop;



    private int type  = 1; // 1 普通标  6 企业直投 7 定投宝

    private InvestPopList popList;

    private HashMap<String,String> retMap;
    private TextView text;

    private ImageView mtriangle;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auto_tender_layout);


        mtriangle = (ImageView) findViewById(R.id.triangle);
        mtriangle.setImageResource(R.drawable.wite_arrow_down);
        text = (TextView) findViewById(R.id.title);
        text.setText("企业直投");

        text.setOnClickListener(this);

        popList =  new InvestPopList(AutoTenderActivity.this);




        popList.addItems(getResources().getStringArray(R.array.auto_tender));
        popList.setDefauleSelect(0);
        popList.setOnItemClickLinstener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (popList.getDefauleSelect() != position) {
                    popList.setDefauleSelect(position);

                    Animation animation = AnimationUtils.loadAnimation(AutoTenderActivity.this, R.anim.triangle_totate2);
                    mtriangle.clearAnimation();
                    mtriangle.setImageResource(R.drawable.wite_arrow_up);
                    mtriangle.startAnimation(animation);
                    animation.setFillAfter(true);


                    switch (position) {

                        case 0:
                            type = 6;
                            text.setText("企业直投");
                            break;

                    }

                    getAutolong();
                    popList.dissmiss();
                }


            }
        });

        invest_money = (EditText) findViewById(R.id.invest_money);
        min_invest = (EditText) findViewById(R.id.min_invest);
        interest_rate = (EditText) findViewById(R.id.interest_rate);
        duration_from = (EditText) findViewById(R.id.duration_from);
        duration_to = (EditText) findViewById(R.id.duration_to);
        account_money = (EditText) findViewById(R.id.account_money);
        end_time = (TextView) findViewById(R.id.end_time);

        check_box_rate = (CheckBox) findViewById(R.id.check_box_rate);
        check_box_date = (CheckBox) findViewById(R.id.check_box_date);
        check_box_tianbiao = (CheckBox) findViewById(R.id.check_box_tianbiao);
        check_box_cancledate = (CheckBox) findViewById(R.id.check_box_cancledate);
        check_box_account = (CheckBox) findViewById(R.id.check_box_account);
        is_use_auto = (ToggleButton) findViewById(R.id.is_use_auto);


        end_time_pop = new TimePopupWindow(this, TimePopupWindow.Type.YEAR_MONTH_DAY);
        end_time_pop.setTime(new Date());

        end_time_pop.setOnTimeSelectListener(new TimePopupWindow.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date) {

                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

                end_time.setText(df.format(date));

            }
        });

        end_time_pop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {



            }
        });

        findViewById(R.id.end_time).setOnClickListener(this);

        findViewById(R.id.back).setOnClickListener(this);
        findViewById(R.id.submit).setOnClickListener(this);


    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish(0);
                break;
            case R.id.end_time:
               if(!end_time_pop.isShowing()){
                   end_time_pop.showAtLocation(v, Gravity.BOTTOM,0,0);
               }
                break;
            case R.id.title:

                if(!popList.isShowing()){
                    Animation animation = AnimationUtils.loadAnimation(AutoTenderActivity.this, R.anim.triangle_totate);
                    mtriangle.clearAnimation();
                    mtriangle.setImageResource(R.drawable.wite_arrow_down);
                    mtriangle.startAnimation(animation);
                    animation.setFillAfter(true);

                    popList.showPOpList(v);
                }
                break;
            case R.id.submit:
                checkoutUserinput();
                if(null != retMap){
                    uplaodAutolong();
                }
            	
            	
                break;


        }
    }







    @Override
    protected void onResume() {
        super.onResume();
        getAutolong();
    }

    // 提交用户反馈到服务器
    public void getAutolong() {
        JsonBuilder builder = new JsonBuilder();
        builder.put("type", type);



        BaseHttpClient.post(getBaseContext(), Default.autolong, builder,
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

                            MyLog.e("获取数据", json.toString());
                            if (statusCode == 200) {
                                // 没有新版本
                                if (json.getInt("status") == 1) {

                                    if (json.has("invest_money")) {
                                        invest_money.setText(json.optString("invest_money", ""));
                                    }

                                    if (json.has("min_invest")) {
                                        min_invest.setText(json.optString("min_invest", ""));
                                    }

                                    if (json.has("is_interest_rate")) {
                                        check_box_rate.setChecked(json.optInt("is_interest_rate", 0) == 0 ? false : true);
                                    }
                                    if (json.has("interest_rate")) {
                                        interest_rate.setText(json.optString("interest_rate", ""));
                                    }

                                    if (json.has("is_duration_from")) {
                                        check_box_date.setChecked(json.optInt("is_duration_from", 0) == 0 ? false : true);
                                    }

                                    if (json.has("duration_from")) {
                                        duration_from.setText(json.optString("duration_from", ""));
                                    }
                                    if (json.has("duration_to")) {
                                        duration_to.setText(json.optString("duration_to", ""));
                                    }

                                    if (json.has("is_auto_day")) {
                                        check_box_tianbiao.setChecked(json.optInt("is_auto_day", 0) == 0 ? false : true);
                                    }

                                    if (json.has("is_account_money")) {
                                        check_box_account.setChecked(json.optInt("is_account_money", 0) == 0 ? false : true);
                                    }

                                    if (json.has("account_money")) {

                                        account_money.setText(json.optString("account_money", ""));
                                    }

                                    if (json.has("is_end_time")) {
                                        check_box_cancledate.setChecked(json.optInt("is_end_time", 0) == 0 ? false : true);
                                    }
                                    if (json.has("end_time")) {

                                        end_time.setText(json.optString("end_time", ""));
                                    }

                                    if (json.has("is_use")) {
                                        is_use_auto.setChecked(json.optInt("is_use", 0) == 0 ? false : true);
                                    }

                                    if(json.has("message")){
                                        showCustomToast(json.getString("message"));
                                    }


                                } else {
                                    String message = json.getString("message");
                                    SystenmApi.showCommonErrorDialog(AutoTenderActivity.this, json.getInt("status"),message);
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



    private void checkoutUserinput(){

        /**开启自动投标*/
        if(is_use_auto.isChecked()){

            retMap = new HashMap<String, String>();

            if(SystenmApi.isNullOrBlank(invest_money.getText().toString())){
                showCustomToast("请输入最大投资金额");

                return;
            }

            if(SystenmApi.isNullOrBlank(min_invest.getText().toString())){
                showCustomToast("请输入最小投资金额");
                return;
            }

            if(check_box_rate.isChecked()){
                if(SystenmApi.isNullOrBlank(interest_rate.getText().toString())){

                    showCustomToast("请输入年化利率");
                    return;
                }

            }

            if(check_box_date.isChecked()){
                if(SystenmApi.isNullOrBlank(duration_from.getText().toString())){

                    showCustomToast("请输入借款起始月份");
                    return;
                }

                if(SystenmApi.isNullOrBlank(duration_to.getText().toString())){

                    showCustomToast("请输入借款结束月份");
                    return;
                }

            }

            if(check_box_tianbiao.isChecked()){

                if(SystenmApi.isNullOrBlank(account_money.getText().toString())){


                    showCustomToast("请输入账户保留金额");
                    return;

                }
            }

            if(check_box_cancledate.isChecked()){


                if(SystenmApi.isNullOrBlank(end_time.getText().toString())){

                    showCustomToast("请输入自动投标终止日期");
                    return;

                }

            }

            retMap.put("type",type+"");
            retMap.put("invest_money",invest_money.getText().toString());
            retMap.put("min_invest",min_invest.getText().toString());
            retMap.put("is_interest_rate",check_box_rate.isChecked()?"1":"0");
            retMap.put("interest_rate",interest_rate.getText().toString());
            retMap.put("is_duration_from",check_box_date.isChecked()?"1":"0");
            retMap.put("duration_from",duration_from.getText().toString());
            retMap.put("duration_to",duration_to.getText().toString());
            retMap.put("is_auto_day",check_box_tianbiao.isChecked()?"1":"0");
            retMap.put("is_account_money",check_box_account.isChecked()?"1":"0");
            retMap.put("account_money",account_money.getText().toString());
            retMap.put("is_end_time",check_box_cancledate.isChecked()?"1":"0");
            retMap.put("end_time",end_time.getText().toString());
            retMap.put("is_use","1");









        }else{

            /**未开启自动投标*/

            retMap = new HashMap<String, String>();



            retMap.put("type",type+"");
            retMap.put("invest_money",invest_money.getText().toString());
            retMap.put("min_invest",min_invest.getText().toString());
            retMap.put("is_interest_rate",check_box_rate.isChecked()?"1":"0");
            retMap.put("interest_rate",interest_rate.getText().toString());
            retMap.put("is_duration_from",check_box_date.isChecked()?"1":"0");
            retMap.put("duration_from",duration_from.getText().toString());
            retMap.put("duration_to",duration_to.getText().toString());
            retMap.put("is_auto_day",check_box_tianbiao.isChecked()?"1":"0");
            retMap.put("is_account_money",check_box_account.isChecked()?"1":"0");
            retMap.put("account_money",account_money.getText().toString());
            retMap.put("is_end_time",check_box_cancledate.isChecked()?"1":"0");
            retMap.put("end_time",end_time.getText().toString());
            retMap.put("is_use","0");


        }

















    }



























    public void uplaodAutolong() {




        JsonBuilder builder = new JsonBuilder();

        Iterator<String> iterator = retMap.keySet().iterator();

        while (iterator.hasNext()){

            String key = iterator.next();
            String value  = retMap.get(key);

            builder.put(key,value);


        }



        BaseHttpClient.post(getBaseContext(), Default.savelong, builder,
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

                            MyLog.e("获取数据", json.toString());
                            if (statusCode == 200) {
                                // 没有新版本
                                if (json.getInt("status") == 1) {
                                    showCustomToast(json.getString("message"));

                                } else {
                                    String message = json.getString("message");
                                    SystenmApi.showCommonErrorDialog(AutoTenderActivity.this, json.getInt("status"),message);
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
