


package com.lmq.main.activity.person.personinfo;





import android.os.Bundle;


import android.view.Gravity;


import android.view.View;


import android.view.View.OnClickListener;


import android.widget.AdapterView;


import android.widget.EditText;


import android.widget.RadioGroup;


import android.widget.Spinner;


import android.widget.TextView;





import com.bigkoo.pickerview.OptionsPopupWindow;


import com.xyh.R;


import com.lmq.http.BaseHttpClient;


import com.lmq.http.JsonHttpResponseHandler;


import com.lmq.main.api.BaseActivity;


import com.lmq.main.api.JsonBuilder;


import com.lmq.main.api.SystenmApi;


import com.lmq.main.util.Default;





import org.apache.http.Header;


import org.json.JSONObject;





import java.util.ArrayList;


import java.util.HashMap;


import java.util.Iterator;





public class Personinfo extends BaseActivity implements OnClickListener {








    private TextView real_name;


    private TextView id_card;


    private TextView phone_num;


    private TextView birthday_time;


    private EditText old_address;


    private EditText address;


    private String hy_choice;


    private TextView xl;


    private TextView hy_text;


    private TextView sr;


    private EditText zy;


    private EditText jkyy;


    private OptionsPopupWindow xl_spinner;


    private OptionsPopupWindow sr_spinner;


    private OptionsPopupWindow hy_spinner;





    private boolean has_info;





    private ArrayList<String> xlList = new ArrayList<String>();


    private ArrayList<String> srList = new ArrayList<String>();


