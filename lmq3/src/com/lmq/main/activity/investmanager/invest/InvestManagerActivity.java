package com.lmq.main.activity.investmanager.invest;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.xyh.R;
import com.lmq.http.BaseHttpClient;
import com.lmq.http.JsonHttpResponseHandler;
import com.lmq.main.activity.investmanager.debet.InvestManageDetailActivity;
import com.lmq.main.api.BaseActivity;
import com.lmq.main.api.JsonBuilder;
import com.lmq.main.api.MyLog;
import com.lmq.main.api.SystenmApi;
import com.lmq.main.dialog.InvestPopList;
import com.lmq.main.item.InvestManageItem;
import com.lmq.main.util.Default;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class InvestManagerActivity extends BaseActivity implements OnClickListener {


    private InvestPopList popList;
    private int current_type = -1;
    private ArrayList<InvestManageItem> data;
    private PullToRefreshListView listView;
    private Adapter adapter;
    private int page = 1;
    private int limit = 6;
    private int production = 0;
    private int tz_type = 0;
    private int hk_type = 4;
    private int totalPage = 0;

    /**下拉菜单记录**/
    private int cp_select = 0;
    private int tz_select = 0;
    private int hk_select = 1;

    private TextView text;


    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.invest_manager_main_layout);

        text = (TextView) findViewById(R.id.title);
        text.setText(R.string.invest_manager);


        findViewById(R.id.back).setOnClickListener(this);
        findViewById(R.id.manage_btn_1).setOnClickListener(this);
        findViewById(R.id.manage_btn_2).setOnClickListener(this);
        findViewById(R.id.manage_btn_3).setOnClickListener(this);

        data = new ArrayList<InvestManageItem>();
        listView = (PullToRefreshListView) findViewById(R.id.invest_manager_list);
        listView.setMode(PullToRefreshBase.Mode.BOTH);
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                if(refreshView.isHeaderShown()){

                    resetPostParams(production);
                    getInvestData(getPostParams(),true);
                }
                if(refreshView.isFooterShown()){

                    if(page<totalPage){

                        page+=1;
                        getInvestData(getPostParams(),false);

                    }else{
//                        showCustomToast("暂无更多数据！");
//                        listView.onRefreshComplete();
                        handler.sendEmptyMessage(1);
                    }
                }
            }
        });
        adapter = new Adapter();
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                position = position - 1;

                if(!adapter.getItem(position).getDeadline().equals("1970-01-01")){

                    Intent intent = new Intent();
                    intent.setClass(InvestManagerActivity.this,InvestManageDetailActivity.class);
                    intent.putExtra("id",adapter.getItem(position).getBorrow_id());
                    startActivity(intent);
                }else {
                    showCustomToast("该标还未进行还款");
                }

            }
        });



        popList = new InvestPopList(InvestManagerActivity.this);
        popList.setOnItemClickLinstener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (position != popList.getDefauleSelect()) {
                    popList.setDefauleSelect(position);

                }
                popList.dissmiss();

                switch (current_type){

                    case 0:
                        production = position;
                        changetLX(position);

                        break;
                    case 1:
                        tz_select = position;
                        tz_type = position;
                        break;
                    case 2:


                        hk_select = position;
                        changetHK(position);

                        break;
                }
                getInvestData(getPostParams(),true);

            }
        });


    }

    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);


            if (msg.what == 1) {
                showCustomToast(("无更多数据！"));

                listView.onRefreshComplete();
            }
        }

    };
    private void changetHK(int position){

        switch (position){

            case 0:
                hk_type = 2;
                break;
            case 1:
                hk_type = 4;
                break;
            case 2:
                hk_type = 1;
                break;
            case 3:
                hk_type = 14;
                break;

        }

    }
    private void changetLX(int position) {

        switch (position) {

            case 0:
                production = 0;
                break;
            case 1:
                production = 6;
                break;


        }
    }

    private JsonBuilder getPostParams(){

        JsonBuilder builder = new JsonBuilder();
        builder.put("page",page);
        builder.put("limit",limit);
        builder.put("production",production);
        builder.put("t",tz_type);
        builder.put("k",hk_type);

        return builder;
    }


    private JsonBuilder resetPostParams(int production){

        page = 0;
        limit = 6;
        this.production = production;
//        tz_type = 0;
//        hk_type = 2;

        return  getPostParams();
    }


    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.back:
                finish(0);
                break;
            case R.id.manage_btn_1:
                showPopList(0, v);

                break;
            case R.id.manage_btn_2:
                showPopList(1, v);

                break;
            case R.id.manage_btn_3:
                showPopList(2, v);

                break;
        }

    }


    private void showPopList(int type, View view) {


        if (current_type != type) {


            if (popList.isShowing()) {

                popList.dissmiss();
            }

            popList.clearData();



            if (type == 0) {
                popList.setDefauleSelect(cp_select);
                popList.addItems(getResources().getStringArray(R.array.invest_type));

            } else if (type == 1) {
                popList.setDefauleSelect(tz_select);
                popList.addItems(getResources().getStringArray(R.array.tz_type));


            } else if (type == 2) {
                popList.setDefauleSelect(hk_select);
                popList.addItems(getResources().getStringArray(R.array.hk_type));


            }

            current_type = type;


        }

        popList.showPOpList(view);


    }

    class Adapter extends BaseAdapter {


        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public InvestManageItem getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MyLog.e("加载VIew");
            ViewHolder viewHolder = null;

            if (null == convertView) {

                viewHolder = new ViewHolder();
                convertView = LinearLayout.inflate(InvestManagerActivity.this, R.layout.invest_manager_item, null);

                viewHolder.invest_manager_item_tbmc = (TextView) convertView.findViewById(R.id.invest_manager_item_tbmc);
                viewHolder.invest_manager_item_ztje = (TextView) convertView.findViewById(R.id.invest_manager_item_ztje);
                viewHolder.invest_manager_item_nhll = (TextView) convertView.findViewById(R.id.invest_manager_item_nhll);
                viewHolder.invest_manager_item_tzsj = (TextView) convertView.findViewById(R.id.invest_manager_item_tzsj);
                viewHolder.invest_manager_item_dqsj = (TextView) convertView.findViewById(R.id.invest_manager_item_dqsj);
                viewHolder.invest_manager_item_dbjg = (TextView) convertView.findViewById(R.id.invest_manager_item_dbjg);

                convertView.setTag(viewHolder);
            } else {

                viewHolder = (ViewHolder) convertView.getTag();

            }

            viewHolder.invest_manager_item_tbmc.setText(adapter.getItem(position).getBorrow_name());
            viewHolder.invest_manager_item_ztje.setText(adapter.getItem(position).getInvestor_capital());
            viewHolder.invest_manager_item_nhll.setText(adapter.getItem(position).getBorrow_interest_rate()+"%");
            viewHolder.invest_manager_item_tzsj.setText(adapter.getItem(position).getInvest_time().equals("1970-01-01")?"-":adapter.getItem(position).getInvest_time());
            viewHolder.invest_manager_item_dqsj.setText(adapter.getItem(position).getDeadline().equals("1970-01-01")?"-":adapter.getItem(position).getDeadline());
//            viewHolder.invest_manager_item_tzsj.setText(adapter.getItem(position).getInvest_time());
//            viewHolder.invest_manager_item_dqsj.setText(adapter.getItem(position).getDeadline());
            viewHolder.invest_manager_item_dbjg.setText(adapter.getItem(position).getDanbao());


            return convertView;
        }


    }

    private static class ViewHolder {


        TextView invest_manager_item_tbmc;
        TextView invest_manager_item_ztje;
        TextView invest_manager_item_nhll;
        TextView invest_manager_item_tzsj;
        TextView invest_manager_item_dqsj;
        TextView invest_manager_item_dbjg;


    }


    @Override
    protected void onResume() {
        super.onResume();
        getInvestData(getPostParams(),true);
    }

    // 提交用户反馈到服务器
    public void getInvestData(JsonBuilder builder,boolean refsh) {

        if(refsh){
            data.clear();
        }
        BaseHttpClient.NO_RAS=true;
        BaseHttpClient.post(getBaseContext(), Default.invest_index, builder,
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

                            MyLog.e("获取数据", json.toString());
                            if (statusCode == 200) {
                                // 没有新版本
                                if (json.getInt("status") == 1) {

                                    MyLog.e("状态值");

                                    if (json.has("totalPage")) {

                                        totalPage = json.optInt("totalPage", 0);
                                    }

                                    if (json.has("nowPage")) {

                                        page = json.optInt("nowPage", 0);
                                    }

                                    if (json.has("list")) {

                                        MyLog.e("获取List");
                                        JSONArray array = json.getJSONArray("list");
                                        for (int i = 0; i < array.length(); i++) {
                                            InvestManageItem item = new InvestManageItem(array.getJSONObject(i));
                                            data.add(item);
                                            MyLog.e("Item --->", item.toString());

                                        }


                                    } else {
                                        showCustomToast(json.getString("message"));

                                    }


                                } else {
                                    String message = json.getString("message");
                                    SystenmApi.showCommonErrorDialog(InvestManagerActivity.this, json.getInt("status"),message);
                                }
                            } else {
                                showCustomToast(R.string.toast1);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        adapter.notifyDataSetChanged();
                        dismissLoadingDialog();
                        listView.onRefreshComplete();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          String responseString, Throwable throwable) {
                        // TODO Auto-generated method stub
                        super.onFailure(statusCode, headers, responseString,
                                throwable);
                        dismissLoadingDialog();
                        showCustomToast(responseString);
                        listView.onRefreshComplete();

                    }

                });



        adapter.notifyDataSetChanged();

    }

    public void finish() {
        super.finish();
    }

}
