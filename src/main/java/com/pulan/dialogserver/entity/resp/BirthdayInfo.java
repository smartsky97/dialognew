package com.pulan.dialogserver.entity.resp;

import java.util.Date;

public class BirthdayInfo {
    private String name;
    private Date birthday;

    public BirthdayInfo() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }
}
