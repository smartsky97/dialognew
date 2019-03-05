package com.pulan.dialogserver.entity.resp;

/**
 * 我的日程视图
 */
public class CalendarMsg {
    private String uuid;
    private String mail_name;
    private String calendar_date;
    private String doc_create_time;
    private String doc_subject;
    private String doc_start_time;
    private String doc_finish_time;
    private String fd_location;
    private String fd_status;

    public CalendarMsg() {
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getMail_name() {
        return mail_name;
    }

    public void setMail_name(String mail_name) {
        this.mail_name = mail_name;
    }

    public String getCalendar_date() {
        return calendar_date;
    }

    public void setCalendar_date(String calendar_date) {
        this.calendar_date = calendar_date;
    }

    public String getDoc_create_time() {
        return doc_create_time;
    }

    public void setDoc_create_time(String doc_create_time) {
        this.doc_create_time = doc_create_time;
    }

    public String getDoc_subject() {
        return doc_subject;
    }

    public void setDoc_subject(String doc_subject) {
        this.doc_subject = doc_subject;
    }

    public String getDoc_start_time() {
        return doc_start_time;
    }

    public void setDoc_start_time(String doc_start_time) {
        this.doc_start_time = doc_start_time;
    }

    public String getDoc_finish_time() {
        return doc_finish_time;
    }

    public void setDoc_finish_time(String doc_finish_time) {
        this.doc_finish_time = doc_finish_time;
    }

    public String getFd_location() {
        return fd_location;
    }

    public void setFd_location(String fd_location) {
        this.fd_location = fd_location;
    }

    public String getFd_status() {
        return fd_status;
    }

    public void setFd_status(String fd_status) {
        this.fd_status = fd_status;
    }

    @Override
    public String toString() {
        return "CalendarMsg{" +
                "uuid='" + uuid + '\'' +
                ", mail_name='" + mail_name + '\'' +
                ", calendar_date='" + calendar_date + '\'' +
                ", doc_create_time='" + doc_create_time + '\'' +
                ", doc_subject='" + doc_subject + '\'' +
                ", doc_start_time='" + doc_start_time + '\'' +
                ", doc_finish_time='" + doc_finish_time + '\'' +
                ", fd_location='" + fd_location + '\'' +
                ", fd_status='" + fd_status + '\'' +
                '}';
    }
}
