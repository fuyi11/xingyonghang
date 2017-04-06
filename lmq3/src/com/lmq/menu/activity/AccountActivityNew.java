package com.lmq.menu.activity;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.xyh.R;
import com.lmq.http.BaseHttpClient;
import com.lmq.http.JsonHttpResponseHandler;
import com.lmq.main.activity.investmanager.InvestManagerMainActivity;
import com.lmq.main.activity.investmanager.borrowmanager.LendMoneyRecordActivity;
import com.lmq.main.activity.user.info.UserInfoDeatilActivity;
import com.lmq.main.activity.user.manager.tenderlogs.TenderLogsActivity;
import com.lmq.main.activity.user.manager.pay.ThirdPayActivity;
import com.lmq.main.activity.investmanager.reward.RewardManagerActivity;
import com.lmq.main.activity.user.manager.message.SitMessageInfoActivity;
import com.lmq.main.activity.login.loginActivity;
import com.lmq.main.activity.user.manager.withdrawal.WithDrawalAcitity;
import com.lmq.main.activity.user.manager.withdrawal.WithDrawalAcitityYB;
import com.lmq.main.api.BaseActivity;
import com.lmq.main.api.MyLog;
import com.lmq.main.api.SystenmApi;
import com.lmq.main.listener.FragmentUpdateListener;
import com.lmq.main.util.Data;
import com.lmq.main.util.Default;
import com.lmq.menu.MainTabNewActivity;
import com.lmq.pay.MoneyMoreMorePayActivity;
import com.lmq.zftpay.ZFTRechargeActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class AccountActivityNew extends BaseActivity implements OnClickListener {

    protected static final String MainTabAct = null;
    public static boolean mRefreshName;
    private FragmentUpdateListener mListener;

    private TextView person_zcze;
    private TextView person_dssy;
    private TextView person_ljsy;
    private TextView title;
    private String mPeopic;
    private SharedPreferences sp;
    /**
     * 退出按钮
     */
    private Button exit_button;

    private DisplayImageOptions options;
    private PullToRefreshScrollView refreshView;
    private Long total_money;

    private TextView cz;
    private TextView tx;
    private RelativeLayout reward_lendmoney;
    private Button znx_btn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        super.setHasExit(true);
        setContentView(R.layout.person_info_layout);
        initView();
        sp = AccountActivityNew.this.getSharedPreferences("user", 0);
    }

    public void setListener(FragmentUpdateListener listener) {
        this.mListener = listener;
    }


    public void initView() {

        person_zcze = (TextView) findViewById(R.id.person_zcze);
        person_dssy = (TextView) findViewById(R.id.person_dssy);
        person_ljsy = (TextView) findViewById(R.id.person_ljsy);
        title = (TextView) findViewById(R.id.title);

        exit_button = (Button) findViewById(R.id.more_exit);
        exit_button.setOnClickListener(this);
        showExitBtn();

        findViewById(R.id.peopleinfo_deal).setOnClickListener(this);// 交易记录
        findViewById(R.id.peopleinfo_info).setOnClickListener(this);
        findViewById(R.id.invest_manager).setOnClickListener(this);
        findViewById(R.id.reward_manager).setOnClickListener(this);
        reward_lendmoney = (RelativeLayout) findViewById(R.id.reward_lendmoney);
        reward_lendmoney.setOnClickListener(this);

        znx_btn = (Button) findViewById(R.id.znx_btn);
        znx_btn.setOnClickListener(this);
        znx_btn.setEnabled(false);

        cz = (TextView) findViewById(R.id.cz);
        cz.setOnClickListener(this);
        cz.setEnabled(false);

        tx = (TextView) findViewById(R.id.tx);
        tx.setOnClickListener(this);
        tx.setEnabled(false);


        refreshView = (PullToRefreshScrollView) findViewById(R.id.refreshView);
        refreshView.setOnRefreshListener(new OnRefreshListener<ScrollView>() {

            @Override
            public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
                // TODO Auto-generated method stub
                doHttpUpdatePeopleInfo();

            }
        });


        options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.default_user_image)
                .showImageOnFail(R.drawable.default_user_image).resetViewBeforeLoading(true)
                .cacheOnDisk(true).imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true)
                .displayer(new FadeInBitmapDisplayer(300)).build();

    }

    /**
     * 退出登录按钮控制
     */
    private void showExitBtn() {

        switch (Default.style) {

            case 0:
                if (Default.userId == 0) {
                    exit_button.setVisibility(View.GONE);
                } else {
                    exit_button.setVisibility(View.VISIBLE);
                }

            default:
                break;

        }
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            // 充值
            case R.id.cz:

                if (Default.IS_ZFT) {

                    startActivity(new Intent(AccountActivityNew.this, ZFTRechargeActivity.class));

                } else if (Default.IS_Qdd){

                    Intent intent = new Intent(AccountActivityNew.this,
                            MoneyMoreMorePayActivity.class);
                    intent.putExtra("type", "cz");
                    startActivity(intent);

                }else{
                    startActivity(new Intent(AccountActivityNew.this, ThirdPayActivity.class));

                }

                break;
            // 提現
            case R.id.tx:

                if (total_money == 0 || total_money == 0.00) {
                    showCustomToast("您的资金为0,不可以提现");

                } else {

                    if (Default.IS_YB) {

                        AccountActivityNew.this.startActivity(
                                new Intent(AccountActivityNew.this, WithDrawalAcitityYB.class));

                    } else if (Default.IS_Qdd){

                        Intent intent = new Intent(AccountActivityNew.this,
                                MoneyMoreMorePayActivity.class);
                        intent.putExtra("type", "tx");
                        startActivity(intent);



                    }else{

                        AccountActivityNew.this.startActivity(
                                new Intent(AccountActivityNew.this, WithDrawalAcitity.class));

                    }
                }

                break;

            // 账户信息
            case R.id.peopleinfo_info:

                Intent intent = new Intent(AccountActivityNew.this,
                        UserInfoDeatilActivity.class);
                startActivityForResult(intent, 1);

                break;
            // 交易记录
            case R.id.peopleinfo_deal:
                AccountActivityNew.this.startActivity(
                        new Intent(AccountActivityNew.this, TenderLogsActivity.class));
                break;
            case R.id.reward_manager:
                //TODO 进入奖励管理界面

                AccountActivityNew.this.startActivity(new Intent(AccountActivityNew.this,
                        RewardManagerActivity.class));
                break;
            case R.id.invest_manager:
                //TODO 进入投资管理界面

                AccountActivityNew.this.startActivity(new Intent(AccountActivityNew.this,
                        InvestManagerMainActivity.class));
                break;
//		// 我的借款
//		case R.id.peopleinfo_jk:
//			startActivity(new Intent(AccountActivityNew.this,
//					LendMoneyRecordActivity.class));
//			break;
            case R.id.znx_btn:
                //TODO 进入站内信界面
                startActivity(new Intent(AccountActivityNew.this,
                        SitMessageInfoActivity.class));
                break;
            // 我的借款
            case R.id.reward_lendmoney:
                startActivity(new Intent(AccountActivityNew.this,
                        LendMoneyRecordActivity.class));
                break;
            case R.id.more_exit:
                showDialog();
                break;

        }

    }

    public void showDialog() {
        AlertDialog.Builder builder = new Builder(this);
        builder.setTitle("退出");
        builder.setMessage("是否退出该用户");
        builder.setIcon(R.drawable.ic_launcher);

        builder.setPositiveButton("退出",
                new android.content.DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        doHttpExit();
                        dialog.dismiss();


                    }

                });
        builder.setNegativeButton("取消",
                new android.content.DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    private void doHttpExit() {
        BaseHttpClient.post(getApplicationContext(), Default.exit, null,
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
                        MyLog.d("zzx", "exit成功" + json.toString());
                        try {

                            if (statusCode == 200) {
                                if (json.getInt("status") == 1) {
                                    SharedPreferences sp = getApplicationContext().getSharedPreferences(
                                            Default.userPreferences, 0);
                                    SharedPreferences.Editor edit = sp.edit();
                                    edit.putBoolean("user_exit", true);
                                    edit.putString(Default.userLastUid, ""+Default.userId);
                                    edit.putBoolean(Default.userLastSl, sp.getBoolean("sl",false));
                                    edit.putBoolean("sl", false);
                                    edit.putString(Default.userPassword, "");
//                                    edit.putString(Default.userPassword, "");
//                                    edit.putBoolean(Default.userRemember, false);
//                                    edit.putString("userid", "0");
                                    MyLog.i("asd","sl="+sp.getBoolean("sl",false));
                                    edit.commit();


                                    BaseHttpClient.clearCookie();
                                    MyLog.d("zzx", "exit成功");
                                    Default.layout_type = Default.pageStyleLogin;
                                    Default.userId = 0;
                                    showExitBtn();
                                    Data.clearInfo();

                                    MainTabNewActivity.mainTabNewActivity.IndexView();

                                    // Intent intent = new Intent();
                                } else {
                                    String message = json.getString("message");
                                    SystenmApi.showCommonErrorDialog(AccountActivityNew.this, json.getInt("status"), message);
                                }
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
                    }

                });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Default.LOGIN_TYPE_3) {

            if (mListener != null)
                mListener.onFramentCallback(1, null);
        }
        StringBuilder sb = new StringBuilder();
        if (resultCode == 100 || resultCode == 200) {// 注册返回
            sb.append("注册返回数据:");
            int code = data.getIntExtra("code", -1);
            String message = data.getStringExtra("message");
            sb.append("code:").append(code).append(",message:").append(message);
            if (code != -1) {
                String AccountNumber = data.getStringExtra("AccountNumber");
                sb.append(",AccountNumber:").append(AccountNumber);
                showCustomToast(sb.toString());
            }
            if (code == 88) {
                showCustomToast("成功绑定托管账户！");
            }
        }

    }

    public void updateUserInfo(JSONObject json) {
        DecimalFormat d = new DecimalFormat("##0.00");
        try {

            if (json.has("is_borrow")) {
                if (json.optInt("is_borrow", 0) == 1) {
                    reward_lendmoney.setVisibility(View.GONE);
                } else {
                    reward_lendmoney.setVisibility(View.GONE);
                }
            }

            if (json.has("username")) {

                title.setText(json.optString("username", ""));

            }
            if (json.has("total")) {

                total_money = json.getLong("total");
                person_zcze.setText(d.format(json.getDouble("total")) + "");

            }

            if (json.has("income")) {

                person_ljsy.setText(json.optString("income", "") + "");

            }

            if (json.has("collect")) {

                person_dssy.setText(json.optString("collect", "") + "");

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void login(int type) {
        startActivityForResult(new Intent(AccountActivityNew.this, loginActivity.class),
                type);
        AccountActivityNew.this
                .overridePendingTransition(R.anim.down_to_up, R.anim.to_up);

        Data.clearInfo();
    }
    /***
     public void showPeopleWithdrawalInfo() {

     BaseHttpClient.post(AccountActivityNew.this, Default.peoInfoWithdrawal, null,
     new JsonHttpResponseHandler() {

    @Override public void onStart() {
    // TODO Auto-generated method stub
    super.onStart();
    showLoadingDialogNoCancle(getResources().getString(
    R.string.toast2));
    }

    @Override public void onSuccess(int statusCode, Header[] headers,
    JSONObject response) {
    // TODO Auto-generated method stub
    super.onSuccess(statusCode, headers, response);
    try {

    if (statusCode == 200) {
    if (response.getInt("status") == 1) {
    Intent intent = new Intent(AccountActivityNew.this,
    peopleInfoWithdrawalActivity.class);

    Data.peopleWithdrawalJson = response;

    AccountActivityNew.this.startActivity(intent);
    AccountActivityNew.this.overridePendingTransition(
    R.anim.right_to_left,
    R.anim.to_left);
    } else {
    showCustomToast(response.getString

    ("message"));
    }
    } else {
    showCustomToast(R.string.toast1);
    }
    } catch (Exception e) {
    e.printStackTrace();
    }
    dismissLoadingDialog();
    }

    @Override public void onFailure(int statusCode, Header[] headers,
    String responseString, Throwable throwable) {
    // TODO Auto-generated method stub
    super.onFailure(statusCode, headers, responseString,
    throwable);
    dismissLoadingDialog();
    showCustomToast(responseString);
    }

    });

     }*/

    /**
     * 获取用户绑定银行卡信息
     */

    public void getUserBankCard() {

        BaseHttpClient.post(AccountActivityNew.this, Default.peoInfoWithdrawal, null,
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
                                          JSONObject response) {
                        // TODO Auto-generated method stub
                        super.onSuccess(statusCode, headers, response);
                        try {

                            if (statusCode == 200) {
                                if (response.getInt("status") == 1) {
                                    MyLog.e("获取提现的信息", response.toString());
                                    // 获取到用户绑定银行卡信息
                                    decoidTheUesrCardInfo(response);

                                } else {
                                    String message = response.getString("message");
                                    SystenmApi.showCommonErrorDialog(AccountActivityNew.this, response.getInt("status"), message);
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

    // 解析获取到银行卡信息
    public void decoidTheUesrCardInfo(JSONObject json) {
        //
        try {
            if (json.getString("bank_num") != null
                    && !json.getString("bank_num").equals("")) {

                Intent intent = new Intent(AccountActivityNew.this,
                        WithDrawalAcitity.class);

                intent.putExtra("bank_num", json.getString("bank_num"));

                if (json.getString("bank_name") != null
                        && !json.getString("bank_name").equals("")) {

                    intent.putExtra("bank_name", json.getString("bank_name"));

                }
                if (json.getString("real_name") != null
                        && !json.getString("real_name").equals("")) {

                    intent.putExtra("real_name", json.getString("real_name"));
                }
                // if (json.getString("all_money") != null
                // && !json.getString("all_money").equals("")) {
                intent.putExtra("all_money", json.getString("all_money"));
                MyLog.e("提现准备传的钱数", json.getString("all_money"));
                // }
                if (json.getString("qixian") != null
                        && !json.getString("qixian").equals("")) {

                    intent.putExtra("qixian", json.getString("qixian"));
                }

                startActivity(intent);

            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void doHttpUpdatePeopleInfo() {

        BaseHttpClient.post(AccountActivityNew.this, Default.peoInfoUpdate, null,
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
                                          JSONObject response) {
                        // TODO Auto-generated method stub
                        super.onSuccess(statusCode, headers, response);

                        try {
                            if (statusCode == 200) {
                                if (response.getInt("status") == 1) {
                                    cz.setEnabled(true);
                                    tx.setEnabled(true);
                                    znx_btn.setEnabled(true);
                                    updateUserInfo(response);
                                } else {
                                    String message = response.getString("message");
                                    SystenmApi.showCommonErrorDialog(AccountActivityNew.this, response.getInt("status"), message);
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

    @Override
    public void onResume() {
        super.onResume();
        showExitBtn();
        doHttpUpdatePeopleInfo();
    }


}
