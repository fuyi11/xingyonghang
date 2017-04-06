package com.lmq.main.item;

import org.json.JSONObject;

/**
 * 借款记录
 */
public class Borrowing_RecordItem {

    private String repayment_type; //还款方式
    private String expired_time;   //逾期天数
    private Long borrow_money; //金额（借款）
    private String borrow_interest_rate; //利率
    private String progress;//进度
    private String deadline;//时间（即将还款）
    private String receive;//已还
    private String borrow_duration; //期限
    private String expired_money_now; //逾期罚金
    private String call_fee_now;//催收费
    private String borrow_status;//状态
    private String bid;
    private String add_time;
    private String borrow_name;
    private String capital;
    private String interest;
    private String substitute_money;
    private String has_borrow;
    private String borrow_times;
    private String sort_order;

    private int  need_pay;
    private int  option;

    public Borrowing_RecordItem(JSONObject json) {

        if (json.has("option")) {

            option = json.optInt("option", 0);
        }
        if (json.has("add_time")) {

            add_time = json.optString("add_time", "0");
        }
        if (json.has("need_pay")) {

            need_pay = json.optInt("need_pay", 0);
        }
        if (json.has("sort_order")) {

            sort_order = json.optString("sort_order", "");
        }
        if (json.has("borrow_times")) {

            borrow_times = json.optString("borrow_times", "");
        }
        if (json.has("has_borrow")) {

            has_borrow = json.optString("has_borrow", "");
        }
        if (json.has("substitute_money")) {

            substitute_money = json.optString("substitute_money", "");
        }
        if (json.has("interest")) {

            interest = json.optString("interest", "");
        }
        if (json.has("capital")) {

            capital = json.optString("capital", "");
        }
        if (json.has("bid")) {

            bid = json.optString("bid", "");
        }
        if (json.has("borrow_name")) {

            borrow_name = json.optString("borrow_name", "");
        }
        if (json.has("repayment_type")) {

            repayment_type = json.optString("repayment_type", "");
        }
        if (json.has("expired_time")) {

            expired_time = json.optString("expired_time", "");
        }
        if (json.has("borrow_money")) {

            borrow_money = json.optLong("borrow_money", 0);
        }
        if (json.has("borrow_interest_rate")) {

            borrow_interest_rate = json.optString("borrow_interest_rate", "");
        }

        if (json.has("progress")) {

            progress = json.optString("progress", "");
        }

        if (json.has("deadline")) {

            deadline = json.optString("deadline", "");
        }

        if (json.has("receive")) {

            receive = json.optString("receive", "");
        }

        if (json.has("borrow_duration")) {

            borrow_duration = json.optString("borrow_duration", "");
        }

        if (json.has("expired_money_now")) {

            expired_money_now = json.optString("expired_money_now", "-");
        }else if(json.isNull("expired_money_now")){
            expired_money_now ="-";
        }

        if (json.has("call_fee_now")) {

            call_fee_now = json.optString("call_fee_now", "-");
        }else if(json.isNull("call_fee_now")){
            call_fee_now ="-";
        }


        if (json.has("borrow_status")) {

            borrow_status = json.optString("borrow_status", "");
        }
    }

    public String getRepayment_type() {
        return repayment_type;
    }

    public void setRepayment_type(String repayment_type) {
        this.repayment_type = repayment_type;
    }

    public String getExpired_time() {
        return expired_time;
    }

    public void setExpired_time(String expired_time) {
        this.expired_time = expired_time;
    }

    public String getBorrow_interest_rate() {
        return borrow_interest_rate;
    }

    public void setBorrow_interest_rate(String borrow_interest_rate) {
        this.borrow_interest_rate = borrow_interest_rate;
    }

    public String getProgress() {
        return progress;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }
    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getReceive() {
        return receive;
    }

    public void setReceive(String receive) {
        this.receive = receive;
    }

    public String getBorrow_duration() {
        return borrow_duration;
    }

    public void setBorrow_duration(String borrow_duration) {
        this.borrow_duration = borrow_duration;
    }
    public String getExpired_money_now() {
        return expired_money_now;
    }

    public void setExpired_money_now(String expired_money_now) {
        this.expired_money_now = expired_money_now;
    }
    public String getCall_fee_now() {
        return call_fee_now;
    }

    public void setCall_fee_now(String call_fee_now) {
        this.call_fee_now = call_fee_now;
    }
    public String getBorrow_status() {
        return borrow_status;
    }
    public void setBorrow_status(String borrow_status) {
        this.borrow_status = borrow_status;
    }
    public String getBorrow_name() {
        return borrow_name;
    }
    public void setBorrow_name(String borrow_name) {
        this.borrow_name = borrow_name;
    }
    public Long getBorrow_money() {
        return borrow_money;
    }

    public void setBorrow_money(Long borrow_money) {
        this.borrow_money = borrow_money;
    }
    public String getBid() {
        return bid;
    }

    public void setBid(String bid) {
        this.bid = bid;
    }
    public String getCapital() {
        return capital;
    }

    public void setCapital(String capital) {
        this.capital = capital;
    }

    public String getInterest() {
        return interest;
    }

    public void setInterest(String interest) {
        this.interest = interest;
    }

    public String getSubstitute_money() {
        return substitute_money;
    }

    public void setSubstitute_money(String substitute_money) {
        this.substitute_money = substitute_money;
    }

    public String getHas_borrow() {
        return has_borrow;
    }

    public void setHas_borrow(String has_borrow) {
        this.has_borrow = has_borrow;
    }

    public String getBorrow_times() {
        return borrow_times;
    }

    public void setBorrow_times(String borrow_times) {
        this.borrow_times = borrow_times;
    }
    public String getSort_order() {
        return sort_order;
    }

    public void setSort_order(String sort_order) {
        this.sort_order = sort_order;
    }
    public int getNeed_pay() {
        return need_pay;
    }

    public void setNeed_pay(int need_pay) {
        this.need_pay = need_pay;
    }
    public String getAdd_time() {
        return add_time;
    }

    public void setAdd_time(String add_time) {
        this.add_time = add_time;
    }
    public int getOption() {
        return option;
    }

    public void setOption(int option) {
        this.option = option;
    }

}
