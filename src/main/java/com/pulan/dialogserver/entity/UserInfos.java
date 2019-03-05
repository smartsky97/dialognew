package com.pulan.dialogserver.entity;

public class UserInfos {
    private String fNumber;
    private String perFName;
    private String posFName;

    public UserInfos() {
    }

    public UserInfos(String fNumber, String perFName, String posFName) {
        this.fNumber = fNumber;
        this.perFName = perFName;
        this.posFName = posFName;
    }

    public String getfNumber() {
        return fNumber;
    }

    public void setfNumber(String fNumber) {
        this.fNumber = fNumber;
    }

    public String getPerFName() {
        return perFName;
    }

    public void setPerFName(String perFName) {
        this.perFName = perFName;
    }

    public String getPosFName() {
        return posFName;
    }

    public void setPosFName(String posFName) {
        this.posFName = posFName;
    }
}
