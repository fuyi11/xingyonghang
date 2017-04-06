package com.lmq.main.item;

import com.lmq.main.enmu.LHBLogsType;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 奖励管理之红包列表/ 红包状态 1已生成 4已抢到 3已过期
 */
public class RedBagItem {
    /**
     * 红包金额
     */
    private String bonus_money;

	/**
     * 上传字段status = 1时才返回，生成时间
     */
    private String create_time;
    /**
     * 上传字段status = 1时才返回，过期时间，上传字段status = 3时才返回，过期时间
     */
    private String validate_et;
    /**
     * 上传字段status = 1时才返回，生成链接
     */
    private String share_url;
    /**
     * 上传字段status = 4时才返回，领取时间
     */
    private String take_time;
    /**
     * 上传字段status = 4时才返回，来源
     */
    private String source_name;
 
    /**
     * 上传字段status = 3时才返回，状态
     */
    private String weilingqu;
   
    public void createRedItem(JSONObject json) {


        if (json.has("bonus_money")) {

        	bonus_money = json.optString("bonus_money", "");
        }

        if (json.has("create_time")) {

        	create_time = json.optString("create_time", "");
        }

        if (json.has("validate_et")) {

        	validate_et = json.optString("validate_et", "");
        }

        if (json.has("share_url")) {

        	share_url = json.optString("share_url", "");
        }
        if (json.has("take_time")) {

        	take_time = json.optString("take_time", "");
        }
        if (json.has("source_name")) {
        	
        	source_name = json.optString("source_name", "");
        }
        if (json.has("weilingqu")) {
        	
        	weilingqu = json.optString("weilingqu", "");
        }


    }
    public String getBonus_money() {
		return bonus_money;
	}

	public void setBonus_money(String bonus_money) {
		this.bonus_money = bonus_money;
	}

	public String getCreate_time() {
		return create_time;
	}

	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}

	public String getValidate_et() {
		return validate_et;
	}

	public void setValidate_et(String validate_et) {
		this.validate_et = validate_et;
	}

	public String getShare_url() {
		return share_url;
	}

	public void setShare_url(String share_url) {
		this.share_url = share_url;
	}

	public String getTake_time() {
		return take_time;
	}

	public void setTake_time(String take_time) {
		this.take_time = take_time;
	}

	public String getSource_name() {
		return source_name;
	}

	public void setSource_name(String source_name) {
		this.source_name = source_name;
	}

	public String getWeilingqu() {
		return weilingqu;
	}

	public void setWeilingqu(String weilingqu) {
		this.weilingqu = weilingqu;
	}
   
}
