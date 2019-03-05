package com.pulan.dialogserver.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pulan.dialogserver.entity.resp.*;
import com.pulan.dialogserver.service.IMenuService;
import com.pulan.dialogserver.shiro.entity.User;
import com.pulan.dialogserver.utils.JdbcMysql_78;
import com.pulan.dialogserver.utils.KdniaoTrackQueryAPI;
import com.pulan.dialogserver.utils.RedisClient;
import org.apache.ibatis.annotations.Param;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.Date;


@RestController // (直接返回数据而不是视图)
@RequestMapping(value = "/message")
public class MenuController {

    private Logger logger = LogManager.getLogger(MenuController.class);
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyyMM");
    private static SimpleDateFormat simpleDateFormat3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    @Autowired
    private IMenuService menuService;


    @Autowired
    private JdbcMysql_78 jdbcMysql_78;


    @Autowired
    private RedisClient redisClient;

    /**
     * 考勤
     *
     * @param request
     * @return
     */

    @RequestMapping(value = "/getAttendance")
    public Object getAttendances(HttpServletRequest request, @RequestParam(required = false, value = "data") String data) {
        ReturnMsg returnMsg = new ReturnMsg();
        try {
            returnMsg.setType("attendance");
            HttpSession session = request.getSession(false);
            if (session != null) {
                User user = (User) session.getAttribute("user");

                AttendanceMsg attendanceMsg = jdbcMysql_78.getAttendanceMsg(user.getMail_name(), data);
                if (attendanceMsg != null) {
                    returnMsg.setResp(JSON.toJSON(attendanceMsg));
                } else {
                    returnMsg.setResp("查无数据");
                }
                logger.info("查询考勤列表成功. userName:" + user.getMail_name() + " date:" + data);
            } else {
                returnMsg.setStatus(-1);
                returnMsg.setResp("session过期");
            }
            return returnMsg;
        } catch (Exception e) {
            returnMsg.setStatus(-1);
            returnMsg.setType("attendance");
            returnMsg.setResp("查询出错");
            logger.error("查询考勤异常. date:" + data);
            return returnMsg;
        }

    }

