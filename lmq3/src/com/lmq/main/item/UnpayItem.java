package com.lmq.main.item;

import org.json.JSONException;
import org.json.JSONObject;

public class UnpayItem {

	private Double amount;
	private String merchantUrl;
	private String orderId;
	private String merchantName;
	private String commodity;
	private String merchantId;
	private int responseMode;
	private String time;
	private long merchantKey;
	private String currencyType;
	private String assuredPay;
	private String remark;
	private String mac;

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public UnpayItem(JSONObject json) {
		try {
			if (json.has("amount")) {

				setAmount(json.getDouble("amount"));

			}

			if (json.has("merchantUrl")) {

				setMerchantUrl(json.getString("merchantUrl"));

			}
			if (json.has("orderId")) {

				setOrderId(json.getString("orderId"));

			}
			if (json.has("merchantName")) {

				setMerchantName(json.getString("merchantName"));

			}
			if (json.has("commodity")) {

				setCommodity(json.getString("commodity"));

			}
			if (json.has("merchantId")) {

				setMerchantId(json.getString("merchantId"));

			}
			if (json.has("responseMode")) {

				setResponseMode(json.getInt("responseMode"));

			}
			if (json.has("currencyType")) {

				setCurrencyType(json.getString("currencyType"));

			}
			if (json.has("assuredPay")) {

				setAssuredPay(json.getString("assuredPay"));

			}
			if (json.has("remark")) {

				setRemark(json.getString("remark"));

			}
			if (json.has("time")) {

				setTime(json.getString("time"));

			}
			if (json.has("mac")) {

				setMac(json.getString("mac"));

			}
			if (json.has("mode")) {

				setMode(json.getString("mode"));

			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public String getMerchantUrl() {
		return merchantUrl;
	}

	public void setMerchantUrl(String merchantUrl) {
		this.merchantUrl = merchantUrl;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getMerchantName() {
		return merchantName;
	}

	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}

	public String getCommodity() {
		return commodity;
	}

	public void setCommodity(String commodity) {
		this.commodity = commodity;
	}

	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	public int getResponseMode() {
		return responseMode;
	}

	public void setResponseMode(int responseMode) {
		this.responseMode = responseMode;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public long getMerchantKey() {
		return merchantKey;
	}

	public void setMerchantKey(long merchantKey) {
		this.merchantKey = merchantKey;
	}

	public String getCurrencyType() {
		return currencyType;
	}

	public void setCurrencyType(String currencyType) {
		this.currencyType = currencyType;
	}

	public String getAssuredPay() {
		return assuredPay;
	}

	public void setAssuredPay(String assuredPay) {
		this.assuredPay = assuredPay;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	private String mode;

}
