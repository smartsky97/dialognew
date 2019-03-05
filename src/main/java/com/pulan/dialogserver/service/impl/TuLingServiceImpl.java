package com.pulan.dialogserver.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.pulan.dialogserver.api.RobotRequest;
import com.pulan.dialogserver.config.SysConfig;
import com.pulan.dialogserver.entity.AiUserLocation;
import com.pulan.dialogserver.entity.tulin.*;
import com.pulan.dialogserver.service.IFlyService;
import com.pulan.dialogserver.service.ITuLingService;
import com.pulan.dialogserver.utils.JdbcUtils;
import com.pulan.dialogserver.utils.MD5Utils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import sun.security.provider.MD5;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class TuLingServiceImpl implements ITuLingService {

    private Logger logger = LogManager.getLogger(TuLingServiceImpl.class);

    @Autowired
    private RobotRequest robotRequest;
    @Autowired
    private SysConfig sysConfig;
    @Autowired
    private JdbcUtils jdbcUtils;
    @Autowired
    private IFlyService iFlyService;

    @Override
    public JSONObject tuLintRobot(String content, String open_id, String longin_name, JSONObject result) {
        logger.info("TuLingMessage:" + content);

        First first = new First();
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(MD5Utils.md5(longin_name));
        userInfo.setApiKey(sysConfig.getRobbotApiKey());

        Perception perception = new Perception();
        InputText inputText = new InputText();
        inputText.setText(content);

        InputImage inputImage = new InputImage();
        SelfInfo selfInfo = new SelfInfo();
        Location location = new Location();
//
//        try {
//            //从数据库获取登录地址
//            Map<String, String> map = jdbcUtils.getAiUserLocation(longin_name);
//            if(map == null){
//
//            } else if (map.size() > 0) {
//                location.setCity(map.get("city"));
//                location.setProvince(map.get("province"));
//                location.setStreet(map.get("street"));
//                location.setDistrict(map.get("district"));
//            }
//        }catch (Exception e){
//
//        }

        selfInfo.setLocation(location);
        perception.setInputImage(inputImage);
        perception.setInputText(inputText);
        perception.setSelfInfo(selfInfo);
        first.setPerception(perception);
        first.setUserInfo(userInfo);
        String tuLintresult = robotRequest.postRequest(sysConfig.getRobbotAPI(), JSON.toJSONString(first));
        logger.info("TuLingRobotResponse-----:" + tuLintresult);
        if (StringUtils.isEmpty(tuLintresult)) {
            logger.error("Tuling robot Recognitied Failed!");
            result.put("error", "Tuling robot cant not response this question!");
            result.put("resp", "");
        } else {
            JSONObject tlObj = JSON.parseObject(tuLintresult);
            String list105 = null;
            String type = null;
            String cont105 = null;
            String url = null;
            try {
                //list105= tlObj.getJSONArray("results").getJSONObject(0).getJSONObject("values").getString("url");
                //type = tlObj.getJSONArray("results").getJSONObject(0).getString("resultType");

                result.put("open_id", open_id);
                JSONArray jsonArray = tlObj.getJSONArray("results");
                logger.info("jsonArray-----:" + jsonArray);
                for (int i = 0; i < jsonArray.size(); i++) {
                    type = tlObj.getJSONArray("results").getJSONObject(i).getString("resultType");
                    switch (type) {
                        case "text":
                            cont105 = tlObj.getJSONArray("results").getJSONObject(i).getJSONObject("values").getString("text");

                            if(content.indexOf("快递")!=-1 || content.indexOf("速递") != -1 || content.indexOf("EMS") != -1){
                                String [] strs = cont105.split("\n\n");
                                List<String> list = new ArrayList<>();
                                for (String ss:strs) {
                                    for (String s:ss.split("\n")) {
                                        list.add(s);
                                    }
                                }
                                result.put("resp",list);
                            } else {
                                result.put("resp", cont105);
                            }




                            break;
                        case "url":
                            url = tlObj.getJSONArray("results").getJSONObject(i).getJSONObject("values").getString("url");
                            result.put("type", "url");
                            result.put("value", url);


                            break;
                        case "list":
                            list105 = tlObj.getJSONArray("results").getJSONObject(i).getJSONObject("values").getString("list");
                            result.put("type", type);
                            result.put("value", list105);
                            result.put("resp", cont105);


                            break;
                        case "news":
                            list105 = tlObj.getJSONArray("results").getJSONObject(i).getJSONObject("values").getString("news");
                            result.put("type", type);
                            result.put("value", list105);
                            result.put("resp", cont105);


                            break;
                        case "image":
                            list105 = tlObj.getJSONArray("results").getJSONObject(i).getJSONObject("values").getString("image");
                            result.put("type", "image");
                            result.put("value", list105);
                            result.put("resp", cont105);


                            break;
                        default:
                            break;
                    }
                }

            } catch (Exception e) {
                result.put("type", type);
                cont105 = "查无数据";
                result.put("resp", cont105);
                logger.info("图灵返回接口解析失败." + e.getCause(), e);
            }
        }
        result.put("type", "text");

        return result;
    }
}
