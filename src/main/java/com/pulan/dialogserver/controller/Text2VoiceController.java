package com.pulan.dialogserver.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pulan.dialogserver.service.IFlyService;
import com.pulan.dialogserver.utils.baidutts.DemoException;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/message")
public class Text2VoiceController {

    private IFlyService iFlyService;  //讯飞服务
    @Autowired
    public Text2VoiceController(IFlyService iFlyService){
        this.iFlyService =iFlyService;
    }

    /**
     * 语音合成接口。
     * @param msgBody
     * @return
     */
    @RequestMapping(value = "/plttsservice",method = RequestMethod.POST)
    public String voice2Text(@RequestBody String msgBody) throws DemoException, JSONException, IOException {
        JSONObject result = new JSONObject();
        if (StringUtils.isEmpty(msgBody)) {
            result.put("resp", "Voice to Text Error,content is null!");
            result.put("type", "error");
        } else {
            JSONObject msgObj = JSON.parseObject(msgBody);
            String type = msgObj.getString("type");
            String content = msgObj.getString("resp");
            if (type.equals("voice")) {
                if (content.length()>30) {
                    return null;
                }
                result = iFlyService.text2Voice(content, result);
            } else {
                result.put("resp", content);
                result.put("type", "text");
            }
        }
        return result.toJSONString();
    }

    @RequestMapping(value = "/plttstest")
    public void test() throws JSONException, DemoException, IOException {
//        TtsUtils.saveVoice("啦啦啦啦噜噜噜哒哒哒");
    }
}
