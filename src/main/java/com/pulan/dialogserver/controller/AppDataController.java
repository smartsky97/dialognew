package com.pulan.dialogserver.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.pulan.dialogserver.entity.resp.ReturnMsg;
import com.pulan.dialogserver.entity.resp.Salary_info;
import com.pulan.dialogserver.entity.resp.WeatherMsg;
import com.pulan.dialogserver.entity.tulin.Weather3;
import com.pulan.dialogserver.shiro.entity.User;
import com.pulan.dialogserver.utils.HttpClient;
import com.pulan.dialogserver.utils.JdbcMysql_78;
import com.pulan.dialogserver.utils.JdbcUtils;
import com.pulan.dialogserver.utils.RedisClient;
import org.apache.commons.collections.map.HashedMap;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 */
@RestController
@RequestMapping(value = "/message")
public class AppDataController {
    private Logger logger = LogManager.getLogger(AppDataController.class);
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    private JdbcUtils jdbcUtils;

    @Autowired
    private HttpClient httpClient;

    @Autowired
    private RedisClient redisClient;


    @Autowired
    private JdbcMysql_78 jdbcMysql_78;


    /**
     *获取当天天气的情况
     * @param request
     * @param city
     * @return
     */
    @RequestMapping(value = "/getWeather")
    public Object getWeather(HttpServletRequest request, @RequestParam(required = false,value = "city") String city) {
        ReturnMsg returnMsg = new ReturnMsg();
        WeatherMsg weatherMsg = new WeatherMsg();
        Map<String,String> map = new HashedMap();

        try {
            if(city == null || city == ""){
                HttpSession session = request.getSession(false);
                if (session != null) {
                    User user = (User) session.getAttribute("user");
                    //        //从数据库获取登录地址
                    map = jdbcUtils.getAiUserLocation(user.getMail_name());
                    city = map.get("city");
                } else {
                    city = "深圳";
                }

            }

            String now_url = "https://free-api.heweather.com/v5/now?city="+city+"&key=1a224c73d7a040c690494714f615e9c3";
            JSONObject jsonObject = JSON.parseObject(httpClient.postRequest(now_url, null));

            logger.info(jsonObject);
            String tmp = jsonObject.getJSONArray("HeWeather5").getJSONObject(0).getJSONObject("now").getString("tmp");
            String cond_code = jsonObject.getJSONArray("HeWeather5").getJSONObject(0).getJSONObject("now").getJSONObject("cond").getString("code");
            String txt = jsonObject.getJSONArray("HeWeather5").getJSONObject(0).getJSONObject("now").getJSONObject("cond").getString("txt");

            weatherMsg.setDate(simpleDateFormat.format(new Date()));
            weatherMsg.setLocation(city);
            weatherMsg.setTmp(tmp);
            weatherMsg.setCond_code(cond_code);
            weatherMsg.setCond_txt(txt);
            returnMsg.setStatus(0);
            returnMsg.setType("weather");
            returnMsg.setResp(weatherMsg);
            return returnMsg;
        } catch (Exception e) {
            returnMsg.setStatus(-1);
            returnMsg.setType("weather");
            returnMsg.setResp("出现异常");

            return returnMsg;
        }


    }


