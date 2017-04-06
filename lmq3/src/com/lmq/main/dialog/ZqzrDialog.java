package com.lmq.main.dialog;

import java.text.DecimalFormat;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xyh.R;
import com.lmq.main.item.InvestManageItem;
import com.lmq.main.util.Default;

public class ZqzrDialog extends BaseDialog {

	private Button mButton;
	private android.view.View.OnClickListener mListener;
	// private EditText mEditText;

	// private String info;

	private double price, dq_money, account_money;
	private String password;
	private EditText pin;
	private EditText buy_money;

    private View dilalog_one_view ;
    private View dilalog_two_view ;
    private View dilalog_three_view ;
    private Button cancleBtn;

    private InvestManageItem item;

	/**
	 * 
	 * @param context
	 * @param dq_money
	 *            债券总额
	 * @param price
	 *            债券转让金额
	 * @param account_money
	 *            用户账户余额
	 * @param password
	 *            支付密码
	 */
	public ZqzrDialog(Context context, final double dq_money, final double price,
			double account_money, String password) {
		super(context);

		this.dq_money = dq_money;
		this.price = price;
		this.account_money = account_money;
		this.password = password;

		init();
	}

    public ZqzrDialog(Context context) {
        super(context);

        initCancleView();
    }

    private void initCancleView(){
        setContentView(R.layout.diloag_zqzr);
        mButton = (Button) findViewById(R.id.gm);
        pin = (EditText) findViewById(R.id.zq_item_pin);

        dilalog_one_view = findViewById(R.id.dilalog_one_view);
        dilalog_two_view = findViewById(R.id.dilalog_two_view);
        dilalog_three_view = findViewById(R.id.dilalog_three_view);

        dilalog_one_view.setVisibility(View.GONE);
        dilalog_three_view.setVisibility(View.GONE);
        dilalog_one_view.setVisibility(View.GONE);

        cancleBtn = (Button) findViewById(R.id.cancle);
        cancleBtn.setVisibility(View.VISIBLE);



    }

	private void init() {
		setContentView(R.layout.diloag_zqzr);
		mButton = (Button) findViewById(R.id.gm);
		TextView ze = (TextView) findViewById(R.id.ze);
		TextView jg = (TextView) findViewById(R.id.jg);
		TextView ye = (TextView) findViewById(R.id.ye);
		pin = (EditText) findViewById(R.id.zq_item_pin);
		buy_money = (EditText) findViewById(R.id.jg2);
		LinearLayout lv_psw = (LinearLayout) findViewById(R.id.lv_psw);
		if(Default.NEW_VERSION){
			buy_money.setVisibility(View.VISIBLE);
			jg.setVisibility(View.GONE);
		}

		pin.setFocusable(true);


		DecimalFormat d = new DecimalFormat("##0.00");
		ze.setText(d.format(price) + "");
		jg.setText(d.format(dq_money) + "");
		ye.setText(d.format(account_money)+ "");



	}

	public void setListener(android.view.View.OnClickListener listener) {
		mListener = listener;
		mButton.setOnClickListener(mListener);
        cancleBtn.setOnClickListener(mListener);
	}

	public void dismiss() {
		super.dismiss();
	}

	public String getPassword() {
		return pin.getText().toString();
	}
	
	
	public String getBugMoney(){
		
		return buy_money.getText().toString();
	}

    public InvestManageItem getItem() {
        return item;
    }

    public void setItem(InvestManageItem item) {
        this.item = item;
    }
}
