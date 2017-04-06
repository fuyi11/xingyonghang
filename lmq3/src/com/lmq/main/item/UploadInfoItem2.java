package com.lmq.main.item;

import org.json.JSONObject;

/**
 * Created by zhaoshuai on 2015/8/15.
 */
public class UploadInfoItem2 {


	private String id;

	private String fileName;
    private String filees;
    private String fileStatus;


    public void init(JSONObject json) {


        if (json.has("id")) {

            id = json.optString("id", "");

        }
        if (json.has("data_name")) {
        	
        	fileName = json.optString("data_name", "");
        	
        }


        if (json.has("deal_credits")) {
            filees = json.optString("deal_credits", "");

        }

        if (json.has("status")) {
            fileStatus = json.optString("status", "");

        }


    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilees() {
        return filees;
    }

    public void setFilees(String filees) {
        this.filees = filees;
    }

    public String getFileStatus() {
        return fileStatus;
    }

    public void setFileStatus(String fileStatus) {
        this.fileStatus = fileStatus;
    }
    public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
