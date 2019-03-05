package com.pulan.dialogserver.entity.resp;

public class ReviewMsg {
    private String uuid;

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

    public String getDoc_create_time() {
        return doc_create_time;
    }

    public void setDoc_create_time(String doc_create_time) {
        this.doc_create_time = doc_create_time;
    }

    public String getFd_type() {
        return fd_type;
    }

    public void setFd_type(String fd_type) {
        this.fd_type = fd_type;
    }

    public String getFd_subject() {
        return fd_subject;
    }

    public void setFd_subject(String fd_subject) {
        this.fd_subject = fd_subject;
    }

    public String getFd_status() {
        return fd_status;
    }

    public void setFd_status(String fd_status) {
        this.fd_status = fd_status;
    }

    private String mail_name;
    private String doc_create_time;
    private String fd_type;
    private String fd_subject;
    private String fd_status;


}
