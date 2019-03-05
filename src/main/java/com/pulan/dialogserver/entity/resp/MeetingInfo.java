package com.pulan.dialogserver.entity.resp;

/**
 * 我的会议视图
 */
public class MeetingInfo {
    private String uuid;
    private String meeting_date;
    private String start_time;
    private String end_time;
    private String fd_subject;
    private String doc_content;
    private String doc_create_time;
    private String doc_create_person;
    private String doc_emcee_person;
    private String fd_host_person;
    private String doc_dept;
    private String meeting_type;
    private String meetingres_place;
    private String meeting_copy_person;
    private String meeting_attend_person;
    private String mail_name;

    public MeetingInfo() {
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

    public String getMeeting_date() {
        return meeting_date;
    }

    public void setMeeting_date(String meeting_date) {
        this.meeting_date = meeting_date;
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

    public String getFd_subject() {
        return fd_subject;
    }

    public void setFd_subject(String fd_subject) {
        this.fd_subject = fd_subject;
    }

    public String getDoc_content() {
        return doc_content;
    }

    public void setDoc_content(String doc_content) {
        this.doc_content = doc_content;
    }

    public String getDoc_create_time() {
        return doc_create_time;
    }

    public void setDoc_create_time(String doc_create_time) {
        this.doc_create_time = doc_create_time;
    }

    public String getDoc_create_person() {
        return doc_create_person;
    }

    public void setDoc_create_person(String doc_create_person) {
        this.doc_create_person = doc_create_person;
    }

    public String getDoc_emcee_person() {
        return doc_emcee_person;
    }

    public void setDoc_emcee_person(String doc_emcee_person) {
        this.doc_emcee_person = doc_emcee_person;
    }

    public String getFd_host_person() {
        return fd_host_person;
    }

    public void setFd_host_person(String fd_host_person) {
        this.fd_host_person = fd_host_person;
    }

    public String getDoc_dept() {
        return doc_dept;
    }

    public void setDoc_dept(String doc_dept) {
        this.doc_dept = doc_dept;
    }

    public String getMeeting_type() {
        return meeting_type;
    }

    public void setMeeting_type(String meeting_type) {
        this.meeting_type = meeting_type;
    }

    public String getMeetingres_place() {
        return meetingres_place;
    }

    public void setMeetingres_place(String meetingres_place) {
        this.meetingres_place = meetingres_place;
    }

    public String getMeeting_copy_person() {
        return meeting_copy_person;
    }

    public void setMeeting_copy_person(String meeting_copy_person) {
        this.meeting_copy_person = meeting_copy_person;
    }

    public String getMeeting_attend_person() {
        return meeting_attend_person;
    }

    public void setMeeting_attend_person(String meeting_attend_person) {
        this.meeting_attend_person = meeting_attend_person;
    }
}
