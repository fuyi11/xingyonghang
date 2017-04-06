package com.lmq.main.item;

import org.json.JSONObject;

/**
 * Created by zhaoshuai on 15/8/7.
 */
public class InvestManageItem {

    private String borrow_name; //标名称
    private String borrow_id;   //投标ID
    private String danbao; //投资 担保名称
    private String borrow_interest_rate; //投标利率
    private String investor_capital;//投标金额
    private String invest_time;//投标添加时间
    private String deadline;//结束时间
    private String interest_rate; //债权利率
    private String addtime; //债权添加时间
    private String id; //债权ID
    private String money;//转让本金
    private String total_periods;//已认购债权（转让期数/总期数）
    private String buy_money;//已认购债权（购买价格）
    private String invest_id;//债权转让协议id
    private int status;
    private int is_debt;


    public InvestManageItem(JSONObject json) {

        if (json.has("is_debt")) {

            is_debt = json.optInt("is_debt", 0);
        }
        if (json.has("status")) {

            status = json.optInt("status", 0);
        }
        if (json.has("buy_money")) {

            buy_money = json.optString("buy_money", "");
        }
        if (json.has("invest_id")) {

            invest_id = json.optString("invest_id", "");
        }
        if (json.has("total_periods")) {

            total_periods = json.optString("total_periods", "");
        }
        if (json.has("money")) {

            money = json.optString("money", "");
        }
        if (json.has("borrow_name")) {

            borrow_name = json.optString("borrow_name", "");
        }

        if (json.has("borrow_id")) {

            borrow_id = json.optString("borrow_id", "");
        }


        if (json.has("danbao")) {

            danbao = json.optString("danbao", "");
        }


        if (json.has("borrow_interest_rate")) {

            borrow_interest_rate = json.optString("borrow_interest_rate", "");
        }


        if (json.has("investor_capital")) {

            investor_capital = json.optString("investor_capital", "");
        }


        if (json.has("invest_time")) {

            invest_time = json.optString("invest_time", "");
        }
        if (json.has("deadline")) {

            deadline = json.optString("deadline", "");
        }

        if (json.has("interest_rate")) {

            interest_rate = json.optString("interest_rate", "");
        }
        if (json.has("id")) {

            id = json.optString("id", "");
        }
        if (json.has("addtime")) {

            addtime = json.optString("addtime", "");
        }
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAddtime() {
        return addtime;
    }

    public void setAddtime(String addtime) {
        this.addtime = addtime;
    }

    public String getInterest_rate() {
        return interest_rate;
    }

    public void setInterest_rate(String interest_rate) {
        this.interest_rate = interest_rate;
    }

    public String getBorrow_name() {
        return borrow_name;
    }

    public void setBorrow_name(String borrow_name) {
        this.borrow_name = borrow_name;
    }

    public String getBorrow_id() {
        return borrow_id;
    }

    public void setBorrow_id(String borrow_id) {
        this.borrow_id = borrow_id;
    }

    public String getDanbao() {
        return danbao;
    }

    public void setDanbao(String danbao) {
        this.danbao = danbao;
    }

    public String getBorrow_interest_rate() {
        return borrow_interest_rate;
    }

    public void setBorrow_interest_rate(String borrow_interest_rate) {
        this.borrow_interest_rate = borrow_interest_rate;
    }

    public String getInvestor_capital() {
        return investor_capital;
    }

    public void setInvestor_capital(String investor_capital) {
        this.investor_capital = investor_capital;
    }

    public String getInvest_time() {
        return invest_time;
    }

    public void setInvest_time(String invest_time) {
        this.invest_time = invest_time;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }
    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getTotal_periods() {
        return total_periods;
    }
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
    public void setTotal_periods(String total_periods) {
        this.total_periods = total_periods;
    }
    public String getBuy_money() {
        return buy_money;
    }
    public void setBuy_money(String buy_money) {
        this.buy_money = buy_money;
    }
    public String getinvest_id() {
        return invest_id;
    }
    public void setinvest_id(String invest_id) {
        this.invest_id = invest_id;
    }
    public int getIs_debt() {
        return is_debt;
    }

    public void setIs_debt(int is_debt) {
        this.is_debt = is_debt;
    }
    @Override
    public String toString() {
        return "InvestManageItem{" +
                "borrow_name='" + borrow_name + '\'' +
                ", borrow_id='" + borrow_id + '\'' +
                ", danbao='" + danbao + '\'' +
                ", borrow_interest_rate='" + borrow_interest_rate + '\'' +
                ", investor_capital='" + investor_capital + '\'' +
                ", invest_time='" + invest_time + '\'' +
                ", deadline='" + deadline + '\'' +
                ", invest_id='" + invest_id + '\'' +
                '}';
    }
}
