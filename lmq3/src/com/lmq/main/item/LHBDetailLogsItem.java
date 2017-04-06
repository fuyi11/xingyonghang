package com.lmq.main.item;

import com.lmq.main.enmu.LHBLogsType;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by zhaoshuai on 15/8/5.
 */
public class LHBDetailLogsItem {
    /**
     * 结息日期
     */
    private String e_time;
    /**
     * 计息金额 / 赎回金额
     */
    private String money;
    /**
     * 本金
     */
    private String funds;
    /**
     * 支付/复投收益状态
     */
    private String yifutou;
    /**
     * 赎回时间
     */
    private String add_time;

    /**
     * 赎回方式
     */
    private String Redeem;
    /**
     * 状态
     */
    private String status_type;
    /**
     * 备注
     */
    private String remark;


    public LHBDetailLogsItem(JSONObject json, LHBLogsType type) {

        switch (type) {


            case SHOU_YI_LOGS_TYPE:
                createSYItem(json);
                break;


            case SHU_HUI_LOGS_TYPE:
                createSHItem(json);
                break;


        }


    }

    /**
     * 初始化赎回Item
     *
     * @param json
     */
    private void createSHItem(JSONObject json) {


        if (json.has("add_time")) {

            add_time = json.optString("add_time", "");
        }

        if (json.has("money")) {

            money = json.optString("money", "");
        }


        if (json.has("Redeem")) {

            Redeem = json.optString("Redeem", "");
        }

        if (json.has("status_type")) {

            status_type = json.optString("status_type", "");
        }
        if (json.has("remark")) {

            remark = json.optString("remark", "");
        }


    }


    /**
     * 初始化收益Item
     *
     * @param json
     */
    private void createSYItem(JSONObject json) {


//        返回字段
//        e_time   结息日期
//        money   计息金额
//        funds   本金
//        yifutou   支付/复投收益状态

        if (json.has("e_time")) {

            e_time = json.optString("e_time", "");
        }

        if (json.has("money")) {

            money = json.optString("money", "");
        }


        if (json.has("funds")) {

            funds = json.optString("funds", "");
        }
        if (json.has("yifutou")) {

            yifutou = json.optString("yifutou", "");
        }


    }

    public String getE_time() {
        return e_time;
    }

    public void setE_time(String e_time) {
        this.e_time = e_time;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getFunds() {
        return funds;
    }

    public void setFunds(String funds) {
        this.funds = funds;
    }

    public String getYifutou() {
        return yifutou;
    }

    public void setYifutou(String yifutou) {
        this.yifutou = yifutou;
    }

    public String getAdd_time() {
        return add_time;
    }

    public void setAdd_time(String add_time) {
        this.add_time = add_time;
    }

    public String getRedeem() {
        return Redeem;
    }

    public void setRedeem(String redeem) {
        Redeem = redeem;
    }

    public String getStatus_type() {
        return status_type;
    }

    public void setStatus_type(String status_type) {
        this.status_type = status_type;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
