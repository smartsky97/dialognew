package com.pulan.dialogserver.entity.tulin;

public class First {
    private Integer reqType = 0;
    private Perception perception;
    private UserInfo userInfo;

    public First() {
    }

    public First(Integer reqType, Perception perception, UserInfo userInfo) {
        this.reqType = reqType;
        this.perception = perception;
        this.userInfo = userInfo;
    }

    public Integer getReqType() {
        return reqType;
    }

    public void setReqType(Integer reqType) {
        this.reqType = reqType;
    }

    public Perception getPerception() {
        return perception;
    }

    public void setPerception(Perception perception) {
        this.perception = perception;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }
}
