package com.lmq.main.activity.investmanager.debet;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.xyh.R;
import com.lmq.http.BaseHttpClient;
import com.lmq.http.JsonHttpResponseHandler;
import com.lmq.main.activity.investmanager.invest.InvestManagerDetailActivity;
import com.lmq.main.activity.tools.LMQWebViewActivity;
import com.lmq.main.api.BaseActivity;
import com.lmq.main.api.JsonBuilder;
import com.lmq.main.api.SystenmApi;
import com.lmq.main.dialog.DedetCancleDialog;
import com.lmq.main.dialog.InvestPopList;
import com.lmq.main.item.InvestManageItem;
import com.lmq.main.util.Default;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

public class InvestManagerDebetActivity extends BaseActivity implements OnClickListener {


    private InvestPopList popList;
    private int current_type = -1;
    private ArrayList<InvestManageItem> data;
    private PullToRefreshListView listView;
    private Adapter adapter;
    private int page = 1;
    private int limit = 8;
    private TextView text;
    private String requestURL = Default.canTransfer;
    private String[] tipsData;
    private int tipsType = 0;


    private DedetCancleDialog dialog;
    private int totalPage = 0;
    private String[] titleArray;

    private ImageView mtriangle;




    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.invest_manage_debet_layout);

        titleArray = getResources().getStringArray(R.array.debet_type);
        text = (TextView) findViewById(R.id.title);
        text.setText("可转让债权");
        text.setOnClickListener(this);


        findViewById(R.id.back).setOnClickListener(this);

        mtriangle = (ImageView) findViewById(R.id.triangle);


        data = new ArrayList<InvestManageItem>();
        listView = (PullToRefreshListView) findViewById(R.id.invest_manager_debet_list);
        adapter = new Adapter();
        listView.setAdapter(adapter);
        tipsData = InvestManagerDebetActivity.this.getResources().getStringArray(R.array.debet_tips1);
        listView.setMode(PullToRefreshBase.Mode.BOTH);
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {

                if(refreshView.isHeaderShown()){
                    resetPostParams();
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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {




                position = position -1;
                switch (tipsType){

                    case 0:
                        //TODO 进入装让界面

                        if(adapter.getItem(position).getIs_debt()==0){
                            showCustomToast("您的标已转让");
                        }else{
                            InvestManageItem item = adapter.getItem(position);
                            Intent intent = new Intent();
                            intent.putExtra("debet_id",item.getId());
                            intent.setClass(InvestManagerDebetActivity.this,InvestManagerDebetZR.class);
                            startActivity(intent);
                        }

                        break;
                    case 1:
                        //TODO 提示撤销
                        if(adapter.getItem(position).getStatus()==2){
                            showCancalDebetDialog(view,adapter.getItem(position));
                        }else{
                            showCustomToast("正在审核中");
                        }
                        break;
                    case 3:
                        //TODO 显示协议

                        JsonBuilder builder = new JsonBuilder();
                        builder.put("invest_id",adapter.getItem(position).getinvest_id());
                        showDebetHtml(builder);
                        break;

                }

            }
        });


        popList = new InvestPopList(InvestManagerDebetActivity.this);
        popList.setOnItemClickLinstener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                if (position != popList.getDefauleSelect()) {
                    popList.setDefauleSelect(position);

                }
                tipsType = position;


                Animation animation = AnimationUtils.loadAnimation(InvestManagerDebetActivity.this, R.anim.triangle_totate2);
                mtriangle.clearAnimation();
                mtriangle.setImageResource(R.drawable.wite_arrow_up);
                mtriangle.startAnimation(animation);
                animation.setFillAfter(true);

                popList.dissmiss();
                resetPostParams();


                switch (position) {

                    case 0:

                        requestURL = Default.canTransfer;
                        break;
                    case 1:

                        requestURL = Default.onBonds;
                        break;
                    case 2:

                        requestURL = Default.successDeb;
                        break;
                    case 3:

                        requestURL = Default.buydetb;
                        break;
                }

                text.setText(titleArray[position]);
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
    public void showCancalDebetDialog(final View view,InvestManageItem item ){

        if(null == dialog){



            dialog = new DedetCancleDialog(InvestManagerDebetActivity.this);
            dialog.setDialogTitle("撤销债券转让");
            dialog.setonClickListener(this);



        }

        dialog.setObject(item);

        if(!dialog.isShowing()){
            dialog.showAsDropDown(view);
        }



    }





    private JsonBuilder getPostParams() {

        JsonBuilder builder = new JsonBuilder();
        builder.put("page", page);
        builder.put("limit", limit);

        return builder;
    }


    private JsonBuilder resetPostParams() {

        page = 0;
        limit = 8;

        return getPostParams();
    }





    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.back:
                finish(0);
                break;

            case R.id.title:

                   Animation animation = AnimationUtils.loadAnimation(InvestManagerDebetActivity.this, R.anim.triangle_totate);
                    mtriangle.clearAnimation();
                    mtriangle.setImageResource(R.drawable.wite_arrow_down);
                    mtriangle.startAnimation(animation);
                    animation.setFillAfter(true);


                showPopList(0, v);

                break;
            case R.id.dialog_submit:

