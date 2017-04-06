package com.lmq.main.activity.investmanager.lhb;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.xyh.R;
import com.lmq.http.BaseHttpClient;
import com.lmq.http.JsonHttpResponseHandler;
import com.lmq.main.api.BaseActivity;
import com.lmq.main.api.JsonBuilder;
import com.lmq.main.api.MyLog;
import com.lmq.main.api.SystenmApi;
import com.lmq.main.enmu.LHBLogsType;
import com.lmq.main.item.LHBDetailLogsItem;
import com.lmq.main.util.Default;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class LHBDetailLogsActivity extends BaseActivity implements OnClickListener {


    private Button hk_btn;
    /**
     * 结清按钮
     */
    private Button jq_btn;

    private PullToRefreshListView listView;


    private LHBAdapter adapter;

    private LHBLogsType type;


    private View lhb_deatil_logs_titl1;
    private View lhb_deatil_logs_titl2;

    private String bitch_no;
    private int totalPage = 0;
    private int currentPage = 1;
    /***
     * 数据源
     */
    private ArrayList<LHBDetailLogsItem> data = new ArrayList<LHBDetailLogsItem>();



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lhb_detail_logs);

        TextView text = (TextView) findViewById(R.id.title);
        text.setText("详细信息");

        findViewById(R.id.back).setOnClickListener(this);
        type = LHBLogsType.SHOU_YI_LOGS_TYPE;

        if(null != getIntent()){

            if(getIntent().hasExtra("bitch_no")){
                bitch_no = getIntent().getStringExtra("bitch_no");
            }
            if(getIntent().hasExtra("type")){
               boolean type_flag  = getIntent().getBooleanExtra("type",false);
                if(!type_flag){
                    type = LHBLogsType.SHU_HUI_LOGS_TYPE;
                }
            }


        }





        lhb_deatil_logs_titl1 = findViewById(R.id.lhb_detaul_log_title1);
        lhb_deatil_logs_titl2 = findViewById(R.id.lhb_detaul_log_title2);

        lhb_deatil_logs_titl2.setVisibility(View.GONE);


        hk_btn = (Button) findViewById(R.id.hk_btn);
        jq_btn = (Button) findViewById(R.id.jq_btn);

        jq_btn.setOnClickListener(this);
        hk_btn.setOnClickListener(this);



        listView = (PullToRefreshListView) findViewById(R.id.lhb_detail_logs_list);
        adapter = new LHBAdapter();
        listView.setAdapter(adapter);
        listView.setMode(PullToRefreshBase.Mode.BOTH);
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {

                if(refreshView.isHeaderShown()){

                    currentPage = 1;
                    getListDataFromSever();
                }

                if(refreshView.isFooterShown()){

                    getListDataFromSever();


                }

            }
        });

        changeLogTitle();




    }


    /***
     * 更改选项卡的颜色
     */
    private void changeLogTitle(){

        switch (type){


            case SHOU_YI_LOGS_TYPE:
                lhb_deatil_logs_titl1.setVisibility(View.GONE);
                lhb_deatil_logs_titl2.setVisibility(View.VISIBLE);


                hk_btn.setTextColor(getResources().getColor(R.color.white));
                hk_btn.setBackground(getResources().getDrawable(R.drawable.segment_one_hit));
                jq_btn.setTextColor(getResources().getColor(R.color.red));
                jq_btn.setBackground(getResources().getDrawable(R.drawable.segment_three));
                break;
            case SHU_HUI_LOGS_TYPE:
                lhb_deatil_logs_titl1.setVisibility(View.VISIBLE);
                lhb_deatil_logs_titl2.setVisibility(View.GONE);


                jq_btn.setTextColor(getResources().getColor(R.color.white));
                jq_btn.setBackground(getResources().getDrawable(R.drawable.segment_three_hit));
                hk_btn.setTextColor(getResources().getColor(R.color.red));
                hk_btn.setBackground(getResources().getDrawable(R.drawable.segment_one));
                
                break;


        }




    }

    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.back:
                finish(0);
                break;
            case R.id.hk_btn:
                type = LHBLogsType.SHOU_YI_LOGS_TYPE;
                changeLogTitle();
                currentPage = 1;
                getListDataFromSever();
                break;
            case R.id.jq_btn:
                type = LHBLogsType.SHU_HUI_LOGS_TYPE;
                changeLogTitle();
                currentPage = 1;
                getListDataFromSever();

                break;


        }
    }




    @Override
    protected void onResume() {
        super.onResume();
        getListDataFromSever();
    }

    private void getListDataFromSever(){

        JsonBuilder builder = new JsonBuilder();
        builder.put("batch",bitch_no);
        builder.put("page",currentPage);
        builder.put("limit","10");
        /***
         * 清除原有数据
         */
        if (data.size() > 0) {
            data.clear();
        }

        //TODO 请求灵活宝服务器信息

        BaseHttpClient.post(getBaseContext(), type==LHBLogsType.SHOU_YI_LOGS_TYPE?Default.getRecord:Default.getLog2, builder,
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


                                    if(json.has("totalPage")){

                                        totalPage = json.optInt("totalPage",0);
                                    }

                                    if(json.has("nowPage")){

                                        currentPage = json.optInt("nowPage",0);
                                    }


//




                                    if (json.has("list")) {


                                        JSONArray array = json.optJSONArray("list");
                                        for (int i = 0; i < array.length(); i++) {
                                            MyLog.e("VIew", "" + i);
                                            LHBDetailLogsItem lhbItem = new LHBDetailLogsItem(array.getJSONObject(i),type);
                                            data.add(lhbItem);

                                        }

                                        updataViewInfo();

                                    }else{

                                        showCustomToast(json.getString("message"));
                                    }


                                } else {
                                    String message = json.getString("message");
                                    SystenmApi.showCommonErrorDialog(LHBDetailLogsActivity.this, json.getInt("status"),message);
                                }
                            } else {
                                showCustomToast(R.string.toast1);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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



                }
        );


    }

    /**
     * 更新显示
     * */
    private void updataViewInfo(){


        adapter.notifyDataSetChanged();


    }




    class LHBAdapter extends BaseAdapter{


        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public LHBDetailLogsItem getItem(int position) {
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
                LayoutInflater mInflater = LayoutInflater.from(LHBDetailLogsActivity.this);
                convertView = mInflater.inflate(R.layout.lhb_detail_logs_item, null);


                viewHolder.lhb_detail_log_item_shsj = (TextView) convertView.findViewById(R.id.lhb_detail_Logs_shsj);
                viewHolder.lhb_detail_log_item_shje = (TextView) convertView.findViewById(R.id.lhb_detail_Logs_shje);
                viewHolder.lhb_detail_log_item_zt = (TextView) convertView.findViewById(R.id.lhb_detail_Logs_zt);
                viewHolder.lhb_detail_log_item_bz = (TextView) convertView.findViewById(R.id.lhb_detail_Logs_bz);



                convertView.setTag(viewHolder);
            }
            else
            {
                viewHolder = (ViewHolder) convertView.getTag();
            }

           switch (type){

               case SHOU_YI_LOGS_TYPE:


                   updateSYItemView(viewHolder, position);

                   break;

               case SHU_HUI_LOGS_TYPE:

                   updateHSItemView(viewHolder,position);

                   break;
           }




            return convertView;
        }


        /**
         *更新交易记录Item
         * @param viewHolder
         */
        private void updateSYItemView(ViewHolder viewHolder,int position){

            viewHolder.lhb_detail_log_item_shsj.setText(adapter.getItem(position).getE_time());
            viewHolder.lhb_detail_log_item_shje.setText(adapter.getItem(position).getMoney());
            viewHolder.lhb_detail_log_item_zt.setText(adapter.getItem(position).getFunds());
            viewHolder.lhb_detail_log_item_bz.setText(adapter.getItem(position).getYifutou());






        }

        /**
         * 更新赎回记录Item
         * @param viewHolder
         */
        private void updateHSItemView(ViewHolder viewHolder,int position){


            viewHolder.lhb_detail_log_item_shsj.setText(adapter.getItem(position).getAdd_time());
            viewHolder.lhb_detail_log_item_shje.setText(adapter.getItem(position).getMoney());
            viewHolder.lhb_detail_log_item_zt.setText(adapter.getItem(position).getStatus_type());
            viewHolder.lhb_detail_log_item_bz.setText(adapter.getItem(position).getRemark());
        }






















    }



    public  static class ViewHolder{

            TextView lhb_detail_log_item_shsj;
            TextView lhb_detail_log_item_shje;
            TextView lhb_detail_log_item_zt;
            TextView lhb_detail_log_item_bz;






    }





    public void finish() {
        super.finish();
    }

}
