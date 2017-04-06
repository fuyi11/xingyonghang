package com.lmq.main.activity.investmanager.reward;

import java.util.ArrayList;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.xyh.R;
import com.lmq.http.BaseHttpClient;
import com.lmq.http.JsonHttpResponseHandler;
import com.lmq.main.api.BaseActivity;
import com.lmq.main.api.JsonBuilder;
import com.lmq.main.api.MyLog;
import com.lmq.main.api.SystenmApi;
import com.lmq.main.dialog.CommonDialog;
import com.lmq.main.dialog.Reward_RedShare_Dialog;
import com.lmq.main.item.RedBagItem;
import com.lmq.main.util.Default;
import com.lmq.view.ListViewForScrollView;
/***
 * 我的红包
 * @author sjc
 *
 */
public class RewardWithRedEnvelopeActivity extends BaseActivity implements OnClickListener {

    /**
     * 已生成
     */
    private RadioButton redbag_ysc_btn;
    /**
     * 已抢到
     */
    private RadioButton redbag_yqd_btn;
    /**
     * 已过期
     */
    private RadioButton redbag_yqg_btn;
    private EditText ed_redmoney;
    private Button red_btn;
    
    private String share_url;
    /**红包金额*/
    private String  redmoney;

    private PullToRefreshScrollView scrollView;
	private ListViewForScrollView listView;
	private ListViewForScrollView listView2;
	private ListViewForScrollView listView3;
	
    private RedBagAdapter adapter;
    private RedBagAdapter2 adapter2;
    private RedBagAdapter3 adapter3;
    
    private JSONArray list = null;
    private int maxPage,curPage = 1, pageCount = 5;
    private int type=0;
//    private Reward_RedShare_Dialog dialog;

