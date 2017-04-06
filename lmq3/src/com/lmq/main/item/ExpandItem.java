package com.lmq.main.item;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class ExpandItem {

	/** 优惠券状态 */
	private int coupon_status;

	/** 优惠券面值 */
	private double money;

	/** 投资多少可用 */
	private double invest_money;

	/** 优惠券过期时间 */
	private String expired_time;

	/** 是否是体验金 */
	private int is_taste;

	/** 使用时间 */
	private String use_time;

	/*** 说明 */
	private String remark;
	
	/*** 优惠券类型 */
	private int type;

	/** 优惠券类型 */
	private ArrayList<String> exp_type;

	public ExpandItem(JSONObject json) {

		try {

			if (json.has("coupon_status")) {

				coupon_status = json.optInt("coupon_status", -11);

			}

			if (json.has("money")) {

				money = json.optDouble("money", -1);
			}

			if (json.has("invest_money")) {

				invest_money = json.optDouble("invest_money", -1);
			}

			if (json.has("expired_time")) {

				expired_time = json.optString("expired_time", "");
			}

			if (json.has("is_taste")) {

				is_taste = json.optInt("is_taste", -1);
			}

			if (json.has("use_time")) {

				use_time = json.optString("use_time", "");

			}
			
			if (json.has("type")) {

				type = json.optInt("type", -1);

			}

			if (json.has("remark")) {

				remark = json.optString("remark", "");

			}

			if (json.has("exp_type")) {
				
				exp_type = new ArrayList<String>();
				JSONArray array = json.optJSONArray("exp_type");
				
				for(int i=0;i<array.length();i++){
					exp_type.add(array.getString(i));
				}

			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public int getCoupon_status() {
		return coupon_status;
	}

	public void setCoupon_status(int coupon_status) {
		this.coupon_status = coupon_status;
	}

	public double getMoney() {
		return money;
	}

	public void setMoney(double money) {
		this.money = money;
	}

	public double getInvest_money() {
		return invest_money;
	}

	public void setInvest_money(double invest_money) {
		this.invest_money = invest_money;
	}

	public String getExpired_time() {
		return expired_time;
	}

	public void setExpired_time(String expired_time) {
		this.expired_time = expired_time;
	}

	public int getIs_taste() {
		return is_taste;
	}

	public void setIs_taste(int is_taste) {
		this.is_taste = is_taste;
	}

	public String getUse_time() {
		return use_time;
	}

	public void setUse_time(String use_time) {
		this.use_time = use_time;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public ArrayList<String> getExp_type() {
		return exp_type;
	}
	
	


	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "ExpandItem [coupon_status=" + coupon_status + ", money="
				+ money + ", invest_money=" + invest_money + ", expired_time="
				+ expired_time + ", is_taste=" + is_taste + ", use_time="
				+ use_time + ", remark=" + remark + ", exp_type=" + exp_type
				+ "]";
	}

}
