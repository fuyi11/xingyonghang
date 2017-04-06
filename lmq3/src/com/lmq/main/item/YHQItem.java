package com.lmq.main.item;

import org.json.JSONObject;

public class YHQItem {

   
    /**
     * 体验券金额(奖励金额)
     */
    private String money;
    /**
     * 最低投标金额使用该抵用券
     */
    private String invest_money;
    /**
     * 过期时间
     */
    private String funds;
    /**
     * 来源(奖励记录：类型)
     */
    private String exp_type;
    /**
     * 优惠券状态 0未使用 1已使用 2已过期
     */
    private int status;
    /**
     * 文字描述
     */
    private String type;
    /**
     * 时间
     */
    private String add_time;
    
	/**
     * 获得详情
     */
    private String remark;
    /**
     * 优惠券类型
     */
    private String coupon_type;

	public void init(JSONObject json) {

        if (json.has("status")) {
        	
        	status = json.optInt("status", 0);
        }
              
        if (json.has("coupon_type")) {

        	coupon_type = json.optString("coupon_type", "");
        }
        
        if (json.has("money")) {
        	
        	money = json.optString("money", "");
        }
        if (json.has("invest_money")) {

        	invest_money = json.optString("invest_money", "");
        }
        if (json.has("funds")) {

        	funds = json.optString("funds", "");
        }
        if (json.has("exp_type")) {
        	
        	exp_type = json.optString("exp_type", "");
        }
        if (json.has("type")) {
        	
        	type = json.optString("type", "");
        }
        if (json.has("add_time")) {
        	
        	add_time = json.optString("add_time", "");
        }
        if (json.has("remark")) {
        	
        	remark = json.optString("remark", "");
        }


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
	public void setInvest_money(String invest_money) {
		this.invest_money = invest_money;
	}
	public String getFunds() {
		return funds;
	}
	public void setFunds(String funds) {
		this.funds = funds;
	}
	public String getExp_type() {
		return exp_type;
	}
	public void setExp_type(String exp_type) {
		this.exp_type = exp_type;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getAdd_time() {
		return add_time;
	}
	public void setAdd_time(String add_time) {
		this.add_time = add_time;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getCoupon_type() {
			return coupon_type;
    }

	public void setCoupon_type(String coupon_type) {
			this.coupon_type = coupon_type;
	}

    @Override
    public String toString() {
        return "YHQItem{" +
                "add_time='" + add_time + '\'' +
                ", remark='" + remark + '\'' +
                ", money='" + money + '\'' +
                ", invest_money='" + invest_money + '\'' +
                ", funds='" + funds + '\'' +
                ", exp_type='" + exp_type + '\'' +
                ", status='" + status + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
