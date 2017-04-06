package com.lmq.main.item;

import org.json.JSONException;
import org.json.JSONObject;

import com.lmq.main.api.MyLog;


public class dealItem {
	// 时间
	private String add_time;
	// 类型
	private String type;
	// 金钱
	private String affect_money;
	/** 冻结资金 */
	private String nouse_money;
	/** 待收本金 */
	private String back_money;
	/** 可用资金 */
	private String blance_money;

	// 描述
	private String info;

	public dealItem(JSONObject json) {
		MyLog.e("json", json.toString());
		try {
			if (json.has("add_time")) {
				setAdd_time(json.getString("add_time"));
			}
			if (json.has("info")) {
				setInfo(json.getString("info"));
			}
			if (json.has("affect_money")) {
				setAffect_money(json.getString("affect_money")+"元");
			}
			if (json.has("type")) {
				this.setType(json.getString("type"));
			}

			if (json.has("account_money")) {
				setBlance_money(json.getString("account_money"+""));
			}
			if (json.has("freeze_money")) {
				setNouse_money(json.getString("freeze_money")+"元");
			}
			if (json.has("collect_money")) {
				setBack_money(json.getString("collect_money")+"元");
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getAdd_time() {
		return add_time;
	}

	public void setAdd_time(String time) {
		this.add_time = time;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getNouse_money() {
		return nouse_money;
	}

	public void setNouse_money(String nouse_money) {
		this.nouse_money = nouse_money;
	}

	public String getBack_money() {
		return back_money;
	}

	public void setBack_money(String back_money) {
		this.back_money = back_money;
	}



	public String getAffect_money() {
		return affect_money;
	}

	public void setAffect_money(String money) {
		this.affect_money = money;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}
	public String getBlance_money() {
		return blance_money;
	}

	public void setBlance_money(String blance_money) {
		this.blance_money = blance_money;
	}

}
