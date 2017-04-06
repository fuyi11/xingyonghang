package com.lmq.main.dialog;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
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

public class Integration_DH_Dialog {
	private Context context;
	private PopupWindow popupWindow;
	private Button cButton, sButton;
	private OnClickListener mClickListener;
	private TextView titleView;
	
	private TextView tv_money;
	private TextView tv_xhjf;
	private TextView tv_kyjf;
	private EditText dh_number;
	private Object object;

	private String money;
	/**消耗积分*/
	private String xhjf;
	/**可用积分*/
	private String kyjf;


	public Integration_DH_Dialog(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;

		View view = LayoutInflater.from(context).inflate(
				R.layout.integration_dh__dialog_layout, null);
		cButton = (Button) view.findViewById(R.id.dialog_cancle);
		sButton = (Button) view.findViewById(R.id.dialog_submit);
		titleView = (TextView) view.findViewById(R.id.dialog_title);
		
		tv_money = (TextView) view.findViewById(R.id.money);
		tv_xhjf = (TextView) view.findViewById(R.id.xhjf);
		tv_kyjf = (TextView) view.findViewById(R.id.kyjf);
		dh_number = (EditText) view.findViewById(R.id.dh_number);


		popupWindow = new PopupWindow(view, LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);

		ColorDrawable cd = new ColorDrawable(0x000000);
		popupWindow.setBackgroundDrawable(cd);


	}

	public void setInfo(String money,String xhjf,String kyjf){



		tv_money.setText(money);
		tv_xhjf.setText(xhjf);
		tv_kyjf.setText(kyjf);


	}
	public String getDh_number(){

		return dh_number.getText().toString();

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
	public TextView getTitleView() {
		return titleView;
	}
	public String getMoney() {
		return money;
	}

	public void setMoney(String money) {
		this.money = money;
	}

	public String getXhjf() {
		return xhjf;
	}

	public void setXhjf(String xhjf) {
		this.xhjf = xhjf;
	}

	public String getKyjf() {
		return kyjf;
	}

	public void setKyjf(String kyjf) {
		this.kyjf = kyjf;
	}
	
	

}
