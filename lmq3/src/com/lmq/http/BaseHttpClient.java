package com.lmq.http;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpCookie;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.List;
import java.util.Random;

import org.apache.http.Header;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Base64;
import android.widget.Toast;

import com.xyh.R;
import com.lmq.main.activity.login.loginActivity;
import com.lmq.main.api.ActivityManager;
import com.lmq.main.api.JsonBuilder;
import com.lmq.main.api.MyLog;
import com.lmq.main.api.SystenmApi;
import com.lmq.main.dialog.CommonDialog;
import com.lmq.main.util.Default;
import com.lmq.main.util.RSAUtils;
import com.lmq.menu.MainTabNewActivity;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

import static java.util.Arrays.*;

public class BaseHttpClient {

    private static boolean picFlag = false;

	// 网络请求URL
	// private static final String BASE_URL = "http://ios.jinrongdai.com";

	public static AsyncHttpClient httpClient = new AsyncHttpClient();
    public static boolean NO_RAS = false;//不加密标识符

	/**
	 * doGet请求
	 *
	 * @param url
	 *            请求URL
	 * @param params
	 *            请求参数
	 * @param
	 *
	 */
	public static void get(Context context, String url, RequestParams params,
			JsonHttpResponseHandler responseHandler) {
		if (NetWorkStatusBroadcastReceiver.netWorkAviable) {
			httpClient.get(getAbsoluteUrl(url), params, responseHandler);
            httpClient.setTimeout(3000000);
		} else {

			Toast.makeText(context, "您的网络连接已中断,请检查你的网络设置", Toast.LENGTH_SHORT)
					.show();
		}

	}



    public static void saveCookie() {
        PersistentCookieStore cookieStore = new PersistentCookieStore(MainTabNewActivity.mainTabNewActivity);
        httpClient.setCookieStore(cookieStore);
    }

    public static List<Cookie> getCookie(){
        PersistentCookieStore cookieStore = new PersistentCookieStore(MainTabNewActivity.mainTabNewActivity);
        List<Cookie> cookies = cookieStore.getCookies();
        return cookies;
    }

    public static void clearCookie(){
        PersistentCookieStore cookieStore = new PersistentCookieStore(MainTabNewActivity.mainTabNewActivity);
        cookieStore.clear();
    }


    public static void normalDoPost(Context context, String url,
			RequestParams requestParams,
			AsyncHttpResponseHandler responseHandler) {

		if (NetWorkStatusBroadcastReceiver.netWorkAviable) {
			httpClient.post(url, requestParams, responseHandler);
            httpClient.setTimeout(3000000);
		} else {

			Toast.makeText(context, "您的网络连接已中断,请检查你的网络设置", Toast.LENGTH_SHORT)
					.show();
		}

	}

	/**
	 *
	 * @param context
	 * @param url
	 *            请求接口地址
	 * @param builder
	 *            请求服务器需要传递的参数 格式为JSON格式数组
	 * @param responseHandler
	 *
	 *            对服务器请求的回调函数 直接解完成的JSON数据
	 */

