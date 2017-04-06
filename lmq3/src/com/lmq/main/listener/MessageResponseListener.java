package com.lmq.main.listener;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;


public interface MessageResponseListener
{
    public abstract void OnMessageResponse(int resultCode, JSONObject jo , Intent intent) throws JSONException;
}
