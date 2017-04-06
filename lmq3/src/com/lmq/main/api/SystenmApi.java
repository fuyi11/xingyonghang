package com.lmq.main.api;

import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.style;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

import com.xyh.R;
import com.lmq.http.AutoLoginHttpPost;
import com.lmq.http.BaseHttpClient;
import com.lmq.http.JsonHttpResponseHandler;
import com.lmq.main.activity.login.loginActivity;
import com.lmq.main.activity.person.personinfo.PersoninfoActivity;
import com.lmq.main.activity.user.info.UserInfoDeatilActivity;
import com.lmq.main.activity.user.manager.apply.ApplyForLinesActivity;
import com.lmq.main.activity.user.manager.bankinfo.AddBankCardActivity;
import com.lmq.main.activity.user.manager.idcard.PeopleInfoSmrz;
import com.lmq.main.activity.user.manager.password.ChangeTenderPasswordActivity;
import com.lmq.main.activity.user.manager.pay.ThirdPayActivity;
import com.lmq.main.dialog.CommonDialog;
import com.lmq.main.util.Default;
import com.lmq.main.util.DesUtil;
import com.lmq.main.util.MD5;
import com.lmq.main.util.RSAUtils;
import com.lmq.menu.MainTabNewActivity;

import static android.support.v4.app.ActivityCompat.finishAffinity;
import static android.support.v4.app.ActivityCompat.startActivity;
import static android.support.v4.app.ActivityCompat.startActivityForResult;

public class SystenmApi {

    public static String[] split(String original, String regex) {
        // 取子串的起始位置　
        int startIndex = 0;
        // 将结果数据先放入Vector中　
        Vector v = new Vector();
        // 返回的结果字符串数组　
        String[] str = null;
        // 存储取子串时起始位置　
        int index = 0;
        // 获得匹配子串的位置　
        startIndex = original.indexOf(regex);
        //
        // System.out.println("0" + startIndex);
        // 如果起始字符串的位置小于字符串的长度，则证明没有取到字符串末尾。　
        // -1代表取到了末尾　
        while (startIndex < original.length() && startIndex != -1) {
            String temp = original.substring(index, startIndex);
            // System.out.println(" " + startIndex);
            // 取子串　　
            v.addElement(temp);
            // 设置取子串的起始位置　　
            index = startIndex + regex.length();
            // 获得匹配子串的位置　　
            startIndex = original.indexOf(regex, startIndex + regex.length());
        }
        // 取结束的子串　
        v.addElement(original.substring(index + 1 - regex.length()));
        // 将Vector对象转换成数组　
        str = new String[v.size()];
        for (int i = 0; i < v.size(); i++) {
            str[i] = (String) v.elementAt(i);
        }
        // 返回生成的数组　
        return str;
    }

    /**
     * 检测邮箱地址是否合法
     *
     * @param email
     * @return true合法 false不合法
     */
    public static boolean isEmail(String email) {
        if (null == email || "".equals(email))
            return false;
        // Pattern p = Pattern.compile("\\w+@(\\w+.)+[a-z]{2,3}"); //简单匹配
        Pattern p = Pattern
                .compile("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|"
                        + "(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");
        Matcher m = p.matcher(email);
        return m.matches();
    }


    /***
     * 四舍五入
     *
     * @param ft
     * @return
     */

    public static float getRounding(double ft) {
        float value = 0;
        int scale = 2;// 设置位数
        int roundingMode = BigDecimal.ROUND_HALF_UP;// 表示四舍五入，可以选择其他舍值方式，例如去尾，等等.
        BigDecimal bd = new BigDecimal(ft);
        bd = bd.setScale(scale, roundingMode);
        value = bd.floatValue();

        return value;
    }

    /**
     * 移动方法
     *
     * @param v      需要移动的View
     * @param startX 起始x坐标
     * @param toX    终止x坐标
     * @param startY 起始y坐标
     * @param toY    终止y坐标
     */
    public static void moveFrontBg(View v, int startX, int toX, int startY,
                                   int toY) {
        TranslateAnimation anim = new TranslateAnimation(startX, toX, startY,
                toY);
        anim.setDuration(200);
        anim.setFillAfter(true);
        v.startAnimation(anim);
    }

