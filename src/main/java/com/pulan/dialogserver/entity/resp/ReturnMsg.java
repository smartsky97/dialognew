package com.pulan.dialogserver.entity.resp;

public class ReturnMsg {

    private Integer status;
    private String type;
    private Object resp;  //返回数据

    public ReturnMsg() {
        this.status = 0;
    }

    public ReturnMsg(Integer status, String type, Object resp) {
        this.status = status;
        this.type = type;
        this.resp = resp;

    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getResp() {
        return resp;
    }

    public void setResp(Object resp) {
        this.resp = resp;
    }

}
