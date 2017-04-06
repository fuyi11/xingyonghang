package com.lmq.main.activity.user.manager.message;

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
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.xyh.R;
import com.lmq.http.BaseHttpClient;
import com.lmq.http.JsonHttpResponseHandler;
import com.lmq.main.api.BaseActivity;
import com.lmq.main.api.JsonBuilder;
import com.lmq.main.api.SystenmApi;
import com.lmq.main.dialog.CommonDialog;
import com.lmq.main.item.ZNXItem;
import com.lmq.main.util.Default;

/***
 * 站内信
 * @author sjc
 *
 */
public class SitMessageInfoActivity extends BaseActivity implements OnClickListener {

    /**
     * 全部
     */
    private RadioButton qb;
    /**
     * 已读
     */
    private RadioButton yd;
    /**
     * 未读
     */
    private RadioButton wd;

    private PullToRefreshListView listView;
    private ZNXAdapter adapter;
    private JSONArray list = null;
    private int maxPage,curPage = 1, pageCount = 8;
    /*
     * 消息类型 0未读 1已读 不传默认是全部
     */
    private int type=0;

    /***
     * 数据源
     */
    private ArrayList<ZNXItem> data = new ArrayList<ZNXItem>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.znx_layout);

        TextView text = (TextView) findViewById(R.id.title);
        text.setText(R.string.znx);

        findViewById(R.id.back).setOnClickListener(this);

        qb = (RadioButton) findViewById(R.id.qb);
        yd = (RadioButton) findViewById(R.id.yd);
        wd = (RadioButton) findViewById(R.id.wd);

        qb.setOnClickListener(this);
        yd.setOnClickListener(this);
        wd.setOnClickListener(this);

        listView = (PullToRefreshListView) findViewById(R.id.znx_list);
        adapter = new ZNXAdapter();
        listView.setAdapter(adapter);
        
        listView.setMode(Mode.BOTH);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                ZNXItem item = adapter.getItem(position-1);

            	 if(type==0 && item.getStatus()==0){
                     doHttpReadMsg(item);
                 }else  if(type==2){
                     doHttpReadMsg(item);
                 }else {
                     showZNXDialog(item.getMsg_name(), item.getMsg_content());
                 }
            }
        });
        
        listView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				if (refreshView.isHeaderShown()) {
                    doHttpMsgList(type);
				} else {
					JsonBuilder builder = new JsonBuilder();
					if (curPage + 1 <= maxPage) {
						curPage++;
						 builder.put("page", curPage);
						 builder.put("limit", pageCount);
						/** 消息类型 0未读 1已读 不传默认是全部 */
						if (type == 0) {
						} else if (type == 1) {
							builder.put("status", 1);
						} else {
							builder.put("status", 0);
						}
                        doHttpUpdateMsgList(builder);
					} else {
						handler.sendEmptyMessage(1);
					}
				}
			}

		});

    }
    
    public void doHttpUpdateMsgList(JsonBuilder builder) {

		BaseHttpClient.post(this, Default.msg_index, builder,
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
                                }else {
                                    String message = response.getString("message");
                                    SystenmApi.showCommonErrorDialog(SitMessageInfoActivity.this, response.getInt("status"),message);
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

						adapter.notifyDataSetChanged();

						listView.onRefreshComplete();
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
						   ZNXItem item = new ZNXItem();
	                        item.createRedItem(templist);
	                        data.add(item);

					}

			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		adapter.notifyDataSetChanged();

		listView.onRefreshComplete();
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

				listView.onRefreshComplete();
			}
		}

	};
    
    public void showZNXDialog(String name,String content ){
        CommonDialog.Builder ibuilder  = new CommonDialog.Builder(this);
        ibuilder.setTitle(name);
        ibuilder.setMessage(content);
        ibuilder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                doHttpMsgList(type);
            }
        });
        ibuilder.create().show();
    }

    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.back:
                finish(0);
                break;
            case R.id.qb:
                this.type=0;
                doHttpMsgList(type);
                break;
            case R.id.yd:
                this.type=1;
                doHttpMsgList(type);
                break;
            case R.id.wd:
                this.type=2;
                doHttpMsgList(type);
                break;
        }
    }



    @Override
    protected void onResume() {
        super.onResume();
        doHttpMsgList(type);
    }

    private void doHttpMsgList(int type){
        /***
         * 清除原有数据
         */
        if (data.size() > 0) {
            data.clear();
        }

        JsonBuilder builder = new JsonBuilder();
        builder.put("page", 0);
        builder.put("limit", pageCount);
        /**消息类型 0未读 1已读 不传默认是全部*/
        if(type==0){
        }else if(type==1){
            builder.put("status",1);
        }else{
            builder.put("status", 0);
        }

        BaseHttpClient.post(getBaseContext(), Default.msg_index, builder,
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
                                    updataViewInfo(json);
                                } else {
                                    String message = json.getString("message");
                                    SystenmApi.showCommonErrorDialog(SitMessageInfoActivity.this, json.getInt("status"),message);
                                }
                            } else {
                                showCustomToast(R.string.toast1);
                            }
                            dismissLoadingDialog();

							adapter.notifyDataSetChanged();
							listView.onRefreshComplete();
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
    
    private void doHttpReadMsg(final ZNXItem item){

        JsonBuilder builder = new JsonBuilder();
        builder.put("id", item.getId());
       
        BaseHttpClient.post(getBaseContext(), Default.changestatus, builder,
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
                                    showZNXDialog(item.getMsg_name(), item.getMsg_content());
                                } else {
                                    String message = json.getString("message");
                                    SystenmApi.showCommonErrorDialog(SitMessageInfoActivity.this, json.getInt("status"),message);
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
                        ZNXItem item = new ZNXItem();
                        item.createRedItem(templist);
                        data.add(item);
                    }
                }
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        }
        adapter.notifyDataSetChanged();
    }
    

    class ZNXAdapter extends BaseAdapter{


        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public ZNXItem getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (null == convertView)
            {
                LayoutInflater mInflater = LayoutInflater.from(SitMessageInfoActivity.this);
                convertView = mInflater.inflate(R.layout.znx_item_layout, null);
            }

            TextView msg_name = (TextView) convertView.findViewById(R.id.msg_name);
            TextView message = (TextView) convertView.findViewById(R.id.message);
            TextView date = (TextView) convertView.findViewById(R.id.date);
            View dian = (View) convertView.findViewById(R.id.dian);

            msg_name.setText(adapter.getItem(position).getMsg_name());
            message.setText(adapter.getItem(position).getTitle());
            date.setText(adapter.getItem(position).getSend_time());
            /**消息未读状态 0未读 1已读**/
            if(adapter.getItem(position).getStatus()==0){
                dian.setVisibility(View.VISIBLE);
            }else{
                dian.setVisibility(View.GONE);
            }
            return convertView;
        }
    }

    public void finish() {
        super.finish();
    }
}