    public static String getMoneyInfo(double money) {
        String info = "";
        int type = 0;

        if (money > 9999 && money < 100000000) {
            type = 1;
            info = money / 10000 + "";
        } else if (money >= 100000000) {
            type = 2;
            info = money / 100000000 + "";
        } else {
            info = money + "";
        }

        int index = info.indexOf(".");
        if (index > 0) {
            String temp = info.substring(index + 1, info.length());
            if (temp.length() == 1) {
                if (Integer.parseInt(temp) > 0)
                    info = info.substring(0, index + 2);
                else
                    info = info.substring(0, index);
            } else {
                if (Integer.parseInt(temp.substring(0, 2)) > 0)
                    info = info.substring(0, index + 3);
                else
                    info = info.substring(0, index);
            }
        }
        if (type == 1) {
            info = info + "万";
        } else if (type == 2) {
            info = info + "亿";
        }

        return info;
    }


    /**
     * 解密服务器返回数据
     * @param response
     * @return
     */
    public static JSONObject decodeResult(JSONObject response) {


        JSONObject returnJson = null;
        if(!response.has("sign") || !response.has("content")){

            returnJson = response;


        } else {


            MyLog.e("httpapi", "4未解密的服务器信息" + response.toString());
            String finalResponse = "";
            /** 解密数据 */
            PrivateKey privateKey = null;

            try {

                privateKey = RSAUtils.loadPrivateKey(Default.LMQ_PRIVATE_KEY);

                if (response.has("content")) {

                    finalResponse = response.getString("content");

                    finalResponse = new String(Base64.decode(
                            finalResponse.getBytes(), Base64.NO_WRAP));
                    StringBuffer sbres = null;
                    if (response.has("comb")) {

                        if (response.getInt("comb") != 0) {

                            String[] responseArray = finalResponse.split(",");

                            sbres = new StringBuffer();
                            for (int i = 0; i < responseArray.length; i++) {

                                sbres.append(new String(RSAUtils.decryptData(
                                        Base64.decode(responseArray[i].getBytes(),
                                                Base64.NO_WRAP), privateKey)));

                            }
                        }

                        finalResponse = sbres.toString();


                    } else {

                        finalResponse = new String(RSAUtils.decryptData(
                                Base64.decode(finalResponse.getBytes(),
                                        Base64.NO_WRAP), privateKey));

                    }
                }


            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            MyLog.e("httpapi", "5解析后字符串" + finalResponse);

            try {
                returnJson = new JSONObject(finalResponse);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        return returnJson;

    }

    /**
     * @param context 上下文
     * @param name    用户名
     * @param pwd     用户密码
     */
    public static void saveUserLoginInfo(Context context, String name,
                                         String pwd) {

        try {
            DesUtil des = new DesUtil();

            SharedPreferences sp = context.getSharedPreferences(
                    Default.userPreferences, 0);
            Editor edit = sp.edit();

            edit.putString(Default.userName, des.encrypt(name));
            edit.putString(Default.userPassword, des.encrypt(pwd));
            edit.putString("uid", Default.userId + "");
            // edit.putBoolean(Default.userRemember, mRemember);
            edit.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    /***
     * 客户端自动登录
     *
     * @param context
     */
    public static void autoLogin(final Context context, final JsonBuilder builder, final String url, final JsonHttpResponseHandler responseHandler) {


    }


    public static String getIPStr(Context context, boolean wifi) {

        String rtnStr = "";
        if (wifi) {

            WifiManager wifiManager = (WifiManager) context
                    .getSystemService(Context.WIFI_SERVICE);
            // 判断wifi是否开启
            if (!wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(true);
            }
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ipAddress = wifiInfo.getIpAddress();

            rtnStr = intToIp(ipAddress);
        } else {

            try {
                for (Enumeration<NetworkInterface> en = NetworkInterface
                        .getNetworkInterfaces(); en.hasMoreElements(); ) {
                    NetworkInterface intf = en.nextElement();
                    for (Enumeration<InetAddress> enumIpAddr = intf
                            .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        if (!inetAddress.isLoopbackAddress()) {
                            rtnStr = inetAddress.getHostAddress().toString();
                        }
                    }
                }
            } catch (SocketException ex) {
            }

        }

        return rtnStr;
    }

    private static String intToIp(int i) {

        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF)
                + "." + (i >> 24 & 0xFF);
    }

    /**
     * @param context 上下文
     * @return returnList 0:userName 1:userPasswrod
     */
    public static List<String> getUserSavedUserNameAndPwd(Context context) {

        List<String> returnList = null;
        SharedPreferences sp = context.getSharedPreferences(
                Default.userPreferences, 0);
        // 获取用户保存的信息
        String name = sp.getString(Default.userName, "");
        String pwd = sp.getString(Default.userPassword, "");
        boolean mRemember = sp.getBoolean(Default.userRemember, false);

        // 判断获取到用户把保存的信息
        if (!name.equals("") && !pwd.equals("")) {


            try {
                DesUtil des = new DesUtil();
                returnList = new ArrayList<String>();
                returnList.add(des.decrypt(name));
                returnList.add(des.decrypt(pwd));
            } catch (Exception e) {


            }


        }

        return returnList;

    }

    /**
     * 注册用户名判断
     */
    public static int ByteLenth(String name) {
        int n = 0;
        for (int i = 0; i < name.length(); i++) {
            if (isChinese(name.charAt(i))) {
                n = n + 2;
            } else {
                n = n + 1;
            }
        }
        return n;
    }

    private static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || ub ==

                Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS || ub ==

                Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A || ub ==

                Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B || ub ==

                Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION || ub ==

                Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
            return true;
        }
        return false;
    }

    /**
     * 判断一个字符 是否为空或者为NULL
     *
     * @param str 待检验的字符串
     * @return 返回的状态
     */
    public static Boolean isNullOrBlank(String str) {

        boolean returnFlag = false;

        if (str == null) {
            returnFlag = true;
        } else if (str.equals("")) {

            returnFlag = true;
        }

        return returnFlag;

    }

    /**
     * 根据要求显示用户名 其他的显示为*
     *
     * @param name
     * @param index
     * @return
     */
    public static String showUserWithIndex(String name, int index) {
        StringBuilder returnStr = new StringBuilder();

        if (!isNullOrBlank(name)) {

            if (index < name.length()) {

                for (int i = 0; i < name.length(); i++) {

                    if (i <= index) {
                        returnStr.append(name.charAt(i));
                    } else {
                        returnStr.append("*");

                    }
                }
            }

        }

        return returnStr.toString();
    }

    /**
     * 清楚用户存储的信息
     *
     * @param context
     */
    public void cleanUserSaveInfo(Context context) {

        SharedPreferences sp = context.getSharedPreferences(
                Default.userPreferences, 0);
        Editor edit = sp.edit();
        edit.putString(Default.userName, "");
        edit.putString(Default.userPassword, "");
        edit.putBoolean(Default.userRemember, false);
        edit.commit();
    }

    /**
     * 图片 圆角
     *
     * @param bitmap
     * @param roundPX
     * @return
     */
    public static Bitmap getRoundCornerBitmap(Bitmap bitmap, float roundPX) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int min = 90;
        if (width < min || height < min) {
            width = height = min;
        }

        Bitmap bitmap2 = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap2);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, width, height);
        final RectF rectF = new RectF(rect);

