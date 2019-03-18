package com.pulan.dialogserver.entity.resp;

/**
 * 工作饱和度返回
 */
public class SaturationMsg {
    private String mail_name;
    private String sat_date;
    private double saturation;
    private String month;
    private Integer mon_kaoqin_days=0;
    private double mon_meeting_times=0;
    private double mon_avg_saturation=0;

    public SaturationMsg() {
    }

    public String getMail_name() {
        return mail_name;
    }

    public void setMail_name(String mail_name) {
        this.mail_name = mail_name;
    }

    public String getSat_date() {
        return sat_date;
    }

    public void setSat_date(String sat_date) {
        this.sat_date = sat_date;
    }

    public double getSaturation() {
        return saturation;
    }

    public void setSaturation(double saturation) {
        this.saturation = saturation;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public Integer getMon_kaoqin_days() {
        return mon_kaoqin_days;
    }

    public void setMon_kaoqin_days(Integer mon_kaoqin_days) {
        this.mon_kaoqin_days = mon_kaoqin_days;
    }

    public double getMon_meeting_times() {
        return mon_meeting_times;
    }

    public void setMon_meeting_times(double mon_meeting_times) {
        this.mon_meeting_times = mon_meeting_times;
    }

    public double getMon_avg_saturation() {
        return mon_avg_saturation;
    }

    public void setMon_avg_saturation(double mon_avg_saturation) {
        this.mon_avg_saturation = mon_avg_saturation;
    }
}
