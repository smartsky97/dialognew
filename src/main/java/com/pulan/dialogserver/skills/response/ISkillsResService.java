package com.pulan.dialogserver.skills.response;

import com.alibaba.fastjson.JSONObject;
import com.pulan.dialogserver.entity.SemanticSlots;

import java.util.List;

public interface ISkillsResService {

    JSONObject hynSingleDialogueRes(List<SemanticSlots> slotList, String converKey, String my_name, JSONObject result);

    JSONObject hynManyDialogueRes(List<SemanticSlots> secondSSot,String converKey,String my_name,JSONObject result);

    JSONObject manisRobotSingleDialogueRes(List<SemanticSlots> slotList, String converKey,JSONObject result);

    JSONObject manisRobotManyDialogueRes(List<SemanticSlots> secondSSot,String converKey,JSONObject result);

    JSONObject hynSendEmail(List<SemanticSlots> secondSSot,String  converKey,JSONObject result);

    JSONObject reminderDoSomething(List<SemanticSlots> slotList,String my_name,JSONObject result, String myname);

    JSONObject oneSlotSkill(String tempService ,List<SemanticSlots> slotsList ,JSONObject result);

    JSONObject openApplications(String tempService,List<SemanticSlots> slotsList ,JSONObject result);

    JSONObject sendMessage(String templateService,List<SemanticSlots> secondSSot,String  converKey,JSONObject result, String mailtoken);

    JSONObject airConditioner(String tempService,List<SemanticSlots> slotList,String  converKey,JSONObject result);

    JSONObject chackExpress(String tempService,List<SemanticSlots> slotList,String  converKey,JSONObject result);
}
