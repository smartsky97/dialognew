package com.pulan.dialogserver.entity.resp;

/**
 * 薪资返回视图
 */
public class Salary_info {
    private String uuid;
    private String mail_name;
    private String month;
    private double fixed_salary;
    private double bonus_base;
    private int achievements_nums;
    private double other_withdrawing;
    private double contribution_deductible;
    private double personal_social;
    private double company_social;

    public Salary_info() {
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

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public double getFixed_salary() {
        return fixed_salary;
    }

    public void setFixed_salary(double fixed_salary) {
        this.fixed_salary = fixed_salary;
    }

    public double getBonus_base() {
        return bonus_base;
    }

    public void setBonus_base(double bonus_base) {
        this.bonus_base = bonus_base;
    }

    public int getAchievements_nums() {
        return achievements_nums;
    }

    public void setAchievements_nums(int achievements_nums) {
        this.achievements_nums = achievements_nums;
    }

    public double getOther_withdrawing() {
        return other_withdrawing;
    }

    public void setOther_withdrawing(double other_withdrawing) {
        this.other_withdrawing = other_withdrawing;
    }

    public double getContribution_deductible() {
        return contribution_deductible;
    }

    public void setContribution_deductible(double contribution_deductible) {
        this.contribution_deductible = contribution_deductible;
    }

    public double getPersonal_social() {
        return personal_social;
    }

    public void setPersonal_social(double personal_social) {
        this.personal_social = personal_social;
    }

    public double getCompany_social() {
        return company_social;
    }

    public void setCompany_social(double company_social) {
        this.company_social = company_social;
    }
}
