package com.lmq.main.activity.user.manager.tenderlogs;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.xyh.R;
import com.lmq.http.BaseHttpClient;
import com.lmq.http.JsonHttpResponseHandler;
import com.lmq.main.api.BaseActivity;
import com.lmq.main.api.JsonBuilder;
import com.lmq.main.api.MyLog;
import com.lmq.main.enmu.LHBLogsType;
import com.lmq.main.item.LHBItem;
import com.lmq.main.util.Default;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
/***
 * 资金记录--详细信息
 * @author sjc
 *
 */
public class MoneyRedcordDetailActivity extends BaseActivity implements OnClickListener{

    /**投资金额*/
    private TextView tv_money;
    /**项目编号*/
    private TextView tv_batch_no;
    /**备注*/
    private TextView tv_remark;
    /**发生时间*/
    private TextView tv_add_time;

    private String remark;
    private String money;
    private String add_time;
    private String batch_no;
    private int Redeem;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.peopele_jl_detail);
        
        TextView text = (TextView) findViewById(R.id.title);
        text.setText(R.string.lhb_detail_title);

        findViewById(R.id.back).setOnClickListener(this);

        Intent intent = getIntent();
       
        if (intent.hasExtra("remark")) {
        	
        	remark = intent.getStringExtra("remark");
        }
        if (intent.hasExtra("money")) {
        	
        	money = intent.getStringExtra("money");
        	
        }
        if (intent.hasExtra("add_time")) {
        	
        	add_time = intent.getStringExtra("add_time");
        }
        if (intent.hasExtra("batch_no")) {
        	
        	batch_no = intent.getStringExtra("batch_no");
        }
        if (intent.hasExtra("Redeem")) {
        	
        	Redeem = intent.getIntExtra("Redeem", 0);
        }

       

       
        tv_money = (TextView) findViewById(R.id.money);
        tv_batch_no = (TextView) findViewById(R.id.batch_no);
        tv_add_time = (TextView) findViewById(R.id.add_time);
        tv_remark = (TextView) findViewById(R.id.remark);

    }

    @Override
    protected void onResume() {
        super.onResume();
        
        if (Redeem==0){
        	tv_money.setTextColor(getResources().getColor(R.color.green));
        	tv_money.setText("-" + money);
        	
        }else{
        	tv_money.setTextColor(getResources().getColor(R.color.red));
        	tv_money.setText("+" + money);
        }
        tv_remark.setText(remark);
        tv_add_time.setText(add_time);
        tv_batch_no.setText(batch_no);
        tv_add_time.setText(add_time);
      

    }

    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.back:
                finish(0);
                break;
        

        }
    }
    public void finish() {
        super.finish();
    }

}
