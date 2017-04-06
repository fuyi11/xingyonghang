package com.lmq.http;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;

import com.xyh.R;
import com.lmq.main.activity.login.loginActivity;
import com.lmq.main.api.ActivityManager;
import com.lmq.main.api.MyLog;
import com.lmq.main.api.SystenmApi;
import com.lmq.main.dialog.CommonDialog;
import com.lmq.main.util.Default;
import com.lmq.menu.MainTabNewActivity;
import com.loopj.android.http.ResponseHandlerInterface;

import java.io.IOException;

public class JsonHttpResponseHandler extends
        com.loopj.android.http.JsonHttpResponseHandler {


    @Override
    public void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
    }


    /****
     * 解析数据 ，再返回给调用者之前对数据进行分析和修改
     *
     * @param responseBody
     * @return
     * @throws JSONException
     */
    @Override
    protected Object parseResponse(byte[] responseBody) throws JSONException {


        MyLog.e("     ", new String(responseBody));

        JSONObject obj = new JSONObject(new String(responseBody));


        if (obj.has("sign") &&  obj.has("content")){

            try {
                if (obj != null && obj.has("session_expired")) {
                    if (obj.getInt("session_expired") == 1) {

                        Default.user_exit = true;

                        CommonDialog.Builder ibuilder  = new CommonDialog.Builder(MainTabNewActivity.mainTabNewActivity);
                        ibuilder.setTitle(R.string.prompt);
                        ibuilder.setMessage("登录已过期请重新登录");
                        ibuilder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Activity a = ActivityManager.getCurrentActivit();
                                        if (a == null)
                                            a = MainTabNewActivity.mainTabNewActivity;
                                        Intent intent = new Intent(a, loginActivity.class);
                                        intent.putExtra("exit", true);
                                        a.startActivity(intent);



                            }
                        });
                        ibuilder.setNegativeButton(R.string.cancel, null);
                        ibuilder.create().show();



                    }

                } else {
                    if (Default.NEW_VERSION) {
                        obj = SystenmApi.decodeResult(obj);


                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        }


        return super.parseResponse(obj.toString().getBytes());

    }

    @Override
    public void sendResponseMessage(HttpResponse response) throws IOException {
        super.sendResponseMessage(response);
    }

    @Override
    public void onPreProcessResponse(ResponseHandlerInterface instance, HttpResponse response) {
        super.onPreProcessResponse(instance, response);
    }


    @Override
    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
        // TODO Auto-generated method stub


        MyLog.e("返回确认1", response.toString());
        if (statusCode == 200) {


        }
    }

    @Override
    public void onFailure(int statusCode, Header[] headers,
                          String responseString, Throwable throwable) {
        // TODO Auto-generated method stub
        System.err.println("解析错误信息" + responseString);
        super.onFailure(statusCode, headers, responseString, throwable);
    }

}