//                if(SystenmApi.isNullOrBlank(dialog.getPassword())){
//                    showCustomToast("请输入交易密码");
//                    return;
//                }else {
                    InvestManageItem item = (InvestManageItem)dialog.getObject();
                    JsonBuilder builder = new JsonBuilder();
                    builder.put("paypass",dialog.getPassword());
                    builder.put("id", item.getId());
                    cancleDebetAction(builder, item);
                    dialog.dismiss();

//                }

                break;
            case R.id.dialog_cancle:

                dialog.dismiss();

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
                popList.addItems(getResources().getStringArray(R.array.debet_type));


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
                convertView = LinearLayout.inflate(InvestManagerDebetActivity.this, R.layout.invest_manager_debet_item, null);

                viewHolder.invest_manager_item_tbmc = (TextView) convertView.findViewById(R.id.invest_manager_item_tbmc);
                viewHolder.invest_manager_item_ztje = (TextView) convertView.findViewById(R.id.invest_manager_item_ztje);
                viewHolder.invest_manager_item_nhll = (TextView) convertView.findViewById(R.id.invest_manager_item_nhll);
                viewHolder.invest_manager_item_tzsj = (TextView) convertView.findViewById(R.id.invest_manager_item_tzsj);
                viewHolder.invest_manager_item_dqsj = (TextView) convertView.findViewById(R.id.invest_manager_item_dqsj);
                viewHolder.invest_manager_item_dbjg = (TextView) convertView.findViewById(R.id.invest_manager_item_dbjg);

                /***提示*/
                viewHolder.debet_item_tips1 = (TextView) convertView.findViewById(R.id.debet_item_tips1);
                viewHolder.debet_item_tips2 = (TextView) convertView.findViewById(R.id.debet_item_tips2);
                viewHolder.debet_item_tips3 = (TextView) convertView.findViewById(R.id.debet_item_tips3);
                viewHolder.debet_item_tips4 = (TextView) convertView.findViewById(R.id.debet_item_tips4);

                convertView.setTag(viewHolder);
            } else {

                viewHolder = (ViewHolder) convertView.getTag();

            }



                cheangeTips();



            viewHolder.debet_item_tips1.setText(tipsData[0]);
            viewHolder.debet_item_tips2.setText(tipsData[1]);
            viewHolder.debet_item_tips3.setText(tipsData[2]);
            viewHolder.debet_item_tips4.setText(tipsData[3]);



            viewHolder.invest_manager_item_tbmc.setText(adapter.getItem(position).getBorrow_name());
            viewHolder.invest_manager_item_ztje.setText(adapter.getItem(position).getInterest_rate()+ "%");
            viewHolder.invest_manager_item_nhll.setText(adapter.getItem(position).getInvestor_capital());
            viewHolder.invest_manager_item_dbjg.setVisibility(View.GONE);
            if (tipsType == 0) {
                viewHolder.invest_manager_item_tzsj.setText(adapter.getItem(position).getAddtime().equals("1970-01-01")?"-":adapter.getItem(position).getAddtime());
                viewHolder.invest_manager_item_dqsj.setText(adapter.getItem(position).getDeadline().equals("1970-01-01")?"-":adapter.getItem(position).getDeadline());
            }

            if (tipsType == 1 || tipsType == 2) {
                viewHolder.invest_manager_item_tzsj.setText(adapter.getItem(position).getMoney());
                viewHolder.invest_manager_item_dqsj.setText(adapter.getItem(position).getAddtime());
            }

            if (tipsType == 3) {
                viewHolder.invest_manager_item_nhll.setText(adapter.getItem(position).getTotal_periods());
                viewHolder.invest_manager_item_tzsj.setText(adapter.getItem(position).getInvestor_capital());
                viewHolder.invest_manager_item_dqsj.setText(adapter.getItem(position).getBuy_money());
            }
            return convertView;
        }









    }

    private static class ViewHolder {


        /**提示*/
        TextView debet_item_tips1;
        TextView debet_item_tips2;
        TextView debet_item_tips3;
        TextView debet_item_tips4;

        TextView invest_manager_item_tbmc;
        TextView invest_manager_item_ztje;
        TextView invest_manager_item_nhll;
        TextView invest_manager_item_tzsj;
        TextView invest_manager_item_dqsj;
        TextView invest_manager_item_dbjg;




    }

    private void cheangeTips(){

        tipsData = null;

        switch (tipsType){

            case 0:
                tipsData = InvestManagerDebetActivity.this.getResources().getStringArray(R.array.debet_tips1);
                break;

            case 1:
                tipsData = InvestManagerDebetActivity.this.getResources().getStringArray(R.array.debet_tips2);
                break;
            case 2:
                tipsData = InvestManagerDebetActivity.this.getResources().getStringArray(R.array.debet_tips3);
                break;
            case 3:
                tipsData = InvestManagerDebetActivity.this.getResources().getStringArray(R.array.debet_tips4);
                break;

        }


    }


    @Override
    protected void onResume() {
        super.onResume();
        getInvestData(getPostParams(),true);
    }



    // 提交用户反馈到服务器
    public void showDebetHtml(JsonBuilder builder) {



        BaseHttpClient.post(getBaseContext(), Default.debt_download, builder,
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


                                    if (json.has("message")) {

                                        String url = json.optString("message", "");

                                        if (!SystenmApi.isNullOrBlank(url)) {

                                            //TODO  跳转网页加载
                                            Intent debetIntent = new Intent(InvestManagerDebetActivity.this, LMQWebViewActivity.class);
                                            debetIntent.putExtra("title", "债券转让协议");
                                            debetIntent.putExtra("url", url);
                                            startActivity(debetIntent);


                                        } else {

                                            showCustomToast("请求出错，请重试");
                                        }


                                    }


                                } else {
                                    String message = json.getString("message");
                                    SystenmApi.showCommonErrorDialog(InvestManagerDebetActivity.this, json.getInt("status"),message);
                                }
                            } else {
                                showCustomToast(R.string.toast1);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        dismissLoadingDialog();
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



    }



    // 提交用户反馈到服务器
    public void cancleDebetAction(JsonBuilder builder, final InvestManageItem item) {



        BaseHttpClient.post(getBaseContext(), Default.cancel_debt, builder,
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

                                    ArrayList<InvestManageItem> tempData = new ArrayList<InvestManageItem>();
                                    Collections.addAll(tempData);

                                    tempData.remove(item);

                                    data.clear();
                                    data.addAll(tempData);


                                    adapter.notifyDataSetChanged();


                                }  else {
                                    String message = json.getString("message");
                                    SystenmApi.showCommonErrorDialog(InvestManagerDebetActivity.this, json.getInt("status"),message);
                                }
                            } else {
                                showCustomToast(R.string.toast1);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        dismissLoadingDialog();
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



    }


    // 提交用户反馈到服务器
    public void getInvestData(JsonBuilder builder ,boolean refsh) {


        if(refsh){
            data.clear();
        }

//        BaseHttpClient.NO_RAS=true;
        BaseHttpClient.post(getBaseContext(), requestURL, builder,
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
                                    SystenmApi.showCommonErrorDialog(InvestManagerDebetActivity.this, json.getInt("status"),message);
                                }
                            } else {
                                showCustomToast(R.string.toast1);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        dismissLoadingDialog();
                        adapter.notifyDataSetChanged();
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
