/**
 *
 */
package com.lmq.main.activity.user.info;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.xyh.R;
import com.lmq.gesture.CreateGesturePasswordActivity;
import com.lmq.http.BaseHttpClient;
import com.lmq.http.JsonHttpResponseHandler;
import com.lmq.main.activity.Yb_InfoActivity;
import com.lmq.main.activity.person.companyinfo.CompanyInfoActivity;
import com.lmq.main.activity.person.personinfo.PersoninfoActivity;
import com.lmq.main.activity.user.manager.bankinfo.AddBankCardActivity;
import com.lmq.main.activity.user.manager.bankinfo.ShowBankCardInfoActivity;
import com.lmq.main.activity.user.manager.email.ApplyUserEmailActivity;
import com.lmq.main.activity.user.manager.idcard.PeopleInfoSmrz;
import com.lmq.main.activity.user.manager.password.ChangeLoginPasswordActivity;
import com.lmq.main.activity.user.manager.password.ChangeTenderPasswordActivity;
import com.lmq.main.activity.user.manager.phone.ModifyUserApplyPhoneActivity;
import com.lmq.main.activity.user.manager.phone.RevisePhoneActivity;
import com.lmq.main.api.BaseActivity;
import com.lmq.main.api.JsonBuilder;
import com.lmq.main.api.MyLog;
import com.lmq.main.api.SystenmApi;
import com.lmq.main.dialog.ApplyPersonOrEnterpriseUserDialog;
import com.lmq.main.dialog.CommonDialog;
import com.lmq.main.util.Default;
import com.lmq.pay.MoneyMoreMoreSqActivity;
import com.lmq.ybpay.YBPayActivity;
import com.lmq.zftpay.ZFTRevisePhoneActivity;
import com.lmq.zftpay.ZFT_InfoActivity;

import org.apache.http.Header;
import org.json.JSONObject;


/**
 * 账户信息
 *
 * @author zzx
 */

