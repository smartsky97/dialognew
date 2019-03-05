package com.pulan.dialogserver.entity.tulin;

public class UserInfo {
    private String apiKey;
    private String userId;

    public UserInfo() {
    }

    public UserInfo(String apiKey, String userId) {
        this.apiKey = apiKey;
        this.userId = userId;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
