package com.lmq.main.activity.investmanager.reward;

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
 * 奖励记录--详细信息
 * @author sjc
 *
 */
public class RewardWithCouponDetailActivity extends BaseActivity implements OnClickListener{


    private TextView tv_exp_type;
    private TextView tv_money;
    /**使用状态*/
    private TextView tv_rewardtzjf_type;
    /**奖励时间*/
    private TextView tv_rewardtzjf_time;
    /**获取详情*/
    private TextView tv_rewardtzjf_detail;

    private String remark;
    private String add_time;
    private String money;
    private int status;
    private String exp_type;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reward_rewards_record_detail);
        
        TextView text = (TextView) findViewById(R.id.title);
        text.setText(R.string.lhb_detail_title);

        findViewById(R.id.back).setOnClickListener(this);

        Intent intent = getIntent();
        if (intent.hasExtra("remark")) {

        	remark = intent.getStringExtra("remark");
        }
        if (intent.hasExtra("add_time")) {
        	
        	add_time = intent.getStringExtra("add_time");
        }
        if (intent.hasExtra("money")) {
        	
        	money = intent.getStringExtra("money");
        }
        if (intent.hasExtra("status")) {
        	
        	status = intent.getIntExtra("status", 0);
        }
        if (intent.hasExtra("exp_type")) {
        	
        	exp_type = intent.getStringExtra("exp_type");
        }


        tv_exp_type = (TextView) findViewById(R.id.exp_type);
        tv_money = (TextView) findViewById(R.id.money);
        tv_rewardtzjf_type = (TextView) findViewById(R.id.tv_rewardtzjf_type);
        tv_rewardtzjf_time = (TextView) findViewById(R.id.tv_rewardtzjf_time);
        tv_rewardtzjf_detail = (TextView) findViewById(R.id.tv_rewardtzjf_detail);


    }

    @Override
    protected void onResume() {
        super.onResume();
        tv_exp_type.setText(exp_type);
        tv_money.setText(money);
        tv_rewardtzjf_time.setText(add_time);
        tv_rewardtzjf_detail.setText(remark);
        /** 0未使用 1已使用 2已过期*/
		if (status==0) {

			tv_rewardtzjf_type.setText("未使用");
			
		}else if(status==1){
			tv_rewardtzjf_type.setText("已使用");
		}else{
			tv_rewardtzjf_type.setText("已过期");
			
		}
//		if (status.equals(0)) {
//			
//			tv_rewardtzjf_type.setText("未使用");
//			
//		}else if(status.equals(1)){
//			tv_rewardtzjf_type.setText("已使用");
//		}else{
//			tv_rewardtzjf_type.setText("已过期");
//			
//		}
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
