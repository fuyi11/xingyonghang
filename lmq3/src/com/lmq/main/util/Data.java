package com.lmq.main.util;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.lmq.main.item.LendMoneyItem;
import com.lmq.main.item.newsItem;
import com.lmq.main.item.tzItem;

public class Data {
//	public static JSONObject peopleDataJson;
	public static JSONObject peopleSafeJson;
	public static JSONObject peopleBounsJson;
	public static JSONObject peopleWithdrawalJson; 
	public static JSONObject peopleinfobindykc;// 绑定银行卡
	
	public static List NewsList = new ArrayList<newsItem>();
    public static List NoticeList = new ArrayList<newsItem>();

//	// 借款列表
//	public static List lendMoneyList = new ArrayList<LendMoneyItem>();

	public static JSONObject tzListItemJson;
	public static JSONObject tzListItem2_1Json;
	public static JSONObject tzListItem3_1Json;
	public static JSONObject tzListItem3_2Json;
	public static JSONObject newsListItemJson;
	public static JSONObject noticeListItemJson;
	public static JSONObject newsListAddJson;
	public static JSONObject noticeListAddJson;
	public static JSONObject zhanghujson;

	// 投资界面 List
	public static List<tzItem> tzListInfo;

	public static void clearInfo() {
		// dealListJson = null;
//		peopleDataJson = null;
		peopleSafeJson = null;
		peopleWithdrawalJson = null;
//		peopleDataJson = null;
		NewsList = null;
		newsListItemJson = null;
		noticeListAddJson = null;
		noticeListItemJson = null;
		newsListAddJson = null;
//		lendMoneyList = null;
		tzListInfo = null; 

	}
}
