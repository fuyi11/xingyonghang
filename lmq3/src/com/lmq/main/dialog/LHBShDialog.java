package com.lmq.main.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.xyh.R;

public class LHBShDialog {
	private Context context;
	private PopupWindow popupWindow;
	private Button cButton, sButton;
	private OnClickListener mClickListener;
	private TextView titleView;
	private EditText lh_sh_money;
	private TextView lh_sh_show;
	private Object object;






	public LHBShDialog(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;

		View view = LayoutInflater.from(context).inflate(
				R.layout.lhb_sh_dialog_layout, null);
		cButton = (Button) view.findViewById(R.id.dialog_cancle);
		sButton = (Button) view.findViewById(R.id.dialog_submit);
		titleView = (TextView) view.findViewById(R.id.dialog_title);
		lh_sh_money = (EditText) view.findViewById(R.id.lhb_sh_money);
		lh_sh_show = (TextView) view.findViewById(R.id.lhb_shje_show);

		popupWindow = new PopupWindow(view, LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);

	}



	public void setObject(Object obj){
		this.object = obj;
	}

	public Object getObject(){

		return this.object;
	}

	public void clearUserInput(){

		lh_sh_money.setText("");
	}
	public void setonClickListener(OnClickListener listener) {

		sButton.setOnClickListener(listener);
		cButton.setOnClickListener(listener);
	}
	

	public void setDialogTitle(String title){

		titleView.setText(title);

	}

	public String geteUserInput(){

		return lh_sh_money.getText().toString();

	}

	public void setShowMoney(String money){

		lh_sh_show.setText(money+"元");
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
