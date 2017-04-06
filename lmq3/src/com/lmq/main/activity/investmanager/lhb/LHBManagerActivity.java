package com.lmq.main.activity.investmanager.lhb;

import java.util.ArrayList;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.xyh.R;
import com.lmq.http.BaseHttpClient;
import com.lmq.http.JsonHttpResponseHandler;
import com.lmq.main.activity.user.manager.tenderlogs.MoneyRecordActivity;
import com.lmq.main.api.BaseActivity;
import com.lmq.main.api.MyLog;
import com.lmq.main.api.SystenmApi;
import com.lmq.main.item.LHBItem;
import com.lmq.main.util.Default;
import com.lmq.view.ListViewForScrollView;

public class LHBManagerActivity extends BaseActivity implements OnClickListener {

    /**
     * 收益
     */
    private TextView lhb_detail_sy;
    /**
     * 资产总额
     */
    private TextView lhb_detail_ze;
    /**
     * 回款按钮
     */
    private RadioButton hk_btn;
    /**
     * 结清按钮
     */
    private RadioButton jq_btn;

    private ListViewForScrollView listView;

    private ArrayList<LHBItem> data = new ArrayList<LHBItem>();

    private LHBAdapter adapter;

    /**
     *资产总额
     */
    private String assets;
    /**
     * 累计收益
     */
    private String interest;
    /**
     * 最近收益
     */
    private String recently;

    /**灵活宝Type*/
    private int type = 0;

    private PullToRefreshScrollView scrollView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lhb_layout);

        TextView text = (TextView) findViewById(R.id.title);
        text.setText(R.string.lhb_main_title);

        findViewById(R.id.back).setOnClickListener(this);
        scrollView = (PullToRefreshScrollView) findViewById(R.id.mscrollView);
        scrollView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        scrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {

                getListDataFromSever(type);

            }
        });


        lhb_detail_sy = (TextView) findViewById(R.id.lhb_detail_sy);
        lhb_detail_ze = (TextView) findViewById(R.id.lhb_detail_ze);
        hk_btn = (RadioButton) findViewById(R.id.hk_btn);
        jq_btn = (RadioButton) findViewById(R.id.jq_btn);

        jq_btn.setOnClickListener(this);
        hk_btn.setOnClickListener(this);


        listView = (ListViewForScrollView) findViewById(R.id.lhb_list);
        adapter = new LHBAdapter();
        listView.setAdapter(adapter);
        
        findViewById(R.id.lv_ze).setOnClickListener(this);



        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //TODO  跳转灵活宝详情页面

                position = position;
