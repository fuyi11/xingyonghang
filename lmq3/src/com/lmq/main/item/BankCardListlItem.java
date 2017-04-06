package com.lmq.main.item;

import org.json.JSONObject;

public class BankCardListlItem {


	/**
     * 银行卡id
     */
    private int id;

	/**
     * 银行id
     */
    private int bank_id;

	/**
     * 银行卡卡名
     */
    private String bank_name;
    /**
     * 银行卡卡号
     */
    private String bank_num;
	private String bank_code;

	public int getBank_codes() {
		return bank_codes;
	}

	public void setBank_codes(int bank_codes) {
		this.bank_codes = bank_codes;
	}

	private int bank_codes;


    public void init(JSONObject json) {

              
        if (json.has("id")) {

        	id = json.optInt("id", 0);
        }
        if (json.has("bank_id")) {

        	bank_id = json.optInt("bank_id", 1);
        }
        if (json.has("bank_name")) {

        	bank_name = json.optString("bank_name", "");
        }
        if (json.has("bank_num")) {
        	
        	bank_num = json.optString("bank_num", "");
        }
        if (json.has("bank_code")) {

			bank_code = json.optString("bank_code", "");
        }
//		if (json.has("bank_code")) {
//
//			bank_codes = json.optInt("bank_code", 0);
//		}

    }

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	public String getBank_name() {
		return bank_name;
	}


	public void setBank_name(String bank_name) {
		this.bank_name = bank_name;
	}


	public String getBank_num() {
		return bank_num;
	}


	public void setBank_num(String bank_num) {
		this.bank_num = bank_num;
	}
	public int getBank_id() {
			return bank_id;
	}

	public void setBank_id(int bank_id) {
			this.bank_id = bank_id;
	}

	public String getBank_code() {
		return bank_code;
	}

	public void setBank_code(String bank_code) {
		this.bank_code = bank_code;
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
