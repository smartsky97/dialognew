package com.pulan.dialogserver.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.pulan.dialogserver.entity.resp.ReturnMsg;
import com.pulan.dialogserver.service.IMenuService;
import com.pulan.dialogserver.shiro.entity.User;
import com.pulan.dialogserver.utils.RedisClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@PropertySource(value = {"classpath:application.properties"},encoding="utf-8")
public class MenuServiceImpl implements IMenuService {

    @Autowired
    private RedisClient redisClient;


    @Value("${attendance_key}")
    private String attendance; //考勤
    @Value("${meetting_key}")
    private String meetting_key; //会议
    @Value("${notify_key}")
    private String notify_key; //待阅
    @Value("${performacne_key}")
    private String perform_key; //饱和度
    @Value("${task_key}")
    private String task_key; //待办任务
    @Value("${schedule_key}")
    private String schedule_key; //日程安排
    @Value("${review_key}")
    private String review_key; //待审批流程
    @Value("${todo_key}")
    private String todo_key; //待办

    @Override
    public ReturnMsg getRedisData(int type,HttpServletRequest request) {
        ReturnMsg returnMsg = new ReturnMsg();
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        JSONObject result = new JSONObject();
        HttpSession session = request.getSession(false);
        String types = "";
        //Session过期
        if(session==null){
            returnMsg.setStatus(-1);
            returnMsg.setType("session");
            result.put("error", "session过期");
            returnMsg.setResp(result);
            return returnMsg;
        }
        User user = (User)session.getAttribute("user");
        String username = user.getEmail();
        Date date = new Date();
        String time = df.format(date); //今天
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, -1);
        String time2 =df.format(calendar.getTime()); //前一天
        String key;
        switch (type){
            case 1:
                types = "attend";
                key = attendance+username+":"+time2+"*"; //考勤
                break;
            case 2:
                types = "saturation";
                key = perform_key+username+":"+time2+"*"; //饱和度
                break;
            case 3:
                types = "todo";
                key = task_key+username+":"+time+"*"; //待办
                break;
            case 4:
                types = "meeting";
                key = meetting_key+username+":"+time+"*"; //会议
                break;
            case 5:
                types = "schedule";
                key = schedule_key+username+":"+time+"*"; //日程
                break;
            case 6:
                types = "toread";
                key = review_key+username+":"+time+"*"; //待阅
                break;
            case 7:
                types = "approver";
                key = notify_key+username+":"+time+"*"; //审批
                break;
            case 8:
                key = todo_key+username +":"+time+"*"; //待办：需要我处理的工作
                break;
            default:
                types = "other";
                key =username+":"+time+"*";
        }
        try {
            System.out.println("数据key："+key);
            Set<String> setrest = redisClient.muhuKey(2, key);
            if (setrest ==null ||setrest.size()==0){
                returnMsg.setResp("抱歉，查无数据!");
                returnMsg.setType("text");
                returnMsg.setStatus(-1);
                //result.put("resp","抱歉，查无数据!");
                //result.put("type","text");
            }else {
                List<String> list =new ArrayList<>();
                for (String str:setrest){
                    String lst =redisClient.get(2,str);
                    list.add(lst);
                }
                int size =list.size();
                returnMsg.setResp(size ==1? list.get(0):list);
                returnMsg.setType(types);
                //result.put("resp",size ==1? list.get(0):list);
                //result.put("type",size ==1?"text":"list");
            }
        } catch (Exception e) {
            returnMsg.setResp("出现异常");
            returnMsg.setType("error");
            returnMsg.setStatus(-1);
            return returnMsg;
        }
        //returnMsg.setResp(result);
        //returnMsg.setType(types);
        return returnMsg;
    }
}
