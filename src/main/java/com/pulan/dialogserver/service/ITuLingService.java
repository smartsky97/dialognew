package com.pulan.dialogserver.service;

import com.alibaba.fastjson.JSONObject;

public interface ITuLingService {

    JSONObject tuLintRobot(String content,String open_id,String longin_name,JSONObject result);
}
