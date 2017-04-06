package com.lmq.main.activity.investmanager.borrowmanager;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import android.widget.Button;
import android.widget.LinearLayout;

import android.widget.ScrollView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.xyh.R;
import com.lmq.http.BaseHttpClient;
import com.lmq.http.JsonHttpResponseHandler;
import com.lmq.main.api.BaseActivity;
import com.lmq.main.api.JsonBuilder;
import com.lmq.main.api.MyLog;
import com.lmq.main.dialog.LendManagerRepaymentDialog;

import com.lmq.main.item.Borrowing_RecordItem;

import com.lmq.main.util.Default;
import com.lmq.view.ListViewForScrollView;

/**
 * 借款管理详情页
 * @author sunjianchao
 *
 */
public class LendMoneyRecordDetailActivity extends BaseActivity implements OnClickListener {


//    private Borrowing_RecordItem item;
    private ArrayList<Borrowing_RecordItem> data;
    private ListViewForScrollView listView;
    private PullToRefreshScrollView scrollView;
    private Adapter adapter;
    private int page = 1;
    private int limit = 5;
    private int totalPage = 0;
    private TextView text;
    private TextView borrow_money;
    private TextView has_borrow;

    private String bid;
    private LendManagerRepaymentDialog dialog;
    private HashMap<String, String> allIndexInfoMap = new HashMap<String, String>();

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.lendmoney_detail_layout);

        text = (TextView) findViewById(R.id.title);
        text.setText("还款列表");

        borrow_money = (TextView) findViewById(R.id.borrow_money);
        has_borrow = (TextView) findViewById(R.id.yhtz);

        findViewById(R.id.back).setOnClickListener(this);
        Intent intent = getIntent();
        if(null != intent){

            bid = intent.getStringExtra("bid");
        }

        scrollView = (PullToRefreshScrollView) findViewById(R.id.refreshView);
        scrollView.setMode(PullToRefreshBase.Mode.BOTH);

        data = new ArrayList<Borrowing_RecordItem>();
        listView = (ListViewForScrollView) findViewById(R.id.lengmoney_detail_list);
        adapter = new Adapter();
        listView.setAdapter(adapter);

        scrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {

                if (refreshView.isHeaderShown()) {

                    resetPostParams();
                    getInvestData(getPostParams(), true);

                }

                if(refreshView.isFooterShown()){

                    if(page<totalPage){

                        page+=1;
                        getInvestData(getPostParams(),false);

                    }else{

                        handler.sendEmptyMessage(1);
                    }


                }

            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {


                Borrowing_RecordItem item = adapter.getItem(position-1);

            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        getInvestData(getPostParams(),true);
    }
    
    private JsonBuilder getPostParams() {

        JsonBuilder builder = new JsonBuilder();
        builder.put("page", page);
        builder.put("limit", limit);
        builder.put("bid", bid);

        return builder;
    }


    private JsonBuilder resetPostParams() {

        page = 0;
        limit = 5;

        return getPostParams();
    }

    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg.arg1 == 2) {
                showCustomToast(msg.getData().getString("info"));
            }
            if (msg.what == 1) {
                showCustomToast(("无更多数据！"));

                scrollView.onRefreshComplete();
            }
        }

    };

    public void LendManagerRepaymentDialog(final View view,String content){

        if(null == dialog){


            dialog = new LendManagerRepaymentDialog(LendMoneyRecordDetailActivity.this);
            dialog.setDialogTitle("还款通知");

            Integer position = (Integer)view.getTag();
            MyLog.e("eeeeeeeeeeeeeeeeeeeeeeee", position + 1 + "");
            dialog.setObject(position);
            dialog.setonClickListener(this);


        }

        dialog.setInfo(content);

        if(!dialog.isShowing()){
            dialog.showAsDropDown(view);
        }


    }

    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.back:
                finish(0);
                break;
        case R.id.dialog_submit:

            Integer position = (Integer)dialog.getObject();
            doHttpHK(v, adapter.getItem(position).getSort_order());
            dialog.dismiss();

                break;
        case R.id.dialog_cancle:

                dialog.dismiss();

                break;

           

        }

    }

    class Adapter extends BaseAdapter {


        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Borrowing_RecordItem getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;

            if (null == convertView) {

                viewHolder = new ViewHolder();
                convertView = LinearLayout.inflate(LendMoneyRecordDetailActivity.this, R.layout.lendmoney_detail_item, null);

                viewHolder.tv_sort_order = (TextView) convertView.findViewById(R.id.tv_sort_order);
                viewHolder.tv_deadline = (TextView) convertView.findViewById(R.id.tv_deadline);
                viewHolder.tv_capital = (TextView) convertView.findViewById(R.id.tv_capital);
                viewHolder.tv_interest = (TextView) convertView.findViewById(R.id.tv_interest);
                viewHolder.tv_receive = (TextView) convertView.findViewById(R.id.tv_receive);
                viewHolder.tv_substitute_money = (TextView) convertView.findViewById(R.id.tv_substitute_money);
                viewHolder.tv_expired_money_now = (TextView) convertView.findViewById(R.id.tv_expired_money_now);
                viewHolder.tv_call_fee_now = (TextView) convertView.findViewById(R.id.tv_call_fee_now);
                viewHolder.tv_need_pay = (TextView) convertView.findViewById(R.id.tv_need_pay);
                viewHolder.tv_hk = (TextView) convertView.findViewById(R.id.tv_ht);

                convertView.setTag(viewHolder);
            } else {

                viewHolder = (ViewHolder) convertView.getTag();

            }

            viewHolder.tv_sort_order.setText(adapter.getItem(position).getSort_order());

            viewHolder.tv_deadline.setText(adapter.getItem(position).getDeadline());
            viewHolder.tv_capital.setText(adapter.getItem(position).getCapital());
            viewHolder.tv_interest.setText(adapter.getItem(position).getInterest());
            viewHolder.tv_receive.setText(adapter.getItem(position).getNeed_pay()== 1?"-":adapter.getItem(position).getReceive());

            viewHolder.tv_substitute_money.setText(adapter.getItem(position).getSubstitute_money());

            viewHolder.tv_expired_money_now.setText(adapter.getItem(position).getExpired_money_now());
            viewHolder.tv_call_fee_now.setText(adapter.getItem(position).getCall_fee_now());

            viewHolder.tv_need_pay.setText(adapter.getItem(position).getNeed_pay() == 1 ? "待还款" : "已还款");

            if (adapter.getItem(position).getNeed_pay()==1){
                viewHolder.tv_hk.setBackgroundResource(R.drawable.repayment_on);
                viewHolder.tv_hk.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub

                        Intent intent = new Intent();
                        intent.putExtra("sort", adapter.getItem(position).getSort_order());
                        intent.putExtra("bid", bid);
                        intent.setClass(LendMoneyRecordDetailActivity.this, LendMoneyDialog.class);
                        startActivity(intent);


                    }
                });
            }else{
                viewHolder.tv_hk.setBackgroundResource(R.drawable.repayment_off);
                viewHolder.tv_hk.setClickable(false);
            }






            return convertView;
        }



    }



    private static class ViewHolder {

        TextView tv_sort_order;
        TextView tv_deadline;
        TextView tv_capital;
        TextView tv_interest;
        TextView tv_receive;
        TextView tv_substitute_money;
        TextView tv_expired_money_now;
        TextView tv_call_fee_now;
        TextView tv_need_pay;
        TextView tv_hk;


    }

    private void doHttpHK(final View v,String sort) {

        JsonBuilder builder = new JsonBuilder();
        builder.put("sort",sort);
        builder.put("bid",bid);

        BaseHttpClient.post(LendMoneyRecordDetailActivity.this, Default.DOPAY, builder,
                new JsonHttpResponseHandler() {

                    @Override
                    public void onStart() {
                        // TODO Auto-generated method stub
                        super.onStart();
                        showLoadingDialogNoCancle(getResources().getString(
                                R.string.toast2));
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          JSONObject json) {
                        // TODO Auto-generated method stub
                        super.onSuccess(statusCode, headers, json);

                        try {
                            MyLog.e("123", json.toString());
                            if (statusCode == 200) {
                                if (json.getInt("status") == 1) {

                                    showCustomToast(json.getString("mssage"));
                                    getInvestData(getPostParams(),true);


                                }  else if (json.getInt("status") == 0) {

                                    showCustomToast(json.getString("mssage"));

                                }else {
                                    showCustomToast(json.getString("message"));
                                }
                            } else {
                                showCustomToast(R.string.toast1);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        dismissLoadingDialog();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          String responseString, Throwable throwable) {
                        // TODO Auto-generated method stub
                        super.onFailure(statusCode, headers, responseString,
                                throwable);
                        dismissLoadingDialog();
                    }

                });

    }

    // 提交用户反馈到服务器
    public void getInvestData(JsonBuilder builder,boolean refsh) {


//        if (data.size() > 0) {
//            data.clear();
//        }
        if(refsh){
            data.clear();
        }

        BaseHttpClient.post(getBaseContext(), Default.PAYLIST, builder,
                new JsonHttpResponseHandler() {

                    @Override
                    public void onStart() {
                        // TODO Auto-generated method stub
                        super.onStart();
                        showLoadingDialogNoCancle(getResources().getString(
                                R.string.toast2));
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          JSONObject json) {
                        // TODO Auto-generated method stub
                        super.onSuccess(statusCode, headers, json);

                        try {

                            if (statusCode == 200) {
                                // 没有新版本
                                if (json.getInt("status") == 1) {

                                   /*
                                            nowPage*/
                                    if (json.has("totalPage")) {

                                        totalPage = json.optInt("totalPage", 0);
                                    }

                                    if (json.has("nowPage")) {

                                        page = json.optInt("nowPage", 0);
                                    }

                                    if (json.has("borrow_money")) {

                                        borrow_money.setText(json.optString("borrow_money", "0") + "元");
                                    }

                                    if (json.has("has_borrow")) {

                                        has_borrow.setText(json.optString("has_borrow", "0") + "元"+","+"共"+json.optString("borrow_times", "0")+"笔");
                                    }

                                    if (json.has("list")) {


                                        JSONArray array = json.getJSONArray("list");

                                        if (null != array && array.length() > 0) {


                                            for (int i = 0; i < array.length(); i++) {
                                                Borrowing_RecordItem item = new Borrowing_RecordItem(array.getJSONObject(i));
                                                data.add(item);
                                            }




                                        }

                                    } else {
                                        showCustomToast(json.getString("message"));

                                    }


                                } else if (json.getInt("status") == 0) {
                                    showCustomToast(json.getString("message"));

                                } else {
                                    showCustomToast(json.getString("message"));
                                }
                            } else {
                                showCustomToast(R.string.toast1);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        dismissLoadingDialog();
                        adapter.notifyDataSetChanged();
                        scrollView.onRefreshComplete();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          String responseString, Throwable throwable) {
                        // TODO Auto-generated method stub
                        super.onFailure(statusCode, headers, responseString,
                                throwable);
                        dismissLoadingDialog();
                        showCustomToast(responseString);
                        scrollView.onRefreshComplete();

                    }

                });

        adapter.notifyDataSetChanged();

    }

    public void finish() {
        super.finish();
    }
    
   

}