    /**
     * 工作饱和度
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/saturation")
    public ReturnMsg saturations(HttpServletRequest request, @RequestParam(required = false, value = "data") String data) {
        ReturnMsg returnMsg = new ReturnMsg();
        try {
            returnMsg.setType("saturation");
            HttpSession session = request.getSession(false);
            if (session != null) {
                User user = (User) session.getAttribute("user");

                SaturationMsg saturationMsg = jdbcMysql_78.getSaturationMsg(user.getMail_name(), data);
                if (saturationMsg != null) {
                    returnMsg.setResp(JSON.toJSON(saturationMsg));
                } else {
                    returnMsg.setResp("查无数据");
                }
                logger.info("查询饱和度成功. userName:" + user.getMail_name() + " date:" + data);
            } else {
                returnMsg.setStatus(-1);
                returnMsg.setResp("session过期");
            }
            return returnMsg;
        } catch (Exception e) {
            returnMsg.setStatus(-1);
            returnMsg.setType("saturation");
            returnMsg.setResp("查询饱和度出错");
            logger.error("查询饱和度明细异常. date:" + data, e);
            return returnMsg;
        }
    }


    /**
     * 待办
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/task", method = RequestMethod.POST)
    public ReturnMsg task(HttpServletRequest request, @RequestParam(required = false, value = "date") String date) {
        ReturnMsg returnMsg = new ReturnMsg();
        returnMsg.setType("todo");
        try {
            HttpSession session = request.getSession(false);
            if (session == null) {
                returnMsg.setStatus(-1);
                returnMsg.setResp("session已过期");
                return returnMsg;
            } else {
                User user = (User) session.getAttribute("user");
                returnMsg.setResp(jdbcMysql_78.queryTodoList(user.getMail_name()));
                returnMsg.setStatus(0);
                logger.info("查询待办列表成功. userName:" + user.getMail_name() + " date:" + date);
            }
        } catch (Exception e) {
            returnMsg.setStatus(-1);
            returnMsg.setResp("查询待办列表异常");
            logger.error("查询待办列表异常.date:" + date, e);
        }
        return returnMsg;
    }



	/**
	 * 待办事项审批流程节点
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/taskapproval", method = RequestMethod.POST)
	public ReturnMsg  taskapproval( HttpServletRequest request,@RequestParam(required = false,value = "uuid")String uuid){
        ReturnMsg returnMsg = new ReturnMsg();
        returnMsg.setType("taskapproval");
        try {
            HttpSession session = request.getSession(false);
            if (session == null) {
                returnMsg.setStatus(-1);
                returnMsg.setResp("session已过期");
                return returnMsg;
            } else {
                User user = (User) session.getAttribute("user");
                returnMsg.setResp(jdbcMysql_78.queryTodoNodeInfoList(uuid, user.getMail_name()));
                returnMsg.setStatus(0);
                logger.info("查询待办流程成功.uuid:" + uuid + " userName:" + user.getMail_name());
            }
        } catch (Exception e) {
            returnMsg.setStatus(-1);
            returnMsg.setResp("查询待办流程异常");
            logger.error("查询待办流程异常", e);
        }
        return returnMsg;
    }

    /**
     * 会议
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/meeting", method = RequestMethod.POST)
    public ReturnMsg meetings(HttpServletRequest request, @RequestParam(required = false, value = "data") String data) {
        ReturnMsg returnMsg = new ReturnMsg();
        returnMsg.setType("meeting");
        try {
            HttpSession session = request.getSession(false);
            if (session == null) {
                returnMsg.setStatus(-1);
                returnMsg.setResp("session已过期");
                return returnMsg;
            } else {
                User user = (User) session.getAttribute("user");
                returnMsg.setResp(jdbcMysql_78.getMeeting(user.getMail_name(), data));
                logger.info("查询会议列表成功.data:" + data + " userName:" + user.getMail_name());
                return returnMsg;
            }
        } catch (Exception e) {
            returnMsg.setStatus(-1);
            returnMsg.setType("meeting");
            returnMsg.setResp("查询会议列表异常");
            logger.error("查询会议列表异常.data:" + data, e);
            return returnMsg;
        }
    }

    /**
     * 日程
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/schedule", method = RequestMethod.POST)
    public ReturnMsg schedules(HttpServletRequest request, @RequestParam(required = false, value = "data") String data) {
        ReturnMsg returnMsg = new ReturnMsg();
        returnMsg.setType("schedule");
        try {
            HttpSession session = request.getSession(false);
            if (session == null) {
                returnMsg.setStatus(-1);
                returnMsg.setResp("session已过期");
                return returnMsg;
            } else {
                User user = (User) session.getAttribute("user");
                returnMsg.setResp(jdbcMysql_78.getCalendar(user.getMail_name(), data));
                logger.info("查询日程列表成功.user:" + user.getMail_name() + " date:" + data);
                return returnMsg;
            }
        } catch (Exception e) {
            returnMsg.setStatus(-1);
            returnMsg.setType("schedule");
            returnMsg.setResp("出现异常");
            logger.error("查询日程列表失败. date:" + data, e);
            return returnMsg;
        }

    }

    /**
     * 待阅
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/review", method = RequestMethod.POST)
    public ReturnMsg review(HttpServletRequest request) {
        ReturnMsg returnMsg = new ReturnMsg();
        returnMsg.setType("toRead");
        try {
            HttpSession session = request.getSession(false);
            if (session == null) {
                returnMsg.setStatus(-1);
                returnMsg.setResp("session已过期");
            } else {
                User user = (User) session.getAttribute("user");
                returnMsg.setResp(jdbcMysql_78.queryReview(user.getMail_name()));
                returnMsg.setStatus(0);
                logger.info("查询待阅列表成功.userName:" + user.getMail_name());
            }
        } catch (Exception e) {
            returnMsg.setStatus(-1);
            returnMsg.setResp("待阅查询出现异常");
            logger.error("待阅查询出现异常", e);
        }
        return returnMsg;
    }

    /**
     * 待阅详细信息
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/reviewDetail", method = RequestMethod.POST)
    public ReturnMsg reviewDetail(HttpServletRequest request, @RequestParam(value = "uuid") String uuid) {
        ReturnMsg returnMsg = new ReturnMsg();
        returnMsg.setType("toReadDetail");
        returnMsg.setStatus(0);
        try {
            returnMsg.setResp(jdbcMysql_78.queryReviewMsgById(uuid));
            logger.info("查询待阅明细成功.uuid:" + uuid);
        } catch (Exception e) {
            returnMsg.setStatus(-1);
            returnMsg.setResp("待阅详情查询异常.");
            logger.error("查询待阅明细异常.uuid:" + uuid, e);
        }
        return returnMsg;
    }


    /**
     * 审批
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/approvalprogress", method = RequestMethod.POST)
    public ReturnMsg approvalprogress(HttpServletRequest request) {
        ReturnMsg returnMsg = new ReturnMsg();
        returnMsg.setType("approvalprogress");
        try {
            HttpSession session = request.getSession(false);
            if (session == null) {
                returnMsg.setStatus(-1);
                returnMsg.setResp("session已过期");
                return returnMsg;
            } else {
                User user = (User) session.getAttribute("user");
                returnMsg.setResp(jdbcMysql_78.queryApprovalList(user.getMail_name()));
                returnMsg.setStatus(0);
                logger.info("查询审批列表成功.user:" + user.getMail_name());
            }
        } catch (Exception e) {
            returnMsg.setStatus(-1);
            returnMsg.setResp("查询审批列表异常");
            logger.error("查询审批列表异常", e);
        }
        return returnMsg;
    }

    /**
     * 审批流程详细信息。
     * 和待办的流程信息一样。
     * @param request
     * @return
     */
    @RequestMapping(value = "/approvalDetail", method = RequestMethod.POST)
    public ReturnMsg approvalDetail(HttpServletRequest request, @RequestParam(value = "id") String id) {
        ReturnMsg returnMsg = new ReturnMsg();
        returnMsg.setType("taskapproval");
        try {
            HttpSession session = request.getSession(false);
            if (session == null) {
                returnMsg.setStatus(-1);
                returnMsg.setResp("session已过期");
                return returnMsg;
            } else {
                User user = (User) session.getAttribute("user");
                // 审批和待办的明细是一样的，共用待办的方法
                returnMsg.setResp(jdbcMysql_78.queryTodoNodeInfoList(id, user.getMail_name()));
                returnMsg.setStatus(0);
                logger.info("查询审批流程成功.user:" + user.getMail_name() + " id:" + id);
            }
        } catch (Exception e) {
            returnMsg.setStatus(-1);
            returnMsg.setResp("查询审批流程异常");
            logger.error("查询审批流程异常.id:"+ id, e);
        }
        return returnMsg;
    }

