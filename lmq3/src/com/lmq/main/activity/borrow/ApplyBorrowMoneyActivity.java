package com.lmq.main.activity.borrow;

import java.util.ArrayList;

import org.apache.http.Header;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPopupWindow;
import com.xyh.R;
import com.lmq.http.BaseHttpClient;
import com.lmq.http.JsonHttpResponseHandler;
import com.lmq.main.api.BaseActivity;
import com.lmq.main.api.JsonBuilder;
import com.lmq.main.api.SystenmApi;
import com.lmq.main.enmu.BorrowButtonType;
import com.lmq.main.util.Default;


public class ApplyBorrowMoneyActivity extends BaseActivity implements OnClickListener {

    private String kindStr;
    private  int kind;

    private boolean processFlag = true; //默认可以点击


    private EditText title_teTextView = null;
    private EditText amount_Editor = null;
    private EditText ed_money = null;
    private EditText nlv = null;
    private EditText ed_tb_reward = null;
    private EditText ed_dxb = null;
    private EditText ed_tbxzje = null;
    private EditText ed_introduce = null;
    private EditText tv_borrow_name = null;

    private Button submit_lendmoney;

    private BorrowButtonType type;

    private ArrayList<String> list_jkyt, list_jkqx, list_minitz, list_maxtz, list_yxsj, list_hkfs,list_jllx;

    private TextView tv_yx_time,tv_jkyt,tv_jkqx,tv_little_money,tv_more_money,huankauntype,tv_jllx,tv_dxb,tv_tbxz,tbjl;

    private View redaw_view,dx_view,xz_view;
    private boolean has_redaw,has_dx,has_xz;
    private OptionsPopupWindow pop;
    private int selectid;
    private String selid;

