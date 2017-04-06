package com.lmq.main.item;

import org.json.JSONArray;
import org.json.JSONObject;

import com.lmq.main.api.MyLog;

public class qddItem {
	private JSONArray LoanJsonList; // 转账列表
	private JSONArray SecondaryJsonList;
	private String LoanOutMoneymoremore; // 付款人钱多多标识 a
	private String LoanInMoneymoremore; // 收款人钱多多标识
	private String OrderNo; // 网贷平台订单号
	private String BatchNo; // 网贷平台标号
	private String Amount; // 金额
	private String PlatformMoneymoremore; // 平台乾多多标识
	private String TransferAction; // 转账类型 1.投标 2.还款 3.其他
	private String Action; // 操作类型 1.手动转账 2.自动转账
	private String TransferType; // 转账方式 1.桥连 2.直连
	private String NeedAudit; // 空.需要审核 1.自动通过
								// private String RandomTimeStamp;
								// //随机时间戳
	private String ReturnURL;
	private String NotifyURL; // 后台通知网址
	private String Remark = "";// 备注
	private String Remark1 = "";// 备注
	private String Remark2 = "";// 备注
	private String Remark3 = "";// 备注
	private String FullAmount = "";// 满标
	private String TransferName = "";// 用途

	public void initTransfer(JSONObject json) {

		try {
			/*if (json.has("SecondaryJsonList"))
				SecondaryJsonList = new JSONArray();
			if (json.has("LoanOutMoneymoremore"))
				LoanOutMoneymoremore = json.getString("LoanOutMoneymoremore");// 付款人钱多多标识
			if (json.has("LoanInMoneymoremore"))
				LoanInMoneymoremore = json.getString("LoanInMoneymoremore");// 收款人钱多多标识
			if (json.has("OrderNo"))
				OrderNo = json.getString("OrderNo");// 网贷平台订单号
			if (json.has("BatchNo"))
				BatchNo = json.getString("BatchNo");// 网贷平台标号
			if (json.has("money"))
				Amount = json.getString("money");// 金额
			if (json.has("Remark"))
				Remark = json.optString("Remark", "");// 备注
			if (json.has("FullAmount"))
				FullAmount = json.optString("FullAmount", "");// 满标
			if (json.has("TransferName"))
				TransferName = json.optString("TransferName", "");// 转账用途
			if (json.has("SecondaryJsonList")
					&& !json.isNull("SecondaryJsonList"))// 二次分配列表
				SecondaryJsonList = json.getJSONArray("SecondaryJsonList");*/
			
			if (json.has("LoanJsonList"))
				LoanJsonList = new JSONArray();
			LoanJsonList = json.getJSONArray("LoanJsonList");//转账列表
			PlatformMoneymoremore = json.getString("PlatformMoneymoremore");// 平台乾多多标识
			TransferAction = json.optString("TransferAction","1");//转账类型 1.投标 2.还款 3.其他
			Action = json.optString("Action","1");//操作类型 1.手动转账 2.自动转账
			TransferType = json.optString("TransferType","2");//转账方式 1.桥连 // 2.直连
			NeedAudit = json.optString("NeedAudit","");// 空.需要审核 1.自动通过
			Remark1 = json.optString("Remark1","");// 空自定义备注
			Remark2 = json.optString("Remark2","");// 空自定义备注
			Remark3 = json.optString("Remark3","");// 空自定义备注
			ReturnURL = json.optString("ReturnURL","");//前台通知网址
			NotifyURL = json.getString("NotifyURL");// 后台通知网址

			
		
		} catch (Exception e) {
			e.printStackTrace();
			MyLog.e("QDD", "QDD=加载钱多多数据错误！");
		}

	}

	public JSONArray getSecondaryJsonList() {
		return SecondaryJsonList;
	}

	public void setSecondaryJsonList(JSONArray secondaryJsonList) {
		SecondaryJsonList = secondaryJsonList;
	}

	public String getLoanOutMoneymoremore() {
		return LoanOutMoneymoremore;
	}

	public void setLoanOutMoneymoremore(String loanOutMoneymoremore) {
		LoanOutMoneymoremore = loanOutMoneymoremore;
	}

	public String getLoanInMoneymoremore() {
		return LoanInMoneymoremore;
	}

	public void setLoanInMoneymoremore(String loanInMoneymoremore) {
		LoanInMoneymoremore = loanInMoneymoremore;
	}

	public String getOrderNo() {
		return OrderNo;
	}

	public void setOrderNo(String orderNo) {
		OrderNo = orderNo;
	}

	public String getBatchNo() {
		return BatchNo;
	}

	public void setBatchNo(String batchNo) {
		BatchNo = batchNo;
	}

	public String getAmount() {
		return Amount;
	}

	public void setAmount(String amount) {
		Amount = amount;
	}

	public String getPlatformMoneymoremore() {
		return PlatformMoneymoremore;
	}

	public void setPlatformMoneymoremore(String platformMoneymoremore) {
		PlatformMoneymoremore = platformMoneymoremore;
	}

	public String getTransferAction() {
		return TransferAction;
	}

	public void setTransferAction(String transferAction) {
		TransferAction = transferAction;
	}

	public String getAction() {
		return Action;
	}

	public void setAction(String action) {
		Action = action;
	}

	public String getTransferType() {
		return TransferType;
	}

	public void setTransferType(String transferType) {
		TransferType = transferType;
	}

	public String getNeedAudit() {
		return NeedAudit;
	}

	public void setNeedAudit(String needAudit) {
		NeedAudit = needAudit;
	}

	public String getReturnURL() {
		return ReturnURL;
	}

	public void setReturnURL(String returnURL) {
		ReturnURL = returnURL;
	}

	public String getNotifyURL() {
		return NotifyURL;
	}

	public void setNotifyURL(String notifyURL) {
		NotifyURL = notifyURL;
	}

	public String getRemark() {
		return Remark;
	}

	public void setRemark(String remark) {
		Remark = remark;
	}

	public String getFullAmount() {
		return FullAmount;
	}

	public void setFullAmount(String fullAmount) {
		FullAmount = fullAmount;
	}

	public String getTransferName() {
		return TransferName;
	}

	public void setTransferName(String transferName) {
		TransferName = transferName;
	}

	public JSONArray getLoanJsonList() {
		return LoanJsonList;
	}

	public void setLoanJsonList(JSONArray loanJsonList) {
		LoanJsonList = loanJsonList;
	}

	public String getRemark1() {
		return Remark1;
	}

	public void setRemark1(String remark1) {
		Remark1 = remark1;
	}

	public String getRemark2() {
		return Remark2;
	}

	public void setRemark2(String remark2) {
		Remark2 = remark2;
	}

	public String getRemark3() {
		return Remark3;
	}

	public void setRemark3(String remark3) {
		Remark3 = remark3;
	}
	
	

}