    /**
     * 我的考勤--- 按天
     * @param request
     * @return
     */
    @RequestMapping(value = "/getAttendanceMsgByDay", method = RequestMethod.GET)
    public ReturnMsg getAttendanceMsgByDay(HttpServletRequest request, @RequestParam(value = "data") String data) {
        ReturnMsg returnMsg = new ReturnMsg();
        try {
            returnMsg.setType("attendance");
            HttpSession session = request.getSession(false);
            if (session != null) {
                User user = (User) session.getAttribute("user");

                AttendanceDayMsg attendanceDayMsg = jdbcMysql_78.getAttendanceMsgByDay(user.getMail_name(), data);
                if (attendanceDayMsg != null) {
                    returnMsg.setResp(JSON.toJSON(attendanceDayMsg));
                } else {
                    returnMsg.setResp("查无数据");
                }
                logger.info("查询每日考勤成功. userName:" + user.getMail_name() + " date:" + data);
            } else {
                returnMsg.setStatus(-1);
                returnMsg.setResp("session过期");
            }
            return returnMsg;
        } catch (Exception e) {
            returnMsg.setStatus(-1);
            returnMsg.setType("attendance");
            returnMsg.setResp("查询出错");
            logger.error("查询考勤异常. date:" + data);
            return returnMsg;
        }
    }