    // 奖励金额
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.borrow_money);

        Intent intent = getIntent();
        if (intent.hasExtra("type")) {

            kind = intent.getIntExtra("type",-1);

             //showCustomToast(kind+"");

        }

        findViewById(R.id.back).setOnClickListener(this);
        TextView title = (TextView) findViewById(R.id.title);
        title.setText("我要借款");
        redaw_view = findViewById(R.id.redaw_view);
        dx_view = findViewById(R.id.dx_view);
        xz_view = findViewById(R.id.xz_view);
        redaw_view.setVisibility(View.GONE);
        dx_view.setVisibility(View.GONE);
        xz_view.setVisibility(View.GONE);

        tv_dxb = (TextView) findViewById(R.id.tv_dxb);
        tv_tbxz = (TextView) findViewById(R.id.tv_tbxz);
        tbjl = (TextView) findViewById(R.id.tbjl);



        ed_money = (EditText) findViewById(R.id.ed_money);
        nlv = (EditText) findViewById(R.id.ed_nlv);
        ed_tb_reward = (EditText) findViewById(R.id.ed_tb_reward);
        ed_dxb = (EditText) findViewById(R.id.ed_dxb);
        ed_introduce = (EditText) findViewById(R.id.ed_introduce);
        ed_tbxzje = (EditText) findViewById(R.id.ed_tbxzje);
        tv_borrow_name = (EditText) findViewById(R.id.ed_tv_borrow_name);


        pop = new OptionsPopupWindow(ApplyBorrowMoneyActivity.this);
        pop.setOnoptionsSelectListener(new OptionsPopupWindow.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {

                switch (type){


                    case BUTTON_TYPE_JKYT:

                        tv_jkyt.setText(list_jkyt.get(options1));


                        break;

                    case BUTTON_TYPE_JKQX:

                       tv_jkqx.setText(list_jkqx.get(options1));


                        break;


                    case BUTTON_TYPE_MINITZ:

                     tv_little_money.setText(list_minitz.get(options1));


                        break;

                    case BUTTON_TYPE_MAXTZ:

                        tv_more_money.setText(list_maxtz.get(options1));


                        break;

                    case BUTTON_TYPE_YXSJ:

                        tv_yx_time.setText(list_yxsj.get(options1));


                        break;


                    case BUTTON_TYPE_HKFS:

                        huankauntype.setText(list_hkfs.get(options1));
                        selectid = options1;

                        if (selectid == 0){
                            selid ="1";

                        }else if (selectid == 1){
                            selid ="2";
                        }else if (selectid == 2){
                            selid ="4";
                        }else if (selectid == 3){
                            selid ="5";
                        }

                        inithkqx();


                        break;
                    case BUTTON_TYPE_JLLX:

                        tv_jllx.setText(list_jllx.get(options1));


                        break;


                }

            }
        });

        findViewById(R.id.rl_jkyt).setOnClickListener(this);
        findViewById(R.id.rl_jkqx).setOnClickListener(this);
        findViewById(R.id.rl_little_money).setOnClickListener(this);
        findViewById(R.id.rl_more_money).setOnClickListener(this);
        findViewById(R.id.rl_yx_time).setOnClickListener(this);
        findViewById(R.id.rl_huankauntype).setOnClickListener(this);
        findViewById(R.id.rl_tbjl).setOnClickListener(this);
        findViewById(R.id.rl_jllx).setOnClickListener(this);
        findViewById(R.id.rl_dxb).setOnClickListener(this);
        findViewById(R.id.rl_tbxz).setOnClickListener(this);
        findViewById(R.id.back).setOnClickListener(this);
        submit_lendmoney = (Button) findViewById(R.id.submit_lendmoney);
        submit_lendmoney.setOnClickListener(this);

        list_jkyt = new ArrayList<String>();
        list_jkqx = new ArrayList<String>();
        list_minitz = new ArrayList<String>();
        list_maxtz = new ArrayList<String>();
        list_yxsj = new ArrayList<String>();
        list_hkfs = new ArrayList<String>();
        list_jllx = new ArrayList<String>();

        tv_yx_time = (TextView) findViewById(R.id.tv_yx_time);
        tv_jkyt = (TextView) findViewById(R.id.tv_jkyt);
        tv_jkqx = (TextView) findViewById(R.id.tv_jkqx);
        tv_little_money = (TextView) findViewById(R.id.tv_little_money);
        tv_more_money = (TextView) findViewById(R.id.tv_more_money);
        huankauntype = (TextView) findViewById(R.id.huankauntype);
        tv_jllx = (TextView) findViewById(R.id.tv_jllx);
        initData();
        initViews();

    }


    private int getIndexWithObject(ArrayList<String> list,String object){





        int rtnInt = 0;
        for(int i=0;i<list.size();i++){

            if(object.equals(list.get(i))){

                rtnInt = i;
               break;
            }

        }





        return rtnInt;






    }


    // 提交用户反馈到服务器
    public void submitAction() {

        JsonBuilder builder = new JsonBuilder();

        builder.put("kind",kind);
        builder.put("interest_rate",nlv.getText().toString());
        builder.put("amount",ed_money.getText().toString());
        builder.put("borrow_use",getIndexWithObject(list_jkyt,tv_jkyt.getText().toString()) + 1);
        builder.put("borrow_duration", getIndexWithObject(list_jkqx,tv_jkqx.getText().toString()) + 1);
        builder.put("borrow_min",tv_little_money.getText().toString());
        builder.put("borrow_max",tv_more_money.getText().toString());
        builder.put("borrow_time", getIndexWithObject(list_yxsj,tv_yx_time.getText().toString())+1);


       // builder.put("repay_kind",getIndexWithObject(list_hkfs,huankauntype.getText().toString())+1);

        builder.put("repay_kind", selid);
        builder.put("is_targeting", has_dx ? 1 : 0);
        builder.put("targeting_pass",ed_dxb.getText().toString());
        builder.put("reward_kind", has_redaw ? 1 : 0);
        builder.put("reward_num",ed_tb_reward.getText().toString());
        builder.put("is_moneycollect", has_xz ? 1 : 0);
        builder.put("moneycollect",ed_tbxzje.getText().toString());
        builder.put("name",tv_borrow_name.getText().toString());
        builder.put("borrow_info",ed_introduce.getText().toString());



        BaseHttpClient.post(getBaseContext(), Default.request_credit, builder
                ,
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
                                    String message = json.getString("message");
                                    SystenmApi.showCommonErrorDialog(ApplyBorrowMoneyActivity.this, json.getInt("status"),message);
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

    private void initData() {

        list_jkyt.clear();
        list_jkyt.addAll(arrayToList(getResources().getStringArray(R.array.jkyt)));

        list_jkqx.clear();
        list_jkqx.addAll(arrayToList(getResources().getStringArray(R.array.jkqx)));

        list_minitz.clear();
        list_minitz.addAll(arrayToList(getResources().getStringArray(R.array.minitz)));

        list_maxtz.clear();
        list_maxtz.addAll(arrayToList(getResources().getStringArray(R.array.maxtz)));

        list_yxsj.clear();
        list_yxsj.addAll(arrayToList(getResources().getStringArray(R.array.yxsj)));

        list_hkfs.clear();
        list_hkfs.addAll(arrayToList(getResources().getStringArray(R.array.hkfs)));

        list_jllx.clear();
        list_jllx.addAll(arrayToList(getResources().getStringArray(R.array.jllx)));

    }

    private void inithkqx() {
        list_jkqx.clear();
        if(selectid == 0){
            list_jkqx.addAll(arrayToList(getResources().getStringArray(R.array.htjkqx)));
        }else {
            list_jkqx.addAll(arrayToList(getResources().getStringArray(R.array.jkqx)));
        }
    }


    private ArrayList<String> arrayToList(String[] array) {

            ArrayList<String> data = new ArrayList<String>();


        for (int i = 0; i < array.length; i++) {
            data.add(array[i]);

        }

        return data;




    }


    protected void initViews() {
        // TODO Auto-generated method stub

//		direct_flag_btn = (ToggleButton) findViewById(R.id.direct_flg);
//		other_tips = (TextView) findViewById(R.id.other_tips);
//		title_teTextView = (EditText) findViewById(R.id.l_title);
//		amount_Editor = (EditText) findViewById(R.id.l_amount);

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
                sleep(10000);
                submit_lendmoney.setEnabled(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }




    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub

        switch (arg0.getId()) {
            case R.id.submit_lendmoney:
               String check =  checkUserInput();
                if(!SystenmApi.isNullOrBlank(check)){
                    submit_lendmoney.setEnabled(false);
                    submitAction();
                    new TimeThread().start();
                }

                break;

            case R.id.rl_jkyt:



                showActionSheet(BorrowButtonType.BUTTON_TYPE_JKYT, arg0);

                break;
            case R.id.rl_jkqx:
                showActionSheet(BorrowButtonType.BUTTON_TYPE_JKQX, arg0);
                break;
            case R.id.rl_little_money:
                showActionSheet(BorrowButtonType.BUTTON_TYPE_MINITZ, arg0);
                break;
            case R.id.rl_more_money:
                showActionSheet(BorrowButtonType.BUTTON_TYPE_MAXTZ, arg0);
                break;
            case R.id.rl_yx_time:
                showActionSheet(BorrowButtonType.BUTTON_TYPE_YXSJ, arg0);
                break;
            case R.id.rl_huankauntype:
                showActionSheet(BorrowButtonType.BUTTON_TYPE_HKFS, arg0);
                break;

            //点击
            case R.id.rl_tbjl:
                if(redaw_view.getVisibility() == View.GONE){
                    has_redaw = true;
                    redaw_view.setVisibility(View.VISIBLE);
                    tbjl.setText("是");
                }else {
                    redaw_view.setVisibility(View.GONE);
                    has_redaw = false;
                    tbjl.setText("否");
                }
                break;
            case R.id.rl_jllx:
                showActionSheet(BorrowButtonType.BUTTON_TYPE_JLLX, arg0);
                break;
            case R.id.rl_dxb:
                if(dx_view.getVisibility() == View.GONE){
                    has_dx = true;
                    dx_view.setVisibility(View.VISIBLE);
                    tv_dxb.setText("是");
                }else {
                    dx_view.setVisibility(View.GONE);
                    has_dx = false;
                    tv_dxb.setText("否");
                }
                break;
            case R.id.rl_tbxz:
                if(xz_view.getVisibility() == View.GONE){
                    has_xz =true;
                    xz_view.setVisibility(View.VISIBLE);
                    tv_tbxz.setText("是");
                }else {
                    xz_view.setVisibility(View.GONE);
                    has_xz =false;
                    tv_tbxz.setText("否");
                }
                break;

            case R.id.back:
                finish();
                break;
            default:
                break;
        }

    }


    private void showActionSheet(BorrowButtonType type, final View parent) {


        this.type = type;

        switch (this.type) {


            case BUTTON_TYPE_JKYT:

                pop.setTitle("借款用途");
                pop.setPicker(list_jkyt);


                break;

            case BUTTON_TYPE_JKQX:

                pop.setTitle("借款期限");
                pop.setPicker(list_jkqx);


                break;


            case BUTTON_TYPE_MINITZ:

                pop.setTitle("最低投标金额");
                pop.setPicker(list_minitz);


                break;

            case BUTTON_TYPE_MAXTZ:

                pop.setTitle("最多投标金额");
                pop.setPicker(list_maxtz);


                break;

            case BUTTON_TYPE_YXSJ:

                pop.setTitle("有效时间");
                pop.setPicker(list_yxsj);


                break;


            case BUTTON_TYPE_HKFS:

                pop.setTitle("还款方式");
                pop.setPicker(list_hkfs);


                break;

            case BUTTON_TYPE_JLLX:

                pop.setTitle("奖励类型");
                pop.setPicker(list_jllx);


                break;


        }
        pop.showAtLocation(parent, Gravity.BOTTOM,0,0);
    }

    private String checkUserInput(){

        if(SystenmApi.isNullOrBlank(ed_money.getText().toString())){

            showCustomToast("请输入借款金额");
            return null;
        }

        if(SystenmApi.isNullOrBlank(nlv.getText().toString())){

            showCustomToast("请输入年利率");
            return null;
        }

        if(SystenmApi.isNullOrBlank(tv_jkyt.getText().toString())){

            showCustomToast("请输入借款用途");
            return null;
        }

        if(SystenmApi.isNullOrBlank(tv_jkqx.getText().toString())){

            showCustomToast("请输入借款期限");
            return null;
        }

        if(SystenmApi.isNullOrBlank(tv_little_money.getText().toString())){

            showCustomToast("请输入最小投资金额");
            return null;
        }

        if(SystenmApi.isNullOrBlank(tv_more_money.getText().toString())){

            showCustomToast("请输入最大投资金额");
            return null;
        }

        if(SystenmApi.isNullOrBlank(tv_yx_time.getText().toString())){

            showCustomToast("请输入有效时间");
            return null;
        }

        if(SystenmApi.isNullOrBlank(huankauntype.getText().toString())){

            showCustomToast("请输入还款方式");
            return null;
        }

        if(has_redaw){


            if(SystenmApi.isNullOrBlank(tv_jllx.getText().toString())){

                showCustomToast("请输入奖励类型");
                return null;
            }

            if(SystenmApi.isNullOrBlank(ed_tb_reward.getText().toString())){

                showCustomToast("请输入奖励比例");
                return null;
            }

        }



        if(has_dx){
            if(SystenmApi.isNullOrBlank(ed_dxb.getText().toString())){

                showCustomToast("请输入定向标密码");
                return null;
            }


        }


        if(has_xz){

            if(SystenmApi.isNullOrBlank(ed_tbxzje.getText().toString())){

                showCustomToast("请输入投标限制金额");
                return null;
            }

        }


        if(SystenmApi.isNullOrBlank(tv_borrow_name.getText().toString())){

            showCustomToast("请输入借款名称");
            return null;
        }


        return "success";

    }



}
