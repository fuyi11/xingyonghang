package com.lmq.main.item;

import org.json.JSONException;
import org.json.JSONObject;

import com.lmq.main.util.Default;

public class BannerItem {

	private int id;
	private String picPath;
	private String title;

	public BannerItem(JSONObject json) throws JSONException {

		if (json.has("id")) {
			setId(json.getInt("id"));
		}

		if (json.has("title")) {
			setTitle(json.getString("title"));

		}

		if (json.has("pic")) {

			setPicPath(Default.ip + json.getString("pic"));
		}

	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPicPath() {
		return picPath;
	}

	public void setPicPath(String picPath) {
		this.picPath = picPath;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
