package com.lmq.http;

import android.content.Context;
import android.widget.Toast;

import com.lmq.main.api.JsonBuilder;
import com.lmq.main.api.MyLog;
import com.lmq.main.util.Default;
import com.lmq.main.util.RSAUtils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.SyncHttpClient;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import java.io.UnsupportedEncodingException;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * Created by zhaoshuai on 15/8/23.
 */
public class AutoLoginHttpPost {




    /**
     *
     * @param context
     * @param url
     *            请求接口地址
     * @param json
     *            请求服务器需要传递的参数 格式为JSON格式数组
     * @param responseHandler
     *
     *            对服务器请求的回调函数 直接解完成的JSON数据
     */

    public static void post(Context context, String url, JsonBuilder json,
                            JsonHttpResponseHandler responseHandler) {





        StringEntity stringEntity = null;
        MyLog.e("httpapi", "1 url=" + BaseHttpClient.getAbsoluteUrl(url));
        if (json == null) {
            json = new JsonBuilder();
        }
        if (Default.userId != 0) {

            json.put("uid", Default.userId);
        }

        if (Default.NEW_VERSION) {
            MyLog.e("httpapi", "2原始参数=" + json.toJsonString().toString());
            /** 获取公钥私钥 */
            PublicKey publicKey = null;
            PrivateKey privateKey = null;
            try {
                publicKey = RSAUtils.loadPublicKey(Default.LMQ_PUBLIC_KEY);
                privateKey = RSAUtils.loadPrivateKey(Default.LMQ_PRIVATE_KEY);

//				String signStr = Base64.encodeToString(RSAUtils
//						.encryptDataWithPrivateKey(json.toJsonString()
//								.getBytes(), privateKey), Base64.NO_WRAP);



                StringBuilder sign  = new StringBuilder();

                BaseHttpClient.getJsonByte(sign, json.toJsonString().toString(), privateKey);

                StringBuilder content  = new StringBuilder();

                BaseHttpClient.getJsonByte(content, json.toJsonString().toString(), publicKey);



                JsonBuilder params = new JsonBuilder();
                params.put("sign", sign.toString());
                params.put("content", content.toString());




                MyLog.e("httpapi", "3加密后数据=" + params.toJsonString().toString());
                try {
                    stringEntity = new StringEntity(
                            (params.toJsonString().toString()), HTTP.UTF_8);

                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                if (NetWorkStatusBroadcastReceiver.netWorkAviable) {
                    BaseHttpClient.httpClient.post(context, BaseHttpClient.getAbsoluteUrl(url), stringEntity,
                            "application/json;charset=UTF-8", responseHandler);
                } else {

                    Toast.makeText(context, "您的网络连接已中断,请检查你的网络设置",
                            Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

        } else {

            try {
                stringEntity = new StringEntity((json.toJsonString().toString()),
                        HTTP.UTF_8);

            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if (NetWorkStatusBroadcastReceiver.netWorkAviable) {


                BaseHttpClient.httpClient.post(context, BaseHttpClient.getAbsoluteUrl(url), stringEntity, "application/json;charset=UTF-8", responseHandler);
            } else {

                Toast.makeText(context, "您的网络连接已中断,请检查你的网络设置",
                        Toast.LENGTH_SHORT).show();
            }

        }

    }
}