public class UserInfoDeatilActivity extends BaseActivity implements
        OnClickListener {

    private TextView bangding;
    /**
     * 易宝修改手机号码
     */
    private RelativeLayout xgsjhm;

    private ApplyPersonOrEnterpriseUserDialog dialog;
    private TextView info[];


    /**
     * 是否开启手机验证1未开启 0开启
     */
    private int is_manual;
    /**
     * 实名认证 状态 姓名 身份证号
     */
    private int real_status;
    private String realName;
    private String realId;
    /**
     * 手机认证状态 手机号
     */
    private int phone_status;
    private String phone;
    /**
     * 邮箱 认证状态 邮箱
     */
    private int email_status;
    private String email;
    /**
     * 银行卡 认证状态 银行卡
     */
    private int card_status;
    private String card;

    /**
     * 绑定托管 认证状态
     */
    private int mmm_status;
    private int sq1_status;
    private int sq2_status;

    /**
     * 绑定状态
     */
    private int bind_status = -1;

    /**
     * 企业或者个人账号
     * <p/>
     * is_transfer 借款类型1企业会员  2个人会员  0未申请
     */

    private int is_transfer = -1;
    private TextView tv_usrename;
    private RelativeLayout rl_zft_bind;
    private  int pin_pass;

    private PullToRefreshScrollView refreshView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.people_info_zhanghu);

        xgsjhm = (RelativeLayout) findViewById(R.id.xgsjhm);
        xgsjhm.setOnClickListener(this);
		if(Default.IS_YB){
			findViewById(R.id.rl_yhkh).setVisibility(View.GONE);
			findViewById(R.id.rl_isTG).setVisibility(View.VISIBLE);
            findViewById(R.id.rl_phone).setVisibility(View.GONE);
            findViewById(R.id.rl_jymm).setVisibility(View.GONE);
			xgsjhm.setVisibility(View.VISIBLE);
		}else if (Default.IS_ZFT){
            rl_zft_bind = (RelativeLayout) findViewById(R.id.rl_zft_bind);
            rl_zft_bind.setOnClickListener(this);
            rl_zft_bind.setVisibility(View.VISIBLE);

            findViewById(R.id.rl_isTG).setVisibility(View.GONE);
            xgsjhm.setVisibility(View.GONE);
        }else if (Default.IS_Qdd){
            findViewById(R.id.rl_yhkh).setVisibility(View.VISIBLE);
            findViewById(R.id.rl_isTG).setVisibility(View.VISIBLE);
//            findViewById(R.id.rl_phone).setVisibility(View.GONE);
            findViewById(R.id.rl_jymm).setVisibility(View.GONE);
            xgsjhm.setVisibility(View.GONE);


		}else {
            findViewById(R.id.rl_yhkh).setVisibility(View.VISIBLE);
            findViewById(R.id.rl_isTG).setVisibility(View.GONE);
            xgsjhm.setVisibility(View.GONE);


        }
		bangding = (TextView) findViewById(R.id.bangding);

        initView();
    }

    protected void onResume() {
        super.onResume();
        doHttp();
//        if(Default.IS_YB && Default.userId!=0 || Default.IS_ZFT){
//            dohttpCheckYb();
//        }


    }

    public void initView() {
        findViewById(R.id.rl_smrz).setOnClickListener(this);
        findViewById(R.id.rl_phone).setOnClickListener(this);
        findViewById(R.id.rl_email).setOnClickListener(this);
        findViewById(R.id.rl_yhkh).setOnClickListener(this);
        findViewById(R.id.rl_dlmm).setOnClickListener(this);
        findViewById(R.id.rl_jymm).setOnClickListener(this);
        findViewById(R.id.rl_xgssmm).setOnClickListener(this);
        findViewById(R.id.rl_bind).setOnClickListener(this);
        findViewById(R.id.back).setOnClickListener(this);
        findViewById(R.id.rl_isTG).setOnClickListener(this);

        tv_usrename = (TextView) findViewById(R.id.tv_usrename);


        if (Default.NEW_VERSION && Default.USE_YBPAY) {

            findViewById(R.id.rl_jymm).setVisibility(View.GONE);
        }


        TextView text = (TextView) findViewById(R.id.title);
        text.setText(R.string.peo_info2);

        info = new TextView[4];
        info[0] = (TextView) findViewById(R.id.tv_smrz);
        info[1] = (TextView) findViewById(R.id.tv_phone);
        info[2] = (TextView) findViewById(R.id.tv_email);
        info[3] = (TextView) findViewById(R.id.tv_yhk);

        refreshView = (PullToRefreshScrollView) findViewById(R.id.refreshView);
        refreshView.setOnRefreshListener(new OnRefreshListener<ScrollView>() {

            @Override
            public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
                // TODO Auto-generated method stub
                doHttp();

            }
        });
    }

    // 接受之前界面返回的数据
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
    }

    private String getStatus(int status) {
        switch (status) {
            case 0:
                return "未验证";
            case 3:
                return "审核中";
            default:
                return "未验证";
        }
    }

    public void updateInfo(JSONObject json) {


        tv_usrename.setText(json.optString("username", ""));
        real_status = json.optInt("real_status", 0);
        is_manual = json.optInt("is_manual", -1);
        realName = getStatus(real_status);
        if (real_status == 1) {
            realName = (json.optString("real"));
            realId = json.optString("real_id");
        }
        /**
         * 验证手机号
         * */
        phone_status = json.optInt("phone_status", 0);
        phone = getStatus(phone_status);
        if (phone_status == 1) {
            phone = json.optString("phone");
        }
        /**
         * 电子邮箱
         */
        email_status = json.optInt("email_status", 0);
        email = json.optString("email");
        /**
         * 银行卡
         */
        card_status = json.optInt("card_status", 0);
        card = getStatus(card_status);
        //如果是宝付认证支付

        /**个人或企业*/
        if (json.has("is_transfer")) {

            is_transfer = json.optInt("is_transfer", -1);
        }

        /***
         * 易宝支付托管绑定状态
         */

        bind_status = json.optInt("bind_status", -1);
        mmm_status = json.optInt("escrow_account", 0);
        sq1_status = json.optInt("invest_auth", 0);
        sq2_status = json.optInt("repayment", 0);

        info[0].setText(realName);
        info[1].setText(phone);
        info[2].setText(email_status == 1 ? email : getStatus(email_status));

        if(Default.IS_ZFT){
            pin_pass=json.optInt("pin_pass", 0);

        }


        if(!Default.IS_YB){

            if (Default.PAY_TYPE == Default.PAY_BF_RZ) {
                info[3].setText(card);
            } else {
//            info[3].setText(card);
            }

            if (Default.PAY_TYPE == Default.PAY_BF_RZ) {
                if (card_status == 1) {
                    card = "已绑定";

                } else {
                    card = "未绑定";
                }
            } else {
                if (card_status == 1) {

                    card = json.optString("card");
                }
            }
        }



    }

    public void checkStatus() {

        CommonDialog.Builder ibuilder = new CommonDialog.Builder(UserInfoDeatilActivity.this);
        ibuilder.setTitle(R.string.prompt);
        ibuilder.setMessage("当前绑定的手机号码是" + phone);
        ibuilder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                if (Default.IS_ZFT){
                    startActivity(new Intent(UserInfoDeatilActivity.this, ZFTRevisePhoneActivity.class));
                }else{

                    startActivity(new Intent(UserInfoDeatilActivity.this, RevisePhoneActivity.class));
                }
            }
        });

        ibuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });
        ibuilder.create().show();


    }
    public void checkPassword(String message) {

        CommonDialog.Builder ibuilder = new CommonDialog.Builder(UserInfoDeatilActivity.this);
        ibuilder.setTitle(R.string.prompt);
        ibuilder.setMessage(message);
        ibuilder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

                    Intent intent = new Intent(UserInfoDeatilActivity.this,ChangeTenderPasswordActivity.class);
                    intent.putExtra("pin_pass", pin_pass);
                    startActivity(intent);

            }
        });

        ibuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });
        ibuilder.create().show();


    }

    //检测是否绑定yb托管
    public void dohttpCheckYb() {
        JsonBuilder builder = new JsonBuilder();

        BaseHttpClient.post(getBaseContext(), Default.Yb_isbind, builder,
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

                        dismissLoadingDialog();
                        if (statusCode == 200) {
                            try {
                                if (json.has("status")) {
                                    if (json.getInt("status") == 1) {

                                        if(Default.IS_YB){
                                            Default.has_Ybbind = true;
                                            bangding.setText("已绑定");
                                        }else {
                                            Default.has_ZFTbind=true;
                                            MyLog.e("支付通绑定完毕");
                                        }


                                    } else {
                                        if(Default.IS_YB){
                                        Default.has_Ybbind = false;
                                        bangding.setText("未绑定");
                                        }else {
                                            Default.has_ZFTbind=false;
                                        }
                                        showCustomToast(json.getString("message"));
                                    }
                                } else {
                                    showCustomToast(json.getString("message"));
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } else {
                            showCustomToast(R.string.toast1);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          String responseString, Throwable throwable) {
                        // TODO Auto-generated method stub
                        super.onFailure(statusCode, headers, responseString,
                                throwable);
                        dismissLoadingDialog();
                    }

                });

    }

    //绑定支付通托管
    public void doHttpZFT() {
        JsonBuilder builder = new JsonBuilder();

        BaseHttpClient.post(getBaseContext(), Default.MoneyMm, builder,
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

                        dismissLoadingDialog();
                        if (statusCode == 200) {
                            try {
                                if (json.has("status")) {
                                    if (json.getInt("status") == 1) {
                                        showCustomToast(json.getString("message"));
                                        startActivity(new Intent(UserInfoDeatilActivity.this, ZFT_InfoActivity.class));
                                    } else if (json.getInt("status") == 1006) {
                                        checkPassword(json.getString("message"));
                                    }else if ( real_status==3) {
                                        showCustomToast("您的实名认证正在审核中");
                                    } else {
                                        String message = json.getString("message");
                                        SystenmApi.showCommonErrorDialog(UserInfoDeatilActivity.this, json.getInt("status"), message);

                                    }
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } else {
                            showCustomToast(R.string.toast1);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          String responseString, Throwable throwable) {
                        // TODO Auto-generated method stub
                        super.onFailure(statusCode, headers, responseString,
                                throwable);
                        dismissLoadingDialog();
                    }

                });

    }






    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            // 实名认证
            case R.id.rl_smrz:

                //TODO 修改实现方式，易宝绑定确认需要写入shareprefence中
                if(Default.IS_YB && Default.has_Ybbind == true){
                    showCustomToast("已完成实名认证");
                }else{
                    Intent intent = new Intent(UserInfoDeatilActivity.this,
                            PeopleInfoSmrz.class);

                    intent.putExtra("real_status", real_status);
                    intent.putExtra("realName", realName);
                    intent.putExtra("realId", realId);

                    startActivity(intent);
                }

                break;
            // 手机号码
            case R.id.rl_phone:

                if(Default.IS_YB){

                    Intent inten = new Intent(UserInfoDeatilActivity.this, YBPayActivity.class);
                    inten.putExtra("YB_TYPE", 9);
                    startActivity(inten);
                }else{

                        if (phone_status == 1) {
                            checkStatus();
                        } else {
                            startActivity(new Intent(UserInfoDeatilActivity.this,
                                    ModifyUserApplyPhoneActivity.class));
                        }


                }

                break;
            // 电子邮箱
            case R.id.rl_email:
                if (email_status == 1) {
                    showCustomToast("您已经绑定邮箱");
                } else if (email_status == 3) {
                    showCustomToast("您的邮箱正在验证中");
                } else {
                    Intent intent2 = new Intent(UserInfoDeatilActivity.this,
                            ApplyUserEmailActivity.class);

                    intent2.putExtra("email", email);
                    startActivity(intent2);
                }
                break;

            // 账户资料
            case R.id.rl_bind:

//                if (is_transfer == 1) {
//                    startActivity(new Intent(UserInfoDeatilActivity.this, CompanyInfoActivity.class));
//
//                } else if (is_transfer == 2) {
//
//                    startActivity(new Intent(UserInfoDeatilActivity.this, PersoninfoActivity.class));
//                } else if (is_transfer == 0) {
//
//                    if (null == dialog) {
//
//                        dialog = new ApplyPersonOrEnterpriseUserDialog(UserInfoDeatilActivity.this);
//                        dialog.setDialogTitle("资料类型");
//                        dialog.setonClickListener(this);
//                    }
//                    dialog.showAsDropDown(v);
//                }
                break;
            // 银行卡
            case R.id.rl_yhkh:
                if (Default.PAY_TYPE == Default.PAY_BF_RZ) {
                    if (card.equals("未绑定")) {
                        if (real_status == 1) {
                            if (is_manual == 1) {
                                phone = null;
                            }
                            Intent intent2 = new Intent(UserInfoDeatilActivity.this,
                                    AddBankCardActivity.class);
                            intent2.putExtra("real_name", realName);
                            intent2.putExtra("mobile", phone);

                            startActivity(intent2);


                        } else {
                            showCustomToast("请先通过实名认证");
                        }
                    } else {
                        showCustomToast("银行卡已绑定");
                    }

                }else {

                    if (real_status == 1) {
//                        Intent intent2 = new Intent(UserInfoDeatilActivity.this,
//                                ZFTAddBankCardActivity.class);
//                        startActivity(intent2);
                        Intent intent2 = new Intent(UserInfoDeatilActivity.this,
                                ShowBankCardInfoActivity.class);
                        startActivity(intent2);

                    } else {
                        showCustomToast("请先通过实名认证");
                    }
                }
                break;

            // 登陆密码
            case R.id.rl_dlmm:
                Intent intent5 = new Intent(UserInfoDeatilActivity.this,
                        ChangeLoginPasswordActivity.class);
                startActivity(intent5);
                break;
            // 支付密码
            case R.id.rl_jymm:
                Intent intent6 = new Intent(UserInfoDeatilActivity.this,
                        ChangeTenderPasswordActivity.class);
                intent6.putExtra("pin_pass",pin_pass );
                startActivity(intent6);
                break;
            // 修改手势密码
            case R.id.rl_xgssmm:

                SharedPreferences sharedPreferences = getSharedPreferences("lmq", 0);
                if (sharedPreferences.getBoolean("sl", false)) {

                    Intent intent7 = new Intent(UserInfoDeatilActivity.this,
                            CreateGesturePasswordActivity.class);
                    startActivity(intent7);

                } else {

                    showCustomToast("您还没有开启手势密码功能");
                }

                break;
            case R.id.dialog_submit:
                dialog.dismiss();

                break;
            case R.id.dialog_cancle:
                dialog.dismiss();

                break;
            case R.id.apply_enterprise:
                startActivity(new Intent(UserInfoDeatilActivity.this, CompanyInfoActivity.class));
                dialog.dismiss();

                break;
            case R.id.apply_person:
                startActivity(new Intent(UserInfoDeatilActivity.this, PersoninfoActivity.class));

                dialog.dismiss();

                break;

            case R.id.xgsjhm:
                Intent inten = new Intent(UserInfoDeatilActivity.this, YBPayActivity.class);
                inten.putExtra("YB_TYPE", 9);
                startActivity(inten);
                break;
            //支付通托管
            case R.id.rl_zft_bind:
                if(Default.has_ZFTbind){

                    startActivity(new Intent(UserInfoDeatilActivity.this, ZFT_InfoActivity.class));
                }else{

                    doHttpZFT();
                }
                break;

            //托管信息
            case R.id.rl_isTG:

                if (real_status != 1){

                    showCustomToast("请先通过实名认证");


                }else {
                    if (Default.IS_Qdd){


                        Intent money = new Intent(UserInfoDeatilActivity.this,
                                MoneyMoreMoreSqActivity.class);
                        money.putExtra("mmm", mmm_status);
                        money.putExtra("sq1", sq1_status);
                        money.putExtra("sq2", sq2_status);
                        startActivity(money);
                    }else if (Default.IS_YB){

                        Intent intent = new Intent(UserInfoDeatilActivity.this, YBPayActivity.class);
                        intent.putExtra("YB_TYPE", Default.TYPE_YB_REGISTER);

                        intent.putExtra("real_name", realName);
                        intent.putExtra("idcard", realId);
                        startActivity(intent);


                    }else {

                        startActivity(new Intent(UserInfoDeatilActivity.this, Yb_InfoActivity.class));

                    }



                }



                break;
        }

 }




    public void doHttp() {

        BaseHttpClient.post(getBaseContext(), Default.peoInfoSafe, null,
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
                                    updateInfo(json);
                                } else {
                                    String message = json.getString("message");
                                    SystenmApi.showCommonErrorDialog(UserInfoDeatilActivity.this, json.getInt("status"), message);
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

        refreshView.onRefreshComplete();

    }

}
