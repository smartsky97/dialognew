package com.pulan.dialogserver.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.pulan.dialogserver.controller.AppMultiDialogController;
import com.pulan.dialogserver.entity.Function;
import com.pulan.dialogserver.entity.SemanticSlots;
import com.pulan.dialogserver.entity.User;
import com.pulan.dialogserver.service.*;
import com.pulan.dialogserver.skills.response.ISkillsResService;
import com.pulan.dialogserver.utils.*;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class MultiDialogServiceImpl implements IMultiDialogService {

    private Logger logger = LogManager.getLogger(AppMultiDialogController.class);
    @Autowired
    private IPulanAiServer pulanAiServer;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private JdbcUtils jdbcUtils;
    @Autowired
    private HelpUtils helpUtils;
    @Autowired
    private IFlyService iFlyService;
    @Autowired
    private ITuLingService iTuLingService;
    @Autowired
    private ISlotsReplaceService iSlotsReplaceService;
    @Autowired
    private HanZi2PinYingUtil hanZi2PinYingUtil;

    @Autowired
    private ISkillsResService iSkillsResService;

    /**
     * App 多轮会话逻辑控制
     *
     * @param msgData app输入飞文本
     * @param my_imei 当前用户APP上IMEI号
     * @param my_name 当前用户的邮箱账号
     * @param result  返回的结果
     * @return 返回值
     */
    @Override
    public String appMultiDialog(String msgData, String my_imei, String my_name,String longin_name, JSONObject result, String mailtoken) {
        String converKey = my_name + ":" + my_imei;
        logger.info("Now User ModelKey：" + converKey);
        Boolean isSlotNull = false;
        try {
            String awaken = jdbcUtils.getAwakenWord();
            JSONObject msgObj = JSON.parseObject(msgData);
            // 返回语言/文字的类型。
            String type = msgObj.getString("type");
            // 文本内容（标识语意槽修改，手动输入，值为input）
            String voicetext = msgObj.getString("resp");
            // 设备编号 opend_id
            String open_id = msgObj.getString("open_id");
            String sure = msgObj.getString("sure");
     //       String inslot_type = msgObj.getString("slot_type"); //语意槽类型。
            // 如果语音内容是 你可以做什么
            if (helpUtils.isDoSomething(voicetext)) {
                List<Function> list = jdbcUtils.getFunctions();
                result.put("resp", list);
                result.put("type", "fun");
                // 语音唤醒
            } else if (voicetext.contains(awaken)) {
                /*List<String> lstx = redisClient.lrange(1,"hyn:hello");
                int resint = helpUtils.getRandomNumber(lstx.size());
                String content = lstx.get(resint);*/
                String content = "主人,我在,有什么我可以帮助您的？";
                if ("voice".equals(type)) {
                    result = iFlyService.text2Voice(content, result);
                    result.put("content", content);
                } else {
                    result.put("resp", content);
                    result.put("type", "text");
                }
            } else {
                // 判断当前用户是否已经存在对话语义问答模板对象。
                String converModel = redisClient.get(1, converKey); //当前用户存储的会话模板。
                if (StringUtils.isEmpty(converModel)) {
                    JSONObject retObj = pulanAiServer.aiServer(voicetext, open_id); //请求语义理解服务。
                    logger.info("PlServer语义结果:" + retObj.getString("retObj"));
                    // 语义理解能识别 error =null ;
                    if (StringUtils.isEmpty(retObj.getString("error"))) {
                        JSONObject retJson = retObj.getJSONObject("retObj");
                        String service = retJson.getString("service");
                        JSONObject semanticObj = retJson.getJSONArray("semantic").getJSONObject(0);
                        JSONObject slots = null;
                        if (semanticObj.containsKey("slots")) {
                            slots = semanticObj.getJSONArray("slots").getJSONObject(0);
                            //日程安排的语义理解中识别不了我
                        }else if(voicetext.contains("我")){
                            slots = JSONObject.parseObject("{\"person\": \"我\",\"keyword\": \"查\"}");
                        }
                        String intent = semanticObj.getString("intent");
                        //根据意图去查询语义模版是否存在
                        Boolean isModelExist = jdbcUtils.isSemanticModelExist(service, intent);
                        if (isModelExist) {
                            List<SemanticSlots> semsot = jdbcUtils.getSemanticSlot(intent, service);
                            for (SemanticSlots slotObj : semsot) {
                                if ("person".equals(slotObj.getSlotName())){
                                    //关闭添加默认值“我”
//                                    slotObj.setSlotValue(slotObj.getDefaultValue());
                                }
                            }
                            redisClient.saveSemanticModel(1, converKey, semsot);// 存储会话空模版
                            // 处理语义槽值, 没有任何语义槽时，直接返回第一个模板问题
                            if (slots == null || slots.isEmpty()) {
                                // 语义槽为空返回第一个语义槽为空的问题。
                                boolean isNull = false;
                                for (SemanticSlots slotObj : semsot) {
                                    String fsolt = slotObj.getSlotValue();
                                    if (StringUtils.isEmpty(fsolt)) {
                                            String question = slotObj.getPrompt();
                                            result.put("resp", question);
                                            result.put("data_type", slotObj.getSlotName());
                                            result.put("type", "text");
                                            isNull = true;
                                            break;
                                    }
                                }
                                //判断上面是否存在空语义槽。
                                if (!isNull) {
                                    String tempIntent = semsot.get(0).getTemplateIntent();
                                    result = iSkillsResService.oneSlotSkill(tempIntent, semsot, result);
                                    redisClient.del(1, converKey);
                                }
                            } else {
                                List<SemanticSlots> slotList = new ArrayList<>();
                                for (SemanticSlots sst : semsot) {
                                    String slotName = sst.getSlotName();
                                    if (slots.containsKey(slotName)) {
                                        String slot_value = slots.getString(slotName);
                                        if (null != sst.getUtterance() && ("PhoneCall".equals(sst.getUtterance()) || "SendSMS".equals(sst.getUtterance()))){
                                            //在打电话或发短信时，添加对应的号码
                                            String phone;
                                            String pinyin = hanZi2PinYingUtil.getNamePinYin(slot_value);
                                            List<String> unameList = hanZi2PinYingUtil.getCnNameByPinyin(pinyin);
                                            if (unameList.size() > 1) { //多个人名
                                                List<User> users = hanZi2PinYingUtil.getUserByPinYin(pinyin);
                                                result.put("resp", JSON.toJSON(users));
                                                result.put("content", "请选择您想要查询的人员名称！");
                                                result.put("type", sst.getTemplateService()+"list");
                                                result.put("types", "list");
                                                logger.info("appMultiDialog出现重复人名：" + result);
                                                // 存在多人名同音问题,重新让用户选择
                                                redisClient.del(1,converKey);
                                                return JSON.toJSONString(result);
                                            } else if (unameList.size() == 1) { //一个人名
                                                phone = jdbcUtils.getPhoneByCnName(unameList.get(0));
                                                sst.setSlotValue(phone);
                                            } else {  //没有找到人名
                                                //todo 推荐一个相识人名
                                                result.put("resp", "抱歉，没有找到关于'" + voicetext + "'的人员信息，请重试要查询的人名！");
                                                result.put("type", "text");
                                                result.put("content", voicetext);
                                                return JSON.toJSONString(result);
                                            }
                                        } else {
                                            switch (slotName) {
                                                case "date":
                                                    sst.setSlotValue(slot_value);
                                                    break;
                                                case "datetime":
                                                    //不能设置之前时间提醒
                                                    if ("107101".equals(sst.getSlotId())) {
                                                        Date now = new Date();
                                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                                        Date val = sdf.parse(slot_value);
                                                        if (now.compareTo(val) > 0) {
                                                            result.put("resp", "你设置的时间已过，请设置之后的要提醒的事件。");
                                                            result.put("type", "text");
                                                            return JSON.toJSONString(result);
                                                        } else {
                                                            sst.setSlotValue(slot_value);
                                                        }
                                                    } else {
                                                        sst.setSlotValue(slot_value);
                                                    }
                                                    break;
                                                case "person":
                                                    if (voicetext.contains("我")) { //人名是否包含我
                                                        sst.setSlotValue("我");
                                                    } else if(!"VisitWebsite".equals(sst.getTemplateService())
                                                            && !"Reminder".equals(sst.getTemplateService())){
                                                        String pinyin = hanZi2PinYingUtil.getNamePinYin(slot_value);
                                                        List<String> unameList = hanZi2PinYingUtil.getCnNameByPinyin(pinyin);
                                                        if (unameList.size() > 1) { //多个人名
                                                            List<User> users = hanZi2PinYingUtil.getUserByPinYin(pinyin);
                                                            result.put("resp", JSON.toJSON(users));
                                                            result.put("content", "请选择您想要查询的人员名称！");
                                                            result.put("type", sst.getTemplateService().toLowerCase() + "list");
                                                            result.put("types", "list");
                                                            logger.info("appMultiDialog出现重复人名：" + result);
                                                            // 存在多人名同音问题,重新让用户选择
                                                            return JSON.toJSONString(result);
                                                        } else if (unameList.size() == 1) { //一个人名
                                                            sst.setSlotValue(unameList.get(0));
                                                        } else {  //没有找到人名
                                                            result.put("resp", "抱歉，没有找到关于'" + slot_value + "'的人员信息，请重试要查询的人名！");
                                                            result.put("type", "text");
                                                            result.put("content", voicetext);
                                                            return JSON.toJSONString(result);
                                                        }
                                                    }
                                                    break;
                                                default:
                                                    sst.setSlotValue(slot_value);
                                                    break;
                                            }
                                        }
                                    }
                                    slotList.add(sst);
                                }
                                // 存储填充后的模板
                                redisClient.saveSemanticModel(1, converKey, slotList);
                                // 再次遍历模板槽，查找是否存在空的槽
                                for (SemanticSlots sst : slotList) {
                                    String jsr_value = sst.getSlotValue();
                                    // 发现空槽，返回问题，提示用户回答
                                    if (StringUtils.isEmpty(jsr_value)) {
                                            String question = sst.getPrompt();
                                            result.put("resp", question);
                                            result.put("data_type", sst.getSlotName());
                                            result.put("type", "text");
                                            isSlotNull = true;
                                            break;
                                    }
                                }
                                // 1、判断第一次会话对象语义槽是否有null值。
                                // 2、语意槽没有null 值 则执行获取数据的服务，返回给用户数据，并且删除之前存储的用户语意模板。
                                // 3、此处是单论对话的处理逻辑。
                                if (!isSlotNull) {
                                    String tempService = slotList.get(0).getTemplateService();//语义理解服务类型
                                    result = getAppResultData(tempService,converKey,slotList,my_imei,my_name,result,mailtoken);
                                }
                            }
                        } else {
                            //找不到相符合的语义模版,调用图灵机器人。
                            redisClient.del(1, converKey);
                            result = iTuLingService.tuLintRobot(voicetext, open_id,longin_name, result);
                            String tlError = result.getString("error");
                            if (!StringUtils.isEmpty(tlError)) {
                                result.put("resp", "主人，我还没有学会这个技能，换个试试吧！");
                                result.put("type", "text");
                            }
                        }
                        // 语义识别理解不了的情况处理。
                    } else {
                        // 语意理解错误处理。
                        redisClient.del(1, converKey);
                        result = iTuLingService.tuLintRobot(voicetext, open_id,longin_name, result);
                        // 对查找菜谱的情况，会是一个数组，把type设置为arraytext,安卓端好解析数据
                        if (result.get("resp").toString().contains("找到菜谱信息")) {
                            result.put("type","arraytext");
                        }
                        //安卓端识别这是图片链接
                        if (result != null && result.get("value")!= null && result.get("value").toString().contains("http://file.tuling123.com/upload/image")) {
                            result.put("type","picurl");
                        }
                        //新闻的数组数据
                        if (result != null && result.get("resp")!= null && result.get("resp").toString().contains("亲，已帮您找到相关新闻") &&
                                result.get("value").toString().startsWith("[") && result.get("value").toString().endsWith("]")) {
                            result.put("type","arraytext");
                        }
                        String tlError = result.getString("error");
                        if (!StringUtils.isEmpty(tlError)) {
                            result.put("resp", "主人，我还没有学会这个技能，换个试试吧！");
                            result.put("type", "text");
                        }
                    }
                    /*
                     * 2、此次会话之前走过plserver语义理解，会话对象存在。
                     * 2.1 获取会话对象，遍历属性为null的语义槽，返回对应的语义槽名称 获取到该语义槽的问题，返回给用户。
                     * 2.2 填充语义槽值。
                     */
                } else {
                    // 反问用户相关语义槽的值，填充redis存储的回话对象。
                    boolean find = false; // 是否找到对应值的空槽
                    boolean changed = false;// 若是安卓请求修改之前的值
                    List<SemanticSlots> secondSSot = JSON.parseArray(converModel, SemanticSlots.class); // 获取用户上次模板对话内容转换 list
                    isSlotNull = false;
                    boolean isBreak = false;
                    boolean isError = false;
                    boolean isTryAgain = false;
                    boolean inchanged = false;//表示是否进入过  循环遍历模板将第一个为空的语意槽的问题返回。   这块代码
                    for (int i = 0; i < secondSSot.size(); i++) {
                        SemanticSlots smst = secondSSot.get(i);
                        String slot_value = smst.getSlotValue();
                        // 在处理相应逻辑之前，查看是否为修改之前语句中错误的字

                        //循环遍历模板将第一个为空的语意槽的问题返回。
                        if (!StringUtils.isEmpty(msgObj.get("editAble")) && "true".equals(msgObj.get("editAble").toString()) && !inchanged) {
                            // 旧的值和当前语句中的值相等，把新的值赋予到redis中
                            if (slot_value != null && slot_value.equals(msgObj.get("oldDbDate")) || (slot_value.equals("我")&&msgObj.get("oldDbDate").toString().contains("我"))) {
                                //当问到邮件、发短信、查快递时，不修改，执行下面的逻辑
                                if ("106100".equals(smst.getSlotId()) || "115101".equals(smst.getSlotId()) || "108100".equals(smst.getSlotId())) {
                                    smst.setSlotValue("");
                                    slot_value = "";
                                } else {
                                    smst.setSlotValue(voicetext);
                                    secondSSot.add(i, smst);
                                    secondSSot.remove(i + 1);
                                    redisClient.saveSemanticModel(1, converKey, secondSSot);
                                    find = true;
                                    changed = true;
                                    isBreak = true;
                                    isSlotNull = true;
                                }
                                inchanged = true;
                            }
                        }
                        if (StringUtils.isEmpty(slot_value) && !changed) {
                            // 在下一个空槽时返回问题给用户回答
                            if (find) {
                                result.put("resp", smst.getPrompt());
                                result.put("type", "text");
                                result.put("data_type", smst.getSlotName());
                                isSlotNull = true;
                                break;
                            } else {
                                //语义模版语义槽值的填充，可以修改，人名输入默认正确，日期重新请求获取。
                                if (type.equals("input")) {
                                    //先去查询是否有这个人
                                    String mail = jdbcUtils.getMailByName(voicetext);
                                    if (StringUtils.isEmpty(mail)) {
                                        result.put("resp", "抱歉，没有找到关于'" + voicetext + "'的人员信息，请重试要查询的人名！");
                                        result.put("type", "try");
                                        result.put("type", "text");
                                        result.put("content", voicetext);
                                        result.put("data_type", smst.getSlotName());
                                        smst.setSlotValue("");
                                        int try_times = smst.getTryCount();
                                        smst.setTryCount(try_times - 1);
                                        secondSSot.add(i, smst);
                                        secondSSot.remove(i + 1);
                                        isBreak = true;
                                        if (try_times <= 0) {
                                            result.put("resp", "抱歉，花花已经很努力了还是没有找到关于'" + voicetext + "'的人员信息，本轮会话结束！");
                                            result.put("type", "text");
                                            isError = true;
                                        } else {
                                            result.put("data_type", smst.getSlotName());
                                            isTryAgain = true;
                                        }
                                    } else {
                                        smst.setSlotValue(voicetext);
                                    }
                                } else {
                                    String inslot_type =smst.getSlotName();
                                    //如果是邮件主题、短信内容 则直接填充词槽
                                    if("SendEmail".equals(smst.getUtterance()) && ("email_subject".equals(smst.getSlotCode())|| "email_content".equals(smst.getSlotCode() ))
                                            || "SendSMS".equals(smst.getUtterance()) && "message_content".equals(smst.getSlotCode()))
                                    {
                                        smst.setSlotValue(voicetext);
                                        secondSSot.add(i, smst);
                                        secondSSot.remove(i + 1);
                                    }else {
                                        switch (inslot_type) {
                                            case "date":
                                                String dateString = iSlotsReplaceService.slotInputReplace(voicetext, smst.getUtterance(), inslot_type);
                                                if (dateString != null) {
                                                    smst.setSlotValue(dateString);
                                                    secondSSot.add(i, smst);
                                                    secondSSot.remove(i + 1);
                                                } else {
                                                    result.put("resp", "抱歉，您提供的日期'" + voicetext + "'格式不正确，请重试日期！");
                                                    result.put("type", "try");
                                                    int try_times = smst.getTryCount();
                                                    smst.setTryCount(try_times - 1);
                                                    smst.setSlotValue("");
                                                    secondSSot.add(i, smst);
                                                    secondSSot.remove(i + 1);
                                                    isBreak = true;
                                                    if (try_times <= 0) {
                                                        result.put("resp", "抱歉，花花已经很努力了还是无法解析您提供的日期格式，本轮会话结束！");
                                                        result.put("type", "text");
                                                        isError = true;
                                                    } else {
                                                        result.put("data_type", inslot_type);
                                                        isTryAgain = true;
                                                    }
                                                }
                                                break;
                                            case "datetime":
                                                String dateTimeString = iSlotsReplaceService.slotInputReplace(voicetext, smst.getUtterance(), inslot_type);
                                                if (dateTimeString != null) {
                                                    smst.setSlotValue(dateTimeString);
                                                    secondSSot.add(i, smst);
                                                    secondSSot.remove(i + 1);
                                                } else {
                                                    result.put("resp", "抱歉，您提供的时间'" + voicetext + "'格式不正确，请重试日期！");
                                                    result.put("type", "try");
                                                    int try_times = smst.getTryCount();
                                                    smst.setTryCount(try_times - 1);
                                                    smst.setSlotValue("");
                                                    secondSSot.add(i, smst);
                                                    secondSSot.remove(i + 1);
                                                    isBreak = true;
                                                    if (try_times <= 0) {
                                                        result.put("resp", "抱歉，花花已经很努力了还是无法解析您提供的日期格式，本轮会话结束！");
                                                        result.put("type", "text");
                                                        isError = true;
                                                    } else {
                                                        result.put("data_type", inslot_type);
                                                        isTryAgain = true;
                                                    }
                                                }
                                                break;
                                            case "person":
                                                String tempService = smst.getTemplateService();
                                                if (tempService.equals("Phone") || tempService.equals("Message")) { //打电话,发短信人名不需要查询数据库。
//                                                    smst.setSlotValue(hanZi2PinYingUtil.getNamePinYin(voicetext));
                                                    String phone;
                                                    String pinyin = "";
                                                    List<String> unameList = new ArrayList<>();
                                                    if (!StringUtils.isEmpty(sure)) {//标识是确定值，用于问名字是，有多个相同的名称，这是安卓端给的确定的值
                                                        unameList.add(voicetext);
                                                    } else {
                                                        pinyin = hanZi2PinYingUtil.getNamePinYin(voicetext);
                                                        unameList = hanZi2PinYingUtil.getCnNameByPinyin(pinyin);
                                                    }
                                                    if (unameList.size() > 1) { //多个人名
                                                        List<User> users = hanZi2PinYingUtil.getUserByPinYin(pinyin);
                                                        result.put("resp", JSON.toJSON(users));
                                                        result.put("content", "请选择您想要查询的人员名称！");
                                                        result.put("type", tempService.toLowerCase()+"list");
                                                        result.put("types", "list");
                                                        logger.info("appMultiDialog出现重复人名：" + result);
                                                        isBreak = true;
                                                        isTryAgain = true;
                                                        smst.setSlotValue("");
                                                        secondSSot.add(i, smst);
                                                        secondSSot.remove(i + 1);
                                                        // 存在多人名同音问题,重新让用户选择
                                                        //在电话或信息重名的情况，把多个重名的信息丢出去，并在redis中把信息删除
                                                        redisClient.del(1,converKey);
                                                    } else if (unameList.size() == 1) { //一个人名
                                                        phone = jdbcUtils.getPhoneByCnName(unameList.get(0));
                                                        //用于安卓端使用修改上次对话功能时使用
                                                        result.put("oldDbDate",phone);
                                                        smst.setSlotValue(phone);
                                                        secondSSot.add(i, smst);
                                                        secondSSot.remove(i + 1);
                                                    } else {  //没有找到人名
                                                        //todo 推荐一个相识人名
                                                        List<String> name = jdbcUtils.getAllChineseName();
                                                        String[] array = new String[name.size()];
                                                        String[] s=name.toArray(array);
                                                        List<SimilarNameUtils.Score> scores = SimilarNameUtils.getSingle().search(s,voicetext,1);
                                                        //找到相似的人名
//                                                        if (scores != null && scores.size() > 0) {
//                                                            SimilarNameUtils.Score people = scores.get(0);
//                                                            //相似度小于10
//                                                            if (people.score<10) {
//                                                                //jp修改部分
//                                                                phone = jdbcUtils.getPhoneByCnName(people.word+"");
//                                                                smst.setSlotValue(phone);
//                                                                secondSSot.add(i, smst);
//                                                                secondSSot.remove(i + 1);
//                                                            } else{
//                                                                result.put("resp", "抱歉，没有找到关于'" + voicetext + "'的人员信息，请重试要查询的人名！");
//                                                                result.put("type", "try");
//                                                                result.put("content", voicetext);
//                                                                result.put("data_type", inslot_type);
//                                                                smst.setSlotValue("");
//                                                                int try_times = smst.getTryCount();
//                                                                smst.setTryCount(try_times - 1);
//                                                                secondSSot.add(i, smst);
//                                                                secondSSot.remove(i + 1);
//                                                                isBreak = true;
//                                                                if (try_times <= 0) {
//                                                                    result.put("resp", "抱歉，花花已经很努力了还是没有找到关于'" + voicetext + "'的人员信息，本轮会话结束！");
//                                                                    result.put("type", "text");
//                                                                    isError = true;
//                                                                } else {
//                                                                    result.put("data_type", inslot_type);
//                                                                    isTryAgain = true;
//                                                                }
//                                                            }
//                                                        } else {
                                                            result.put("resp", "抱歉，没有找到关于'" + voicetext + "'的人员信息，请重试要查询的人名！");
                                                            result.put("type", "try");
                                                            result.put("type", "text");
                                                            result.put("content", voicetext);
                                                            result.put("data_type", inslot_type);
                                                            smst.setSlotValue("");
                                                            int try_times = smst.getTryCount();
                                                            smst.setTryCount(try_times - 1);
                                                            secondSSot.add(i, smst);
                                                            secondSSot.remove(i + 1);
                                                            isBreak = true;
                                                            if (try_times <= 0) {
                                                                result.put("resp", "抱歉，花花已经很努力了还是没有找到关于'" + voicetext + "'的人员信息，本轮会话结束！");
                                                                result.put("type", "text");
                                                                isError = true;
                                                            } else {
                                                                result.put("data_type", inslot_type);
                                                                isTryAgain = true;
                                                            }
//                                                        }
                                                    }
                                                } else {
                                                    if (voicetext.contains("我")) { //人名是否包含我
                                                        smst.setSlotValue("我");
                                                        secondSSot.add(i, smst);
                                                        secondSSot.remove(i + 1);
                                                    } else {
                                                        String personName = iSlotsReplaceService.slotInputReplace(voicetext, smst.getUtterance(), inslot_type);
                                                        //String personName = hanZi2PinYingUtil.getNamePinYin(personName);
                                                        if (personName != null) { //是否能提取到人名。
                                                            String email_name = jdbcUtils.getEmailByCnName(personName);
                                                            String pinyin = "";
                                                            List<String> unameList = new ArrayList<>();
//                                                            if (StringUtils.isEmpty(email_name)) {
                                                            // 当出现人名相同的情况，由安卓端传来确定的人名
                                                            if (!StringUtils.isEmpty(sure)) {//标识是确定值
                                                                unameList.add(personName);
                                                            } else {
                                                                pinyin = hanZi2PinYingUtil.getNamePinYin(personName);
                                                                unameList = hanZi2PinYingUtil.getCnNameByPinyin(pinyin);
                                                            }
                                                            if (unameList.size() > 1) { //多个人名
                                                                List<User> users = hanZi2PinYingUtil.getUserByPinYin(pinyin);
                                                                result.put("resp", JSON.toJSON(users));
                                                                result.put("content", "请选择您想要查询的人员名称！");
                                                                result.put("type", tempService.toLowerCase() + "list");
                                                                result.put("types", "list");
                                                                logger.info("appMultiDialog出现重复人名：" + result);
                                                                isBreak = true;
                                                                isTryAgain = true;
                                                                smst.setSlotValue("");
                                                                secondSSot.add(i, smst);
                                                                secondSSot.remove(i + 1);
                                                                // 存在多人名同音问题,重新让用户选择
                                                            } else if (unameList.size() == 1) { //一个人名
                                                                email_name = jdbcUtils.getEmailByCnName(unameList.get(0));
//                                                                    smst.setSlotValue(email_name);
                                                                //jp修改部分
                                                                if (("Attendance".equals(smst.getTemplateService())&& "kaoqin_person".equals(smst.getSlotCode()))
//                                                                || ("Meeting".equals(smst.getTemplateService())&& "meeting_person".equals(smst.getSlotCode()))
                                                                ) {
                                                                    smst.setSlotValue(personName);
                                                                    result.put("",personName);
                                                                } else {
                                                                    smst.setSlotValue(email_name);
                                                                    //用于安卓端使用修改上次对话功能时使用
                                                                    result.put("oldDbDate",email_name);
                                                                }

                                                                secondSSot.add(i, smst);
                                                                secondSSot.remove(i + 1);
                                                            } else {  //没有找到人名
                                                                //todo 推荐一个相识人名
                                                                List<String> name = jdbcUtils.getAllChineseName();
                                                                String[] array = new String[name.size()];
                                                                String[] s=name.toArray(array);
                                                                List<SimilarNameUtils.Score> scores = SimilarNameUtils.getSingle().search(s,voicetext,1);
                                                                //找到相似的人名
                                                                /*if (scores != null && scores.size() > 0) {
                                                                    SimilarNameUtils.Score people = scores.get(0);
                                                                    //相似度小于10
                                                                    if (people.score<10) {
                                                                        email_name = jdbcUtils.getEmailByCnName(people.word+"");
                                                                        if (StringUtils.isEmpty(email_name)) {
                                                                            result.put("resp", "抱歉，当前人员未设置邮箱");
                                                                            result.put("type", "text");
                                                                            isBreak = true;
                                                                            isError = true;
                                                                        }
                                                                        //jp修改部分
                                                                        if ("Attendance".equals(smst.getTemplateService())
                                                                                && "kaoqin_person".equals(smst.getSlotCode())) {
                                                                            smst.setSlotValue(people.word+"");
                                                                        } else {
                                                                            smst.setSlotValue(email_name);
                                                                        }
                                                                        secondSSot.add(i, smst);
                                                                        secondSSot.remove(i + 1);
                                                                    } else {
                                                                        result.put("resp", "抱歉，没有找到关于'" + personName + "'的人员信息，请重试要查询的人名！");
                                                                        result.put("type", "try");
                                                                        result.put("content", personName);
                                                                        result.put("data_type", inslot_type);
                                                                        smst.setSlotValue("");
                                                                        int try_times = smst.getTryCount();
                                                                        smst.setTryCount(try_times - 1);
                                                                        secondSSot.add(i, smst);
                                                                        secondSSot.remove(i + 1);
                                                                        isBreak = true;
                                                                        if (try_times <= 0) {
                                                                            result.put("resp", "抱歉，花花已经很努力了还是没有找到关于'" + personName + "'的人员信息，本轮会话结束！");
                                                                            result.put("type", "text");
                                                                            isError = true;
                                                                        } else {
                                                                            result.put("data_type", inslot_type);
                                                                            isTryAgain = true;
                                                                        }
                                                                    }
                                                                } else {*/
                                                                    result.put("resp", "抱歉，没有找到关于'" + personName + "'的人员信息，请重试要查询的人名！");
                                                                    result.put("type", "try");
                                                                    result.put("type", "text");
                                                                    result.put("content", personName);
                                                                    result.put("data_type", inslot_type);
                                                                    smst.setSlotValue("");
                                                                    int try_times = smst.getTryCount();
                                                                    smst.setTryCount(try_times - 1);
                                                                    secondSSot.add(i, smst);
                                                                    secondSSot.remove(i + 1);
                                                                    isBreak = true;
                                                                    if (try_times <= 0) {
                                                                        result.put("resp", "抱歉，花花已经很努力了还是没有找到关于'" + personName + "'的人员信息，本轮会话结束！");
                                                                        result.put("type", "text");
                                                                        isError = true;
                                                                    } else {
                                                                        result.put("data_type", inslot_type);
                                                                        isTryAgain = true;
                                                                    }
                                                               /* }*/
                                                            }
//                                                            } else {
//                                                                if ("Attendance".equals(smst.getTemplateService())
//                                                                        && "kaoqin_person".equals(smst.getSlotCode())) {
//                                                                    smst.setSlotValue(personName);
//                                                                } else {
//                                                                    smst.setSlotValue(email_name);
//                                                                }
//                                                                secondSSot.add(i, smst);
//                                                                secondSSot.remove(i + 1);
//                                                            }
                                                        } else { //如果人名提取不到，换用户的原话当作人名继续查找。
                                                            String email_name2 = jdbcUtils.getEmailByCnName(voicetext);
                                                            if (StringUtils.isEmpty(email_name2)) {
                                                                String pinyin2 = hanZi2PinYingUtil.getNamePinYin(voicetext);
                                                                List<String> unameList2 = hanZi2PinYingUtil.getCnNameByPinyin(pinyin2);
                                                                if (unameList2.size() > 1) {
                                                                    List<User> users = hanZi2PinYingUtil.getUserByPinYin(pinyin2);
                                                                    result.put("resp", JSON.toJSON(users));
                                                                    result.put("content", "请选择您想要查询的人员名称！");
                                                                    result.put("type", tempService.toLowerCase()+"list");
                                                                    result.put("types", "list");
                                                                    logger.info("appMultiDialog2出现重复人名：" + result);
                                                                    isBreak = true;
                                                                    isTryAgain = true;
                                                                    smst.setSlotValue("");
                                                                    secondSSot.add(i, smst);
                                                                    secondSSot.remove(i + 1);
                                                                    // 存在多人名同音问题,重新让用户选择
                                                                } else if (unameList2.size() == 1) {
                                                                    email_name2 = jdbcUtils.getEmailByCnName(unameList2.get(0));
                                                                    smst.setSlotValue(email_name2);
                                                                    secondSSot.add(i, smst);
                                                                    secondSSot.remove(i + 1);
                                                                } else if(helpUtils.isEmail(voicetext)){ //判断本身是不是邮件地址
                                                                    smst.setSlotValue(voicetext);
                                                                    secondSSot.add(i, smst);
                                                                    secondSSot.remove(i + 1);
                                                                }else{
                                                                    result.put("resp", "抱歉，没有找到关于'" + voicetext + "'的人员信息，请重试要查询的人名！");
                                                                    result.put("type", "try");
                                                                    result.put("type", "text");
                                                                    result.put("content", voicetext);
                                                                    result.put("data_type", inslot_type);
                                                                    smst.setSlotValue("");
                                                                    int try_times = smst.getTryCount();
                                                                    smst.setTryCount(try_times - 1);
                                                                    secondSSot.add(i, smst);
                                                                    secondSSot.remove(i + 1);
                                                                    isBreak = true;
                                                                    if (try_times <= 0) {
                                                                        result.put("resp", "抱歉，花花已经很努力了还是没有找到关于'" + voicetext + "'的人员信息，本轮会话结束！");
                                                                        result.put("type", "text");
                                                                        isError = true;
                                                                    } else {
                                                                        result.put("data_type", inslot_type);
                                                                        isTryAgain = true;
                                                                    }
                                                                }
                                                            } else {
                                                                smst.setSlotValue(email_name2);
                                                                secondSSot.add(i, smst);
                                                                secondSSot.remove(i + 1);
                                                            }
                                                        }
                                                    }
                                                }
                                                break;
                                            default:
                                                String slotValue = iSlotsReplaceService.slotInputReplace(voicetext, smst.getUtterance(), inslot_type);
                                                if (slotValue != null) {
                                                    smst.setSlotValue(slotValue);
                                                    //用于安卓端使用修改上次对话功能时使用
                                                    result.put("oldDbDate",slotValue);
                                                    secondSSot.add(i, smst);
                                                    secondSSot.remove(i + 1);
                                                } else {
                                                    result.put("resp", "抱歉，您提供的'" + voicetext + "'和要求的语义槽格式不匹配，请重试！");
                                                    result.put("type", "try");
                                                    result.put("type", "text");
                                                    int try_times = smst.getTryCount();
                                                    smst.setTryCount(try_times - 1);
                                                    smst.setSlotValue("");
                                                    secondSSot.add(i, smst);
                                                    secondSSot.remove(i + 1);
                                                    isBreak = true;
                                                    if (try_times <= 0) {
                                                        result.put("resp", "抱歉，花花已经很努力了语义槽还是匹配不上，本轮会话结束！");
                                                        result.put("type", "text");
                                                        isError = true;
                                                    } else {
                                                        result.put("data_type", inslot_type);
                                                        isTryAgain = true;
                                                    }
                                                }
                                                break;
                                        }
                                    }
                                }
                                redisClient.saveSemanticModel(1, converKey, secondSSot);
                                find = true;
                            }
                        }
                        if (isBreak) {
                            break;
                        }
                    }
                    if (isError) {
                        redisClient.del(1, converKey);
                        return JSON.toJSONString(result);
                    } else if (isTryAgain || isSlotNull) {
                        return JSON.toJSONString(result);
                    }
                    // 1、所有语意槽填满以后去获取业务数据。
                    // 2、多轮对话完成获取返回数据。
                    if (!isSlotNull) {
                        //满值语意槽处理拉取数据。
                        String tempdService = secondSSot.get(0).getTemplateService();
                        result = getAppResultData(tempdService,converKey,secondSSot,my_imei,my_name,result,mailtoken);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("语意技能出错Error：" + e.getMessage(),e);
            try {
                redisClient.del(1, converKey);
            } catch (Exception e1) {
                e1.printStackTrace();
                logger.error("出错",e1);
            }
            result.put("type", "text");
            result.put("resp", "抱歉，花花没有找到您想要的，请换个试试！");
        }
            return JSON.toJSONString(result);
    }

    /**
     * 穿山甲机器人多轮回话逻辑控制。
     * @param msgData 机器人翻译的汉字输入。
     * @param result  返回给机器人的结果
     */
    @Override
    public String manisRobotMultiDialog(String msgData, JSONObject result) {
        String my_name = "manisrobot@fantasia";
        String my_imei ="abc123456789";
        String converKey = my_name+":"+my_imei;
        logger.info("Now User ModelKey：" + converKey);
        Boolean isSlotNull = false;
        try {
            String awaken = jdbcUtils.getAwakenWord();
            JSONObject msgObj = JSON.parseObject(msgData);
            String type = msgObj.getString("type"); // 返回语言/文字的类型。
            String voicetext = msgObj.getString("resp"); // 文本内容（标识语意槽修改，手动输入，值为input）
            String open_id = "robot123456789"; // 设备编号 opend_id
            String inslot_type = msgObj.getString("slot_type"); //语意槽类型。
            // 如果语音内容是 你可以做什么
            if (helpUtils.isDoSomething(voicetext)) {
                List<Function> list = jdbcUtils.getFunctions();
                result.put("resp", list);
                result.put("type", "fun");
                // 语音唤醒
            } else if (voicetext.contains(awaken)) {
                String content ="您好，主人我在，请问有什么我可以帮助您的！";
                result.put("resp", content);
                result.put("type", "text");
            } else {
                // 判断当前用户是否已经存在对话语义问答模板对象。
                String converModel = redisClient.get(1, converKey); //当前用户存储的会话模板。
                if (StringUtils.isEmpty(converModel)) {
                    JSONObject retObj = pulanAiServer.aiServer(voicetext, open_id); //请求语义理解服务。
                    logger.info("PlServer语义结果:" + retObj.getString("retObj"));
                    // 语义理解能识别
                    if (StringUtils.isEmpty(retObj.getString("error"))) {
                        JSONObject retJson = retObj.getJSONObject("retObj");
                        String service = retJson.getString("service");
                        JSONArray semanticAry;
                        JSONObject semanticObj;
                        // 判断是否存在semantic语意键
                        if (retJson.containsKey("semantic")) {
                            semanticAry = retJson.getJSONArray("semantic");
                            semanticObj = semanticAry.getJSONObject(0);
                        } else {
                            throw new Exception("semantic is null");
                        }
                        JSONObject slots = null;
                        if (semanticObj.containsKey("slots")) {
                            slots = semanticObj.getJSONArray("slots").getJSONObject(0);
                        }
                        String intent = semanticObj.getString("intent");
                        //根据意图去查询语义模版是否存在
                        Boolean isModelExist = jdbcUtils.isSemanticModelExist(service, intent);
                        if (isModelExist) {
                            List<SemanticSlots> semsot = jdbcUtils.getSemanticSlot(intent, service);
                            redisClient.saveSemanticModel(1, converKey, semsot);// 存储会话空模版
                            // 处理语义槽值, 没有任何语义槽时，直接返回第一个模板问题
                            if (slots == null || slots.isEmpty()) {
                                SemanticSlots slotObj = semsot.get(0);
                                // 语义槽为空返回第一个问题。
                                String question = slotObj.getPrompt();
                                result.put("resp", question);
                                result.put("slot_type", slotObj.getSlotName());
                                result.put("type", "robot");
                            } else {
                                List<SemanticSlots> slotList = new ArrayList<>();
                                for (SemanticSlots sst : semsot) {
                                    String slotName = sst.getSlotName();
                                    if (slots.containsKey(slotName)) {
                                        String slot_value = slots.getString(slotName);
                                        sst.setSlotValue(slot_value);
                                    }
                                    slotList.add(sst);
                                }
                                // 存储填充后的模板
                                redisClient.saveSemanticModel(1, converKey, slotList);
                                // 再次遍历模板槽，查找是否存在空的槽
                                for (SemanticSlots sst : slotList) {
                                    String jsr_value = sst.getSlotValue();
                                    // 发现空槽，返回问题，提示用户回答
                                    if (StringUtils.isEmpty(jsr_value)) {
                                        String question = sst.getPrompt();
                                        result.put("resp", question);
                                        result.put("slot_type", sst.getSlotName());
                                        result.put("type", "robot");
                                        isSlotNull = true;
                                        break;
                                    }
                                }
                                // 1、判断第一次会话对象语义槽是否有null值。
                                // 2、语意槽没有null 值 则执行获取数据的服务，返回给用户数据，并且删除之前存储的用户语意模板。
                                // 3、此处是单论对话的处理逻辑。
                                if (!isSlotNull) {
                                    String tempfServer = slotList.get(0).getTemplateService();//语义理解服务类型
                                    result = getAppResultData(tempfServer,converKey,slotList,my_imei,my_name,result,null);
                                }
                            }
                        } else {
                            //找不到相符合的语义模版,调用图灵机器人。
                            redisClient.del(1, converKey);
                            result.put("resp", "");
                            result.put("error", "0000");
                        }
                        // 语义识别理解不了的情况处理。
                    } else {
                        // 语意理解错误处理。
                        redisClient.del(1, converKey);
                        result.put("resp", "");
                        result.put("error", "0000");
                    }
                    /**
                     * 2、此次会话之前走过plserver语义理解，回话对象存在。
                     * 2.1 获取会话对象，遍历属性为null的语义槽，返回对应的语义槽名称 获取到该语义槽的问题，返回给用户。
                     * 2.2 填充语义槽值。
                     */
                } else {
                    // 反问用户相关语义槽的值，填充redis存储的回话对象。
                    boolean find = false; // 是否找到对应值的空槽
                    List<SemanticSlots> secondSSot = JSON.parseArray(converModel, SemanticSlots.class); // 获取用户上次模板对话内容转换 list
                    isSlotNull = false;
                    boolean isBreak = false;
                    boolean isError = false;
                    boolean isTryAgain = false;
                    for (int i = 0; i < secondSSot.size(); i++) {
                        SemanticSlots smst = secondSSot.get(i);
                        String slot_value = smst.getSlotValue();
                        //循环遍历模板将第一个为空的语意槽的问题返回。
                        if (StringUtils.isEmpty(slot_value)) {
                            // 在下一个空槽时返回问题给用户回答
                            if (find) {
                                result.put("resp", smst.getPrompt());
                                result.put("type", "robot");
                                result.put("slot_type", smst.getSlotName());
                                isSlotNull = true;
                                break;
                            } else {
                                //语义模版语义槽值的填充，可以修改，人名输入默认正确，日期重新请求获取。
                                if (StringUtils.isEmpty(inslot_type)) {
                                    smst.setSlotValue(voicetext);
                                } else {
                                    switch (inslot_type) {
                                        case "date":
                                            String dateString = iSlotsReplaceService.slotInputReplace(voicetext,smst.getUtterance(),inslot_type);
                                            if (dateString !=null){
                                                String pattern = "\\d{4}-\\d{2}-\\d{2}";
                                                boolean isMatch = Pattern.matches(pattern, dateString);
                                                if (isMatch) {
                                                    smst.setSlotValue(dateString);
                                                    secondSSot.add(i, smst);
                                                    secondSSot.remove(i + 1);
                                                } else {
                                                    result.put("resp", "抱歉，您提供的日期'" + voicetext + "'格式不正确，请重试日期！");
                                                    result.put("type", "try");
                                                    int try_times = smst.getTryCount();
                                                    smst.setTryCount(try_times - 1);
                                                    smst.setSlotValue("");
                                                    secondSSot.add(i, smst);
                                                    secondSSot.remove(i + 1);
                                                    isBreak = true;
                                                    if (try_times <= 0) {
                                                        result.put("resp", "抱歉，花花已经很努力了还是无法解析您提供的日期格式，本轮会话结束！");
                                                        result.put("type", "text");
                                                        isError = true;
                                                    } else {
                                                        result.put("slot_type", inslot_type);
                                                        isTryAgain = true;
                                                    }
                                                }
                                            }else {
                                                result.put("resp", "抱歉，您提供的日期'" + voicetext + "'格式不正确，请重试日期！");
                                                result.put("type", "try");
                                                int try_times = smst.getTryCount();
                                                smst.setTryCount(try_times - 1);
                                                smst.setSlotValue("");
                                                secondSSot.add(i, smst);
                                                secondSSot.remove(i + 1);
                                                isBreak = true;
                                                if (try_times <= 0) {
                                                    result.put("resp", "抱歉，花花已经很努力了还是无法解析您提供的日期格式，本轮会话结束！");
                                                    result.put("type", "text");
                                                    isError = true;
                                                } else {
                                                    result.put("slot_type", inslot_type);
                                                    isTryAgain = true;
                                                }
                                            }
                                            break;
                                        case "datetime":
                                            String dateTimeString = iSlotsReplaceService.slotInputReplace(voicetext, smst.getUtterance(), inslot_type);
                                            if (dateTimeString !=null){
                                                String pattern2 = "\\d{4}-\\d{2}-\\d{2}\\s\\d{2}:\\d{2}:\\d{2}";
                                                boolean isMatchDateTime = Pattern.matches(pattern2, dateTimeString);
                                                if (isMatchDateTime) {
                                                    smst.setSlotValue(dateTimeString);
                                                    secondSSot.add(i, smst);
                                                    secondSSot.remove(i + 1);
                                                } else {
                                                    result.put("resp", "抱歉，您提供的时间'" + dateTimeString + "'格式不正确，请重试日期！");
                                                    result.put("type", "try");
                                                    int try_times = smst.getTryCount();
                                                    smst.setTryCount(try_times - 1);
                                                    smst.setSlotValue("");
                                                    secondSSot.add(i, smst);
                                                    secondSSot.remove(i + 1);
                                                    isBreak = true;
                                                    if (try_times <= 0) {
                                                        result.put("resp", "抱歉，花花已经很努力了还是无法解析您提供的日期格式，本轮会话结束！");
                                                        result.put("type", "text");
                                                        isError = true;
                                                    } else {
                                                        result.put("slot_type", inslot_type);
                                                        isTryAgain = true;
                                                    }
                                                }
                                            }else {
                                                result.put("resp", "抱歉，您提供的时间'" + voicetext + "'格式不正确，请重试日期！");
                                                result.put("type", "try");
                                                int try_times = smst.getTryCount();
                                                smst.setTryCount(try_times - 1);
                                                smst.setSlotValue("");
                                                secondSSot.add(i, smst);
                                                secondSSot.remove(i + 1);
                                                isBreak = true;
                                                if (try_times <= 0) {
                                                    result.put("resp", "抱歉，花花已经很努力了还是无法解析您提供的日期格式，本轮会话结束！");
                                                    result.put("type", "text");
                                                    isError = true;
                                                } else {
                                                    result.put("slot_type", inslot_type);
                                                    isTryAgain = true;
                                                }
                                            }

                                            break;
                                        case "person":
                                            String tempService = smst.getTemplateService();
                                            if (tempService.equals("Phone") || tempService.equals("Message")) { //打电话,发短信人名不需要查询数据库。
                                                smst.setSlotValue(voicetext);
                                            } else {
                                                if (voicetext.contains("我")) {
                                                    smst.setSlotValue("my");
                                                    secondSSot.add(i, smst);
                                                    secondSSot.remove(i + 1);
                                                } else {
                                                    String personName = iSlotsReplaceService.slotInputReplace(voicetext, smst.getUtterance(), inslot_type);
                                                    String email_name = jdbcUtils.getEmailByCnName(personName);
                                                    if (StringUtils.isEmpty(email_name)) {
                                                        String pinyin = hanZi2PinYingUtil.getNamePinYin(personName);
                                                        List<String> unameList = hanZi2PinYingUtil.getCnNameByPinyin(pinyin);
                                                        if (unameList.size() > 1) {
                                                            result.put("resp", unameList);
                                                            result.put("content", "请选择您想要查询的人员名称！");
                                                            result.put("type", "text");
                                                            result.put("types", "list");
                                                            logger.info("manisRobotMultiDialog1出现重复人名：" + result);
                                                            isBreak = true;
                                                            isTryAgain = true;
                                                            smst.setSlotValue("");
                                                            secondSSot.add(i, smst);
                                                            secondSSot.remove(i + 1);
                                                            // 存在多人名同音问题,重新让用户选择
                                                        } else if (unameList.size() == 1) {
                                                            email_name = jdbcUtils.getEmailByCnName(unameList.get(0));
                                                            smst.setSlotValue(email_name);
                                                            secondSSot.add(i, smst);
                                                            secondSSot.remove(i + 1);
                                                        } else {
                                                            String email_name2 = jdbcUtils.getEmailByCnName(voicetext);
                                                            if (StringUtils.isEmpty(email_name2)) {
                                                                String pinyin2 = hanZi2PinYingUtil.getNamePinYin(voicetext);
                                                                List<String> unameList2 = hanZi2PinYingUtil.getCnNameByPinyin(pinyin2);
                                                                if (unameList2.size() > 1) {
                                                                    result.put("resp", unameList2);
                                                                    result.put("content", "请选择您想要查询的人员名称！");
                                                                    result.put("type", "text");
                                                                    result.put("types", "list");
                                                                    logger.info("出现重复人名：" + result);
                                                                    isBreak = true;
                                                                    isTryAgain = true;
                                                                    smst.setSlotValue("");
                                                                    secondSSot.add(i, smst);
                                                                    secondSSot.remove(i + 1);
                                                                    // 存在多人名同音问题,重新让用户选择
                                                                } else if (unameList2.size() == 1) {
                                                                    email_name2 = jdbcUtils.getEmailByCnName(unameList2.get(0));
                                                                    smst.setSlotValue(email_name2);
                                                                    secondSSot.add(i, smst);
                                                                    secondSSot.remove(i + 1);
                                                                } else {
                                                                    result.put("resp", "抱歉，没有找到关于'" + voicetext + "'的人员信息，请重试要查询的人名！");
                                                                    result.put("type", "try");
                                                                    result.put("content", voicetext);
                                                                    result.put("slot_type", inslot_type);
                                                                    smst.setSlotValue("");
                                                                    int try_times = smst.getTryCount();
                                                                    smst.setTryCount(try_times - 1);
                                                                    secondSSot.add(i, smst);
                                                                    secondSSot.remove(i + 1);
                                                                    isBreak = true;
                                                                    if (try_times <= 0) {
                                                                        result.put("resp", "抱歉，花花已经很努力了还是没有找到关于'" + voicetext + "'的人员信息，本轮会话结束！");
                                                                        result.put("type", "text");
                                                                        isError = true;
                                                                    } else {
                                                                        result.put("slot_type", inslot_type);
                                                                        isTryAgain = true;
                                                                    }
                                                                }
                                                            } else {
                                                                smst.setSlotValue(email_name2);
                                                                secondSSot.add(i, smst);
                                                                secondSSot.remove(i + 1);
                                                            }
                                                        }
                                                    } else {
                                                        smst.setSlotValue(email_name);
                                                        secondSSot.add(i, smst);
                                                        secondSSot.remove(i + 1);
                                                    }
                                                }
                                            }
                                            break;
                                        default:
                                            String slotValue = iSlotsReplaceService.slotInputReplace(voicetext,smst.getUtterance(),inslot_type);
                                            if (slotValue != null) {
                                                smst.setSlotValue(slotValue);
                                                secondSSot.add(i, smst);
                                                secondSSot.remove(i + 1);
                                            } else {
                                                result.put("resp", "抱歉，您提供的'" + voicetext + "'和要求的格式不相符，请重试！");
                                                result.put("type", "try");
                                                int try_times = smst.getTryCount();
                                                smst.setTryCount(try_times - 1);
                                                smst.setSlotValue("");
                                                secondSSot.add(i, smst);
                                                secondSSot.remove(i + 1);
                                                isBreak = true;
                                                if (try_times <= 0) {
                                                    result.put("resp", "抱歉，花花已经很努力了还是不能理解您提供的内容，本轮会话结束！");
                                                    result.put("type", "text");
                                                    isError = true;
                                                } else {
                                                    result.put("slot_type", inslot_type);
                                                    isTryAgain = true;
                                                }
                                            }
                                            break;
                                    }
                                }
                                redisClient.saveSemanticModel(1, converKey, secondSSot);
                                find = true;
                            }
                        }
                        if (isBreak) {
                            break;
                        }
                    }
                    if (isError) {
                        redisClient.del(1, converKey);
                        return JSON.toJSONString(result);
                    } else if (isTryAgain || isSlotNull) {
                        return JSON.toJSONString(result);
                    }
                    // 1、所有语意槽填满以后去获取业务数据。
                    // 2、多轮对话完成获取返回数据。
                    if (!isSlotNull) {
                        //满值语意槽处理拉取数据。
                        String tempService = secondSSot.get(0).getTemplateService();
                        result =getAppResultData(tempService,converKey,secondSSot,my_imei,my_name,result,null);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("语意技能出错Error：" + e.getMessage());
            try {
                result.put("type", "text");
                result.put("resp", "抱歉，花花没有找到您想要的换个试试！");
                redisClient.del(1, converKey);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        return JSON.toJSONString(result);
    }

    private JSONObject getAppResultData(String templateService, String converKey, List<SemanticSlots> slotList,
                                        String my_imei, String my_name, JSONObject result,String mailtoken){
        try {
            switch (templateService){
                case "AirConditioner": //空调控制
                    result = iSkillsResService.airConditioner(templateService ,slotList ,converKey ,result);
                    break;
                case "Phone": //电话
                    result = iSkillsResService.oneSlotSkill(templateService ,slotList ,result);
                    redisClient.del(1,converKey);
                    break;
                case "Reminder": //提醒
                    result = iSkillsResService.reminderDoSomething(slotList ,my_imei ,result, my_name);
                    redisClient.del(1, converKey);
                    break;
                case "VisitWebsite": //浏览网页
                    result = iSkillsResService.openApplications(templateService ,slotList ,result);
                    redisClient.del(1, converKey);
                    break;
                case "MeetingRoom": //预定会议室
                    result = iSkillsResService.oneSlotSkill(templateService ,slotList ,result);
                    redisClient.del(1, converKey);
                    break;
                //发短信 //发邮件
                case "Message": case "Email":
                    result = iSkillsResService.sendMessage(templateService ,slotList, converKey, result, mailtoken);
                    break;
                case "Estate": //房地产销售
                    result = iSkillsResService.oneSlotSkill(templateService ,slotList ,result);
                    redisClient.del(1, converKey);
                    break;
                case "Express": //查快递
                    result = iSkillsResService.chackExpress(templateService, slotList, converKey, result);
                    break;
                default: //默认都是花样年业务数据查询技能。
                    result = iSkillsResService.hynManyDialogueRes(slotList, converKey, my_name, result);
                    break;
            }
        }catch (Exception e){
            result.put("resp","MultiDialog Error!" +e.getMessage());
            result.put("type","error");
        }
        return result;
    }

    /*private JSONObject getAppResultData(String templateService ,String converKey ,List<SemanticSlots> slotList ,
                                        String my_imei ,String my_name ,JSONObject result){
        try {
            switch (templateService){
                case "Attendance": //查考勤
                    result = iSkillsResService.hynManyDialogueRes(slotList, converKey,my_name, result);
                    break;
                case "Meeting": //查会议
                    result = iSkillsResService.hynManyDialogueRes(slotList, converKey,my_name, result);
                    break;
                case "Performance": //查饱和度
                    result = iSkillsResService.hynManyDialogueRes(slotList, converKey,my_name, result);
                    break;
                case "Task": //查待办任务
                    result = iSkillsResService.hynManyDialogueRes(slotList, converKey,my_name, result);
                    break;
                case "Schedule": //查日程安排
                    result = iSkillsResService.hynManyDialogueRes(slotList, converKey,my_name, result);
                    break;
                case "ApprovalProgress": //查待审批流程
                    result = iSkillsResService.hynManyDialogueRes(slotList, converKey,my_name, result);
                    break;
                case "Review": //查待阅事项
                    result = iSkillsResService.hynManyDialogueRes(slotList, converKey,my_name, result);
                    break;
                case "AirConditioner": //空调控制
                    result = iSkillsResService.airConditioner(templateService ,slotList ,converKey ,result);
                    break;
                case "Phone": //电话
                    result = iSkillsResService.oneSlotSkill(templateService ,slotList ,result);
                    redisClient.del(1,converKey);
                    break;
                case "Reminder": //提醒
                    result = iSkillsResService.reminderDoSomething(slotList ,my_imei ,result);
                    redisClient.del(1, converKey);
                    break;
                case "VisitWebsite": //浏览网页
                    result = iSkillsResService.openApplications(templateService ,slotList ,result);
                    redisClient.del(1, converKey);
                    break;
                case "MeetingRoom": //预定会议室
                    result = iSkillsResService.oneSlotSkill(templateService ,slotList ,result);
                    redisClient.del(1, converKey);
                    break;
                case "Message": //发短信
                    result = iSkillsResService.sendMessage(templateService ,slotList, converKey, result);
                    break;
                case "Email": //发邮件
                    result = iSkillsResService.sendMessage(templateService ,slotList ,converKey ,result);
                    break;
                case "Estate": //房地产销售
                    result = iSkillsResService.oneSlotSkill(templateService ,slotList ,result);
                    redisClient.del(1, converKey);
                    break;
                default:
                    result.put("resp","没有处理的语义模板，请联系管理员。");
                    result.put("type","text");
                    break;
            }
        }catch (Exception e){
            result.put("resp","MultiDialog Error!" +e.getMessage());
            result.put("type","error");
        }
        return result;
    }*/
}
