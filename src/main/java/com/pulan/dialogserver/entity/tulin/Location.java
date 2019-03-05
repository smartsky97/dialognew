package com.pulan.dialogserver.entity.tulin;

public class Location {
    private String city;
    private String province;
    private String street;

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    private String district;

    public Location() {
    }

    public Location(String city, String province, String street) {
        this.city = city;
        this.province = province;
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }
}
