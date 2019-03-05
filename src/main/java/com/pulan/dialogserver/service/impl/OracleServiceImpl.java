package com.pulan.dialogserver.service.impl;

import com.pulan.dialogserver.entity.UserInfos;
import com.pulan.dialogserver.service.IOracleService;

import com.pulan.dialogserver.utils.OracleUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;

@Service
public class OracleServiceImpl implements IOracleService {


    @Autowired
    private OracleUtil oracleUtil;

    @Override
    public List<UserInfos> userInfosList(String fNumber) {
        String sql = "SELECT U.FNUMBER ,PER.FNAME_L2 as PERFNAME_L2,POS.FNAME_L2 as POSFNAME_L2 \n" +
                "FROM CNFANTASIA.T_BD_PERSON  PER\n" +
                "LEFT  JOIN CNFANTASIA.T_HR_BDEMPLOYEETYPE EM ON EM.FID=PER.FEMPLOYEETYPEID\n" +
                "LEFT  JOIN CNFANTASIA.T_ORG_POSITIONMEMBER  ME ON ME.FPERSONID=PER.FID\n" +
                "LEFT JOIN CNFANTASIA.T_PM_USER U ON U.FPERSONID=ME.FPERSONID\n" +
                "LEFT  JOIN CNFANTASIA.T_ORG_POSITION  POS ON POS.FID=ME.FPOSITIONID\n" +
                "LEFT  JOIN CNFANTASIA.T_ORG_POSITIONHIERARCHY  PH ON PH.FCHILDID =POS.FID\n" +
                "WHERE EM.FINSERVICE !=2 AND  PH.FPARENTID IN(\n" +
                "SELECT FPOSITIONID FROM CNFANTASIA.T_ORG_POSITIONMEMBER PE\n" +
                "LEFT JOIN CNFANTASIA.T_PM_USER U ON U.FPERSONID=PE.FPERSONID\n" +
                " WHERE  PE.FISPRIMARY=1 AND FNUMBER=?\n" +
                ")  ";
        List<UserInfos> userInfosList = new ArrayList<>();
        userInfosList = oracleUtil.findUserInfos(sql,fNumber);

        return userInfosList;

    }
}