    private ArrayList<String> hyrList = new ArrayList<String>();
   private HashMap<String, String> retMap;








    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);


        setContentView(R.layout.person_data_1_layout);





        TextView text = (TextView) findViewById(R.id.title);


        text.setText("个人资料");











        if (null != getIntent()) {





            has_info = getIntent().getBooleanExtra("has_info", false);


        }








        real_name = (TextView) findViewById(R.id.real_name);


        id_card = (TextView) findViewById(R.id.id_card);


        phone_num = (TextView) findViewById(R.id.phone_num);


        birthday_time = (TextView) findViewById(R.id.birthday_time);





        old_address = (EditText) findViewById(R.id.old_address);


        address = (EditText) findViewById(R.id.address);


        zy = (EditText) findViewById(R.id.zy);





        jkyy = (EditText) findViewById(R.id.jkyy);


        xl = (TextView) findViewById(R.id.xl);


        sr = (TextView) findViewById(R.id.sr);


        hy_text = (TextView) findViewById(R.id.hy_text);














        xl_spinner = new OptionsPopupWindow(Personinfo.this);


        xl_spinner.setTitle("请选择学历");





        xlList.add("高中或以下");


        xlList.add("大专");


        xlList.add("本科");


        xlList.add("研究生或以上");








        xl_spinner.setPicker(xlList);





        xl_spinner.setOnoptionsSelectListener(new OptionsPopupWindow.OnOptionsSelectListener() {


            @Override


            public void onOptionsSelect(int options1, int option2, int options3) {





                xl.setText(xlList.get(options1));








            }


        });





        sr_spinner = new OptionsPopupWindow(Personinfo.this);


        sr_spinner.setTitle("请选择月收入");











        srList.add("1000元以下");


        srList.add("1001-2000元");


        srList.add("2000-5000元");


        srList.add("5000-10000元");


        srList.add("10000-20000元");


        srList.add("20000-50000元");


        srList.add("50000元以上");





        sr_spinner.setPicker(srList);





        sr_spinner.setOnoptionsSelectListener(new OptionsPopupWindow.OnOptionsSelectListener() {


            @Override


            public void onOptionsSelect(int options1, int option2, int options3) {





                sr.setText(srList.get(options1));








            }


        });





        hy_spinner =  new OptionsPopupWindow(Personinfo.this);


        hyrList.add("未婚");


        hyrList.add("已婚");


        hy_spinner.setPicker(hyrList);


        hy_spinner.setOnoptionsSelectListener(new OptionsPopupWindow.OnOptionsSelectListener() {


            @Override


            public void onOptionsSelect(int options1, int option2, int options3) {





                hy_text.setText(hyrList.get(options1));





            }


        });














        findViewById(R.id.back).setOnClickListener(this);


        findViewById(R.id.submit).setOnClickListener(this);


        findViewById(R.id.xl_btn).setOnClickListener(this);


        findViewById(R.id.sr_btn).setOnClickListener(this);


        findViewById(R.id.hy_btn).setOnClickListener(this);








    }





    public void onClick(View v) {


        switch (v.getId()) {


            case R.id.back:


                finish(0);


                break;


            case R.id.submit:


                //TODO 提交个人资料


                has_info = false;


                checkoutUserInput();


                break;


            case R.id.xl_btn:


                xl_spinner.showAtLocation(v, Gravity.BOTTOM, 0, 0);


                break;


            case R.id.sr_btn:


                sr_spinner.showAtLocation(v, Gravity.BOTTOM, 0, 0);


                break;


            case R.id.hy_btn:


                hy_spinner.showAtLocation(v, Gravity.BOTTOM, 0, 0);


                break;








        }


    }





    private void checkoutUserInput() {





        if (SystenmApi.isNullOrBlank(old_address.getText().toString())) {





            showCustomToast("请输入籍贯地址");


            return;





        }








        if (SystenmApi.isNullOrBlank(address.getText().toString())) {





            showCustomToast("请输入现居住地址");


            return ;





        }








        if (SystenmApi.isNullOrBlank(hy_text.getText().toString())) {





            showCustomToast("请选择婚姻状况");


            return ;





        }





        if (SystenmApi.isNullOrBlank(xl.getText().toString())) {





            showCustomToast("请选择最高学历");


            return ;





        }


        if (SystenmApi.isNullOrBlank(sr.getText().toString())) {





            showCustomToast("请选择月收入");


            return ;





        }








        if (SystenmApi.isNullOrBlank(zy.getText().toString())) {





            showCustomToast("请输入职业");


            return ;





        }





        if (SystenmApi.isNullOrBlank(jkyy.getText().toString())) {





            showCustomToast("请输入借款原因");


            return ;





        }





         retMap = new HashMap<String, String>();








        retMap.put("origin_place", old_address.getText().toString());


        retMap.put("address", address.getText().toString());


        retMap.put("marry", hy_text.getText().toString());


        retMap.put("education", xl.getText().toString());


        retMap.put("income", sr.getText().toString());


        retMap.put("profession", zy.getText().toString());


        retMap.put("borrowing_causes", jkyy.getText().toString());


        uplaodAndgetInfo();






    }








    private void updateInfo(JSONObject json) {














        if (json.has("real_name")) {








            real_name.setText(json.optString("real_name", ""));





        }


        if (json.has("idcard")) {








            id_card.setText(json.optString("idcard", ""));





        }


        if (json.has("user_phone")) {








            phone_num.setText(json.optString("user_phone", ""));





        }


        if (json.has("birthday")) {





            birthday_time.setText(json.optString("birthday", ""));





        }


        if (json.has("origin_place")) {








            old_address.setText(json.optString("origin_place", ""));





        }


        if (json.has("address")) {








            address.setText(json.optString("address", ""));





        }


        if (json.has("marry")) {








            hy_text.setText(json.optString("marry", ""));





        }


        if (json.has("borrowing_causes")) {








            jkyy.setText(json.optString("borrowing_causes", ""));





        }


        if (json.has("profession")) {








            zy.setText(json.optString("profession", ""));





        }





        if(json.has("education")){





            xl.setText(json.optString("education",""));


        }


        if(json.has("income")){





            sr.setText(json.optString("income",""));


        }








    }





    @Override


    protected void onResume() {


        super.onResume();


        uplaodAndgetInfo();


    }





    // 提交用户反馈到服务器


    public void uplaodAndgetInfo() {





        JsonBuilder builder = new JsonBuilder();


        if (has_info) {
            builder.put("is_data", "1");

        } else {
            builder.put("is_data", "2");

         if(null != retMap) {

             Iterator<String> iterator = retMap.keySet().iterator();


             while (iterator.hasNext()) {


                 String key = iterator.next();


                 String value = retMap.get(key);


                 builder.put(key, value);


             }
         }


        }








        BaseHttpClient.post(getBaseContext(), Default.people, builder


                ,


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



                                    if (has_info) {


                                        updateInfo(json);

                                    } else {


                                        showCustomToast(json.getString("message"));
                                        finish();


                                    }


                                } else {
                                    String message = json.getString("message");
                                    SystenmApi.showCommonErrorDialog(Personinfo.this, json.getInt("status"),message);
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





    public void finish() {


        super.finish();


    }





}
 

 

   



