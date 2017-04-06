package com.lmq.main.activity.person.companyinfo;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.xyh.R;
import com.lmq.http.BaseHttpClient;
import com.lmq.http.JsonHttpResponseHandler;
import com.lmq.main.api.BaseActivity;
import com.lmq.main.api.JsonBuilder;
import com.lmq.main.api.SystenmApi;
import com.lmq.main.item.UploadInfoItem;
import com.lmq.main.util.Default;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

public class CompanyUpload extends BaseActivity implements OnClickListener {


    private EditText file_name;
    private ListView listView;
    private boolean has_info;
    private ArrayList<UploadInfoItem> data;
    private UploadFileAdapter adapter;
    private int currentPage;
    private int totalPage;
    private int limit;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data_upload_layout);

        data = new ArrayList<UploadInfoItem>();
        if (null != getIntent()) {

            if (getIntent().hasExtra("has_info")) {

                has_info = getIntent().getBooleanExtra("has_info", false);
            }

        }
        TextView text = (TextView) findViewById(R.id.title);
        text.setText("资料上传");


        file_name = (EditText) findViewById(R.id.file_name);

        listView = (ListView) findViewById(R.id.upload_list);
        adapter = new UploadFileAdapter();
        listView.setAdapter(adapter);


        findViewById(R.id.back).setOnClickListener(this);
        findViewById(R.id.upload_btn).setOnClickListener(this);
        findViewById(R.id.submit).setOnClickListener(this);

    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish(0);
                break;
            case R.id.submit:
                //TODO 上传Action
                break;
            case R.id.upload_btn:
                //TODO 选择上传文件Action
                break;


        }
    }

    // 提交用户反馈到服务器
    public void uploadAndGetUploadInfo() {

        JsonBuilder builder = new JsonBuilder();
        if (has_info) {
            builder.put("isdata", "1");
            builder.put("page", "1");
            builder.put("limit", "10");
        } else {

            //填入参数


        }


        BaseHttpClient.post(getBaseContext(), Default.editdata, builder,
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
                                    // initData(json);
                                    // 获取新版本
                                    if (has_info) {

                                        data.clear();

                                        if (json.has("totalPage")) {

                                            totalPage = json.optInt("totalPage", 0);

                                        }

                                        if (json.has("nowPage")) {

                                            currentPage = json.optInt("nowPage", 0);

                                        }


                                        //TODO 更新数据
                                        if (json.has("list")) {


                                            JSONArray array = json.optJSONArray("list");

                                            for (int i = 0; i < array.length(); i++) {

                                                UploadInfoItem item = new UploadInfoItem(array.getJSONObject(i));

                                                data.add(item);

                                            }


                                        }

                                    } else {
                                        showCustomToast(json.getString("message"));
                                    }

                                } else {
                                    String message = json.getString("message");
                                    SystenmApi.showCommonErrorDialog(CompanyUpload.this, json.getInt("status"),message);
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


    @Override
    protected void onResume() {
        super.onResume();
        uploadAndGetUploadInfo();
    }

    public void finish() {
        super.finish();
    }


    class UploadFileAdapter extends BaseAdapter {


        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public UploadInfoItem getItem(int position) {
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
                convertView = LinearLayout.inflate(CompanyUpload.this, R.layout.upload_info_itemlayout, null);


                viewHolder.file_name = (TextView) convertView.findViewById(R.id.file_name);
                viewHolder.file_dec = (TextView) convertView.findViewById(R.id.upload_file_dec);
                viewHolder.file_status = (TextView) convertView.findViewById(R.id.upload_status);


                convertView.setTag(viewHolder);
            } else {

                viewHolder = (ViewHolder) convertView.getTag();

            }


            viewHolder.file_name.setText(adapter.getItem(position).getFileName());
            viewHolder.file_dec.setText(adapter.getItem(position).getFilees());
            viewHolder.file_status.setText(adapter.getItem(position).getFileStatus());


            return convertView;
        }
    }


    public static final class ViewHolder {

        private TextView file_name;
        private TextView file_dec;
        private TextView file_status;


    }


}




