package com.lmq.main.item;

import org.json.JSONObject;

/**
 * Created by zhaoshuai on 15/8/4.
 */
public class LHBItem {


    /**
     * 项目编号
     */
    private String batch_no;
    /**
     * 年化收益
     */
    private String interest_rate;
    /**
     * 还款日期；投资：年化收益
     */
    private String deadline;
    /**
     * 累计收益
     */
    private String interest;
    /**
     * 当前本息
     */
    private String money;
    /**
     * 投资本金
     */
    private String capital;
    private String out_money;

	public LHBItem(JSONObject json) {


        if (json.has("batch_no")) {

            batch_no = json.optString("batch_no", "");
        }
        if (json.has("interest_rate")) {

            deadline = json.optString("deadline", "");
        }
        if (json.has("interest")) {

            interest = json.optString("interest", "");
        }
        if (json.has("money")) {

            money = json.optString("money", "");
        }
        if (json.has("capital")) {

            capital = json.optString("capital", "");
        }
        if (json.has("interest_rate")) {

            interest_rate = json.optString("interest_rate", "");
        }
        if (json.has("out_money")) {

            out_money = json.optString("out_money", "");
        }


    }
	

    public String getBatch_no() {
        return batch_no;
    }

    public void setBatch_no(String batch_no) {
        this.batch_no = batch_no;
    }

    public String getInterest_rate() {
        return interest_rate;
    }

    public void setInterest_rate(String interest_rate) {
        this.interest_rate = interest_rate;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getInterest() {
        return interest;
    }

    public void setInterest(String interest) {
        this.interest = interest;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getCapital() {
        return capital;
    }

    public void setCapital(String capital) {
        this.capital = capital;
    }
    public String getOut_money() {
        return out_money;
    }

    public void setOut_money(String out_money) {
        this.out_money = out_money;
    }

    @Override
    public String toString() {
        return "LHBItem{" +
                "batch_no='" + batch_no + '\'' +
                ", interest_rate='" + interest_rate + '\'' +
                ", deadline='" + deadline + '\'' +
                ", interest='" + interest + '\'' +
                ", money='" + money + '\'' +
                ", capital='" + capital + '\'' +
                '}';
    }
}