//                position = position - 1;


                if(type==0){

                    Intent intent = new Intent();

                    intent.putExtra("batch",adapter.getItem(position).getBatch_no());
                    intent.setClass(LHBManagerActivity.this,LHBDetailActivity.class);
                    startActivity(intent);


                }



            }
        });


    }

    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.back:
                finish(0);
                break;
            case R.id.hk_btn:
                type = 0;
                getListDataFromSever(type);
                break;
            case R.id.jq_btn:
                type = 1;
                getListDataFromSever(type);
                break;
            case R.id.lv_ze:
            	
            	LHBManagerActivity.this.startActivity(
    					new Intent(LHBManagerActivity.this, MoneyRecordActivity.class));
            	break;


        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getListDataFromSever(type);
    }

    private void getListDataFromSever(int index){
        /***
         * 清除原有数据
         */
        if (data.size() > 0) {
            data.clear();
        }
        adapter.notifyDataSetChanged();

        //TODO 请求灵活宝服务器信息

        BaseHttpClient.post(getBaseContext(), index==0?Default.user_index:Default.getEndItem, null,
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


//




                                    if (json.has("list")) {

                                        if (json.has("assets")) {

                                            assets = json.optString("assets", "");
                                        }

                                        if (json.has("interest")) {

                                            interest = json.optString("interest", "");
                                        }

                                        if (json.has("recently")) {

                                            recently = json.optString("recently", "");
                                        }

                                        JSONArray array = json.optJSONArray("list");
                                        for (int i = 0; i < array.length(); i++) {
                                            MyLog.e("VIew",""+i);
                                            LHBItem lhbItem = new LHBItem(array.getJSONObject(i));
                                            data.add(lhbItem);

                                        }

                                        updataViewInfo();

                                    }else{

                                        showCustomToast(json.getString("message"));
                                    }


                                } else {
                                    String message = json.getString("message");
                                    SystenmApi.showCommonErrorDialog(LHBManagerActivity.this, json.getInt("status"),message);
                                }
                            } else {
                                showCustomToast(R.string.toast1);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        dismissLoadingDialog();
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


    }

    /**
     * 更新显示
     * */
    private void updataViewInfo(){

        lhb_detail_sy.setText(interest);
        lhb_detail_ze.setText(assets + "元");
        adapter.notifyDataSetChanged();
        scrollView.onRefreshComplete();
        scrollView.smootScrollToTop();



    }




    class LHBAdapter extends BaseAdapter{


        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public LHBItem getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (null == convertView)
            {
                viewHolder = new ViewHolder();
                LayoutInflater mInflater = LayoutInflater.from(LHBManagerActivity.this);
                convertView = mInflater.inflate(R.layout.lhb_list_hk_item, null);


                viewHolder.lhb_hk_item_xmbh = (TextView) convertView.findViewById(R.id.lhb_hk_item_xmbh);
                viewHolder.lhb_hk_item_nhsy= (TextView) convertView.findViewById(R.id.lhb_hk_item_nhsy);
                viewHolder.lhb_hk_item_ljsy = (TextView) convertView.findViewById(R.id.lhb_hk_item_ljsy);
                viewHolder.lhb_hk_item_ysbx = (TextView) convertView.findViewById(R.id.lhb_hk_item_ysbx);
                viewHolder.lhb_hk_item_tzbj = (TextView) convertView.findViewById(R.id.lhb_hk_item_tzbj);
                viewHolder.lhb_item_cum3 = (TextView) convertView.findViewById(R.id.lhb_item_cum3);
                viewHolder.lhb_item_cum4 = (TextView) convertView.findViewById(R.id.lhb_item_cum4);


                convertView.setTag(viewHolder);
            }
            else
            {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            if(type == 1){
                viewHolder.lhb_item_cum3.setText(getResources().getText(R.string.invest_lhb_item_syze));
                viewHolder.lhb_item_cum4.setText(getResources().getText(R.string.invest_lhb_item_jqrq));

            }

            MyLog.e("获取数据",data.toString());
            if(viewHolder.lhb_hk_item_xmbh == null){

                MyLog.e("判断","空指针");

            }
            viewHolder.lhb_hk_item_xmbh.setText(data.get(position).getBatch_no());
            viewHolder.lhb_hk_item_tzbj.setText(data.get(position).getCapital());
            viewHolder.lhb_hk_item_nhsy.setText(data.get(position).getInterest_rate()+"%");
            viewHolder.lhb_hk_item_ljsy.setText(data.get(position).getInterest());
            viewHolder.lhb_hk_item_ysbx.setText(data.get(position).getOut_money());

            if(type == 1){
             viewHolder.lhb_hk_item_tzbj.setText(data.get(position).getMoney());
             viewHolder.lhb_hk_item_ysbx.setText(data.get(position).getDeadline());
            }



            return convertView;
        }
    }



    public  static class ViewHolder{

            TextView lhb_hk_item_xmbh;
            TextView lhb_hk_item_nhsy;
            TextView lhb_hk_item_ljsy;
            TextView lhb_hk_item_ysbx;
            TextView lhb_hk_item_tzbj;
            TextView lhb_item_cum3;
            TextView lhb_item_cum4;





    }





    public void finish() {
        super.finish();
    }

}
