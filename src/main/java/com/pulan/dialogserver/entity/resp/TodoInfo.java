package com.pulan.dialogserver.entity.resp;

public class TodoInfo {

    private  String uuid;

    private  String todo_type;

    private  String fd_status;

    private  String doc_create_time;

    private  String fd_create_person;

    private  String doc_subject;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

   public String getTodo_type() {
        return todo_type;
    }

    public void setTodo_type(String todo_type) {
        this.todo_type = todo_type;
    }

    public String getFd_status() {
        return fd_status;
    }

    public void setFd_status(String fd_status) {
        this.fd_status = fd_status;
    }

    public String getDoc_create_time() {
        return doc_create_time;
    }

    public void setDoc_create_time(String doc_create_time) {
        this.doc_create_time = doc_create_time;
    }

    public String getFd_create_person() {
        return fd_create_person;
    }

    public void setFd_create_person(String fd_create_person) {
        this.fd_create_person = fd_create_person;
    }

    public String getDoc_subject() {
        return doc_subject;
    }

    public void setDoc_subject(String doc_subject) {
        this.doc_subject = doc_subject;
    }
}