	public static void post(final Context context, final String url, final  JsonBuilder builder,
			final JsonHttpResponseHandler responseHandler) {

        saveCookie();

        if(!url.equals(Default.login)&&!url.equals(Default.exit)){


            SharedPreferences sp = context.getSharedPreferences(
                    Default.userPreferences, 0);

            long lasterLoginTime = sp.getLong("lastLoginTime", 0);
            long nowTime  = System.currentTimeMillis();
            /***
             * 计算上次登录时间与当前时间的差
             *
             *
             */

            boolean loginflag = false;
            if(Default.needRelogin){
                Default.needRelogin = false;
                loginflag = true;
            }else{

                loginflag = (nowTime-lasterLoginTime)/(1000*60)>0.2&&!Default.user_exit;
            }
            if(loginflag){



                List<String> userInfo = SystenmApi.getUserSavedUserNameAndPwd(context);

                MyLog.e("自动登录-----1");

                if(null != userInfo){
                    MyLog.e("自动登录-----2");
                    String name = userInfo.get(0);
                    String pwd = userInfo.get(1);

                    JsonBuilder loginbuilder = new JsonBuilder();

                    loginbuilder.put("sUserName", name);
                    loginbuilder.put("sPassword", pwd);

                    AutoLoginHttpPost.post(context, Default.login, loginbuilder,
                            new JsonHttpResponseHandler() {

                                @Override
                                public void onStart() {
                                    // TODO Auto-generated method stub
                                    super.onStart();
                                }

                                @Override
                                public void onSuccess(int statusCode, Header[] headers,
                                                      JSONObject json) {
                                    // TODO Auto-generated method stub
                                    super.onSuccess(statusCode, headers, json);
                                    MyLog.e("123",
                                            "登陆后的数据:" + json.toString());
                                    if (statusCode == 200) {
                                        MyLog.e("自动登录-----3");
                                        try {
                                            if (json.has("status")) {
                                                if (json.getInt("status") == 1) {


                                                    Default.userId = json.optLong("uid", 0);
                                                    SharedPreferences sp = MainTabNewActivity.mainTabNewActivity.getSharedPreferences(
                                                            Default.userPreferences, 0);
                                                    SharedPreferences.Editor edit = sp.edit();
                                                    edit.putLong("lastLoginTime", System.currentTimeMillis());
                                                    edit.commit();


                                                        MyLog.e("回调下一个", "111111111111");
                                                        doPost(context,builder,url,responseHandler);
                                                    httpClient.setTimeout(3000000);


                                                } else {


                                                    showAutoLoginErrorDialog(json
                                                            .getString("message"));
                                                }
                                            } else {


                                                showAutoLoginErrorDialog(json
                                                        .getString("message"));
                                            }

                                        } catch (Exception e) {

                                            showAutoLoginErrorDialog("登录异常");
                                        }

                                    } else {

                                        showAutoLoginErrorDialog("登录异常");
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

                }else{

                    picFlag = false;
                    doPost(context,builder,url,responseHandler);
                    httpClient.setTimeout(3000000);
                }





            }else {

                picFlag = false;
                doPost(context,builder,url,responseHandler);
                httpClient.setTimeout(3000000);
            }


        }else{


            picFlag = false;
            doPost(context, builder, url, responseHandler);
            httpClient.setTimeout(3000000);
        }


	}

    private static  CommonDialog.Builder ibuilder ;

    /**
     * 弹出用户自动登录错误提示
     * @param message
     */
    private static void showAutoLoginErrorDialog(String message){
        if (ibuilder == null){

            ibuilder = new CommonDialog.Builder(MainTabNewActivity.mainTabNewActivity);
            Default.user_exit = true;
            Default.userId = 0;

            ibuilder.setTitle(R.string.prompt);
            ibuilder.setMessage(message);
            ibuilder.setPositiveButton("去登陆", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    Activity a = ActivityManager.getCurrentActivit();
                    if (a == null)
                        a = MainTabNewActivity.mainTabNewActivity;
                    Intent intent = new Intent(a, loginActivity.class);
                    intent.putExtra("exit", true);
                    a.startActivity(intent);
                }
            });
            ibuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    MainTabNewActivity.mainTabNewActivity.IndexView();
                }
            });
            if (!ibuilder.create().isShowing()){
                ibuilder.create().show();
            }
        }



    }

    private static  void doPost(Context context,JsonBuilder json,String url,JsonHttpResponseHandler responseHandler){



        StringEntity stringEntity = null;
        MyLog.e("httpapi", "1 url=" + getAbsoluteUrl(url));
        if (json == null) {
            json = new JsonBuilder();
        }
        if (Default.userId != 0) {

            json.put("uid", Default.userId);
        }

        if (Default.NEW_VERSION&&!NO_RAS) {

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

                getJsonByte(sign,json.toJsonString().toString(),privateKey);

                StringBuilder content  = new StringBuilder();

                getJsonByte(content,json.toJsonString().toString(),publicKey);



                JsonBuilder params = new JsonBuilder();
                params.put("sign", sign.toString());
                params.put("content", content.toString());

                if(picFlag){
                    params.put("comb","1");
                }



                MyLog.e("httpapi", "3加密后数据=" + params.toJsonString().toString());
                try {
                    stringEntity = new StringEntity(
                            (params.toJsonString().toString()), HTTP.UTF_8);

                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                if (NetWorkStatusBroadcastReceiver.netWorkAviable) {
                    httpClient.post(context, getAbsoluteUrl(url), stringEntity,
                            "application/json;charset=UTF-8", responseHandler);
                    httpClient.setTimeout(3000000);
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
                httpClient.post(context, getAbsoluteUrl(url), stringEntity,
                        "application/json;charset=UTF-8", responseHandler);
                httpClient.setTimeout(3000000);
            } else {

                Toast.makeText(context, "您的网络连接已中断,请检查你的网络设置",
                        Toast.LENGTH_SHORT).show();
            }

        }
        
        NO_RAS = false;
    }
    /**
     * 判断是否是一个中文汉字
     *
     * @param c
     *            字符
     * @return true表示是中文汉字，false表示是英文字母
     * @throws UnsupportedEncodingException
     *             使用了JAVA不支持的编码格式
     */
    public static boolean isChineseChar(char c)
            throws UnsupportedEncodingException {
        // 如果字节数大于1，是汉字
        // 以这种方式区别英文字母和中文汉字并不是十分严谨，但在这个题目中，这样判断已经足够了
        return String.valueOf(c).getBytes("UTF-8").length > 1;
    }

    /**
     * 按字节截取字符串
     *
     * @param orignal
     *            原始字符串
     * @param count
     *            截取位数
     * @return 截取后的字符串
     * @throws UnsupportedEncodingException
     *             使用了JAVA不支持的编码格式
     */
    public static String substring(String orignal, int count)
            throws UnsupportedEncodingException {
        // 原始字符不为null，也不是空字符串
        if (orignal != null && !"".equals(orignal)) {
            // 将原始字符串转换为GBK编码格式
            orignal = new String(orignal.getBytes(), "UTF-8");//
            // System.out.println(orignal);
            //System.out.println(orignal.getBytes().length);
            // 要截取的字节数大于0，且小于原始字符串的字节数
            if (count > 0 && count < orignal.getBytes("UTF-8").length) {
                StringBuffer buff = new StringBuffer();
                char c;
                for (int i = 0; i < count; i++) {
                    System.out.println(count);
                    c = orignal.charAt(i);
                    buff.append(c);
                    if (isChineseChar(c)) {
                        // 遇到中文汉字，截取字节总数减1
                        --count;
                    }
                }
                //   System.out.println(new String(buff.toString().getBytes("GBK"),"UTF-8"));
                return new String(buff.toString().getBytes(),"UTF-8");
            }
        }
        return orignal;
    }



    public  static void getJsonByte(StringBuilder con ,String json,PublicKey publicKey) throws UnsupportedEncodingException {


       String temp = "";
       String content = "";
       byte[] jsonbytes = json.getBytes("UTF-8");






       System.out.println("长度" + jsonbytes.length);

       if(jsonbytes.length>=100){

           picFlag = true;


           int jsonlengt = jsonbytes.length;
           for(int i=0;i<jsonlengt;i+=100){

               int cutRange = jsonlengt-i>=100?100:jsonlengt-i;
               String temps = new String(jsonbytes,i,cutRange);
               System.out.println("拆分"+temps);
               System.out.println("拆分"+temps.getBytes().length);
			   byte[] buf = new byte[cutRange];
			   System.arraycopy(jsonbytes, i, buf, 0, cutRange);

               content = Base64.encodeToString(RSAUtils.encryptData(
							   buf, publicKey),
                       Base64.NO_WRAP);

               con.append(content).append(",");



           }

           con.replace(con.length() - 1, con.length(), "");


       }else{

           temp = json;
           content = Base64.encodeToString(RSAUtils.encryptData(
                           temp.getBytes(), publicKey),
                   Base64.NO_WRAP);
           con.append(content);

       }




    }


    public  static void getJsonByte(StringBuilder con ,String json,PrivateKey privateKey) throws UnsupportedEncodingException {


        String content = "";

            content = Base64.encodeToString(RSAUtils
                    .encryptDataWithPrivateKey(json
                            .getBytes(), privateKey), Base64.NO_WRAP);
            con.append(content);


    }

	/**
     *
     * @param context
     * @param downloadUrl
     *            下载地址
     * @param responseHandler
     *            回调函数
     */

    public static void downloadFile(Context context, String downloadUrl,
                                    FileAsyncHttpResponseHandler responseHandler) {
        if (NetWorkStatusBroadcastReceiver.netWorkAviable) {
            httpClient.get(downloadUrl, responseHandler);
        } else {
            Toast.makeText(context, "您的网络连接已中断,请检查你的网络设置", Toast.LENGTH_SHORT)
                    .show();
        }

    }

    /**
     *
     * @param context
     * @param downloadUrl
     *            下载地址
     * @param responseHandler
     *            回调函数
     */

    public static void getFileFromServer(Context context, String downloadUrl,
                                    AsyncHttpResponseHandler responseHandler) {
        if (NetWorkStatusBroadcastReceiver.netWorkAviable) {
            httpClient.get(getAbsoluteUrl(downloadUrl), responseHandler);
        } else {
            Toast.makeText(context, "您的网络连接已中断,请检查你的网络设置", Toast.LENGTH_SHORT)
                    .show();
        }

    }



	public static File createTempFile(String namePart, int byteSize,
			Context context) {
		try {
			File f = File.createTempFile(namePart, ".apk",
					context.getFilesDir());
			FileOutputStream fos = new FileOutputStream(f);
			Random r = new Random();
			byte[] buffer = new byte[byteSize];
			r.nextBytes(buffer);
			fos.write(buffer);
			fos.flush();
			fos.close();
			return f;
		} catch (Throwable t) {
			MyLog.e("BaseClient", "createTempFile failed", t);
		}
		return null;
	}

	/**
	 * 
	 * @param url
	 *            接口地址
	 * @return
	 */
	public  static String getAbsoluteUrl(String url) {



		return Default.ip.trim() + url.trim();
	}



}
