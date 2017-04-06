package com.lmq.main.item;

import org.json.JSONObject;

public class TB_YHQ_Item {

   
    /**
     * 优惠券id
     */
    private int id;

	/**
     *优惠券金额
     */
    private String money;
    /**
     * 优惠券满多少可用
     */
    private String invest_money;
    
	/**
     * 优惠券到期时间
     */
    private String expired_time;
	/**
	 * 优惠券来源
	 */
	private String exp_type;
	/**
	 * 优惠券类型
	 */
	private String coupon_type;
	/**
	 * 优惠券描述
	 */
	private String desc;

	private String type;


    public void init(JSONObject json) {

        if (json.has("id")) {
        	
        	id = json.optInt("id", 0);
        }
              
        if (json.has("money")) {

            money = json.optString("money", "");
        }
        if (json.has("invest_money")) {

        	invest_money = json.optString("invest_money", "");
        }
        if (json.has("expired_time")) {
        	
        	expired_time = json.optString("expired_time", "");
        }

		if (json.has("exp_type")) {

			exp_type = json.optString("exp_type", "");
		}

		if (json.has("coupon_type")) {

			coupon_type = json.optString("coupon_type", "");
		}

		if (json.has("desc")) {

			desc = json.optString("desc", "");
		}

		if (json.has("type")) {

			type = json.optString("type", "");
		}




	}

    public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getMoney() {
		return money;
	}
	public void setMoney(String money) {
		this.money = money;
	}
	public String getInvest_money() {
		return invest_money;
	}

	public String getExp_type() {
		return exp_type;
	}

	public void setExp_type(String exp_type) {
		this.exp_type = exp_type;
	}

	public String getCoupon_type() {
		return coupon_type;
	}

	public void setCoupon_type(String coupon_type) {
		this.coupon_type = coupon_type;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public void setInvest_money(String invest_money) {
		this.invest_money = invest_money;
	}
	public String getExpired_time() {
		return expired_time;
	}
	public void setExpired_time(String expired_time) {
		this.expired_time = expired_time;
	}


	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
