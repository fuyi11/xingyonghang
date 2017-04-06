package com.lmq.main.item;

import org.json.JSONObject;

public class IntegralItem {

    /**
     * 当code=1时才返回，时间
     */
    private String add_time;

	/**
     * 当code=1时才返回，类型
     */
    private String affect_integral;
    /**
     * 当code=1时才返回，详情（当code=2时才返回，简介）
     */
    private String info;
    /**
     * 当code=1时才返回，积分
     */
    private String integral_log;
    /**
     * 当code=2时才返回，抵现券id
     */
    private String goodid;
    /**
     * 当code=2时才返回，抵现券金额
     */
    private String money;
    /**
     * 当code=2时才返回，需要积分
     */
    private String integral;
    

    public void init(JSONObject json) {

              
        if (json.has("add_time")) {

        	add_time = json.optString("add_time", "");
        }
        if (json.has("affect_integral")) {

        	affect_integral = json.optString("affect_integral", "");
        }
        if (json.has("info")) {

        	info = json.optString("info", "");
        }
        if (json.has("integral_log")) {
        	
        	integral_log = json.optString("integral_log", "");
        }
        if (json.has("goodid")) {
        	
        	goodid = json.optString("goodid", "");
        }
        if (json.has("money")) {
        	
        	money = json.optString("money", "");
        }
        if (json.has("integral")) {
        	
        	integral = json.optString("integral", "");
        }


    }

    public String getAdd_time() {
		return add_time;
	}


	public void setAdd_time(String add_time) {
		this.add_time = add_time;
	}


	public String getAffect_integral() {
		return affect_integral;
	}


	public void setAffect_integral(String affect_integral) {
		this.affect_integral = affect_integral;
	}


	public String getInfo() {
		return info;
	}


	public void setInfo(String info) {
		this.info = info;
	}


	public String getIntegral_log() {
		return integral_log;
	}


	public void setIntegral_log(String integral_log) {
		this.integral_log = integral_log;
	}


	public String getGoodid() {
		return goodid;
	}


	public void setGoodid(String goodid) {
		this.goodid = goodid;
	}


	public String getMoney() {
		return money;
	}


	public void setMoney(String money) {
		this.money = money;
	}


	public String getIntegral() {
		return integral;
	}


	public void setIntegral(String integral) {
		this.integral = integral;
	}

//    @Override
//    public String toString() {
//        return "IntegralItem{" +
//                "add_time='" + add_time + '\'' +
//                ", remark='" + remark + '\'' +
//                ", money='" + money + '\'' +
//                ", invest_money='" + invest_money + '\'' +
//                ", funds='" + funds + '\'' +
//                ", exp_type='" + exp_type + '\'' +
//                ", status='" + status + '\'' +
//                ", type='" + type + '\'' +
//                '}';
//    }
}
