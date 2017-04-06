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


public class LHBHintDialog {
	private Context context;
	private PopupWindow popupWindow;
	private Button cButton, sButton;
	private OnClickListener mClickListener;
	private TextView titleView;
	
	private static TextView tv_user_money;
	private TextView tv_zf_money;
	private TextView tv_yjsy;
	private TextView tv_term;
	private EditText passwrod;
	private Object object;

	private String user_money;
	private String zf_money;
	private String yjsy;
	private String term;

	public LHBHintDialog(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;

		View view = LayoutInflater.from(context).inflate(
				R.layout.lhb_dialog_layout, null);
		cButton = (Button) view.findViewById(R.id.dialog_cancle);
		sButton = (Button) view.findViewById(R.id.dialog_submit);
		titleView = (TextView) view.findViewById(R.id.dialog_title);
		tv_user_money = (TextView) view.findViewById(R.id.user_money);
		tv_zf_money = (TextView) view.findViewById(R.id.zf_money);
		tv_yjsy = (TextView) view.findViewById(R.id.yjsy);
		tv_term = (TextView) view.findViewById(R.id.term);
		passwrod = (EditText) view.findViewById(R.id.dialog_password);


		popupWindow = new PopupWindow(view, LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);

	}

	public void setInfo(String user_money,String money,String yjsy,String fcqx){



		tv_user_money.setText(user_money);
		tv_zf_money.setText(money);
		tv_yjsy.setText(yjsy);
		tv_term.setText(fcqx);


	}

	public void setObject(Object obj){
		this.object = obj;
	}

	public Object getObject(){

		return this.object;
	}

	public void clearPassword(){

		passwrod.setText("");
	}
	public void setonClickListener(OnClickListener listener) {

		sButton.setOnClickListener(listener);
		cButton.setOnClickListener(listener);
	}
	

	public void setDialogTitle(String title){

		titleView.setText(title);

	}

	public String getPassword(){

		return passwrod.getText().toString();

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

	public String getUser_money() {
		return user_money;
	}

	public void setUser_money(String user_money) {
		this.user_money = user_money;
	}

	public String getZf_money() {
		return zf_money;
	}

	public void setZf_money(String zf_money) {
		this.zf_money = zf_money;
	}

	public String getYjsy() {
		return yjsy;
	}

	public void setYjsy(String yjsy) {
		this.yjsy = yjsy;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	

}