    /***
     * 数据源
     */
    private ArrayList<RedBagItem> data = new ArrayList<RedBagItem>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reward_myredpacket_list);

        TextView text = (TextView) findViewById(R.id.title);
        text.setText(R.string.reward_redpackets);

        
        findViewById(R.id.back).setOnClickListener(this);

        ed_redmoney = (EditText) findViewById(R.id.ed_redmoney);
        red_btn = (Button) findViewById(R.id.red_btn);
        red_btn.setOnClickListener(this);
        
        redbag_ysc_btn = (RadioButton) findViewById(R.id.redbag_ysc_btn);
        redbag_yqd_btn = (RadioButton) findViewById(R.id.redbag_yqd_btn);
        redbag_yqg_btn = (RadioButton) findViewById(R.id.redbag_yqg_btn);
        
        redbag_ysc_btn.setOnClickListener(this);
        redbag_yqd_btn.setOnClickListener(this);
        redbag_yqg_btn.setOnClickListener(this);
        
        scrollView = (PullToRefreshScrollView) findViewById(R.id.refreshView);
        scrollView.setMode(PullToRefreshBase.Mode.BOTH);

        listView = (ListViewForScrollView) findViewById(R.id.redbag_list);
        listView2 = (ListViewForScrollView) findViewById(R.id.redbag_list2);
        listView3 = (ListViewForScrollView) findViewById(R.id.redbag_list3);
        adapter = new RedBagAdapter();
        adapter2 = new RedBagAdapter2();
        adapter3 = new RedBagAdapter3();
        
        listView.setAdapter(adapter);
		listView.setTag("1");

        listView2.setAdapter(adapter2);
		listView2.setTag("2");

        listView3.setAdapter(adapter3);
		listView3.setTag("3");
        
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {


				if(type==0){
				showIntegrationDHDialog(view,adapter.getItem(position).getShare_url());
				
				}


			}
		});

        

        scrollView.setOnRefreshListener(new OnRefreshListener<ScrollView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {

				if (refreshView.isHeaderShown()) {

					getListDataRddBag(type);
				} else {
					JsonBuilder builder = new JsonBuilder();
					if (curPage + 1 <= maxPage) {
						curPage++;
						builder.put("page",curPage);
						builder.put("limit",pageCount);
						if(type==0){
						builder.put("status", 1);
						}else if(type==1){
						builder.put("status", 4);	
						}else{
						builder.put("status", 3);		
						}
						doHttp(builder);
					} else {
						handler.sendEmptyMessage(1);
					}
				}

			}
		});

    }

    public void doHttp(JsonBuilder builder) {

		BaseHttpClient.post(this, Default.bonus_index, builder,
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
										  JSONObject response) {
						// TODO Auto-generated method stub
						super.onSuccess(statusCode, headers, response);
						try {

							if (statusCode == 200) {
								if (response.getInt("status") == 1) {
									updateAddInfo(response);
								} else {
									String message = response.getString("message");
									SystenmApi.showCommonErrorDialog(RewardWithRedEnvelopeActivity.this, response.getInt("status"), message);
								}
							} else {
								showCustomToast(R.string.toast1);
							}
							dismissLoadingDialog();
						} catch (Exception e) {
							e.printStackTrace();
						}

						dismissLoadingDialog();
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
										  Throwable throwable, JSONObject errorResponse) {
						// TODO Auto-generated method stub
						super.onFailure(statusCode, headers, throwable,
								errorResponse);

						scrollView.onRefreshComplete();
						adapter.notifyDataSetChanged();
						dismissLoadingDialog();
					}
				});
	}
    
    public void updateAddInfo(JSONObject json) {
		try {

			maxPage = json.getInt("totalPage");
			list = null;
			if (!json.isNull("list")) {
				list = json.getJSONArray("list");

				if (list != null)
					for (int i = 0; i < list.length(); i++) {
						JSONObject templist = list.getJSONObject(i);
						RedBagItem item = new RedBagItem();
						item.createRedItem(templist);
						data.add(item);

					}

			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(type == 0){
			adapter.notifyDataSetChanged();
		}else if(type == 1){
			adapter2.notifyDataSetChanged();
		}else{
			adapter3.notifyDataSetChanged();
		}

		scrollView.onRefreshComplete();
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

    public void showIntegrationDHDialog(final View view,final String url){

		CommonDialog.Builder ibuilder  = new CommonDialog.Builder(this);
		ibuilder.setTitle("立即兑换");
		ibuilder.setMessage("亲们，这是我给你的红包，快来领去吧!"+url);
		ibuilder.setPositiveButton(R.string.share, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				SystenmApi.showShareView(RewardWithRedEnvelopeActivity.this, "分享红包", "亲，这是我给你们的红包，快来领取吧",
						url);
				dialog.dismiss();
			}
		});
		ibuilder.setNegativeButton(R.string.back,new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		ibuilder.create().show();

    }
    
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.back:
                finish(0);
                break;
            case R.id.redbag_ysc_btn:
				this.type=0;
            	data.clear();
    			adapter2.notifyDataSetChanged();
    			listView2.setVisibility(View.GONE);
    			adapter3.notifyDataSetChanged();
    			listView3.setVisibility(View.GONE);
    			listView.setVisibility(View.VISIBLE);
                adapter.notifyDataSetChanged();
				getListDataRddBag(type);
                break;
            case R.id.redbag_yqd_btn:
				this.type=1;
				data.clear();
    			adapter.notifyDataSetChanged();
    			adapter3.notifyDataSetChanged();
    			listView.setVisibility(View.GONE);
    			listView3.setVisibility(View.GONE);
				listView2.setVisibility(View.VISIBLE);
                adapter2.notifyDataSetChanged();
				getListDataRddBag(type);
                break;
            case R.id.redbag_yqg_btn:
				this.type=2;
				data.clear();
            	adapter.notifyDataSetChanged();
    			adapter2.notifyDataSetChanged();
    			listView.setVisibility(View.GONE);
    			listView2.setVisibility(View.GONE);
				listView3.setVisibility(View.VISIBLE);
            	adapter3.notifyDataSetChanged();
				getListDataRddBag(type);
                break;
            case R.id.red_btn:
            	
			if (ed_redmoney.getText() != null
					&& ed_redmoney.getText().length() != 0
					&& !ed_redmoney.getText().equals("0")) {

				redmoney = ed_redmoney.getText().toString();




				red_btn.setEnabled(false);
				getRddBagSend();
				new TimeThread().start();



			} else {

				showCustomToast("请输入红包金额！");
				return;
			}

			break;
           /* case R.id.dialog_submit:

    			v.setEnabled(false);
    			SystenmApi.showShareView(this, "分享红包", "亲，这是我给你们的红包，快来领取吧",
						share_url);
				dialog.dismiss();
    			v.setEnabled(true);
                break;
            case R.id.dialog_cancle:

                dialog.dismiss();

                break;*/


        }
    }

	/**
	 * 计时线程（防止在一定时间段内重复点击按钮）
	 */
	private class TimeThread extends Thread {
		public void run() {
			try {
				sleep(1000);
				red_btn.setEnabled(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

    

    @Override
    protected void onResume() {
        super.onResume();
        getListDataRddBag(type);
    }

    private void getRddBagSend(){

        JsonBuilder builder = new JsonBuilder();
		builder.put("bonus_money", redmoney);
		
        BaseHttpClient.post(getBaseContext(), Default.send, builder,
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

                                    MyLog.e("获取奖励管理之红包生成", "" + json.toString());
                                    if (json.has("message")) {

                                    showCustomToast(json.getString("message"));
                                    ed_redmoney.getText().clear();
									getListDataRddBag(type);
                        			}
                                 

                                } else {
									String message = json.getString("message");
									SystenmApi.showCommonErrorDialog(RewardWithRedEnvelopeActivity.this, json.getInt("status"),message);
								}
                            } else {
                                showCustomToast(R.string.toast1);
                            }
                            dismissLoadingDialog();

							adapter.notifyDataSetChanged();
							scrollView.onRefreshComplete();
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


    }
    
    private void getListDataRddBag(int type){
        /***
         * 清除原有数据
         */
        if (data.size() > 0) {
            data.clear();
        }

        JsonBuilder builder = new JsonBuilder();
		builder.put("page", curPage);
		builder.put("limit", pageCount);
		if(type==0){
		builder.put("status", 1);
		}else if(type==1){
		builder.put("status", 4);	
		}else{
		builder.put("status", 3);		
		}
		
        BaseHttpClient.post(getBaseContext(), Default.bonus_index, builder,
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

                                    MyLog.e("获取红包信息", "" + json.toString());

                                    updataViewInfo(json);
                                } else {
									String message = json.getString("message");
									SystenmApi.showCommonErrorDialog(RewardWithRedEnvelopeActivity.this, json.getInt("status"),message);
								}
                            } else {
                                showCustomToast(R.string.toast1);
                            }
                            dismissLoadingDialog();

							adapter.notifyDataSetChanged();
							scrollView.onRefreshComplete();
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


    }

    /**
     * 更新显示
     * */
    private void updataViewInfo(JSONObject json){
    	try {
    		
    		maxPage = json.getInt("totalPage");
			curPage = json.getInt("nowPage");
    		
			if (!json.isNull("list")) {
				list = json.getJSONArray("list");

				if (list != null){
					for (int i = 0; i < list.length(); i++) {
						JSONObject templist = list.getJSONObject(i);
						RedBagItem item = new RedBagItem();
						item.createRedItem(templist);
						data.add(item);
					}
				}
			}
    	
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	if(type == 0){
			adapter.notifyDataSetChanged();
		}else if(type == 1){
			adapter2.notifyDataSetChanged();
		}else{
			adapter3.notifyDataSetChanged();
		}

		scrollView.onRefreshComplete();


    }

    class RedBagAdapter extends BaseAdapter{


        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public RedBagItem getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
		public View getView(int position, View convertView, ViewGroup parent) {

			MyLog.e("123","1--------------");
			RedBagItem item = (RedBagItem) data.get(position);

			ViewHolderFirst viewHolderFirst;
			if(convertView == null){
				LayoutInflater mInflater = LayoutInflater.from(RewardWithRedEnvelopeActivity.this);
				convertView = mInflater.inflate(
						R.layout.reward_adapter_item_redpacket_ysc, null);
					viewHolderFirst = new ViewHolderFirst();

					viewHolderFirst.tv_rewardpacket_money = (TextView) convertView.
							findViewById(R.id.tv_rewardpacket_money);
					viewHolderFirst.tv_rewardpacket_sctime = (TextView) convertView.
	                		findViewById(R.id.tv_rewardpacket_sctime);
					viewHolderFirst.tv_rewardpacket_gqtime = (TextView) convertView.
	                		findViewById(R.id.tv_rewardpacket_gqtime);

					convertView.setTag(viewHolderFirst);



				}else {

				viewHolderFirst = (ViewHolderFirst) convertView.getTag();
			}


				viewHolderFirst.tv_rewardpacket_money.setText(adapter.getItem(position).getBonus_money()+"元");
				viewHolderFirst.tv_rewardpacket_sctime.setText(adapter.getItem(position).getCreate_time());
				viewHolderFirst.tv_rewardpacket_gqtime.setText(adapter.getItem(position).getValidate_et());

			return convertView;
		}


    }
    
    class RedBagAdapter2 extends BaseAdapter {

		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public RedBagItem getItem(int position) {
			return data.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			MyLog.e("123","2--------------");
			RedBagItem item = (RedBagItem) data.get(position);

			ViewHolderSecond viewHolderSecond = null;
			if(null == convertView){

				viewHolderSecond = new ViewHolderSecond();
				LayoutInflater mInflater = LayoutInflater.from(RewardWithRedEnvelopeActivity.this);
				convertView = mInflater.inflate(
						R.layout.reward_adapter_item_redpacket_yqd, null);

				viewHolderSecond. tv_rewardpacket_money = (TextView) convertView
						.findViewById(R.id.tv_rewardpacket_money);
				viewHolderSecond. tv_rewardpacket_lqtime = (TextView) convertView
						.findViewById(R.id.tv_rewardpacket_lqtime);
				viewHolderSecond. tv_rewardpacket_source = (TextView) convertView
						.findViewById(R.id.tv_rewardpacket_source);


				convertView.setTag(viewHolderSecond);
			}else {

				viewHolderSecond = (ViewHolderSecond) convertView.getTag();
			}

			viewHolderSecond.tv_rewardpacket_money.setText(adapter.getItem(position).getBonus_money()+"元");
			viewHolderSecond.tv_rewardpacket_lqtime.setText(adapter.getItem(position).getTake_time());
			viewHolderSecond.tv_rewardpacket_source.setText(adapter.getItem(position).getSource_name());



			return convertView;
		}
	}

    class RedBagAdapter3 extends BaseAdapter {

		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public RedBagItem getItem(int position) {
			return data.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			MyLog.e("123","3--------------");
			RedBagItem item = (RedBagItem) data.get(position);

			ViewHolderThird viewHolderThird = null;
			if(null == convertView){

				viewHolderThird = new ViewHolderThird();
				LayoutInflater mInflater = LayoutInflater.from(RewardWithRedEnvelopeActivity.this);
				convertView = mInflater.inflate(
						R.layout.reward_adapter_item_redpacket_ygq, null);

				viewHolderThird. tv_rewardpacket_money = (TextView) convertView
						.findViewById(R.id.tv_rewardpacket_money);
				viewHolderThird. tv_rewardpacket_gqtime = (TextView) convertView
						.findViewById(R.id.tv_rewardpacket_gqtime);
				viewHolderThird. tv_rewardpacket_zt = (TextView) convertView
						.findViewById(R.id.tv_rewardpacket_zt);


				convertView.setTag(viewHolderThird);
			}else {

				viewHolderThird = (ViewHolderThird) convertView.getTag();
			}

			viewHolderThird.tv_rewardpacket_money.setText(adapter.getItem(position).getBonus_money()+"元");
			viewHolderThird.tv_rewardpacket_gqtime.setText(adapter.getItem(position).getTake_time());
			viewHolderThird.tv_rewardpacket_zt.setText(adapter.getItem(position).getSource_name());



			return convertView;
		}
	}

    public  static class ViewHolderFirst{

            TextView tv_rewardpacket_money;
            TextView tv_rewardpacket_sctime;
            TextView tv_rewardpacket_gqtime;
            
    }
    public  static class ViewHolderSecond{
    	
    	TextView tv_rewardpacket_money;
    	TextView tv_rewardpacket_lqtime;
    	TextView tv_rewardpacket_source;
    	
    }
    public  static class ViewHolderThird{
    	
    	TextView tv_rewardpacket_money;
    	TextView tv_rewardpacket_gqtime;
    	TextView tv_rewardpacket_zt;
    	
    }


    public void finish() {
        super.finish();
    }

}
