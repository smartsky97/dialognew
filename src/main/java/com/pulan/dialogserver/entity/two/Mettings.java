package com.pulan.dialogserver.entity.two;

public class Mettings {
    private String meetingType;
    private String meetingName;
    private String meetingDescribe;
    private String meetingLeaseReason; //会议租借理由
    private String meetingStartTime;
    private String meetingEndTime;
    private String meetingHost;//会议主持人
    private String meetingOrganization;//会议组织人
    private String meetingCreate;//   会议创建人
    private String meetingPlace ;//会议地点
    private String participants ;//与会人员
    private String copyPeople; //抄送人
    private String noticeMode; //通知方式

    public Mettings() {
    }

    public Mettings(String meetingType, String meetingName, String meetingDescribe, String meetingLeaseReason, String meetingStartTime, String meetingEndTime, String meetingHost, String meetingOrganization, String meetingCreate, String meetingPlace, String participants, String copyPeople, String noticeMode) {
        this.meetingType = meetingType;
        this.meetingName = meetingName;
        this.meetingDescribe = meetingDescribe;
        this.meetingLeaseReason = meetingLeaseReason;
        this.meetingStartTime = meetingStartTime;
        this.meetingEndTime = meetingEndTime;
        this.meetingHost = meetingHost;
        this.meetingOrganization = meetingOrganization;
        this.meetingCreate = meetingCreate;
        this.meetingPlace = meetingPlace;
        this.participants = participants;
        this.copyPeople = copyPeople;
        this.noticeMode = noticeMode;
    }

    public String getMeetingType() {
        return meetingType;
    }

    public void setMeetingType(String meetingType) {
        this.meetingType = meetingType;
    }

    public String getMeetingName() {
        return meetingName;
    }

    public void setMeetingName(String meetingName) {
        this.meetingName = meetingName;
    }

    public String getMeetingDescribe() {
        return meetingDescribe;
    }

    public void setMeetingDescribe(String meetingDescribe) {
        this.meetingDescribe = meetingDescribe;
    }

    public String getMeetingLeaseReason() {
        return meetingLeaseReason;
    }

    public void setMeetingLeaseReason(String meetingLeaseReason) {
        this.meetingLeaseReason = meetingLeaseReason;
    }

    public String getMeetingStartTime() {
        return meetingStartTime;
    }

    public void setMeetingStartTime(String meetingStartTime) {
        this.meetingStartTime = meetingStartTime;
    }

    public String getMeetingEndTime() {
        return meetingEndTime;
    }

    public void setMeetingEndTime(String meetingEndTime) {
        this.meetingEndTime = meetingEndTime;
    }

    public String getMeetingHost() {
        return meetingHost;
    }

    public void setMeetingHost(String meetingHost) {
        this.meetingHost = meetingHost;
    }

    public String getMeetingOrganization() {
        return meetingOrganization;
    }

    public void setMeetingOrganization(String meetingOrganization) {
        this.meetingOrganization = meetingOrganization;
    }

    public String getMeetingCreate() {
        return meetingCreate;
    }

    public void setMeetingCreate(String meetingCreate) {
        this.meetingCreate = meetingCreate;
    }

    public String getMeetingPlace() {
        return meetingPlace;
    }

    public void setMeetingPlace(String meetingPlace) {
        this.meetingPlace = meetingPlace;
    }

    public String getParticipants() {
        return participants;
    }

    public void setParticipants(String participants) {
        this.participants = participants;
    }

    public String getCopyPeople() {
        return copyPeople;
    }

    public void setCopyPeople(String copyPeople) {
        this.copyPeople = copyPeople;
    }

    public String getNoticeMode() {
        return noticeMode;
    }

    public void setNoticeMode(String noticeMode) {
        this.noticeMode = noticeMode;
    }

    @Override
    public String toString() {
        return "Mettings{" +
                "meetingType='" + meetingType + '\'' +
                ", meetingName='" + meetingName + '\'' +
                ", meetingDescribe='" + meetingDescribe + '\'' +
                ", meetingLeaseReason='" + meetingLeaseReason + '\'' +
                ", meetingStartTime='" + meetingStartTime + '\'' +
                ", meetingEndTime='" + meetingEndTime + '\'' +
                ", meetingHost='" + meetingHost + '\'' +
                ", meetingOrganization='" + meetingOrganization + '\'' +
                ", meetingCreate='" + meetingCreate + '\'' +
                ", meetingPlace='" + meetingPlace + '\'' +
                ", participants='" + participants + '\'' +
                ", copyPeople='" + copyPeople + '\'' +
                ", noticeMode='" + noticeMode + '\'' +
                '}';
    }
}
