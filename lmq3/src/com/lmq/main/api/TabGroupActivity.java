package com.lmq.main.api;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ActivityGroup;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;

import com.xyh.R;

public class TabGroupActivity extends ActivityGroup
{
    
    private ArrayList<String> mIdList;
    private String            TAG = "TabGroup";
    
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (mIdList == null)
            mIdList = new ArrayList<String>();
    }
    
    public void finishFromChild(Activity child)
    {
        LocalActivityManager manager = getLocalActivityManager();
        int index = mIdList.size() - 1;
        if (index < 1)
        {
            finish();
            return;
        }
        
        manager.destroyActivity(mIdList.get(index), true);
        mIdList.remove(index);
        index--;
        String lastId = mIdList.get(index);
        Intent lastIntent = manager.getActivity(lastId).getIntent();
        
        lastIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        Window newWindow = manager.startActivity(lastId, lastIntent);
        setContentView(newWindow.getDecorView());
    }
    
    public void startChildActivity(String id, Intent intent)
    { 
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        LocalActivityManager manager = getLocalActivityManager();
        
        Window window = manager.startActivity(id, intent);
        View temp = window.getDecorView(); 
         
        
        if (window != null)
        {
            mIdList.add(id);
            setContentView(temp);
        }
        for (String temp2 : mIdList)
        {
            MyLog.d(TAG, "启动存在的页面:" + temp2);
        }
//        overridePendingTransition(R.anim.left_to_right, R.anim.to_right);
    }
    
    public void startChildActivity(String id, Intent intent,View view,int type)
    { 
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        LocalActivityManager manager = getLocalActivityManager();
        Window window = manager.startActivity(id, intent);
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
        View temp = window.getDecorView(); 
        
        if (window != null)
        {
            mIdList.add(id);
            setContentView(temp);
        }
        for (String temp2 : mIdList)
        {
            MyLog.d(TAG, "启动存在的页面:" + temp2);
        }
        
//        temp.startAnimation(AnimationUtils.loadAnimation(TabGroupActivity.this, R.anim.right_to_left));
//        view.startAnimation(AnimationUtils.loadAnimation(TabGroupActivity.this, R.anim.to_left));
        
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            if (mIdList.size() == 1)
            {
                return false;
            }
            onBackPressed();
            MyLog.d(TAG, "父类返回建立触发");
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }
    
    public void goBack()
    {
        if (mIdList.size() == 1)
        {
            onBackPressed();
        }
    }
    
    public int getActicityNum()
    {
        if (mIdList == null)
        {
            return 0;
        }
        else
        {
            return mIdList.size();
        }
    }
    
    public void onBackPressed()
    {
        int length = mIdList.size();
        if (length > 1)
        {
            Activity current = getLocalActivityManager().getActivity(
                    mIdList.get(length - 1));
            current.finish();
        }
        for (String tem : mIdList)
        {
            MyLog.d(TAG, "存在的页面" + tem);
        }
    }
    
    public void removeActivity(String name)
    {
        
        Activity current = getLocalActivityManager().getActivity(name);
        current.finish();
    }
    
}
