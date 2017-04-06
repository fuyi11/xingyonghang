package com.lmq.main.listener;

import org.json.JSONObject;

public interface ConnectResponseListener
{
    
    public void onConnectResponseCallback(JSONObject json);
    public void onFail(JSONObject json);
    
}
