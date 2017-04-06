package com.lmq.main.activity.user.manager.tenderlogs;

import java.util.ArrayList;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

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
import com.lmq.http.JsonHttpResponseHandler;
import com.lmq.main.api.BaseActivity;
import com.lmq.main.api.JsonBuilder;
import com.lmq.main.api.SystenmApi;
import com.lmq.main.item.dealItem2;
import com.lmq.main.util.Default;

/**
 * 资金记录
 */
public class MoneyRecordActivity extends BaseActivity implements
        OnItemClickListener {

    private PullToRefreshListView mListView;
    private ArrayList<dealItem2> mList;
    private LayoutInflater mInflater;
    private dealAdapter adapter;

    private int maxPage, curPage = 1, pageCount = 10;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.people_info_jiaoyi_new);
        
        initView();
    }
    
    @Override
    protected void onResume() {
    	// TODO Auto-generated method stub
    	super.onResume();
    	doHttp();
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
        textview.setText("资金记录");
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
                intent.putExtra("remark", mList.get(position - 1).getRemark());
                intent.putExtra("money", mList.get(position - 1).getMoney());
                intent.putExtra("add_time", mList.get(position - 1).getAdd_time());
                intent.putExtra("batch_no", mList.get(position - 1).getBatch_no());
                intent.putExtra("Redeem", mList.get(position - 1).getRedeem());
                intent.setClass(MoneyRecordActivity.this,
                		MoneyRedcordDetailActivity.class);

                startActivity(intent);


            }
        });


    }

    public void updateInfo(JSONObject json) {
        mList = new ArrayList<dealItem2>();
        JSONArray list = null;
        try {

            maxPage = json.getInt("totalPage");

            if (!json.isNull("list")) {
                list = json.getJSONArray("list");
            }
            if (list != null)
                for (int i = 0; i < list.length(); i++) {
                    JSONObject temp = list.getJSONObject(i);
                    dealItem2 item = new dealItem2(temp);
                    mList.add(item);

                }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        adapter.notifyDataSetChanged();

        mListView.onRefreshComplete();
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
            dealItem2 item = (dealItem2) mList.get(position);
            
            String money = item.getMoney();

            if (item.getRedeem()==0) {
                holder.money.setTextColor(getResources()
                        .getColor(R.color.green));
                holder.iv.setBackgroundResource(R.drawable.shu);
                holder.money.setText("-"+money);
            } else {
                holder.money.setTextColor(getResources().getColor(R.color.red));
                holder.iv.setBackgroundResource(R.drawable.tou);
                holder.money.setText("+" + money);
            }
            holder.time.setText(item.getAdd_time());
            holder.type.setText(item.getBatch_no());
            return convertView;
        }
    }

    private class ItemHolder {
        TextView money, time, type;
        ImageView iv;
    }

//    public void onItemClick(AdapterView<?> parent, View view, int position,
//                            long id) {
//    	Money_RecordItem item = (Money_RecordItem) mList.get(position);
//
//        AlertDialog.Builder builder = new Builder(this);
//        builder.setTitle(item.getType());
//        builder.setMessage(item.getInfo());
//        builder.show();
//    }

    public void doHttp() {

        curPage = 1;
        JsonBuilder builder = new JsonBuilder();
        builder.put("limit", pageCount);
        builder.put("page", curPage);

        BaseHttpClient.post(getBaseContext(), Default.getLog, builder,
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
                                    SystenmApi.showCommonErrorDialog(MoneyRecordActivity.this, json.getInt("status"), message);
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

        BaseHttpClient.post(getBaseContext(), Default.getLog, builder, new JsonHttpResponseHandler() {

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
                            SystenmApi.showCommonErrorDialog(MoneyRecordActivity.this, json.getInt("status"), message);
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
                    dealItem2 item = new dealItem2(temp);
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

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
	}

}
