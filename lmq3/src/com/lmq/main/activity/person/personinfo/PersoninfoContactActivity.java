
package com.lmq.main.activity.person.personinfo;





import android.os.Bundle;


import android.view.Gravity;


import android.view.View;


import android.view.View.OnClickListener;


import android.widget.AdapterView;


import android.widget.EditText;


import android.widget.RadioButton;


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


import java.util.Collections;


import java.util.HashMap;


import java.util.Iterator;





public class PersoninfoContactActivity extends BaseActivity implements OnClickListener {








    private EditText first_contact_people;


    private EditText first_contact_tel;





    private EditText second_contact_people;


    private EditText second_contact_tel;








    private boolean has_info;





    private TextView first_contact_relationship;


    private TextView second_contact_relationship;





    private boolean firstReClick = true;





    private OptionsPopupWindow re_pop;


    private ArrayList<String> reList = new ArrayList<String>();


    private HashMap<String, String> retMap;






    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);


        setContentView(R.layout.contact_way_layout);





        TextView text = (TextView) findViewById(R.id.title);


        text.setText("联系方式");








        if (null != getIntent()) {





            if (getIntent().hasExtra("has_info")) {





                has_info = getIntent().getBooleanExtra("has_info", false);





            }





        }








        re_pop = new OptionsPopupWindow(PersoninfoContactActivity.this);





        reList.add("家庭成员");


        reList.add("朋友");


        reList.add("商业伙伴");








        re_pop.setPicker(reList);





        re_pop.setOnoptionsSelectListener(new OptionsPopupWindow.OnOptionsSelectListener() {


            @Override


            public void onOptionsSelect(int options1, int option2, int options3) {








                if(firstReClick){





                    first_contact_relationship.setText(reList.get(options1));





                }else {





                    second_contact_relationship.setText(reList.get(options1));





                }
























































            }


        });

































































        first_contact_people = (EditText) findViewById(R.id.first_contact_people);


        first_contact_tel = (EditText) findViewById(R.id.first_contact_tel);








        second_contact_people = (EditText) findViewById(R.id.second_contact_people);


        second_contact_tel = (EditText) findViewById(R.id.second_contact_tel);





        first_contact_relationship = (TextView) findViewById(R.id.first_contact_re);





        second_contact_relationship = (TextView) findViewById(R.id.second_contact_re);





        findViewById(R.id.first_re_btn).setOnClickListener(this);


        findViewById(R.id.second_re_btn).setOnClickListener(this);











        findViewById(R.id.back).setOnClickListener(this);


        findViewById(R.id.submit).setOnClickListener(this);








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


            case R.id.first_re_btn:


                //TODO 提交个人资料


                firstReClick = true;


                re_pop.showAtLocation(v, Gravity.BOTTOM,0,0);


                break;


            case R.id.second_re_btn:


                //TODO 提交个人资料


                firstReClick = false;


                re_pop.showAtLocation(v, Gravity.BOTTOM, 0, 0);


                break;








        }


    }











    private void checkoutUserInput() {





        if (SystenmApi.isNullOrBlank(first_contact_people.getText().toString())) {





            showCustomToast("请输入第一联系人");


            return ;





        }








        if (SystenmApi.isNullOrBlank(first_contact_tel.getText().toString())) {





            showCustomToast("请输入第一联系人电话");


            return ;





        }








        if (SystenmApi.isNullOrBlank(first_contact_relationship.getText().toString())) {





            showCustomToast("请选择第一联系人关系");


            return ;





        }





        if (SystenmApi.isNullOrBlank(second_contact_people.getText().toString())) {





            showCustomToast("请输入第二联系人");


            return ;





        }


        if (SystenmApi.isNullOrBlank(second_contact_tel.getText().toString())) {





            showCustomToast("请输入第二联系人电话");


            return ;





        }








        if (SystenmApi.isNullOrBlank(second_contact_relationship.getText().toString())) {





            showCustomToast("请选择第二联系人关系");


            return ;





        }








        retMap = new HashMap<String, String>();








        retMap.put("contact1", first_contact_people.getText().toString());


        retMap.put("contact1_tel", first_contact_tel.getText().toString());


        retMap.put("contact1_re", first_contact_relationship.getText().toString());








        retMap.put("contact2", second_contact_people.getText().toString());


        retMap.put("contact2_tel", second_contact_tel.getText().toString());


        retMap.put("contact2_re", second_contact_relationship.getText().toString());






        uplaodAndgetInfo();



    }








    private void updateInfo(JSONObject json) {














        if (json.has("contact1")) {








            first_contact_people.setText(json.optString("contact1", ""));





        }


        if (json.has("contact1_tel")) {








            first_contact_tel.setText(json.optString("contact1_tel", ""));





        }


        if (json.has("contact1_re")) {








            first_contact_relationship.setText(json.optString("contact1_re",""));








        }


        if (json.has("contact2")) {








            second_contact_people.setText(json.optString("contact2", ""));





        }


        if (json.has("contact2_tel")) {








            second_contact_tel.setText(json.optString("contact2_tel", ""));





        }


        if (json.has("contact2_re")) {





            second_contact_relationship.setText(json.optString("contact2_re", ""));











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








        BaseHttpClient.post(getBaseContext(), Default.editcontact, builder


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
                                    SystenmApi.showCommonErrorDialog(PersoninfoContactActivity.this, json.getInt("status"),message);
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
