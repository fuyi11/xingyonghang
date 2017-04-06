package com.lmq.main.item;

import org.json.JSONException;
import org.json.JSONObject;

import com.lmq.main.api.MyLog;


public class dealItem2 {
	/**项目编号*/
	private String batch_no;

	/**发生时间*/
	private String add_time;
	/**赎回金额*/
	private String money;
	/** 类型 0是赎回，1是投资 */
	private int Redeem;

	/** 状态 */
	private String status_type;
	/** 备注 */
	private String remark;

	public dealItem2(JSONObject json) {
		MyLog.e("json", json.toString());
		try {
			if (json.has("batch_no")) {
				setBatch_no(json.getString("batch_no"));
			}
			if (json.has("add_time")) {
				setAdd_time(json.getString("add_time"));
			}
			if (json.has("money")) {
				setMoney(json.getString("money"));
			}
			if (json.has("Redeem")) {
				setRedeem(json.getInt("Redeem"));
			}
			if (json.has("remark")) {
				setRemark(json.getString("remark"));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getBatch_no() {
		return batch_no;
	}

	public void setBatch_no(String batch_no) {
		this.batch_no = batch_no;
	}

	public String getAdd_time() {
		return add_time;
	}

	public void setAdd_time(String add_time) {
		this.add_time = add_time;
	}

	public String getMoney() {
		return money;
	}

	public void setMoney(String money) {
		this.money = money;
	}

	public int getRedeem() {
		return Redeem;
	}

	public void setRedeem(int redeem) {
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