    /**
     * 日程-按天查询
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/getScheduleByDay", method = RequestMethod.POST)
    public ReturnMsg getschedulesList(HttpServletRequest request, @RequestParam(required = false, value = "data") String data) {
        ReturnMsg returnMsg = new ReturnMsg();
        returnMsg.setType("schedule");
        try {
            HttpSession session = request.getSession(false);
            if (session == null) {
                returnMsg.setStatus(-1);
                returnMsg.setResp("session已过期");
                return returnMsg;
            } else {
                User user = (User) session.getAttribute("user");
                returnMsg.setResp(jdbcMysql_78.getCalendarByDay(user.getMail_name(), data));
                logger.info("查询日程列表成功.user:" + user.getMail_name() + " date:" + data);
                return returnMsg;
            }
        } catch (Exception e) {
            returnMsg.setStatus(-1);
            returnMsg.setType("schedule");
            returnMsg.setResp("出现异常");
            logger.error("查询日程列表失败. date:" + data, e);
            return returnMsg;
        }

    }

    /**
     * 日程-按UUID 查询详情
     *saturation
     * @param request
     * @return
     */
    @RequestMapping(value = "/getScheduleByUuid", method = RequestMethod.POST)
    public ReturnMsg getScheduleByUuid(HttpServletRequest request, @RequestParam(required = false, value = "uuid") String uuid) {
        ReturnMsg returnMsg = new ReturnMsg();
        returnMsg.setType("schedule");
        try {
            HttpSession session = request.getSession(false);
            if (session == null) {
                returnMsg.setStatus(-1);
                returnMsg.setResp("session已过期");
                return returnMsg;
            } else {
                User user = (User) session.getAttribute("user");
                returnMsg.setResp(jdbcMysql_78.getCalendarByUuid(user.getMail_name(), uuid));
                logger.info("查询日程列表成功.user:" + user.getMail_name() + " uuid:" + uuid);
                return returnMsg;
            }
        } catch (Exception e) {
            returnMsg.setStatus(-1);
            returnMsg.setType("schedule");
            returnMsg.setResp("出现异常");
            logger.error("查询日程列表失败. date:" + uuid, e);
            return returnMsg;
        }
    }

    //获取快递信息
    @RequestMapping(value = "/getExpress")
    public JSON getExpress(@Param("expCode") String expCode,@Param("expNo") String expNo) throws Exception {
        KdniaoTrackQueryAPI kdniaoTrackQueryAPI = KdniaoTrackQueryAPI.getInstance();
        String val = kdniaoTrackQueryAPI.getOrderTracesByJson(kdniaoTrackQueryAPI.getCompany(expCode),expNo);
        JSON json = JSON.parseObject(val);
        ((JSONObject) json).put("type","express");
        logger.info("快递信息;"+json);
        return json;
    }
    @Autowired
    JavaMailSender mailSender;
    @RequestMapping(value = "/sendMail")
    public boolean sendMail(HttpServletRequest request,@Param("to") String to,@Param("cc") String cc,@Param("title") String title,@Param("text") String text) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("121670142@qq.com");
        String[] toto = to.split(";");
        message.setTo(toto);
        if (!StringUtils.isEmpty(cc)) {
            String[] cccc = cc.split(";");
            message.setCc(cccc);
        }
        message.setSubject(title);
        message.setText(text);

        mailSender.send(message);
        return true;
    }

    /**
     * 手工添加日程安排
     */
    @RequestMapping(value = "/addCalendarPlanNewadd", method = RequestMethod.POST)
    public ReturnMsg addCalendarPlanNewadd (HttpServletRequest request,@RequestBody CalendarMsg calendarMsg) {
        ReturnMsg returnMsg = new ReturnMsg();
        returnMsg.setType("schedule");
        HttpSession session = request.getSession(false);
        if (session == null) {
            returnMsg.setStatus(-1);
            returnMsg.setResp("session已过期");
            return returnMsg;
        } else {
            if (StringUtils.isEmpty(calendarMsg.getMail_name())
                    || StringUtils.isEmpty(calendarMsg.getDoc_start_time()) || StringUtils.isEmpty(calendarMsg.getDoc_finish_time())
                    || StringUtils.isEmpty(calendarMsg.getDoc_subject())) {

            } else {
                if (calendarMsg.getDoc_start_time().length()<19) {
                    calendarMsg.setDoc_start_time(calendarMsg.getDoc_start_time()+":00");
                }
                if (calendarMsg.getDoc_finish_time().length()<19) {
                    calendarMsg.setDoc_finish_time(calendarMsg.getDoc_finish_time()+":00");
                }
                calendarMsg.setDoc_create_time(simpleDateFormat3.format(new Date()));
                calendarMsg.setFd_status("未开始");
                returnMsg.setResp("添加成功");
                jdbcMysql_78.addCalendarPlanNewadd(calendarMsg);
                return returnMsg;
            }
            returnMsg.setResp("添加失败");
            return returnMsg;
        }
    }
}
