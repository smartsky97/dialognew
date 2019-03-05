package com.pulan.dialogserver.entity.resp;
/**
 * 考勤查询返回-按天
 */
public class AttendanceDayMsg {

    private String mail_name;
    private String cn_name;
    private String kaoqin_date;
    private double work_time;
    private String start_time;
    private String end_time;
    private int is_late;
    private int is_early;
    private int is_not_clock;

    public AttendanceDayMsg() {
    }

    public AttendanceDayMsg(String mail_name, String cn_name, String kaoqin_date, double work_time, String start_time, String end_time, int is_late, int is_early, int is_not_clock) {
        this.mail_name = mail_name;
        this.cn_name = cn_name;
        this.kaoqin_date = kaoqin_date;
        this.work_time = work_time;
        this.start_time = start_time;
        this.end_time = end_time;
        this.is_late = is_late;
        this.is_early = is_early;
        this.is_not_clock = is_not_clock;
    }

    public String getMail_name() {
        return mail_name;
    }

    public void setMail_name(String mail_name) {
        this.mail_name = mail_name;
    }

    public String getCn_name() {
        return cn_name;
    }

    public void setCn_name(String cn_name) {
        this.cn_name = cn_name;
    }

    public String getKaoqin_date() {
        return kaoqin_date;
    }

    public void setKaoqin_date(String kaoqin_date) {
        this.kaoqin_date = kaoqin_date;
    }

    public double getWork_time() {
        return work_time;
    }

    public void setWork_time(double work_time) {
        this.work_time = work_time;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public int getIs_late() {
        return is_late;
    }

    public void setIs_late(int is_late) {
        this.is_late = is_late;
    }

    public int getIs_early() {
        return is_early;
    }

    public void setIs_early(int is_early) {
        this.is_early = is_early;
    }

    public int getIs_not_clock() {
        return is_not_clock;
    }

    public void setIs_not_clock(int is_not_clock) {
        this.is_not_clock = is_not_clock;
    }
}
