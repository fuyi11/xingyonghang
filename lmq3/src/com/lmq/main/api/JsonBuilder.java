package com.lmq.main.api;

import org.json.JSONException;
import org.json.JSONObject;

import com.lmq.main.util.Default;

public class JsonBuilder
{
    
    private JSONObject json = null;
    
    public JsonBuilder()
    {
        json = new JSONObject();   
        put("uid", Default.userId);//这块默认每个协议都有
    }
    public JSONObject getJson()
    {
        return json;
    }
    
    public void put(String name, boolean value)
    {
        try
        {
            json.put(name, value);
        }
        catch (JSONException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public void put(String name, int value)
    {
        try
        {
            json.put(name, value);
        }
        catch (JSONException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public void put(String name, long value)
    {
        try
        {
            json.put(name, value);
        }
        catch (JSONException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public void put(String name, double value)
    {
        try
        {
            json.put(name, value);
        }
        catch (JSONException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public void put(String name, Object value)
    {
        try
        {
            json.put(name, value);
        }
        catch (JSONException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public void putOpt(String name, Object value)
    {
        try
        {
            json.putOpt(name, value);
        }
        catch (JSONException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public String toJsonString()
    {
        return json.toString();
    }
}
