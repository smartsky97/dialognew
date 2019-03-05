package com.pulan.dialogserver.service;

import com.alibaba.fastjson.JSONObject;
import com.pulan.dialogserver.utils.baidutts.DemoException;
import org.json.JSONException;

import java.io.IOException;

public interface IFlyService {
    JSONObject voice2Text(String voiceUrl);
    JSONObject text2Voice(String text,JSONObject jsonObject) throws JSONException, DemoException, IOException;
    JSONObject iflySemanticUnderstand(String text,JSONObject result);
}
