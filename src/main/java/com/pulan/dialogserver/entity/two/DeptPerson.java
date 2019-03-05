package com.pulan.dialogserver.entity.two;

/**
 * 公司部门人员信息
 */
public class DeptPerson {

    private String id;
    private String cnName;
    private String department;
    private String mobile;
    private String email;
    private String mailName;


    public DeptPerson() {
    }

    public DeptPerson(String id, String cnName, String department, String mobile, String email) {
        this.id = id;
        this.cnName = cnName;
        this.department = department;
        this.mobile = mobile;
        this.email = email;
    }

    public DeptPerson(String id, String cnName, String department, String mobile, String email, String mailName) {
        this.id = id;
        this.cnName = cnName;
        this.department = department;
        this.mobile = mobile;
        this.email = email;
        this.mailName = mailName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCnName() {
        return cnName;
    }

    public void setCnName(String cnName) {
        this.cnName = cnName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMailName() {
        return mailName;
    }

    public void setMailName(String mailName) {
        this.mailName = mailName;
    }
}