    /**
     * 获取3天的天气情况
     * @param request
     * @param city
     * @return
     */
    @RequestMapping(value = "/getWeather3")
    public Object getWeatherBy3(HttpServletRequest request, @RequestParam(required = false,value = "city") String city) {
        ReturnMsg returnMsg = new ReturnMsg();
        WeatherMsg weatherMsg = new WeatherMsg();
        Map<String,String> map = new HashedMap();
        List<Weather3> list = new ArrayList<>();
        try {
            if(city == null || city == ""){
                HttpSession session = request.getSession(false);
                if (session != null) {
                    User user = (User) session.getAttribute("user");
                    //        //从数据库获取登录地址
                    map = jdbcUtils.getAiUserLocation(user.getMail_name());
                    city = map.get("city");
                } else {
                    city = "深圳";
                }

            }

            String now_url = "https://free-api.heweather.com/v5/now?city="+city+"&key=1a224c73d7a040c690494714f615e9c3";
            JSONObject jsonObject2 = JSON.parseObject(httpClient.postRequest(now_url, null));

            String fl = jsonObject2.getJSONArray("HeWeather5").getJSONObject(0).getJSONObject("now").getString("fl");


            String forecast_url = "https://free-api.heweather.com/v5/forecast?city="+city+"&key=1a224c73d7a040c690494714f615e9c3";

            JSONObject jsonObject = JSON.parseObject(httpClient.postRequest(forecast_url, null));

            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather5").getJSONObject(0).getJSONArray("daily_forecast");
            Weather3 weather3 = null;
            for (int i = 0; i < jsonArray.size() ; i++) {
                int wendu = Integer.parseInt(jsonArray.getJSONObject(i).getJSONObject("tmp").getString("max")) + Integer.parseInt(jsonArray.getJSONObject(i).getJSONObject("tmp").getString("min"));
                weather3 = new Weather3();
                weather3.setFl(fl);
                weather3.setDate(jsonArray.getJSONObject(i).getString("date"));
                weather3.setTmp(Integer.toString(wendu/2));
                weather3.setHum(jsonArray.getJSONObject(i).getString("hum"));
                weather3.setTmp_max(jsonArray.getJSONObject(i).getJSONObject("tmp").getString("max"));
                weather3.setTmp_min(jsonArray.getJSONObject(i).getJSONObject("tmp").getString("min"));
                weather3.setCond_txt_n(jsonArray.getJSONObject(i).getJSONObject("cond").getString("txt_n"));
                weather3.setWind_sc(jsonArray.getJSONObject(i).getJSONObject("wind").getString("sc"));
                weather3.setWind_dir(jsonArray.getJSONObject(i).getJSONObject("wind").getString("dir"));


                list.add(weather3);
            }
            returnMsg.setStatus(0);
            returnMsg.setType("weather");
            returnMsg.setResp(list);
            return returnMsg;
        } catch (Exception e) {
            returnMsg.setStatus(-1);
            returnMsg.setType("weather");
            returnMsg.setResp("出现异常");

            return returnMsg;
        }


    }

    /**
     * 考勤结果
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/getAttendanceResult", method = RequestMethod.POST)
    public ReturnMsg getAttendanceResult(HttpServletRequest request, @RequestParam(required = false, value = "data") String data
            ,@RequestParam(required = false, value = "mail_name") String mail_name) {
        ReturnMsg returnMsg = new ReturnMsg();
        returnMsg.setType("attendanceResult");
        try {
            HttpSession session = request.getSession(false);
            if (session == null) {
                returnMsg.setStatus(-1);
                returnMsg.setResp("session已过期");
                return returnMsg;
            } else {
                String name ="";
                User user = (User) session.getAttribute("user");
                if (!StringUtils.isEmpty(mail_name)) {
                    name = mail_name;
                }else {
                    name = user.getMail_name();
                }
                returnMsg.setResp(jdbcMysql_78.getAttendanceResult(name, data));
                return returnMsg;
            }

        } catch (Exception e) {
            returnMsg.setStatus(-1);
            returnMsg.setType("attendanceResult");
            returnMsg.setResp("出现异常");
            return returnMsg;
        }

    }

    /**
     * 薪资
     * @param request
     * @param data
     * @return
     */
    @RequestMapping(value = "/getSalaryInfo", method = RequestMethod.POST)
    public ReturnMsg getSalaryInfo(HttpServletRequest request, @RequestParam(required = false, value = "data") String data) {
        ReturnMsg returnMsg = new ReturnMsg();
        returnMsg.setType("salaryInfo");
        try {
            HttpSession session = request.getSession(false);
            if (session == null) {
                returnMsg.setStatus(-1);
                returnMsg.setResp("session已过期");
                return returnMsg;
            } else {
                User user = (User) session.getAttribute("user");
                Salary_info salary_info = jdbcMysql_78.getSalary_info(user.getMail_name(),data);
                if(salary_info != null){
                    returnMsg.setResp(salary_info);
                } else {
                    returnMsg.setResp("查无数据");
                }
                return returnMsg;
            }

        } catch (Exception e) {
            returnMsg.setStatus(-1);
            returnMsg.setType("salaryInfo");
            returnMsg.setResp("出现异常");
            return returnMsg;
        }

    }

