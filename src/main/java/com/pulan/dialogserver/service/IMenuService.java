package com.pulan.dialogserver.service;

import com.pulan.dialogserver.entity.resp.ReturnMsg;
import com.pulan.dialogserver.shiro.entity.User;

import javax.servlet.http.HttpServletRequest;


public interface IMenuService {


    ReturnMsg getRedisData(int type, HttpServletRequest request);
}
