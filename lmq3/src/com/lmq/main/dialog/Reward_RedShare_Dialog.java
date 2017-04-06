package com.lmq.main.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.xyh.R;

import java.util.ArrayList;

public class Reward_RedShare_Dialog {
	private Context context;
	private PopupWindow popupWindow;
	private Button cButton, sButton;
	private OnClickListener mClickListener;
	private TextView titleView;
	private TextView tv_url;
	private Object object;
	
	private String url;
	

	public Reward_RedShare_Dialog(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;

		View view = LayoutInflater.from(context).inflate(
				R.layout.redbagshare_dialog_layout, null);
		cButton = (Button) view.findViewById(R.id.dialog_cancle);
		sButton = (Button) view.findViewById(R.id.dialog_submit);
		titleView = (TextView) view.findViewById(R.id.dialog_title);
		tv_url = (TextView) view.findViewById(R.id.url);


		popupWindow = new PopupWindow(view, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);

	}

	public void setInfo(String url){

		tv_url.setText(url);

	}

	public void setObject(Object obj){
		this.object = obj;
	}

	public Object getObject(){

		return this.object;
	}

	public void setonClickListener(OnClickListener listener) {

		sButton.setOnClickListener(listener);
		cButton.setOnClickListener(listener);
	}
	

	public void setDialogTitle(String title){

		titleView.setText(title);

	}


	// 下拉式 弹出 pop菜单 parent 右下角
	public void showAsDropDown(View parent) {



		popupWindow.showAtLocation(parent,Gravity.CENTER,0,0);


		// 使其聚集
		popupWindow.setFocusable(true);
		// 设置允许在外点击消失
		popupWindow.setOutsideTouchable(false);
		// 刷新状态
		popupWindow.update();
	}

    public boolean isShowing(){

        return popupWindow.isShowing();

    }
	// 隐藏菜单
	public void dismiss() {
		popupWindow.dismiss();
	}


}
