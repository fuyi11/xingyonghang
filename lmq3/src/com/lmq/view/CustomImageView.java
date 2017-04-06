package com.lmq.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.FloatMath;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.xyh.R;
import com.lmq.main.api.MyLog;
import com.lmq.main.util.Default;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class CustomImageView extends LinearLayout implements OnTouchListener {

	// 定制View
	private Context context;
	private DisplayImageOptions options;

	private ImageView showImageView;
	private ProgressBar spinner;
	private static final String TAG = "PhotoViewer";
	public static final int RESULT_CODE_NOFOUND = 200;

	Matrix matrix = new Matrix();
	Matrix savedMatrix = new Matrix();

	Bitmap bitmap;

	/** 最小缩放比例 */
	float minScaleR = 1.0f;
	/** 最大缩放比例 */
	static final float MAX_SCALE = 10f;

	/** 初始状态 */
	static final int NONE = 0;
	/** 拖动 */
	static final int DRAG = 1;
	/** 缩放 */
	static final int ZOOM = 2;

	/** 当前模式 */
	int mode = NONE;
	private boolean isClose = false;

	PointF prev = new PointF();
	PointF mid = new PointF();
	float dist = 1f;
	private String URL;

	public CustomImageView(Context context, String URL) {
		super(context);
		// TODO Auto-generated constructor stub
		// 获取定制的View
		this.context = context;
		this.URL = URL;
		addSubView();

	}

	protected void addSubView() {

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.costom_image, this);
		showImageView = (ImageView) findViewById(R.id.image);
		spinner = (ProgressBar) findViewById(R.id.loading);
		options = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.default_user_image)
				.showImageOnFail(R.drawable.default_user_image)
				.resetViewBeforeLoading(true).cacheOnDisk(true)
				.imageScaleType(ImageScaleType.EXACTLY)
				.bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true)
				.displayer(new FadeInBitmapDisplayer(300)).build();

		ImageLoader.getInstance().displayImage(URL, showImageView, options,
				new SimpleImageLoadingListener() {
					@Override
					public void onLoadingStarted(String imageUri, View view) {
						spinner.setVisibility(View.VISIBLE);
					}

					@Override
					public void onLoadingFailed(String imageUri, View view,
							FailReason failReason) {
						String message = null;
						switch (failReason.getType()) {
						case IO_ERROR:
							message = "Input/Output error";
							break;
						case DECODING_ERROR:
							message = "解析错误";
							break;
						case NETWORK_DENIED:
							message = "网络错误";
							break;
						case OUT_OF_MEMORY:
							message = "内存错误";
							break;
						case UNKNOWN:
							message = "位置错误";
							break;
						}
						Toast.makeText(context, message, Toast.LENGTH_SHORT)
								.show();

						spinner.setVisibility(View.GONE);
					}

					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {

						bitmap = loadedImage;
						 minZoom();
						center();
						showImageView.setImageMatrix(matrix);
						//showImageView.setScaleType(ImageView.ScaleType.CENTER);

						spinner.setVisibility(View.GONE);
					}
				});

		showImageView.setOnTouchListener(this);// 设置触屏监听

	}

	/**
	 * 触屏监听
	 */
	public boolean onTouch(View v, MotionEvent event) {

		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		// 主点按下
		case MotionEvent.ACTION_DOWN:
			MyLog.e("MotionEvent.ACTION_DOWN");
			savedMatrix.set(matrix);
			prev.set(event.getX(), event.getY());
			mode = DRAG;
			break;
		// 副点按下
		case MotionEvent.ACTION_POINTER_DOWN:
			isClose = false;
			MyLog.e("MotionEvent.ACTION_POINTER_DOWN");
			dist = spacing(event);
			// 如果连续两点距离大于10，则判定为多点模式
			if (spacing(event) > 10f) {
				savedMatrix.set(matrix);
				midPoint(mid, event);
				mode = ZOOM;
			}
			break;
		case MotionEvent.ACTION_UP:
			MyLog.e("MotionEvent.ACTION_UP");
			isClose = true;
			break;
		case MotionEvent.ACTION_POINTER_UP:
			MyLog.e("MotionEvent.ACTION_POINTER_UP");
			mode = NONE;
			isClose = false;
			// savedMatrix.set(matrix);

			break;
		case MotionEvent.ACTION_MOVE:
			MyLog.e("MotionEvent.ACTION_MOVE");
			isClose = false;
			if (mode == DRAG) {
				matrix.set(savedMatrix);
				matrix.postTranslate(event.getX() - prev.x, event.getY()
						- prev.y);
			} else if (mode == ZOOM) {
				float newDist = spacing(event);
				if (newDist > 10f) {
					matrix.set(savedMatrix);
					float tScale = newDist / dist;
					matrix.postScale(tScale, tScale, mid.x, mid.y);
				}
			}
			break;
		}
		MyLog.e("Isclose--->" + isClose);
		showImageView.setImageMatrix(matrix);
		CheckView();
		if (isClose) {
			return false;
		} else {
			return true;
		}

	}

	/**
	 * 限制最大最小缩放比例，自动居中
	 */
	
	private void CheckView() {
		float p[] = new float[9];
		matrix.getValues(p);
		if (mode == ZOOM) {
			if (p[0] < minScaleR) {
				// Log.d("", "当前缩放级别:"+p[0]+",最小缩放级别:"+minScaleR);
				matrix.setScale(minScaleR, minScaleR);
			}
			if (p[0] > MAX_SCALE) {
				// Log.d("", "当前缩放级别:"+p[0]+",最大缩放级别:"+MAX_SCALE);
				matrix.set(savedMatrix);
			}
		}
		center();
	}

	/**
	 * 最小缩放比例，最大为100%
	 */
	private void minZoom() {
		minScaleR = Math.min(
				(float) Default.dm.widthPixels / (float) bitmap.getWidth(),
				(float) Default.dm.heightPixels / (float) bitmap.getHeight());
		if (minScaleR < 1.0) {
			matrix.postScale(minScaleR, minScaleR);
		}
	}

	private void center() {
		center(true, true);
	}

	/**
	 * 横向、纵向居中
	 */
	protected void center(boolean horizontal, boolean vertical) {

		Matrix m = new Matrix();
		m.set(matrix);
		RectF rect = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
		m.mapRect(rect);

		float height = rect.height();
		float width = rect.width();

		float deltaX = 0, deltaY = 0;

		if (vertical) {
			// 图片小于屏幕大小，则居中显示。大于屏幕，上方留空则往上移，下方留空则往下移
			int screenHeight = Default.dm.heightPixels;
			if (height < screenHeight) {
				deltaY = (screenHeight - height) / 2 - rect.top;
			} else if (rect.top > 0) {
				deltaY = -rect.top;
			} else if (rect.bottom < screenHeight) {
				deltaY = showImageView.getHeight() - rect.bottom;
			}
		}

		if (horizontal) {
			int screenWidth = Default.dm.widthPixels;
			if (width < screenWidth) {
				deltaX = (screenWidth - width) / 2 - rect.left;
			} else if (rect.left > 0) {
				deltaX = -rect.left;
			} else if (rect.right < screenWidth) {
				deltaX = screenWidth - rect.right;
			}
		}
		matrix.postTranslate(deltaX, deltaY);
	}

	/**
	 * 两点的距离
	 */
	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	/**
	 * 两点的中点
	 */
	private void midPoint(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}

}
