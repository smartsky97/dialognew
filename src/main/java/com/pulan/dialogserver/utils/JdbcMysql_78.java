package com.pulan.dialogserver.utils;

import com.alibaba.fastjson.JSONObject;
import com.pulan.dialogserver.entity.resp.*;
import com.pulan.dialogserver.entity.two.DeptPerson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
public class JdbcMysql_78 {
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");

    private static  SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM-dd");;

    @Autowired
    @Qualifier("mysqlJdbcTemplate_78")
    private JdbcTemplate jdbcTemplate;


    /**
     * 我的会议
     *
     * @param mail_name
     * @return
     */
    public List<MeetingInfo> getMeeting(String mail_name, String date) {
        if (date == null || date == "") {
            date = simpleDateFormat.format(new Date());
        }
        String dateval = "%Y-%m";
        if (date != null && date.length()>7) {
            dateval = "%Y-%m-%d";
        }
        String sql = "SELECT mc.*,ma.mail_name,ma.meeting_title,ma.meeting_time,ma.meeting_place \n" +
                "FROM `meeting_attend` ma,`meeting_info` mc\n" +
                "WHERE ma.fd_meeting_id = mc.uuid\n" +
                "AND  mail_name = ?\n" +
                "AND  DATE_FORMAT(meeting_date,'"+dateval+"') = ?  order by meeting_time desc\n";
        List<MeetingInfo> list = new ArrayList<>();
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql, mail_name, date);
        MeetingInfo meetingInfo = null;
        while (rs.next()) {
            meetingInfo = new MeetingInfo();
            meetingInfo.setUuid(rs.getString(1));
            meetingInfo.setMeeting_date(rs.getString(2));
            meetingInfo.setStart_time(rs.getString(3));
            meetingInfo.setEnd_time(rs.getString(4));
            meetingInfo.setFd_subject(rs.getString(5));
            meetingInfo.setDoc_content(rs.getString(6));
            meetingInfo.setDoc_create_time(rs.getString(7));
            meetingInfo.setDoc_create_person(rs.getString(8));
            meetingInfo.setDoc_emcee_person(rs.getString(9));
            meetingInfo.setFd_host_person(rs.getString(10));
            meetingInfo.setDoc_dept(rs.getString(11));
            meetingInfo.setMeeting_type(rs.getString(12));
            meetingInfo.setMeetingres_place(rs.getString(13));
            meetingInfo.setMeeting_copy_person(rs.getString(14));
            meetingInfo.setMeeting_attend_person(rs.getString(15));
            meetingInfo.setMail_name(rs.getString(16));
            list.add(meetingInfo);
        }
        return list;
    }


    /**
     * 我的日程
     *
     * @param mail_name
     * @return
     */
    public List<CalendarMsg> getCalendar(String mail_name, String date) {
        if (date == null || date == "") {
            date = simpleDateFormat.format(new Date());
        }
        String sql ="SELECT cp.*,cpi.`calendar_date`,cpi.`mail_name` FROM `calendar_plan` cp\n" +
                "INNER JOIN `calendar_plan_index` cpi\n" +
                "ON cp.uuid = cpi.`calendar_plan_id`\n" +
                "WHERE cpi.`mail_name`=?\n" +
                "AND DATE_FORMAT(cpi.calendar_date,'%Y-%m') = ?" +
                " UNION ALL "+
                " SELECT " +
                "'' uuid," +
                "doc_create_time," +
                "doc_subject," +
                "doc_start_time," +
                "doc_finish_time," +
                "fd_location," +
                "fd_status," +
                "DATE_FORMAT( doc_finish_time, '%Y-%m-%d' ) calendar_date," +
                "mail_name " +
                "FROM " +
                " calendar_plan_newadd " +
                "WHERE " +
                "mail_name = ?  " +
                "AND DATE_FORMAT( doc_finish_time, '%Y-%m' ) = ? order by calendar_date";

        List<CalendarMsg> list = new ArrayList<>();
        System.out.println(sql+" "+mail_name+" "+date);
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql, mail_name, date, mail_name, date);
        CalendarMsg calendarMsg = null;
        while (rs.next()) {
            calendarMsg = new CalendarMsg();
            calendarMsg.setUuid(rs.getString(1));
            calendarMsg.setDoc_create_time(rs.getString(2));
            calendarMsg.setDoc_subject(rs.getString(3));
            calendarMsg.setDoc_start_time(rs.getString(4));
            calendarMsg.setDoc_finish_time(rs.getString(5));
            calendarMsg.setFd_location(rs.getString(6));
            calendarMsg.setFd_status(rs.getString(7));
            calendarMsg.setCalendar_date(rs.getString(8));
            calendarMsg.setMail_name(rs.getString(9));
            list.add(calendarMsg);
        }

        return list;
    }


    /**
     * 获取考勤结果（日期）
     *
     * @param mail_name
     * @param date
     * @return
     */
    public List<Object> getAttendanceResult(String mail_name, String date) {
        List<Object> list = new ArrayList<>();
        if (date == null || date == "") {
            date = simpleDateFormat.format(new Date());
        }
        String sql = "SELECT * FROM kaoqin_result \n" +
                "WHERE mail_name = ?\n" +
                " AND  DATE_FORMAT(kaoqin_date,'%Y-%m') = ?";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql, mail_name, date);

        JSONObject jsonObject = null;
        int i = 1;
        while (rs.next()) {
            jsonObject = new JSONObject();
            jsonObject.put("key",i);
            jsonObject.put("value",rs.getString(3));
            list.add(jsonObject);
            i++;
        }

        return list;
    }


    /**
     * 待阅列表
     *
     * @param mail_name
     * @return
     */
    public List<ReviewMsg> queryReview(String mail_name,String ... date) {
        String timepar = "";
        if (date.length>0) {
            timepar = " and DATE_FORMAT(doc_create_time,'%Y-%m-%d')='"+date[0]+"' ";
        }
        String sql = "SELECT uuid,au.cn_name mail_name,doc_create_time,fd_type,fd_subject,fd_status FROM to_be_read br LEFT JOIN " +
                "ai_user au ON br.mail_name = au.mail_name WHERE fd_status = '0' AND br.mail_name = ?  " + timepar +
                "order by doc_create_time desc";
        List<ReviewMsg> list = new ArrayList<>();
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql, mail_name);
        ReviewMsg reviewMsg = null;
        while (rs.next()) {
            reviewMsg = new ReviewMsg();
            reviewMsg.setUuid(rs.getString(1));
            reviewMsg.setMail_name(rs.getString(2));
            reviewMsg.setDoc_create_time(rs.getString(3));
            reviewMsg.setFd_type(rs.getString(4));
            reviewMsg.setFd_subject(rs.getString(5));
            reviewMsg.setFd_status(rs.getString(6));
            list.add(reviewMsg);
        }

        return list;
    }

    /**
     * 通过uuid 查询待阅详细信息。
     *
     * @param uuid
     * @return
     */
    public ReviewMsg queryReviewMsgById(String uuid) {
        String sql = "SELECT uuid,au.cn_name mail_name,doc_create_time,fd_type,fd_subject,fd_status FROM to_be_read br LEFT JOIN " +
                "  ai_user au ON br.mail_name = au.mail_name WHERE uuid = ? ";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql, uuid);
        ReviewMsg reviewMsg = null;
        while (rs.next()) {
            reviewMsg = new ReviewMsg();
            reviewMsg.setUuid(rs.getString(1));
            reviewMsg.setMail_name(rs.getString(2));
            reviewMsg.setDoc_create_time(rs.getString(3));
            reviewMsg.setFd_type(rs.getString(4));
            reviewMsg.setFd_subject(rs.getString(5));
            reviewMsg.setFd_status(rs.getString(6));
        }
        return reviewMsg;
    }

    /**
     * 待办列表（我处理的）
     * @param mail_name
     * @return
     */
    public List<TodoInfo> queryTodoList(String mail_name,String ... date){
        String timepar = "";
        if (date.length>0) {
            timepar = " and DATE_FORMAT(doc_create_time,'%Y-%m-%d')='"+date[0]+"' ";
        }

        String sql = "SELECT uuid,todo_type,fd_status,doc_create_time,fd_create_person_name,doc_subject FROM todo_info a LEFT JOIN " +
                "current_todo_node b ON a.uuid = b.fd_id LEFT JOIN ai_user iu ON a.fd_create_person = iu.mail_name WHERE a.fd_status = '20' AND b.mail_name = ? " + timepar +
                "order by doc_create_time desc";
        List<TodoInfo> list = new ArrayList<>();
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql, mail_name);
        TodoInfo todoInfo = null;
        while (rs.next()) {
            todoInfo = new TodoInfo();
            todoInfo.setUuid(rs.getString(1));
            todoInfo.setTodo_type(rs.getString(2));
            todoInfo.setFd_status(rs.getString(3));
            todoInfo.setDoc_create_time(rs.getString(4));
            todoInfo.setFd_create_person(rs.getString(5));
            todoInfo.setDoc_subject(rs.getString(6));
            list.add(todoInfo);
        }
        return list;
    }


    /**
     * 查询审批列表,（我创建的）
     * @param mail_name
     * @return
     */
    public List<TodoInfo> queryApprovalList(String mail_name){

        String sql = "SELECT uuid,todo_type,fd_status,doc_create_time, iu.cn_name fd_create_person, doc_subject  FROM todo_info ti LEFT JOIN ai_user iu ON ti.fd_create_person = iu.mail_name WHERE fd_status = '20' " +
                " AND fd_create_person = ? ";

        List<TodoInfo> list = new ArrayList<>();
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql, mail_name);
        TodoInfo todoInfo = null;
        while (rs.next()) {
            todoInfo = new TodoInfo();
            todoInfo.setUuid(rs.getString(1));
            todoInfo.setTodo_type(rs.getString(2));
            todoInfo.setFd_status(rs.getString(3));
            todoInfo.setDoc_create_time(rs.getString(4));
            todoInfo.setFd_create_person(rs.getString(5));
            todoInfo.setDoc_subject(rs.getString(6));
            list.add(todoInfo);
        }
        return list;
    }


    /**
     * 查询待办流程审批历史
     * @param uuid
     * @return
     */
    public List<TodoNodeInfo> queryTodoNodeInfoList(String uuid,String mail_name){
        String sql = "SELECT distinct fd_id,fd_action_name,fd_action_info,fd_handler_cn_name,fd_handle_time,fd_from FROM ( " +
                "SELECT fd_id,fd_action_name,fd_action_info,fd_handler_cn_name,fd_handle_time,fd_from FROM history_todo_node " +
                "WHERE fd_id= ? ORDER BY fd_handle_time) as te " +
                "UNION ALL " +
                "SELECT fd_id,'待你审批' fd_action_name, '' fd_action_info,cn_name ,'' fd_handle_time, '' fd_from FROM current_todo_node " +
                "WHERE fd_id= ? AND mail_name = ?  ";

        List<TodoNodeInfo> list = new ArrayList<>();
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql, uuid,uuid,mail_name);
        TodoNodeInfo todoNodeInfo = null;
        while (rs.next()) {
            todoNodeInfo = new TodoNodeInfo();
            todoNodeInfo.setFd_id(rs.getString(1));
            todoNodeInfo.setFd_action_name(rs.getString(2));
            todoNodeInfo.setFd_action_info(rs.getString(3));
            todoNodeInfo.setFd_handler_cn_name(rs.getString(4));
            todoNodeInfo.setFd_handle_time(rs.getString(5));
            todoNodeInfo.setFd_from(rs.getString(6));
            list.add(todoNodeInfo);
        }
        return list;
    }


    /**
     * 我的考勤
     * @param mail_name
     * @param date
     * @returnd
     */
    public AttendanceMsg getAttendanceMsg(String mail_name, String date) {
        if (date == null || date == "") {
            date = simpleDateFormat.format(new Date());
        }
        String sql = "SELECT\n" +
                "mail_name , cn_name AS NAME,MONTH AS DATE,\n" +
                "normal_number_days normal_punching,mon_attendance_ratio attend_rate,\n" +
                "mon_late_num late_times,mon_leave_num  leave_times,mon_not_clock_num noPunch_times,\n" +
                "mon_overtime_houes overtimes,mon_attendance_time work_times\n" +
                " FROM kaoqin_month \n" +
                " WHERE mail_name = ?\n" +
                " AND `month` = ?";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql, mail_name, date);
        AttendanceMsg attendanceMsg = null;
        while (rs.next()) {
            attendanceMsg = new AttendanceMsg();
            attendanceMsg.setMail_name(rs.getString(1));
            attendanceMsg.setName(rs.getString(2));
            attendanceMsg.setDate(rs.getString(3));
            attendanceMsg.setNormal_punching(rs.getString(4));
            attendanceMsg.setAttend_rate(rs.getString(5) + "%");
            attendanceMsg.setLate_times(rs.getString(6));
            attendanceMsg.setLeave_times(rs.getString(7));
            attendanceMsg.setNoPunch_times(rs.getString(8));
            attendanceMsg.setOvertimes(rs.getString(9));
            attendanceMsg.setWork_times(rs.getString(10));
            return attendanceMsg;
        }

        return null;

    }
    /**
     * 我的考勤--- 按天
     * @param mail_name
     * @param date
     * @return
     */
    public AttendanceDayMsg getAttendanceMsgByDay(String mail_name, String date) {
        if (date == null || date == "") {
            date = simpleDateFormat2.format(new Date());
        }
        String sql = "SELECT * FROM `kaoqin_day` \n" +
                "WHERE mail_name = ?\n" +
                "AND `kaoqin_date` =?";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql, mail_name, date);
        AttendanceDayMsg attendanceDayMsg = null;
        while (rs.next()) {
            attendanceDayMsg = new AttendanceDayMsg();
            attendanceDayMsg.setMail_name(rs.getString(1));
            attendanceDayMsg.setCn_name(rs.getString(2));
            attendanceDayMsg.setKaoqin_date(rs.getString(3));
            attendanceDayMsg.setWork_time(rs.getDouble(4));
            attendanceDayMsg.setStart_time(rs.getString(5));
            attendanceDayMsg.setEnd_time(rs.getString(6));
            attendanceDayMsg.setIs_late(rs.getInt(7));
            attendanceDayMsg.setIs_early(rs.getInt(8));
            attendanceDayMsg.setIs_not_clock(rs.getInt(9));
            return attendanceDayMsg;
        }

        return null;

    }

    /**
     * 工作饱和度
     * @param mail_name
     * @param date
     * @return
     */
    public SaturationMsg getSaturationMsg(String mail_name, String date) throws Exception {
        String date1 = null;
        String date2 = null;
        if (null == date || "".equals(date)) {
            SimpleDateFormat simpleDateFormats = new SimpleDateFormat("yyyy-MM-dd");
            date1 = simpleDateFormats.format(new Date());
            date2 = simpleDateFormat.format(new Date());
        } else {
            date1 = date;
            if (date1.length()>7) {
                date2 = DateUtils.getStringForDateString(date,"yyyy-MM-dd","yyyy-MM");
            } else {
                date2 = date1;
            }
        }
        SqlRowSet rs = null;
        String sql1 = "SELECT * FROM `saturation_month` sm " +
                "WHERE sm.mail_name = ? " +
                "AND sm.month = ?";

        rs= jdbcTemplate.queryForRowSet(sql1, mail_name, date2);
        SaturationMsg saturationMsg  = new SaturationMsg();
        while (rs.next()) {
            saturationMsg.setMail_name(rs.getString(1));
            saturationMsg.setMonth(rs.getString(2));
            saturationMsg.setMon_kaoqin_days(rs.getInt(3));
            saturationMsg.setMon_meeting_times(rs.getDouble(4));
            saturationMsg.setMon_avg_saturation(rs.getDouble(6));
        }
        String sql2 = "  SELECT * FROM `saturation`" +
                "  WHERE mail_name = ?" +
                "  AND sat_date = ?";
        rs= jdbcTemplate.queryForRowSet(sql2, mail_name, date1);
        while (rs.next()) {
            saturationMsg.setSat_date(rs.getString(2));
            saturationMsg.setSaturation(rs.getDouble(3));
        }
        if(saturationMsg.getSat_date() == null){
            saturationMsg.setSat_date(date1);
        }
        return saturationMsg;
    }

    /**
     * 我的薪资
     * @param mail_name
     * @param date
     * @return
     */
    public  Salary_info getSalary_info(String mail_name, String date){
        if (date == null || date == "") {
            date = simpleDateFormat.format(new Date());
        }
        String sql ="SELECT \n" +
                "`uuid`,`mail_name`,`month`,`fixed_salary`,\n" +
                "`bonus_base`,`achievements_nums`,`other_withdrawing`,\n" +
                "`contribution_deductible`,`personal_social`,`company_social` \n" +
                "FROM\n" +
                "`huaxiaomi`.`salary_info`\n" +
                "WHERE mail_name =?\n" +
                "AND `month` =?";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql, mail_name, date);
        Salary_info salary_info =null;
        while (rs.next()){
            salary_info = new Salary_info();
            salary_info.setUuid(rs.getString(1));
            salary_info.setMail_name(rs.getString(2));
            salary_info.setMonth(rs.getString(3));
            salary_info.setFixed_salary(rs.getDouble(4));
            salary_info.setBonus_base(rs.getDouble(5));
            salary_info.setAchievements_nums(rs.getInt(6));
            salary_info.setOther_withdrawing(rs.getDouble(7));
            salary_info.setContribution_deductible(rs.getDouble(8));
            salary_info.setPersonal_social(rs.getDouble(9));
            salary_info.setCompany_social(rs.getDouble(10));
            return salary_info;
        }
        return null;
    }


    /**
     * 我的日程按天查询
     * @param mail_name
     * @param date
     * @return
     */
    public List<CalendarMsg> getCalendarByDay(String mail_name, String date) {
        if (date == null || date == "") {
            date = simpleDateFormat2.format(new Date());
        }
        String sql ="SELECT cp.*,cpi.`calendar_date`,cpi.`mail_name` FROM `calendar_plan` cp\n" +
                "INNER JOIN `calendar_plan_index` cpi\n" +
                "ON cp.uuid = cpi.`calendar_plan_id`\n" +
                "WHERE cpi.`mail_name`=?\n" +
                "AND cpi.calendar_date=?\n";
        List<CalendarMsg> list = new ArrayList<>();
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql, mail_name, date);
        CalendarMsg calendarMsg = null;
        while (rs.next()) {
            calendarMsg = new CalendarMsg();
            calendarMsg.setUuid(rs.getString(1));
            calendarMsg.setDoc_create_time(rs.getString(2));
            calendarMsg.setDoc_subject(rs.getString(3));
            calendarMsg.setDoc_start_time(rs.getString(4));
            calendarMsg.setDoc_finish_time(rs.getString(5));
            calendarMsg.setFd_location(rs.getString(6));
            calendarMsg.setFd_status(rs.getString(7));
            calendarMsg.setCalendar_date(rs.getString(8));
            calendarMsg.setMail_name(rs.getString(9));
            list.add(calendarMsg);
        }

        return list;
    }
    /**
     * 我的日程按ID 查询详情
     * @param mail_name
     * @param uuid
     * @return
     */
    public CalendarMsg getCalendarByUuid(String mail_name, String uuid) {

        String sql ="SELECT cp.*,cpi.`calendar_date`,cpi.`mail_name` FROM `calendar_plan` cp\n" +
                "INNER JOIN `calendar_plan_index` cpi\n" +
                "ON cp.uuid = cpi.`calendar_plan_id`\n" +
                "WHERE cpi.`mail_name`=?\n" +
                "AND  cp.`uuid`=?\n";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql, mail_name, uuid);
        CalendarMsg calendarMsg = null;
        while (rs.next()) {
            calendarMsg = new CalendarMsg();
            calendarMsg.setUuid(rs.getString(1));
            calendarMsg.setDoc_create_time(rs.getString(2));
            calendarMsg.setDoc_subject(rs.getString(3));
            calendarMsg.setDoc_start_time(rs.getString(4));
            calendarMsg.setDoc_finish_time(rs.getString(5));
            calendarMsg.setFd_location(rs.getString(6));
            calendarMsg.setFd_status(rs.getString(7));
            calendarMsg.setCalendar_date(rs.getString(8));
            calendarMsg.setMail_name(rs.getString(9));
        }

        return calendarMsg;
    }

    /**
     * 获取公司部门人员
     * @return
     */
    public List<DeptPerson> getDepartmentPersonnel() {

        String sql =" SELECT  id ,cn_name,department,mobile,email,mail_name FROM `ai_user`";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql);
        List<DeptPerson> list = new ArrayList<>();
        DeptPerson deptPerson = null;
        while (rs.next()) {
            deptPerson = new DeptPerson();
            deptPerson.setId(rs.getString(1));
            deptPerson.setCnName(rs.getString(2));
            deptPerson.setDepartment(rs.getString(3));
            deptPerson.setMobile(rs.getString(4));
            deptPerson.setEmail(rs.getString(5));
            deptPerson.setMailName(rs.getString(6));
            list.add(deptPerson);
        }

        return list;
    }

    public boolean addCalendarPlanNewadd(CalendarMsg calendarMsg) {
        String sql = "insert into calendar_plan_newadd (`mail_name`,`doc_create_time`,`doc_subject`,`doc_start_time`,`doc_finish_time`,`fd_location`,`fd_status`)" +
                " values (?,?,?,?,?,?,?)";
        int falg = jdbcTemplate.update(sql,new Object[] {calendarMsg.getMail_name(),calendarMsg.getDoc_create_time(),calendarMsg.getDoc_subject(),calendarMsg.getDoc_start_time(),
        calendarMsg.getDoc_finish_time(),calendarMsg.getFd_location(),calendarMsg.getFd_status()});
        return (falg == 0) ? false : true;
    }
}
