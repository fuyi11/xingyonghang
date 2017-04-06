package com.lmq.main.item;

import org.json.JSONObject;

public class LHB_Tz_Item {

	 /**
     * 是否设置支付密码=1  已设置,=0  没设置
     */
    private int has_pin;
 

	/**
     * 投资：项目编号id
     */
    private String bao_id;
   
	/**
     * 投资：年化收益
     */
    private String interest_rate;
 
	/**
     * 剩余时间
     */
    private String lefttime;
    /**
     * 可投金额
     */
    private String remain_money;
    /**
     * 计划金额
     */
    private String funds;
    /**
     * 封存期限
     */
    private String term;
    /**
     * 起投金额
     */
    private String start_funds;
    /**
     * 账户余额
     */
    private String user_money;

	/**
     *bao_status现在1234分别是：还没开始，可投，可投，已结束。
	 在1和4两个情况下，倒计时lefttime视为无效即可。
     */
    private int bao_status;

    public void init(JSONObject json) {
		try {
			 if (json.has("bao_status")) {

				 bao_status = json.optInt("bao_status",0);
		    }
			 if (json.has("has_pin")) {

				 has_pin = json.optInt("has_pin", 0);
		    }
			 if (json.has("bao_id")) {
				 
				 bao_id = json.optString("bao_id", "");
			 }
			 if (json.has("interest_rate")) {

				 if (json.optString("interest_rate")==null || json.optString("interest_rate").equals("")){
					 interest_rate ="0%";
				 }else if(json.optString("interest_rate").equals("%")){
					 interest_rate ="0%";
				 }else{

					 interest_rate = json.optString("interest_rate", "");
				 }
				 

			 }
	        
	        if (json.has("lefttime")) {

				if (json.optString("lefttime")==null || json.optString("lefttime").equals("")){
					lefttime ="0";

				}else{

					lefttime = json.optString("lefttime", "");
				}

	        }
	        if (json.has("remain_money")) {

				if (json.optString("remain_money")==null || json.optString("remain_money").equals("")){
					remain_money ="0元";
				}else if(json.optString("remain_money").equals("元")){
					remain_money ="0元";
				}else{

					remain_money = json.optString("remain_money", "");

				}
	        	
	        }
	        if (json.has("funds")) {

				if (json.optString("funds")==null || json.optString("funds").equals("")){
					funds ="0元";
				}else if(json.optString("funds").equals("元")){
					funds ="0元";
				}else{

					funds = json.optString("funds", "");
				}
	        	

	        }
	        if (json.has("term")) {

				if (json.optString("term")==null || json.optString("term").equals("")){
					term ="0天";
				}else if(json.optString("term").equals("天")){
					term ="0天";
				}else{

					term = json.optString("term", "");
				}
	        	

	        }
	        if (json.has("start_funds")) {
				if (json.optString("start_funds")==null || json.optString("start_funds").equals("")){
					start_funds ="0";

				}else{

					start_funds = json.optString("start_funds", "");
				}

	        }
	        if (json.has("user_money")) {
	        	
	        	user_money = json.optString("user_money", "");
	        }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
  
    public String getLefttime() {
		return lefttime;
	}

	public void setLefttime(String lefttime) {
		this.lefttime = lefttime;
	}

	public String getRemain_money() {
		return remain_money;
	}

	public void setRemain_money(String remain_money) {
		this.remain_money = remain_money;
	}

	public String getFunds() {
		return funds;
	}

	public void setFunds(String funds) {
		this.funds = funds;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public String getStart_funds() {
		return start_funds;
	}

	public void setStart_funds(String start_funds) {
		this.start_funds = start_funds;
	}

	public String getUser_money() {
		return user_money;
	}

	public void setUser_money(String user_money) {
		this.user_money = user_money;
	}
   public String getInterest_rate() {
			return interest_rate;
   }

   public void setInterest_rate(String interest_rate) {
			this.interest_rate = interest_rate;
  }
   public String getBao_id() {
		return bao_id;
	}


	public void setBao_id(String bao_id) {
		this.bao_id = bao_id;
	}
	   public int getHas_pin() {
			return has_pin;
		}


		public void setHas_pin(int has_pin) {
			this.has_pin = has_pin;
		}
	public int getBao_status() {
		return bao_status;
	}

	public void setBao_status(int bao_status) {
		this.bao_status = bao_status;
	}

   
}
