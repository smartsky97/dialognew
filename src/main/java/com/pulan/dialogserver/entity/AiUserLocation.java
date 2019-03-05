package com.pulan.dialogserver.entity;

import java.sql.Timestamp;

public class AiUserLocation {

    private Integer id;
    private String loginName;
    private String addr;
    private String country;
    private String province;
    private String city;
    private String district;
    private String street;
    private Timestamp currtime;

    public AiUserLocation() {
    }

    public AiUserLocation(Integer id, String loginName, String addr, String country, String province, String city, String district, String street, Timestamp currtime) {
        this.id = id;
        this.loginName = loginName;
        this.addr = addr;
        this.country = country;
        this.province = province;
        this.city = city;
        this.district = district;
        this.street = street;
        this.currtime = currtime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public Timestamp getCurrtime() {
        return currtime;
    }

    public void setCurrtime(Timestamp currtime) {
        this.currtime = currtime;
    }
}
