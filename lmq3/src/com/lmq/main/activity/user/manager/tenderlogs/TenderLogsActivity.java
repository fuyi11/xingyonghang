package com.lmq.main.activity.user.manager.tenderlogs;

import java.util.ArrayList;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.xyh.R;
import com.lmq.http.BaseHttpClient;
import com.lmq.main.api.BaseActivity;
import com.lmq.main.api.JsonBuilder;
import com.lmq.main.api.SystenmApi;
import com.lmq.main.item.dealItem;
import com.lmq.main.util.Default;
import com.lmq.http.JsonHttpResponseHandler;

/**
 * 交易记录——新页面
 */
public class TenderLogsActivity extends BaseActivity implements
        OnItemClickListener {

    private PullToRefreshListView mListView;
    private ArrayList<dealItem> mList;
    private LayoutInflater mInflater;
    private dealAdapter adapter;

    private int maxPage, curPage = 1, pageCount = 10;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.people_info_jiaoyi_new);
        doHttp();
        initView();
    }

    protected void initView() {
        mListView = (PullToRefreshListView) findViewById(R.id.listview);
        mListView.setMode(PullToRefreshBase.Mode.BOTH);
        mListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {

                if (refreshView.isHeaderShown()) {
                    doHttp();
                } else {


                    if (curPage + 1 <= maxPage) {
                        curPage++;
                        JsonBuilder builder = new JsonBuilder();
                        builder.put("limit", pageCount);
                        builder.put("page", curPage);
                        doHttp(builder);
                    } else {
                        showCustomToast(("无更多数据！"));
                        handler.sendEmptyMessage(1);
                    }
                }


            }
        });
        mInflater = LayoutInflater.from(this);
        adapter = new dealAdapter();
        mListView.setAdapter(adapter);
        TextView textview = (TextView) findViewById(R.id.title);
        textview.setText("交易记录");
        findViewById(R.id.back).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {


                Intent intent = new Intent();
                intent.putExtra("type", mList.get(position - 1).getType());
                intent.putExtra("affect_money", mList.get(position - 1).getAffect_money());
                intent.putExtra("account_money", mList.get(position - 1).getBlance_money());
                intent.putExtra("collect_money", mList.get(position - 1).getBack_money());
                intent.putExtra("freeze_money", mList.get(position - 1).getNouse_money());
                intent.putExtra("add_time", mList.get(position - 1).getAdd_time());
                intent.putExtra("info", mList.get(position - 1).getInfo());
                intent.setClass(TenderLogsActivity.this,
                        TenderLogDetailActivity.class);

                startActivity(intent);


            }
        });


    }

    public void updateInfo(JSONObject json) {
        mList = new ArrayList<dealItem>();
        JSONArray list = null;
        try {

            maxPage = json.getInt("totalPage");
            curPage = json.getInt("nowPage");
            if (!json.isNull("list")) {
                list = json.getJSONArray("list");
            }
            if (list != null)
                for (int i = 0; i < list.length(); i++) {
                    JSONObject temp = list.getJSONObject(i);
                    dealItem item = new dealItem(temp);
                    mList.add(item);

                }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        adapter.notifyDataSetChanged();
        handler.sendEmptyMessage(1);
    }

    class dealAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            if (mList == null)
                return 0;
            else
                return mList.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            // TODO Auto-generated method stub
            return super.getItemViewType(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ItemHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.adapter_item_jy_detail,
                        null);
                holder = new ItemHolder();
                holder.money = (TextView) convertView.findViewById(R.id.money);
                holder.time = (TextView) convertView.findViewById(R.id.time);
                holder.iv = (ImageView) convertView.findViewById(R.id.iv);
                holder.type = (TextView) convertView.findViewById(R.id.type);

                convertView.setTag(holder);
            } else {
                holder = (ItemHolder) convertView.getTag();
            }
            dealItem item = (dealItem) mList.get(position);
            String affect_money = item.getAffect_money();

            if (affect_money.lastIndexOf("-") >= 0) {
                holder.money.setTextColor(getResources()
                        .getColor(R.color.green));
                holder.iv.setBackgroundResource(R.drawable.zhi);
                holder.money.setText(affect_money);
            } else {
                holder.money.setTextColor(getResources().getColor(R.color.red));
                holder.iv.setBackgroundResource(R.drawable.shou);
                holder.money.setText("+" + affect_money);
            }
            holder.time.setText(item.getAdd_time());

            holder.type.setText(item.getType());
            return convertView;
        }
    }

    private class ItemHolder {
        TextView money, time, type;
        ImageView iv;
    }

    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        dealItem item = (dealItem) mList.get(position);

        AlertDialog.Builder builder = new Builder(this);
        builder.setTitle(item.getType());
        builder.setMessage(item.getInfo());
        builder.show();
    }

    public void doHttp() {

        curPage = 1;
        JsonBuilder builder = new JsonBuilder();
        builder.put("limit", pageCount);
        builder.put("page", curPage);

        BaseHttpClient.post(getBaseContext(), Default.tradinglog, builder,
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
                                if (json.getInt("status") == 1) {
                                    updateInfo(json);
                                } else {
                                    String message = json.getString("message");
                                    SystenmApi.showCommonErrorDialog(TenderLogsActivity.this, json.getInt("status"), message);
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
                        mListView.onRefreshComplete();
                        showCustomToast(responseString);

                    }

                });

    }

    public void doHttp(JsonBuilder builder) {

        BaseHttpClient.post(getBaseContext(), Default.tradinglog, builder, new JsonHttpResponseHandler() {

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
                        if (json.getInt("status") == 1) {
                            addInfo(json);
                        } else {
                            String message = json.getString("message");
                            SystenmApi.showCommonErrorDialog(TenderLogsActivity.this, json.getInt("status"), message);
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
                super.onFailure(statusCode, headers, responseString, throwable);
                dismissLoadingDialog();
                showCustomToast(responseString);
                mListView.onRefreshComplete();
            }

        });
    }

    public void addInfo(JSONObject json) {
        //mList = new ArrayList<dealItem>();
        JSONArray list = null;
        try {
            maxPage = json.getInt("totalPage");
            if (!json.isNull("list")) {
                list = json.getJSONArray("list");
            }
            if (list != null)
                for (int i = 0; i < list.length(); i++) {
                    JSONObject temp = list.getJSONObject(i);
                    dealItem item = new dealItem(temp);
                    mList.add(item);
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
        adapter.notifyDataSetChanged();

        mListView.onRefreshComplete();
    }

    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg.what == 1) {
                mListView.onRefreshComplete();
            }
        }

    };
}