    //发邮件的验证
    @RequestMapping(value = "/emaillogin")
    public Object emaillogin(@RequestParam(required = false, value = "accessToken")String accessToken,
                             @RequestParam(required = false, value = "emailAddress")String emailAddress,
                             @RequestParam(required = false, value = "userCode")String userCode) {
        String now_url = "http://192.168.0.67:9104/jq-exchange/pc/login-V2";
        String body = "{\"accessToken\":\""+accessToken+"\",\"emailAddress\":\""+emailAddress+"\",\"userCode\":\""+userCode+"\"}";
        System.out.println(body);
        JSONObject jsonObject = JSON.parseObject(httpClient.postRequest(now_url, body));
        System.out.println(jsonObject);
        return jsonObject;
    }

    //发送邮件
    @RequestMapping(value = "/sendEmailTo")
    public Object sendEmailTo(@RequestParam("mailtoken") String mailtoken, @RequestParam("to") String to, @RequestParam(value = "cc",required = false) String cc,
                              @RequestParam("title") String title, @RequestParam("text") String text) {
        logger.info("参数："+to);
        logger.info("参数："+cc);
        String sendemail = "http://192.168.0.67:9104/jq-exchange/send";
        mailtoken = "c1407764650842ad83284330cf84e9b4";
        String[] tos = to.split(",");
        StringBuffer sb = new StringBuffer();
        sb.append("[");
        for (int i=0;i<tos.length;i++){
            if (i==tos.length-1) {
                sb.append("{\"address\":\""+tos[i]+"\"}");
            }else {
                sb.append("{\"address\":\""+tos[i]+"\"},");
            }
        }
        sb.append("]");

        StringBuffer ccbuffer = new StringBuffer();
        if (!StringUtils.isEmpty(cc)) {
            String[] ccs = to.split(",");
            ccbuffer.append("[");
            for (int i=0;i<ccs.length;i++){
                if (i==ccs.length-1) {
                    ccbuffer.append("{\"address\":\""+ccs[i]+"\"}");
                }else {
                    ccbuffer.append("{\"address\":\""+ccs[i]+"\"},");
                }
            }
            ccbuffer.append("]");
        }

        StringBuffer buffer = new StringBuffer();
        buffer.append("------footfoodapplicationrequestnetwork\r\n");
        buffer.append("Content-Disposition: form-data; name=\"");
        buffer.append("toRecipients");
        buffer.append("\"\r\n\r\n");
        buffer.append(sb.toString());
        buffer.append("\r\n");

        if (!StringUtils.isEmpty(cc)) {
            buffer.append("------footfoodapplicationrequestnetwork\r\n");
            buffer.append("Content-Disposition: form-data; name=\"");
            buffer.append("ccRecipients");
            buffer.append("\"\r\n\r\n");
            buffer.append(ccbuffer.toString());
            buffer.append("\r\n");
        }

        buffer.append("------footfoodapplicationrequestnetwork\r\n");
        buffer.append("Content-Disposition: form-data; name=\"");
        buffer.append("subject");
        buffer.append("\"\r\n\r\n");
        buffer.append(title);
        buffer.append("\r\n");

        buffer.append("------footfoodapplicationrequestnetwork\r\n");
        buffer.append("Content-Disposition: form-data; name=\"");
        buffer.append("messageBody");
        buffer.append("\"\r\n\r\n");
        buffer.append(text);
        buffer.append("\r\n");

        buffer.append("------footfoodapplicationrequestnetwork--\r\n");

        JSONObject jsonObject = JSON.parseObject(httpClient.postRequest(sendemail, buffer.toString(),"multipart/form-data",mailtoken));
        logger.info("邮件信息："+jsonObject.toJSONString());
        return jsonObject;
    }
}
