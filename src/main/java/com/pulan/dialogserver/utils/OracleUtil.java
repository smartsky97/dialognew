package com.pulan.dialogserver.utils;

import com.pulan.dialogserver.entity.UserInfos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * oracle数据操作工具类
 */
@Component
public class OracleUtil {

    @Autowired
    @Qualifier("oraclesJdbcTemplate")
    private JdbcTemplate jdbcTemplate;


    public List<UserInfos> findUserInfos(String sql, String params){
        List<UserInfos> userInfosList = new ArrayList<>();
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql,params);
        UserInfos userInfos = null;
        while (rs.next()) {
            String fNumbers = rs.getString("FNUMBER");
            String perFName = rs.getString("PERFNAME_L2");
            String posFName = rs.getString("POSFNAME_L2");
            userInfos = new UserInfos(fNumbers, perFName, posFName);
            userInfosList.add(userInfos);
        }

        return userInfosList;
    }



}
