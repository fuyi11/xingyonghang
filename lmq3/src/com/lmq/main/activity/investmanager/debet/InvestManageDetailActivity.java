package com.lmq.main.activity.investmanager.debet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
import com.lmq.main.api.SystenmApi;
import com.lmq.main.util.Default;
import com.lmq.view.ListViewForScrollView;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class InvestManageDetailActivity extends BaseActivity implements OnClickListener {


    private ListViewForScrollView listView;
    private ArrayList<HKDetailItem> data;
    private Adapter adapter;
    private PullToRefreshScrollView scrollView;

    private int totalPage;
    private int currentpage = 1;
    private String  id;

    private String has_pay;
    private String fail_pay;

    private TextView hasPayTextView;
    private TextView failPayTextVIew;


	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.invest_hk_detail_layout);

		TextView text = (TextView) findViewById(R.id.title);
		text.setText("还款详情");

        data = new ArrayList<HKDetailItem>();
        Intent intent = getIntent();
        if(null != intent){

            if(intent.hasExtra("id")){

                id = intent.getStringExtra("id");
            }

        }

        hasPayTextView = (TextView) findViewById(R.id.hk_detail_yzfbx);
        failPayTextVIew = (TextView) findViewById(R.id.hk_detail_wzfbx);





        scrollView = (PullToRefreshScrollView) findViewById(R.id.mscrollView);
        scrollView.setMode(PullToRefreshBase.Mode.BOTH);
        scrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollView>() {
          @Override
          public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {

              if(refreshView.isHeaderShown()){
                  getDataFromServer(true);
                  scrollView.onRefreshComplete();
              }

              if(refreshView.isFooterShown()){

                  if(currentpage < totalPage){
                      currentpage+=1;
                      getDataFromServer(false);
                  }else{

                      showCustomToast("无更多数据");
                  }

                  scrollView.onRefreshComplete();
              }

          }
      });

        listView = (ListViewForScrollView) findViewById(R.id.hk_lists);
        adapter = new Adapter();
        listView.setAdapter(adapter);

		findViewById(R.id.back).setOnClickListener(this);

	}

    @Override
    protected void onResume() {
        super.onResume();
        getDataFromServer(true);
    }

    public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish(0);
			break;


		}
	}

	// 提交用户反馈到服务器
	public void getDataFromServer(final boolean reflsh) {

		// 构造请求参数
		JsonBuilder jsonBuilder = new JsonBuilder();




                jsonBuilder.put("page",currentpage);
                jsonBuilder.put("limit", "10");
                jsonBuilder.put("id", id);


		// 获取当前手机系统信息
		BaseHttpClient.post(getBaseContext(), Default.tenddetail, jsonBuilder,
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
                            dismissLoadingDialog();
                            if (statusCode == 200) {


                                if (json.getInt("status") == 1) {

                                    if (json.has("totalPage")) {

                                        totalPage = json.optInt("totalPage", 0);

                                    }
                                    if (json.has("nowPage")) {

                                        currentpage = json.optInt("nowPage", 1);

                                    }
                                    if (json.has("have_pay")) {

                                        has_pay = json.optString("have_pay", "");

                                    }
                                    if (json.has("fail_pay")) {

                                        fail_pay = json.optString("fail_pay", "");

                                    }

                                    hasPayTextView.setText(has_pay);
                                    failPayTextVIew.setText(fail_pay);


                                    if (reflsh) {

                                        data.clear();
                                    }

                                    if (json.has("list")) {


                                        JSONArray array = json.getJSONArray("list");

                                        for (int i = 0; i < array.length(); i++) {


                                            HKDetailItem item = new HKDetailItem(array.getJSONObject(i));

                                            data.add(item);


                                        }

                                        adapter.notifyDataSetChanged();


                                    }


                                }else {
                                    String message = json.getString("message");
                                    SystenmApi.showCommonErrorDialog(InvestManageDetailActivity.this, json.getInt("status"),message);
                                }
                            } else {
                                showCustomToast(R.string.toast1);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        adapter.notifyDataSetChanged();
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

        if (reflsh){
            scrollView.smootScrollToTop();
        }


        adapter.notifyDataSetChanged();

	}





    private class Adapter extends BaseAdapter{


        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public HKDetailItem getItem(int position) {
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
                convertView = LinearLayout.inflate(InvestManageDetailActivity.this, R.layout.invest_manage_hk_detail_layout, null);

                viewHolder.yjTime = (TextView) convertView.findViewById(R.id.yj_time);
                viewHolder.yjMoney = (TextView) convertView.findViewById(R.id.yj_money);
                viewHolder.sjStatus = (TextView) convertView.findViewById(R.id.sj_status);



                convertView.setTag(viewHolder);
            } else {

                viewHolder = (ViewHolder) convertView.getTag();

            }


            viewHolder.yjTime.setText(adapter.getItem(position).getYjTime());
            viewHolder.yjMoney.setText(adapter.getItem(position).getYjMoney());
            viewHolder.sjStatus.setText(adapter.getItem(position).getSjStatus());
            return convertView;
        }
    }






    private static class ViewHolder{

        private TextView yjTime;
        private TextView yjMoney;
        private TextView sjStatus;

    }


    private class HKDetailItem{

        private  String yjTime;
        private String yjMoney;
        private String sjStatus;


        public HKDetailItem(JSONObject json) {

            if (json.has("deadline")){

                yjTime = json.optString("deadline","");


            }


            if (json.has("yj_money")){

                yjMoney = json.optString("yj_money","");


            }


            if (json.has("sj_type")){

                sjStatus = json.optString("sj_type","");


            }
        }

        public String getYjTime() {
            return yjTime;
        }

        public void setYjTime(String yjTime) {
            this.yjTime = yjTime;
        }

        public String getYjMoney() {
            return yjMoney;
        }

        public void setYjMoney(String yjMoney) {
            this.yjMoney = yjMoney;
        }

        public String getSjStatus() {
            return sjStatus;
        }

        public void setSjStatus(String sjStatus) {
            this.sjStatus = sjStatus;
        }
    }













}
