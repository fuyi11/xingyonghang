package com.lmq.main.api;

import java.util.Timer;
import java.util.TimerTask;

import com.lmq.main.listener.TimerListener;
 

public class Time
{
    
    String      curTime; 
    private int mTime[] = new int[4];
    
    private Timer timer;
    
    private TimerListener listener;
    
    public Time(String value)
    {
        curTime = value;
        timer = new Timer(true);
        timer.schedule(new TimerTask()
        {
            
            @Override
            public void run()
            {
                if(getTime() == null)
                    return ;
                
                JsonBuilder builder = new JsonBuilder();
                builder.put("day", mTime[0]);
                builder.put("hours", mTime[1]);
                builder.put("minutes", mTime[2]);
                builder.put("seconds", mTime[3]);
               listener.onBack(builder.getJson());
            }
        }, 1000,1000);
    }
    
    public void setListener(TimerListener listener)
    {
        this.listener = listener;
    }
    
    public int[] getTime()
    {
        String temp[] = SystenmApi.split(curTime, ",");
        if(temp.length == 1&&temp[0].equals("已结束"))
        {
            this.stop();
            return null;
        }
        
        for (int i = temp.length - 1; i >= 0; i--)
        {
            mTime[i] = Integer.parseInt(temp[i]);
        }
        mTime[3]--;
        if (mTime[3] <= 0)// 秒
        {
            mTime[3] = 60;
            mTime[2]--;
            
            if (mTime[2] <= 0)// 分
            {
                mTime[2] = 60;
                mTime[1]--;
            }
            if (mTime[1] <= 0)// 时
            {
                mTime[1] = 24;
                mTime[0]--;
            }
            if (mTime[0] <= 0)//天
            {
                mTime[0] = 0;
            }
        }
        curTime = mTime[0]+","+mTime[1]+","+mTime[2]+","+mTime[3];
//        MyLog.d("zzx", curTime);
        return mTime;
    }
    
    public void stop()
    {
        timer.cancel();
        timer.purge();
    }
}
