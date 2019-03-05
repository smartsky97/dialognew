package com.pulan.dialogserver.service;

import com.pulan.dialogserver.entity.UserInfos;

import java.util.List;

public interface IOracleService {

    public List<UserInfos> userInfosList(String fNumber);
}
