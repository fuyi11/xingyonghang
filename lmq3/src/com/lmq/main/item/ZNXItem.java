package com.lmq.main.item;

import com.lmq.main.enmu.LHBLogsType;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 站内信
 */
public class ZNXItem {
    /**
     * 消息id
     */
    private int id;
    /**
     * 消息未读状态 0未读 1已读
     */
    private int status;
	/**
     * 消息类型
     */
    private String msg_name;

	/**
     * 消息时间
     */
    private String send_time;
    /**
     * 消息简介
     */
    private String title;
    /**
     * 消息内容
     */
    private String msg_content;

 
   
    public void createRedItem(JSONObject json) {


        if (json.has("status")) {

        	status = json.optInt("status", 0);
        }
        if (json.has("id")) {
        	
        	id = json.optInt("id", 0);
        }

        if (json.has("msg_name")) {

        	msg_name = json.optString("msg_name", "");
        }

        if (json.has("send_time")) {

        	send_time = json.optString("send_time", "");
        }

        if (json.has("title")) {

        	title = json.optString("title", "");
        }
        if (json.has("msg_content")) {

        	msg_content = json.optString("msg_content", "");
        }


    }
    public int getId() {
		return id;
	}



	public void setId(int id) {
		this.id = id;
	}



	public String getMsg_name() {
		return msg_name;
	}



	public void setMsg_name(String msg_name) {
		this.msg_name = msg_name;
	}



	public String getSend_time() {
		return send_time;
	}



	public void setSend_time(String send_time) {
		this.send_time = send_time;
	}



	public String getTitle() {
		return title;
	}



	public void setTitle(String title) {
		this.title = title;
	}



	public String getMsg_content() {
		return msg_content;
	}



	public void setMsg_content(String msg_content) {
		this.msg_content = msg_content;
	}
	
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}

   
}
