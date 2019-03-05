package com.pulan.dialogserver.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pulan.dialogserver.entity.resp.ReturnMsg;
import com.pulan.dialogserver.entity.two.EmailInfo;
import com.pulan.dialogserver.entity.two.Mettings;
import com.pulan.dialogserver.utils.JdbcMysql_78;
import org.apache.commons.collections.map.HashedMap;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/message")
public class AppTwophaseController {
    private Logger logger = LogManager.getLogger(AppTwophaseController.class);


    @Autowired
    private JdbcMysql_78 jdbcMysql_78;
    /**
     * 获取会议地点
     * @param
     * @return
     */
    @RequestMapping(value = "/getMeetingPlace")
    public Object getMeetingPlace(@RequestParam(required = false, value = "startTime") String startTime, @RequestParam(required = false,value ="endTime") String endTime) {
        ReturnMsg returnMsg = new ReturnMsg();
        logger.info(startTime + " - " + endTime);
        try {
            List<Object> list = new ArrayList<>();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id",1);
            jsonObject.put("meetingName","真会议室");
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("id",2);
            jsonObject1.put("meetingName","小会议室");
            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("id",3);
            jsonObject2.put("meetingName","喜会议室");
            JSONObject jsonObject3 = new JSONObject();
            jsonObject3.put("id",4);
            jsonObject3.put("meetingName","和会议室");

            list.add(jsonObject);
            list.add(jsonObject1);
            list.add(jsonObject2);
            list.add(jsonObject3);
            returnMsg.setStatus(0);
            returnMsg.setResp(list);
            returnMsg.setType("meetingPlace");
        }catch (Exception e){
            returnMsg.setStatus(-1);
            returnMsg.setResp("获取会议室出错");
            returnMsg.setType("meetingPlace");
            return returnMsg;
        }
       return returnMsg;
    }

    /**
     * 获取会议类型
     * @param
     * @return
     */
    @RequestMapping(value = "/getMeetingType")
    public Object getMeetingType() {
        ReturnMsg returnMsg = new ReturnMsg();
        try {
            List<String> list = new ArrayList<>();
            list.add("集团级别会议");
            list.add("公司级别会议");
            list.add("部门级别会议");
            list.add("板块级别会议");
            list.add("其他会议");
            returnMsg.setStatus(0);
            returnMsg.setResp(list);
            returnMsg.setType("meetingType");
        }catch (Exception e){
            returnMsg.setStatus(-1);
            returnMsg.setResp("获取会类型出错");
            returnMsg.setType("meetingType");
            return returnMsg;
        }
        return returnMsg;
    }



    /**
     * 获取公司相关部门人员
     * @param
     * @return
     */
    @RequestMapping(value = "/getDepartmentPersonnel")
    public Object getDepartmentPersonnel() {
        ReturnMsg returnMsg = new ReturnMsg();
        try {
            returnMsg.setStatus(0);
            returnMsg.setResp(jdbcMysql_78.getDepartmentPersonnel());
            returnMsg.setType("DepartmentPersonnel");
        }catch (Exception e){
            returnMsg.setStatus(-1);
            returnMsg.setResp("获取部门员工出错");
            returnMsg.setType("DepartmentPersonnel");
            return returnMsg;
        }
        return returnMsg;
    }

    /**
     * 预约会议室
     * @param
     * @return
     */
    @RequestMapping(value = "/makeMeeting")
    public Object makeMeeting(@RequestBody String msgBody) {
        ReturnMsg returnMsg = new ReturnMsg();
        try {
            logger.info(msgBody);
            Mettings mettings = JSONObject.parseObject(msgBody,Mettings.class);
            logger.info(mettings);
            returnMsg.setStatus(0);
            returnMsg.setResp("预约成功");
            returnMsg.setType("makeMeeting");
        }catch (Exception e){
            returnMsg.setStatus(-1);
            returnMsg.setResp("预约会议室出错");
            returnMsg.setType("makeMeeting");
            return returnMsg;
        }
        return returnMsg;
    }

    /**
     * 发邮件
     * @param
     * @return
     */
    @RequestMapping(value = "/sendEmail")
    public Object sendEmail(@RequestBody String msgBody) {
        ReturnMsg returnMsg = new ReturnMsg();
        try {
            logger.info(msgBody);
            EmailInfo emailInfo = JSONObject.parseObject(msgBody,EmailInfo.class);
            logger.info(emailInfo);
            returnMsg.setStatus(0);
            returnMsg.setResp("发送成功");
            returnMsg.setType("sendEmail");
        }catch (Exception e){
            returnMsg.setStatus(-1);
            returnMsg.setResp("邮件发送出错");
            returnMsg.setType("sendEmail");
            return returnMsg;
        }
        return returnMsg;
    }
}
