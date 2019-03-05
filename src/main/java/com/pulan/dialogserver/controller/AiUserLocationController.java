package com.pulan.dialogserver.controller;

import com.alibaba.fastjson.JSONObject;
import com.pulan.dialogserver.entity.AiUserLocation;
import com.pulan.dialogserver.service.ITuLingService;
import com.pulan.dialogserver.utils.JdbcUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/message")
public class AiUserLocationController {
    @Autowired
    private JdbcUtils jdbcUtils;


    @Autowired
    private ITuLingService iTuLingService;

    @RequestMapping(value = "/pushlocation")
    public Object uploadaiuserlocationinfo(@RequestBody String msgBody){
        JSONObject jsonObject = new JSONObject();
        int i =0;
        try {
            AiUserLocation aiUserLocation = JSONObject.parseObject(msgBody,AiUserLocation.class);
            i = jdbcUtils.saveAiUserLocation(aiUserLocation);
            if (i > 0){
                jsonObject.put("status",0);
                jsonObject.put("resp","上报成功");
            }else {
                jsonObject.put("status",-1);
                jsonObject.put("resp","上报失败");
            }
        }catch (Exception e){
            jsonObject.put("status",-1);
            jsonObject.put("resp","上报失败");
        }

        return jsonObject;
    }


//    @RequestMapping(value = "/testss")
//    public Object testss(@RequestParam("text") String text){
//
//
//        return iTuLingService.tuLintRobot(text,"1",new JSONObject());
//    }
}
