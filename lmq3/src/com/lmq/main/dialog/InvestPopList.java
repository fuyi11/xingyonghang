package com.lmq.main.dialog;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.xyh.R;

import java.util.ArrayList;

/**
 * Created by zhaoshuai on 15/8/7.
 */
public class InvestPopList {

    private ArrayList<String> data = new ArrayList<String>();

    private ListView listView;

    private Context context;

    private PopupWindow popList;

    private PopListAdapter adapter;

    private AdapterView.OnItemClickListener listener;

    private int defauleSelect = 0;


    public InvestPopList(Context context) {
        this.context = context;

        View view = LayoutInflater.from(context).inflate(
                R.layout.pop_list, null);

        listView = (ListView) view.findViewById(R.id.list_menu);

        adapter = new PopListAdapter();
        listView.setAdapter(adapter);

        popList = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        popList.setFocusable(true);
        popList.setOutsideTouchable(false);
        popList.setBackgroundDrawable(new BitmapDrawable());




    }


    /***
     * 设置当前选中项
     * @param defauleSelect
     */
    public void setDefauleSelect(int defauleSelect){

        this.defauleSelect = defauleSelect;

        adapter.notifyDataSetChanged();
    }

    /***
     *获取当前选中项
     * @return
     */
    public int getDefauleSelect(){

        return defauleSelect;

    }



    public  boolean isShowing(){

        return popList.isShowing();
    }

    public void showPOpList(View parent){

        if (popList != null && popList.isShowing()) {




            popList.dismiss();
        }
        else {
            popList.showAsDropDown(parent);

            popList.update();
        }


    }




    /***
     * 添加点击事件
     * @param listener
     */
    public void setOnItemClickLinstener(AdapterView.OnItemClickListener listener){

        this.listener = listener;

        listView.setOnItemClickListener(this.listener);

    }


    /***
     * 添加数据
     * @param arrays
     */
    public void addItems(String[] arrays){


        for(String items:arrays){

            data.add(items);
        }

        adapter.notifyDataSetChanged();

    }


    /***
     * 清楚数据
     */
    public void clearData(){

        data.clear();

    }


    public void dissmiss(){

        popList.dismiss();
    }




    private class PopListAdapter extends BaseAdapter{


        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public String getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(
                        R.layout.pop_list_item, null);
                holder = new ViewHolder();



                holder.lsit_title = (TextView) convertView.findViewById(R.id.list_title);
                holder.select_status = (ImageView) convertView.findViewById(R.id.select_status);
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.lsit_title.setText(data.get(position));

            /**设置选中状态显示图标**/
            if(position == defauleSelect){

                holder.select_status.setVisibility(View.VISIBLE);
            }else {

                holder.select_status.setVisibility(View.GONE);
            }





            return convertView;
        }


    }







    public final class ViewHolder {
        TextView lsit_title;
        ImageView select_status;
    }










}
