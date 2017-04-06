package com.lmq.main.item;

import org.json.JSONException;
import org.json.JSONObject;

public class LendMoneyItem {

	private int statue;
	// 借款号
	private int id;

	// 借款标题
	private String title;

	// 借款金额
	private double amount;

	// 还款金额
	private double repay_amount;

	// 年化率
	private double interest_rate;

	// 还款方式
	private String repay_kind;

	// 还款时间
	private String repay_due_datae;

	// 还款单位
	private String repay_due_uint;

	// 标类型
	private int kind;

	public LendMoneyItem(JSONObject json) {
		try {
			if (json.has("id")) {
				setId(json.getInt("id"));
			}
			if (json.has("title")) {

				setTitle(json.getString("title"));
			}

			if (json.has("amount")) {

				setAmount(json.getDouble("amount"));
			}

			if (json.has("repay_amount")) {

				setRepay_amount(json.getDouble("repay_amount"));
			}

			if (json.has("interest_rate")) {

				setInterest_rate(json.getDouble("interest_rate"));
			}

			if (json.has("repay_kind")) {

				setRepay_kind(json.getString("repay_kind"));
			}

			if (json.has("repay_due_date")) {

				setRepay_due_datae(json.getString("repay_due_date"));
			}

			if (json.has("repay_due_unit")) {

				setRepay_due_uint(json.getString("repay_due_unit"));

			}

			if (json.has("kind")) {

				setKind(json.getInt("kind"));
			}

			if (json.has("status")) {

				setStatue(json.getInt("status"));
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public double getRepay_amount() {
		return repay_amount;
	}

	public void setRepay_amount(double repay_amount) {
		this.repay_amount = repay_amount;
	}

	public double getInterest_rate() {
		return interest_rate;
	}

	public void setInterest_rate(double interest_rate) {
		this.interest_rate = interest_rate;
	}

	public String getRepay_kind() {
		return repay_kind;
	}

	public void setRepay_kind(String repay_kind) {
		this.repay_kind = repay_kind;
	}

	public String getRepay_due_datae() {
		return repay_due_datae;
	}

	public void setRepay_due_datae(String repay_due_datae) {
		this.repay_due_datae = repay_due_datae;
	}

	public String getRepay_due_uint() {
		return repay_due_uint;
	}

	public void setRepay_due_uint(String repay_due_uint) {
		this.repay_due_uint = repay_due_uint;
	}

	public int getKind() {
		return kind;
	}

	public void setKind(int kind) {
		this.kind = kind;
	}

	public int getStatue() {
		return statue;
	}

	public void setStatue(int statue) {
		this.statue = statue;
	}

	@Override
	public String toString() {
		return "LendMoneyItem [statue=" + statue + ", id=" + id + ", title="
				+ title + ", amount=" + amount + ", repay_amount="
				+ repay_amount + ", interest_rate=" + interest_rate
				+ ", repay_kind=" + repay_kind + ", repay_due_datae="
				+ repay_due_datae + ", repay_due_uint=" + repay_due_uint
				+ ", kind=" + kind + "]";
	}
	
	

}
