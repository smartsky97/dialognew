package com.pulan.dialogserver.service;

import com.alibaba.fastjson.JSONObject;

public interface PulanAiServer {
    JSONObject aiServer(String content,String openid);
    JSONObject slotServer(String content,String openid,String type);
}