        paint.setColor(color);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawRoundRect(rectF, roundPX, roundPX, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return bitmap2;
    }

    /***
     * 显示分享界面
     *
     * @param context
     * @param title
     * @param Contenttext
     * @param shareURL
     */
    public static void showShareView(Context context, String title,
                                     String Contenttext, String shareURL) {

        ShareSDK.initSDK(context);

        OnekeyShare oks = new OnekeyShare();

        // 关闭sso授权
        oks.disableSSOWhenAuthorize();

        Platform[] p = ShareSDK.getPlatformList();

        for (int i = 0; i < p.length; i++) {

            String name = p[i].getName();

            if (name.equals(QZone.NAME) || name.equals(QQ.NAME)
                    || name.equals(Wechat.NAME)
                    || name.equals(WechatMoments.NAME)) {

            } else {
                oks.addHiddenPlatform(name);

            }

        }


        oks.setTitle(title);
        oks.setTitleUrl(shareURL);
        oks.setText(Contenttext);
        oks.setImageUrl(Default.ip + "Style/H/images/sharePicture.png");
        oks.setUrl(shareURL);
        oks.setComment(Contenttext);
        oks.setSite(context.getString(R.string.app_name));
        oks.setSiteUrl(shareURL);
        // 启动分享GUI
        oks.show(context);


    }

    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public static String getVersion(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(),
                    0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ActivityInfo getBrowserApp(Context context) {
        String default_browser = "android.intent.category.DEFAULT";
        String browsable = "android.intent.category.BROWSABLE";
        String view = "android.intent.action.VIEW";

        Intent intent = new Intent(view);
        intent.addCategory(default_browser);
        intent.addCategory(browsable);
        Uri uri = Uri.parse("http://");
        intent.setDataAndType(uri, null);

        // 找出手机当前安装的所有浏览器程序
        List<ResolveInfo> resolveInfoList = context.getPackageManager()
                .queryIntentActivities(intent,
                        PackageManager.GET_INTENT_FILTERS);
        if (resolveInfoList.size() > 0) {
            ActivityInfo activityInfo = resolveInfoList.get(0).activityInfo;
            return activityInfo;
        } else {
            return null;
        }
    }


    public static final void showCommonErrorDialog(Context context,int status,String message) {

        boolean is_show = true;

        final Intent intent = new Intent();

        String btnTitle = "";

        switch (status) {

    case 0:
        //获取协议内容失败
        is_show = false;
        break;

    case 1002:
        // 未登录状态
        btnTitle = "去登录";
        intent.setClass(context, loginActivity.class);
        break;

    case 1003:
        //已经掉线
        btnTitle = "去登录";
        intent.setClass(context, loginActivity.class);
        break;

    case 1004:

        //未实名认证
        btnTitle = "去认证";
        intent.setClass(context, PeopleInfoSmrz.class);

        break;

    case 1005:

        //未绑定银行卡
        btnTitle = "去绑定";
        intent.setClass(context, AddBankCardActivity.class);
        break;
    case 1006:
        //未设置交易密码
        btnTitle = "去设置";
        intent.setClass(context, ChangeTenderPasswordActivity.class);

        break;

    case 1007:
//                //未绑定托管
//                message = "你还没有绑定托管！";
//                btnTitle = "去绑定";
//                intent.setClass(context,ChangeTenderPasswordActivity.class);
//                break;

    case 1008:
        //余额不足
        btnTitle = "去充值";
        intent.setClass(context, ThirdPayActivity.class);
        break;
    case 1009:
        //未申请vip
//                message = "你还没有设置交易密码！";
//                btnTitle = "去设置";
//                intent.setClass(context,.class);

        break;
    case 1010:
        //未完善资料
        btnTitle = "去完善";
        intent.setClass(context, PersoninfoActivity.class);
        break;
    case 1011:
        //为申请信用额度不足，请申请信用额度！";
        btnTitle = "去申请";
        intent.setClass(context, ApplyForLinesActivity.class);
        break;
    case 1012:
        //未选择借款类型
        btnTitle = "去选择";
        intent.setClass(context, UserInfoDeatilActivity.class);
        break;


        }


        if(is_show) {
            CommonDialog.Builder ibuilder = new CommonDialog.Builder(context);
            ibuilder.setTitle(R.string.prompt);
            ibuilder.setMessage(message);
            ibuilder.setPositiveButton(btnTitle, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();
                    MainTabNewActivity.mainTabNewActivity.startActivity(intent);

                }
            });
            ibuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    MainTabNewActivity.mainTabNewActivity.IndexView();

                }
            });
            ibuilder.create().show();
        }else{

            MainTabNewActivity.mainTabNewActivity.showCustomToast(message);

        }

    }
    //判断手机格式是否正确
    public static boolean isMobileNO(String mobiles) {
        String telRegex = "^((13[0-9])|(15[^4,\\D])|(18[0-3,5-9])|(14[5,7])|(17[0,5,7]))\\d{8}$";
//        String telRegex = "13\\d{9}|14[57]\\d{8}|15[012356789]\\d{8}|18[01256789]\\d{8}|17[0678]\\d{8}";
        if (TextUtils.isEmpty(mobiles)) return false;
        else return mobiles.matches(telRegex);
    }

    public static void setDfText(TextView tv, Context context, int style, int start, int end) {
        SpannableString styledText = new SpannableString(tv.getText().toString());
        styledText.setSpan(new TextAppearanceSpan(context, style), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        tv.setText(styledText, TextView.BufferType.SPANNABLE);
    }
    /***
     * 根据银行卡id获取银行卡名称
     */
    public static String getBankCardName(int id){
        String name = "";
        switch (id){
            case 1:
                name = "中国银行";
                break;
            case 2:
                name = "工商银行";
                break;
            case 3:
                name = "农业银行";
                break;
            case 4:
                name = "交通银行";
                break;
            case 5:
                name = "广发银行";
                break;
            case 6:
                name = "深发银行";
                break;
            case 7:
                name = "建设银行";
                break;
            case 8:
                name = "上海浦发银行";
                break;
            case 10:
                name = "招商银行";
                break;
            case 11:
                name = "邮政储蓄银行";
                break;
            case 12:
                name = "民生银行";
                break;
            case 13:
                name = "兴业银行";
                break;
            case 14:
                name = "广东发展银行";
                break;
            case 16:
                name = "深圳发展银行";
                break;
            case 17:
                name = "中信银行";
                break;
            case 18:
                name = "华夏银行";
                break;
            case 19:
                name = "中国光大银行";
                break;
            case 20:
                name = "北京银行";
                break;
            case 25:
                name = "宁波银行";
                break;
            case 28:
                name = "平安银行";
                break;
            case 54:
                name = "郑州银行";
                break;
            default:
                name = "其他银行";
                break;
        }
        return name;
    }
    /***
     * 根据银行卡id获取银行卡名称
     */
    public static int getBankCardPhoto(int id){
        int name = R.drawable.bank_1;
        switch (id){
            case 1:
                name = R.drawable.b_bank_1;
                break;
            case 2:
                name = R.drawable.b_bank_2;
                break;
            case 3:
                name = R.drawable.b_bank_3;
                break;
            case 4:
                name = R.drawable.b_bank_4;
                break;
            case 5:
                name = R.drawable.b_bank_5;
                break;
            case 6:
                name = R.drawable.b_bank_6;
                break;
            case 7:
                name = R.drawable.b_bank_7;
                break;
            case 8:
                name = R.drawable.b_bank_8;
                break;
            case 10:
                name = R.drawable.b_bank_10;
                break;
            case 11:
                name = R.drawable.b_bank_11;
                break;
            case 12:
                name = R.drawable.b_bank_12;
                break;
            case 13:
                name = R.drawable.b_bank_13;
                break;
            case 14:
                name = R.drawable.b_bank_14;
                break;
            case 16:
                name = R.drawable.b_bank_16;
                break;
            case 17:
                name = R.drawable.b_bank_17;
                break;
            case 18:
                name = R.drawable.b_bank_18;
                break;
            case 19:
                name = R.drawable.b_bank_19;
                break;
            case 20:
                name = R.drawable.b_bank_20;
                break;
            case 25:
                name = R.drawable.b_bank_25;
                break;
            case 28:
                name = R.drawable.b_bank_28;
                break;
            case 54:
                name = R.drawable.b_bank_54;
                break;
            default:
                name = R.drawable.b_bank_1;
                break;
        }
        return name;
    }

    public static String getBankcode(String code){
        String bankcode = code;
        if(code.length()>8){
            bankcode= code.substring(0,4)+"********"+code.substring(code.length()-4,code.length());
        }
        return bankcode;
    }

}
