package com.pulan.dialogserver.entity.resp;

public class AttendanceResult {
    private String mail_name;
    private String kaoqin_date;
    private String kaoqin_type;
    private Integer week;

    public AttendanceResult() {
    }

    public String getMail_name() {
        return mail_name;
    }

    public void setMail_name(String mail_name) {
        this.mail_name = mail_name;
    }

    public String getKaoqin_date() {
        return kaoqin_date;
    }

    public void setKaoqin_date(String kaoqin_date) {
        this.kaoqin_date = kaoqin_date;
    }

    public String getKaoqin_type() {
        return kaoqin_type;
    }

    public void setKaoqin_type(String kaoqin_type) {
        this.kaoqin_type = kaoqin_type;
    }

    public Integer getWeek() {
        return week;
    }

    public void setWeek(Integer week) {
        this.week = week;
    }
}
