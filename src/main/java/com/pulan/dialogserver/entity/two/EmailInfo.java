package com.pulan.dialogserver.entity.two;

public class EmailInfo {
    private String recipients;
    private String copyPeople;
    private String subject;
    private String content;

    public EmailInfo() {
    }

    public EmailInfo(String recipients, String copyPeople, String subject, String content) {
        this.recipients = recipients;
        this.copyPeople = copyPeople;
        this.subject = subject;
        this.content = content;
    }

    public String getRecipients() {
        return recipients;
    }

    public void setRecipients(String recipients) {
        this.recipients = recipients;
    }

    public String getCopyPeople() {
        return copyPeople;
    }

    public void setCopyPeople(String copyPeople) {
        this.copyPeople = copyPeople;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "EmailInfo{" +
                "recipients='" + recipients + '\'' +
                ", copyPeople='" + copyPeople + '\'' +
                ", subject='" + subject + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
