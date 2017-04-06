package com.lmq.main.dialog;

import android.app.Dialog;
import android.content.Context;

public class MyProgressDialog extends Dialog
{
    
    public MyProgressDialog(Context context)
    {
        super(context);
        // TODO Auto-generated constructor stub
    }
    
    public MyProgressDialog(Context context, int theme)
    {
        super(context, theme);
        // TODO Auto-generated constructor stub
    }
    
    public MyProgressDialog(Context context, boolean cancelable,
            OnCancelListener cancelListener)
    {
        super(context, cancelable, cancelListener);
        // TODO Auto-generated constructor stub
    }
    
    

    @Override
    public void setContentView(int layoutResID)
    { 
        super.setContentView(layoutResID);
//        this.setContentView(view, params);
        init();
    }
    
    public void init()
    {
        this.setCancelable(false); 
    }
    
    
    
    
}
