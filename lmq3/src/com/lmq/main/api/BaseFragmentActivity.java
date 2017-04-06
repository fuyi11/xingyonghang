package com.lmq.main.api;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.xyh.R;
import com.lmq.http.BaseHttpClient;
import com.lmq.http.JsonHttpResponseHandler;
import com.lmq.main.util.Default;

import org.apache.http.Header;
import org.json.JSONObject;

public class BaseFragmentActivity extends FragmentActivity
{


    protected void onCreate(Bundle arg0)
    {
        super.onCreate(arg0);

    }


    //点击返回
    private long lastTime;
    private int closeNum;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                closeNum++;
                if (System.currentTimeMillis() - lastTime > 5000) {
                    closeNum = 0;
                }
                lastTime = System.currentTimeMillis();
                if (closeNum == 1) {
                    doHttp();
                    finish();
                } else {

                    showCustomToast(R.string.toast6);
                }
            }
            return false;
        }

        return super.onKeyDown(keyCode, event);
    }

    private void doHttp() {

        BaseHttpClient.post(getBaseContext(), Default.exit, null,
                new JsonHttpResponseHandler() {

                    @Override
                    public void onStart() {
                        // TODO Auto-generated method stub
                        super.onStart();

                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          JSONObject response) {
                        // TODO Auto-generated method stub
                        super.onSuccess(statusCode, headers, response);

                        try {

                            if (statusCode == 200) {

                                if (response.getInt("status") == 1) {
                                    MyLog.d("zzx", "exit成功");
                                    ActivityManager.closeAllActivity();
                                } else {
                                    MyLog.d("zzx", "exit失败");
                                }
                            } else {
                                MyLog.d("zzx", "exit失败");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          String responseString, Throwable throwable) {
                        // TODO Auto-generated method stub
                        super.onFailure(statusCode, headers, responseString,
                                throwable);
                    }

                });

    }

    // -------------------------------------------------------

    // --------------------------------------------------------
    /** 短暂显示Toast提示(来自res) **/
    protected void showShortToast(int resId) {
        Toast.makeText(BaseFragmentActivity.this, getString(resId), Toast.LENGTH_SHORT)
                .show();
    }

    /** 短暂显示Toast提示(来自String) **/
    protected void showShortToast(String text) {
        Toast.makeText(BaseFragmentActivity.this, text, Toast.LENGTH_SHORT).show();
    }

    /** 长时间显示Toast提示(来自res) **/
    protected void showLongToast(int resId) {
        Toast.makeText(BaseFragmentActivity.this, getString(resId), Toast.LENGTH_LONG)
                .show();
    }

    /** 长时间显示Toast提示(来自String) **/
    protected void showLongToast(String text) {
        Toast.makeText(BaseFragmentActivity.this, text, Toast.LENGTH_LONG).show();
    }

    /** 显示自定义Toast提示(来自res) **/
    // protected void showCustomToast(int resId) {
    // View toastRoot = LayoutInflater.from(BaseActivity.BaseFragmentActivity.this).inflate(
    // R.layout.common_toast, null);
    // ((HandyTextView) toastRoot.findViewById(R.id.toast_text))
    // .setText(getString(resId));
    // Toast toast = new Toast(BaseActivity.BaseFragmentActivity.this);
    // toast.setGravity(Gravity.CENTER, 0, 0);
    // toast.setDuration(Toast.LENGTH_SHORT);
    // toast.setView(toastRoot);
    // toast.show();
    // }

    /** 显示自定义Toast提示(来自String) **/
    protected void showCustomToast(String text) {
        View toastRoot = LayoutInflater.from(BaseFragmentActivity.this).inflate(
                R.layout.common_toast, null);
        ((TextView) toastRoot.findViewById(R.id.toast_text)).setText(text);
        Toast toast = new Toast(BaseFragmentActivity.this);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(toastRoot);
        toast.show();
    }

    protected void showCustomToast(int id) {

        if (Default.hdf_show_error_info)
            showCustomToast(getResources().getString(id).toString());
    }

    // ---------------------------------------------------------------


    
}

