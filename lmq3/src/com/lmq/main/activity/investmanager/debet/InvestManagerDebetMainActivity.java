package com.lmq.main.activity.investmanager.debet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.xyh.R;
import com.lmq.http.BaseHttpClient;
import com.lmq.http.JsonHttpResponseHandler;
import com.lmq.main.api.BaseActivity;
import com.lmq.main.api.JsonBuilder;
import com.lmq.main.api.SystenmApi;
import com.lmq.main.dialog.InvestPopList;
import com.lmq.main.item.InvestManageItem;
import com.lmq.main.util.Default;
import com.lmq.view.DrawableCenterButton;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class InvestManagerDebetMainActivity extends BaseActivity implements OnClickListener {


    private InvestPopList popList;
    private int current_type = -1;
    private ArrayList<InvestManageItem> data;
    private ListView listView;
    private Adapter adapter;
    private int page = 0;
    private int limit = 5;
    private DrawableCenterButton text;


    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.invest_manager_main_layout);

        text = (DrawableCenterButton) findViewById(R.id.title);
        text.setText(R.string.invest_manager_debet);
        text.setOnClickListener(this);


        findViewById(R.id.back).setOnClickListener(this);

        data = new ArrayList<InvestManageItem>();
        listView = (ListView) findViewById(R.id.invest_manager_list);
        adapter = new Adapter();
        listView.setAdapter(adapter);


        popList = new InvestPopList(InvestManagerDebetMainActivity.this);
        popList.setOnItemClickLinstener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {



                if (position != popList.getDefauleSelect()) {
                    popList.setDefauleSelect(position);
                    popList.dissmiss();
                }

                switch (current_type) {

                    case 0:
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                }

                getInvestData(getPostParams());

            }
        });


    }


    private JsonBuilder getPostParams() {

        JsonBuilder builder = new JsonBuilder();
        builder.put("page", page);
        builder.put("limit", limit);

        return builder;
    }


    private JsonBuilder resetPostParams(int production) {

        page = 0;
        limit = 5;

        return getPostParams();
    }


    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.back:
                finish(0);
                break;

            case R.id.title:
                showPopList(0, v);

                break;
        }

    }


    private void showPopList(int type, View view) {


        if (current_type != type) {


            if (popList.isShowing()) {

                popList.dissmiss();
            }

            popList.clearData();
            popList.setDefauleSelect(0);


            if (type == 0) {
                popList.addItems(getResources().getStringArray(R.array.debet_main_type));

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
            ViewHolder viewHolder = null;

            if (null == convertView) {

                viewHolder = new ViewHolder();
                convertView = LinearLayout.inflate(InvestManagerDebetMainActivity.this, R.layout.invest_manager_item, null);

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
            viewHolder.invest_manager_item_tzsj.setText(adapter.getItem(position).getInvest_time());
            viewHolder.invest_manager_item_dqsj.setText(adapter.getItem(position).getDeadline());
            viewHolder.invest_manager_item_dbjg.setVisibility(View.GONE);


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
        getInvestData(getPostParams());
    }

    // 提交用户反馈到服务器
    public void getInvestData(JsonBuilder builder) {


        data.clear();
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

                            if (statusCode == 200) {
                                // 没有新版本
                                if (json.getInt("status") == 1) {

                                    if (json.has("list")) {


                                        JSONArray array = json.getJSONArray("list");

                                        if (null != array && array.length() > 0) {


                                            for (int i = 0; i < array.length(); i++) {
                                                InvestManageItem item = new InvestManageItem(array.getJSONObject(i));
                                                data.add(item);
                                            }


                                        }

                                    } else {
                                        showCustomToast(json.getString("message"));

                                    }


                                }  else {
                                    String message = json.getString("message");
                                    SystenmApi.showCommonErrorDialog(InvestManagerDebetMainActivity.this, json.getInt("status"),message);
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
                        showCustomToast(responseString);

                    }

                });

        adapter.notifyDataSetChanged();

    }

    public void finish() {
        super.finish();
    }

}
