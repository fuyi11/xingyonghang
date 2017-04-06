package com.lmq.main.activity.user.manager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.lmq.main.item.UploadInfoItem2;
import com.lmq.main.util.BitmapToStrUtil;
import com.lmq.main.util.Default;
import com.lmq.main.util.ImageTools;
import com.lmq.view.ListViewForScrollView;

/**
 * 资料上传
 */

public class UploadUserInfoActivity extends BaseActivity implements
		OnClickListener {

	private static final int PHOTO_WITH_DATA = 18; // 从SD卡中得到图片
	private static final int PHOTO_WITH_CAMERA = 37;// 拍摄照片
    //身份证正面图片保存路径
	private String id_card_first_path;
	//身份证反面图片保存路径
	private String id_card_second_path;

	private String upload_id_card_1;
	
	private PullToRefreshScrollView scrollView;
	private ListViewForScrollView listView;
	private JSONArray list = null;
	private int maxPage,curPage = 1, pageCount = 7;
	
	private EditText ed_file_name;
	
	private ArrayList<UploadInfoItem2> data  = new ArrayList<UploadInfoItem2>();
	private UploadFileAdapter adapter;
	private String data_name;
	
	

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.data_upload_layout);

		TextView text = (TextView) findViewById(R.id.title);
        text.setText("资料上传");



		findViewById(R.id.back).setOnClickListener(this);
		findViewById(R.id.submit).setOnClickListener(this);
		findViewById(R.id.choice).setOnClickListener(this);
		
		ed_file_name = (EditText) findViewById(R.id.file_name);
		
		scrollView = (PullToRefreshScrollView) findViewById(R.id.refreshView);
		scrollView.setMode(PullToRefreshBase.Mode.BOTH);
		
		listView = (ListViewForScrollView) findViewById(R.id.upload_list);
		adapter = new UploadFileAdapter();
		listView.setAdapter(adapter);

		scrollView.setOnRefreshListener(new OnRefreshListener<ScrollView>() {
			
			@Override
			public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {

				if (refreshView.isHeaderShown()) {

					doHttpEditData();
				} else {
					JsonBuilder builder = new JsonBuilder();
					if (curPage + 1 <= maxPage) {

						curPage+=1;
						builder.put("page",curPage);
						builder.put("limit",pageCount);
						builder.put("is_data",1);
						doHttp(builder);
					} else {
						handler.sendEmptyMessage(1);
					}
				}

			}
		});

	}
	
	/**
	 * 刷新
	 * */
	public void doHttp(JsonBuilder builder) {

		BaseHttpClient.NO_RAS=true;
		BaseHttpClient.post(this, Default.editdata, builder,
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
									SystenmApi.showCommonErrorDialog(UploadUserInfoActivity.this, response.getInt("status"), message);
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
						UploadInfoItem2 item = new UploadInfoItem2();
						item.init(templist);
						data.add(item);

					}

			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			adapter.notifyDataSetChanged();

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
	
	protected void onResume() {
		super.onResume();

		doHttpEditData();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.choice:
			openPictureSelectDialog();
			break;
		case R.id.submit:
			if(SystenmApi.isNullOrBlank(ed_file_name.getText().toString())){
				showCustomToast("请输入文件名称再上传");
				return;
						
			}
			if(SystenmApi.isNullOrBlank(upload_id_card_1)){
				showCustomToast("请输入图片资料");
				return;
				
			}
			
			doHttpSubmit();
			break;
		case R.id.btn_delete:
			//delfile 删除资料
			Integer position = (Integer)v.getTag();
			UploadInfoItem2 item = adapter.getItem(position);
			 showCheckDialog(item);
			break;
		case R.id.back:
			
			finish();
			break;
		}

	}
	
	public void showCheckDialog(final UploadInfoItem2 item2) {
		AlertDialog.Builder builder = new Builder(UploadUserInfoActivity.this);
		builder.setTitle("友情提示");
		builder.setMessage("删除文件"+item2.getFileName()+"吗");

		builder.setNegativeButton("取消",
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						
					}
				});

		builder.setPositiveButton("确认",
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						DeleteData(item2);
						
					}
				});
		builder.create().show();

	}

	/**
	 * 删除数据
	 * */
	public void DeleteData(final UploadInfoItem2 item2) {
		
		JsonBuilder builder = new JsonBuilder();
		builder.put("id",item2.getId());
		
		BaseHttpClient.NO_RAS=false;
		BaseHttpClient.post(getBaseContext(), Default.delfile, builder, 
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


                             showCustomToast("删除成功");

                             data.remove(item2);
                             adapter.notifyDataSetChanged();



                         } else {
							 String message = json.getString("message");
							 SystenmApi.showCommonErrorDialog(UploadUserInfoActivity.this, json.getInt("status"), message);
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
				super.onFailure(statusCode, headers, responseString, throwable);
				dismissLoadingDialog();
				showCustomToast(responseString);

			}

		});

	}
	
	/**
	 * 
	 * 保存提交
	 * */
	public void doHttpSubmit() {
	
		JsonBuilder builder = new JsonBuilder();
		builder.put("name",ed_file_name.getText().toString());
		builder.put("uploadfile",upload_id_card_1);
		builder.put("is_data",2);
		

		BaseHttpClient.NO_RAS=true;
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
                         if (json.getInt("status") == 1) {


                             MyLog.e("获取上传资料信息", "" + json.toString());
                             showCustomToast(json.getString("message"));
                             doHttpEditData();

                         } else {
							 String message = json.getString("message");
							 SystenmApi.showCommonErrorDialog(UploadUserInfoActivity.this, json.getInt("status"), message);
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
				super.onFailure(statusCode, headers, responseString, throwable);
				dismissLoadingDialog();
				showCustomToast(responseString);

			}

		});

	}
	/**
	 * 获取数据
	 * */
	public void doHttpEditData() {
		/***
		 * 清除原有数据
		 */
		if (data.size() > 0) {
			data.clear();
		}
        curPage = 1;

		JsonBuilder builder = new JsonBuilder();
		builder.put("page",curPage);
		builder.put("limit",pageCount);
		builder.put("is_data",1);
		

		BaseHttpClient.NO_RAS=true;
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
                         if (json.getInt("status") == 1) {


                             MyLog.e("获取上传资料信息", "" + json.toString());

                             initData(json);
                         } else {
							 String message = json.getString("message");
							 SystenmApi.showCommonErrorDialog(UploadUserInfoActivity.this, json.getInt("status"), message);
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
				scrollView.onRefreshComplete();
			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
					String responseString, Throwable throwable) {
				// TODO Auto-generated method stub
				super.onFailure(statusCode, headers, responseString, throwable);
				dismissLoadingDialog();
				showCustomToast(responseString);

			}

		});

	}
	
	public void initData(JSONObject json) {

     try{
    	 maxPage = json.getInt("totalPage");
		 curPage = json.getInt("nowPage");
			if (!json.isNull("list")) {
				list = json.getJSONArray("list");

				if (list != null)
					for (int i = 0; i < list.length(); i++) {
						JSONObject templist = list.getJSONObject(i);
						UploadInfoItem2 item = new UploadInfoItem2();
						item.init(templist);
						data.add(item);
					}

			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		  adapter.notifyDataSetChanged();

		  scrollView.smootScrollToTop();
	}
	
	class UploadFileAdapter extends BaseAdapter {


        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public UploadInfoItem2 getItem(int position) {
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
                convertView = LinearLayout.inflate(UploadUserInfoActivity.this, R.layout.upload_info_itemlayout, null);


                viewHolder.file_name = (TextView) convertView.findViewById(R.id.show_file_name);
                viewHolder.file_dec = (TextView) convertView.findViewById(R.id.upload_file_dec);
                viewHolder.file_status = (TextView) convertView.findViewById(R.id.upload_file_status);
                viewHolder.btn_delete = (Button) convertView.findViewById(R.id.btn_delete);
                


                convertView.setTag(viewHolder);
            } else {

                viewHolder = (ViewHolder) convertView.getTag();

            }

            viewHolder.file_name.setText(adapter.getItem(position).getFileName());
            viewHolder.file_dec.setText(adapter.getItem(position).getFilees());
            viewHolder.file_status.setText(adapter.getItem(position).getFileStatus());
           
            viewHolder.btn_delete.setTag(position);
            viewHolder.btn_delete.setOnClickListener(UploadUserInfoActivity.this);
            
            
            if(adapter.getItem(position).getFilees().equals("1")){
            	viewHolder.btn_delete.setVisibility(View.VISIBLE);
            	viewHolder.file_dec.setVisibility(View.GONE);
            	
            }else{
            	viewHolder.btn_delete.setVisibility(View.GONE);
            	viewHolder.file_dec.setVisibility(View.VISIBLE);
            }


            return convertView;
        }
    }


    public static final class ViewHolder {

        private TextView file_name;
        private TextView file_dec;
        private TextView file_status;
        private Button btn_delete;


    }	
    
    private void openPictureSelectDialog() {
		// 自定义Context,添加主题
		Context dialogContext = new ContextThemeWrapper(UploadUserInfoActivity.this,
				android.R.style.Theme_Light);
		String[] choiceItems = new String[2];
		choiceItems[0] = "相机拍摄"; // 拍照
		choiceItems[1] = "本地相册"; // 从相册中选择
		ListAdapter adapter = new ArrayAdapter<String>(dialogContext,
				android.R.layout.simple_list_item_1, choiceItems);
		// 对话框建立在刚才定义好的上下文上
		AlertDialog.Builder builder = new AlertDialog.Builder(dialogContext);
		builder.setTitle("请选择照片");
		builder.setSingleChoiceItems(adapter, -1,
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Default.isgestures = true;
				switch (which) {
				case 0: // 相机
					doTakePhoto();
					break;
				case 1: // 从图库相册中选取
					doPickPhotoFromGallery();
					break;
				}
				dialog.dismiss();
			}
		});
		builder.create().show();
	}

	/** 拍照获取相片 **/
	private void doTakePhoto() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); // 调用系统相机

		// /String imageNamePath = "image"+(int)(Math.random()*100)+
		String imge_path_Str = "";

		imge_path_Str = "image.png";

		Uri imageUri = Uri.fromFile(new File(Environment
				.getExternalStorageDirectory(), imge_path_Str));
		// 指定照片保存路径（SD卡），image.png为一个临时文件，每次拍照后这个图片都会被替换
		intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

		// 直接使用，没有缩小
		startActivityForResult(intent, PHOTO_WITH_CAMERA); // 用户点击了从相机获取
	}

	/** 从相册获取图片 **/
	private void doPickPhotoFromGallery() {
		Intent intent = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(intent, PHOTO_WITH_DATA); // 取得相片后返回到本画面
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
    MyLog.e("5555555555555555555", resultCode+"^^");
		if (resultCode == -1) { // 返回成功
			switch (requestCode) {
			case PHOTO_WITH_CAMERA: {// 拍照获取图片
				String status = Environment.getExternalStorageState();
				if (status.equals(Environment.MEDIA_MOUNTED)) { // 是否有SD卡

					String image_path_Str = "";
					image_path_Str = "/image.png";
					id_card_first_path = Environment
							.getExternalStorageDirectory()+"/temp/" + image_path_Str;


					BitmapFactory.Options bitmapFactoryOptions = new BitmapFactory.Options();
					bitmapFactoryOptions.inJustDecodeBounds = true;
					bitmapFactoryOptions.inSampleSize = 2;
					bitmapFactoryOptions.inJustDecodeBounds = false;
					Bitmap bitmap = BitmapFactory.decodeFile(Environment
							.getExternalStorageDirectory() + image_path_Str, bitmapFactoryOptions);


					// 压缩图片存储到SDCard下面

					if (bitmap != null) {

						// 为防止原始图片过大导致内存溢出，这里先缩小原图显示，然后释放原始Bitmap占用的内存
						Bitmap smallBitmap = ImageTools.comp(bitmap);
						bitmap.recycle();

						// imgName = createPhotoFileName();
						// 写一个方法将此文件保存到本应用下面啦


//						mImageView.setImageBitmap(smallBitmap);
						savePicture("image.png", smallBitmap);
						try {
							upload_id_card_1 = BitmapToStrUtil
									.encodeBase64File(id_card_first_path);
						} catch (Exception e1) {
							e1.printStackTrace();
						}


//						doHttpSetImg();


					}
//					Toast.makeText(UploadUserInfoActivity.this, "已保存本应用的files文件夹下",
//							Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(UploadUserInfoActivity.this, "没有SD卡",
							Toast.LENGTH_LONG).show();
				}
				break;
			}
			case PHOTO_WITH_DATA: {// 从图库中选择图片
				ContentResolver resolver = UploadUserInfoActivity.this.getContentResolver();
				// 照片的原始资源地址
				Uri originalUri = data.getData();
				try {
					// 使用ContentProvider通过URI获取原始图片

					String image_path_Str = "";
					image_path_Str = "/photos.png";
					id_card_first_path = Environment
							.getExternalStorageDirectory()+"/temp/" + image_path_Str;


					Bitmap photo = MediaStore.Images.Media.getBitmap(resolver,
							originalUri);

					if (photo != null) {

						// 为防止原始图片过大导致内存溢出，这里先缩小原图显示，然后释放原始Bitmap占用的内存
						Bitmap smallBitmap = ImageTools.comp(photo);
						photo.recycle();

						// 设置身份证正面照片
//						mImageView.setImageBitmap(smallBitmap);
						savePicture("photos.png", smallBitmap);
						try {
							upload_id_card_1 = BitmapToStrUtil
									.encodeBase64File(id_card_first_path);
						} catch (Exception e1) {
							e1.printStackTrace();
						}

//						doHttpSetImg();

					}

					// iv_temp.setImageURI(originalUri); //在界面上显示图片
					Toast.makeText(UploadUserInfoActivity.this, "已保存本应用的files文件夹下",
							Toast.LENGTH_LONG).show();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			}
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/** 保存图片在用户手机之中 **/
	private void savePicture(String fileName, Bitmap bitmap) {

		FileOutputStream b = null;
		String filePathStr = Environment.getExternalStorageDirectory()
				+ "/temp/";
		File file = new File(filePathStr);
		if (!file.exists()) {

			file.mkdirs();

		}

		try {
			b = new FileOutputStream(filePathStr + fileName);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				b.flush();
				b.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	} 
	
	

}
