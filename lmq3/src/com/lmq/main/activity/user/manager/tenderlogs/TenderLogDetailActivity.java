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
 * 交易记录--详细信息
 * @author sjc
 *
 */
public class TenderLogDetailActivity extends BaseActivity implements OnClickListener{

    /**消息类型*/
    private TextView tv_type;
    /**影响金额*/
    private TextView tv_affect_money;
    /**可用资金*/
    private TextView tv_account_money;
    /**待收本金*/
    private TextView tv_collect_money;
    /**冻结资金*/
    private TextView tv_freeze_money;
    /**详细信息*/
    private TextView tv_info;
    /**发生时间*/
    private TextView tv_add_time;

    private String type;
    private String add_time;
    /**影响金额*/
    private String affect_money;
    /**可用资金*/
    private String account_money;
    /**待收本金*/
    private String collect_money;
    /**冻结资金*/
    private String freeze_money;
    private String info;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.peopele_jy_detail);
        
        TextView text = (TextView) findViewById(R.id.title);
        text.setText(R.string.lhb_detail_title);

        findViewById(R.id.back).setOnClickListener(this);

        Intent intent = getIntent();
        if (intent.hasExtra("type")) {

        	type = intent.getStringExtra("type");
        }
        if (intent.hasExtra("affect_money")) {
        	
        	affect_money = intent.getStringExtra("affect_money");
        }
        if (intent.hasExtra("account_money")) {
        	
        	account_money = intent.getStringExtra("account_money");
        }
       
        if (intent.hasExtra("collect_money")) {
        	
        	collect_money = intent.getStringExtra("collect_money");
        }
        if (intent.hasExtra("freeze_money")) {
        	
        	freeze_money = intent.getStringExtra("freeze_money");
        }
        if (intent.hasExtra("add_time")) {
        	
        	add_time = intent.getStringExtra("add_time");
        }
        if (intent.hasExtra("info")) {
        	
        	info = intent.getStringExtra("info");
        }

       

        tv_type = (TextView) findViewById(R.id.type);
        tv_affect_money = (TextView) findViewById(R.id.affect_money);
        tv_account_money = (TextView) findViewById(R.id.account_money);
        tv_collect_money = (TextView) findViewById(R.id.collect_money);
        tv_freeze_money = (TextView) findViewById(R.id.freeze_money);
        tv_info = (TextView) findViewById(R.id.info);
        tv_add_time = (TextView) findViewById(R.id.add_time);


    }

    @Override
    protected void onResume() {
        super.onResume();
        tv_type.setText(type);
        tv_affect_money.setText(affect_money);
        
        if (affect_money.lastIndexOf("-") >= 0) {
        	tv_affect_money.setTextColor(getResources()
					.getColor(R.color.green));
		} else {
			tv_affect_money.setTextColor(getResources().getColor(R.color.red));
			
		}
        tv_account_money.setText(account_money+"元");
        tv_collect_money.setText(collect_money);
        tv_freeze_money.setText(freeze_money);
        tv_info.setText(info);
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
