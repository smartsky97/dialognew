package com.pulan.dialogserver.entity.resp;

/**
 * 考勤查询返回-按月
 */
public class AttendanceMsg {
    private String mail_name;
    private String date;
    private String name;
    private String work_times;
    private String attend_rate;
    private String late_times;
    private String leave_times;
    private String noPunch_times;
    private String overtimes;
    private String normal_punching;
    public AttendanceMsg() {
    }

    public String getMail_name() {
        return mail_name;
    }

    public void setMail_name(String mail_name) {
        this.mail_name = mail_name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWork_times() {
        return work_times;
    }

    public void setWork_times(String work_times) {
        this.work_times = work_times;
    }

    public String getAttend_rate() {
        return attend_rate;
    }

    public void setAttend_rate(String attend_rate) {
        this.attend_rate = attend_rate;
    }

    public String getLate_times() {
        return late_times;
    }

    public void setLate_times(String late_times) {
        this.late_times = late_times;
    }

    public String getLeave_times() {
        return leave_times;
    }

    public void setLeave_times(String leave_times) {
        this.leave_times = leave_times;
    }

    public String getNoPunch_times() {
        return noPunch_times;
    }

    public void setNoPunch_times(String noPunch_times) {
        this.noPunch_times = noPunch_times;
    }

    public String getOvertimes() {
        return overtimes;
    }

    public void setOvertimes(String overtimes) {
        this.overtimes = overtimes;
    }

    public String getNormal_punching() {
        return normal_punching;
    }

    public void setNormal_punching(String normal_punching) {
        this.normal_punching = normal_punching;
    }

    @Override
    public String toString() {
        return "AttendanceMsg{" +
                "mail_name='" + mail_name + '\'' +
                ", date='" + date + '\'' +
                ", name='" + name + '\'' +
                ", work_times='" + work_times + '\'' +
                ", attend_rate='" + attend_rate + '\'' +
                ", late_times='" + late_times + '\'' +
                ", leave_times='" + leave_times + '\'' +
                ", noPunch_times='" + noPunch_times + '\'' +
                ", overtimes='" + overtimes + '\'' +
                ", normal_punching='" + normal_punching + '\'' +
                '}';
    }
}
