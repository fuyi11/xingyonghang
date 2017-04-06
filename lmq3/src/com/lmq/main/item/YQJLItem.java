package com.lmq.main.item;

import org.json.JSONObject;

public class YQJLItem {

   
    /**
     * 邀请用户
     */
    private String user_name;
  
	/**
     * 用户注册时间
     */
    private String reg_time;
    /**
     * 用户状态
     */
    private String yizhuce;
    /**
     * 邀请奖励
     */
    private String money;
       
	/**
     * 是否生效
     */
    private String be;


    public void init(JSONObject json) {

              
        if (json.has("user_name")) {

        	user_name = json.optString("user_name", "");
        }
        if (json.has("reg_time")) {

        	reg_time = json.optString("reg_time", "");
        }
        if (json.has("yizhuce")) {

        	yizhuce = json.optString("yizhuce", "");
        }
        if (json.has("money")) {
        	
        	money = json.optString("money", "");
        }
        if (json.has("be")) {
        	
        	be = json.optString("be", "");
        }
  


    }

    public String getUser_name() {
		return user_name;
	}


	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}


	public String getReg_time() {
		return reg_time;
	}


	public void setReg_time(String reg_time) {
		this.reg_time = reg_time;
	}


	public String getYizhuce() {
		return yizhuce;
	}


	public void setYizhuce(String yizhuce) {
		this.yizhuce = yizhuce;
	}


	public String getMoney() {
		return money;
	}


	public void setMoney(String money) {
		this.money = money;
	}


	public String getBe() {
		return be;
	}


	public void setBe(String be) {
		this.be = be;
	}

    @Override
    public String toString() {
        return "YQJLItem{" +
                "user_name='" + user_name + '\'' +
                ", remark='" + reg_time + '\'' +
                ", yizhuce='" + yizhuce + '\'' +
                ", money='" + money + '\'' +
                ", be='" + be + '\'' +

                '}';
    }
}
