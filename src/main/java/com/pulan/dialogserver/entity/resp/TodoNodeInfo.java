package com.pulan.dialogserver.entity.resp;

public class TodoNodeInfo {

    private  String fd_id;

    private  String fd_action_name;
    private  String fd_action_info;
    private  String fd_handler_cn_name;
    private  String fd_from;

    public String getFd_from() {
        return fd_from;
    }

    public void setFd_from(String fd_from) {
        this.fd_from = fd_from;
    }

    public String getFd_id() {
        return fd_id;
    }

    public void setFd_id(String fd_id) {
        this.fd_id = fd_id;
    }

    public String getFd_action_name() {
        return fd_action_name;
    }

    public void setFd_action_name(String fd_action_name) {
        this.fd_action_name = fd_action_name;
    }

    public String getFd_action_info() {
        return fd_action_info;
    }

    public void setFd_action_info(String fd_action_info) {
        this.fd_action_info = fd_action_info;
    }

    public String getFd_handler_cn_name() {
        return fd_handler_cn_name;
    }

    public void setFd_handler_cn_name(String fd_handler_cn_name) {
        this.fd_handler_cn_name = fd_handler_cn_name;
    }

    public String getFd_handle_time() {
        return fd_handle_time;
    }

    public void setFd_handle_time(String fd_handle_time) {
        this.fd_handle_time = fd_handle_time;
    }

    private  String fd_handle_time;


}
