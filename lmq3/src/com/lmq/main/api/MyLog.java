package com.lmq.main.api;

import com.lmq.main.util.Default;

import android.util.Log;

public class MyLog 
{
    public static void d(String tag,String msg)
    {
    	if(Default.showLog)
        Log.d(tag, msg);
    }
    
    public static void v(String tag,String msg)
    {
    	if(Default.showLog)
        Log.v(tag, msg);
    }
    public static void w(String tag,String msg)
    {
    	if(Default.showLog)
        Log.w(tag, msg);
    }
    public static void e(String tag,String msg)
    {
    	if(Default.showLog)
        Log.e(tag, msg);
    }
    public static void e(String tag,String msg,Throwable t)
    {
    	if(Default.showLog)
        Log.e(tag, msg,t);
    }

    public static void e(String msg)
    {
    	if(Default.showLog)
        e("erro", msg);
    }

    public static void i(String tag,String msg)
    {
    	if(Default.showLog)
        Log.i(tag, msg);
    }
}
