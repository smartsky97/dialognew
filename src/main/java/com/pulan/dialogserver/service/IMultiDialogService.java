package com.pulan.dialogserver.service;

import com.alibaba.fastjson.JSONObject;

public interface IMultiDialogService {

    String appMultiDialog(String msgData, String myIemi, String myName,String longin_name, JSONObject result,String mailtoken);
    String manisRobotMultiDialog(String msgData,JSONObject result);
}
