package com.lmq.main.dialog;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.xyh.R;

public class PopDialog {
	private ArrayList<String> itemList;
	private Context context;
	private PopupWindow popupWindow;
	private ListView listView;
	private OnItemClickListener listener;
	private PopAdapter popAdapter;
	private int defaultSelect = 0;
	private Button cButton, sButton;
	private OnClickListener mClickListener;
	private TextView titleView;
	private View topTools;
	private int x = 0,y=0;
	private int  gravity = Gravity.BOTTOM;
    private int type = 0;

	
	
	
	public void setGravity(int gravity ){
		
		this.gravity = gravity;
	}
	
	public void setPopShowLoaction(int x,int y){
		
		this.x = x;
		this.y = y;
		
	}

    /***
     * 设置默认的显示类型
     * @param type
     */
    public void setType(int type){
        this.type = type;
    }

	public int getDefaultSelect() {
		return defaultSelect;

	}

	public void setDefaultSelect(int defaultSelect) {
		this.defaultSelect = defaultSelect;
		popAdapter.notifyDataSetChanged();

	}

	public void clearData(){
		
		if(itemList.size() > 0){
			itemList.clear();
		}
	}
	
	
	public PopDialog(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;

		itemList = new ArrayList<String>(5);
		View view = LayoutInflater.from(context).inflate(
				R.layout.show_package_dialog, null);
		cButton = (Button) view.findViewById(R.id.cButton);
		sButton = (Button) view.findViewById(R.id.sButton);
		titleView = (TextView) view.findViewById(R.id.name);
		
		topTools = view.findViewById(R.id.top_tools);
		// 设置 listview
		listView = (ListView) view.findViewById(R.id.pkg_list);
		popAdapter = new PopAdapter();
		listView.setAdapter(popAdapter);

		popupWindow = new PopupWindow(view, LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);

	}

	// 设置菜单项点击监听器
	public void setOnItemClickListener(OnItemClickListener listener) {
		this.listener = listener;
		listView.setOnItemClickListener(this.listener);
	}

	public void setonClickListener(OnClickListener listener) {

		sButton.setOnClickListener(listener);
		cButton.setOnClickListener(listener);
	}
	
	public void hidenTopTools(){
		
		topTools.setVisibility(View.GONE);
		
	}

	// 批量添加菜单项
	public void addItems(String[] items) {
		for (String s : items)
			itemList.add(s);
	}

	// 单个添加菜单项
	public void addItem(String item) {
		itemList.add(item);
	}
	
	public void setDialogTitle(String title){
		
		titleView.setText(title);
		
	}

	// 下拉式 弹出 pop菜单 parent 右下角
	public void showAsDropDown(View parent) {
        if(type == 0){

            popupWindow.showAtLocation(parent, Gravity.BOTTOM, x, y);

        }else{

            popupWindow.showAsDropDown(parent);

        }



		// 使其聚集
		popupWindow.setFocusable(true);
		// 设置允许在外点击消失
		popupWindow.setOutsideTouchable(false);
		// 刷新状态
		popupWindow.update();
	}

    public boolean isShowing(){

        return popupWindow.isShowing();

    }
	// 隐藏菜单
	public void dismiss() {
		popupWindow.dismiss();
	}

	// 适配器
	private final class PopAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return itemList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return itemList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder;
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(
						R.layout.pomenu_item, null);
				holder = new ViewHolder();

				convertView.setTag(holder);

				holder.groupItem = (TextView) convertView
						.findViewById(R.id.textView);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.groupItem.setText(itemList.get(position));

			if (defaultSelect == position) {
				holder.groupItem.setBackgroundColor(context.getResources()
						.getColor(R.color.blue));
			} else {
				holder.groupItem.setBackgroundColor(context.getResources()
						.getColor(R.color.white));
			}

			return convertView;
		}

		private final class ViewHolder {
			TextView groupItem;
		}
	}
}
