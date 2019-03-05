package com.pulan.dialogserver.utils;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

@Component
public class HttpClient {
    public String postRequest(String url, String body) {
        String result;
        BufferedReader reader;
        try {
            URL apiUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            if (body != null) {
                PrintWriter writer = new PrintWriter(connection.getOutputStream());
                writer.write(body);
                writer.flush();
                writer.close();
            }
            connection.connect();
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            String line;
            StringBuffer buffer = new StringBuffer();
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            reader.close();
            result = buffer.toString();
            return result;
        } catch (Exception e) {
            return null;
        }
    }

    public String postRequest(String url, String body, String contentType, String mailtoken) {
        String result;
        BufferedReader reader;
        try {
            URL apiUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setConnectTimeout(7000);
            connection.setReadTimeout(7000);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", contentType+"; boundary=----footfoodapplicationrequestnetwork");
            connection.setRequestProperty("chatset", "UTF-8");
            connection.setRequestProperty("mail-token", mailtoken);
            if(body != null){
                PrintWriter writer = new PrintWriter(connection.getOutputStream());
                writer.write(body);
                writer.flush();
                writer.close();
            }
            connection.connect();
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            String line;
            StringBuffer buffer = new StringBuffer();
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }

            result = buffer.toString();
            if (connection != null) {
                connection.disconnect();
            }
            reader.close();
            return result;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public static void main(String[] args) {
        String result;
        BufferedReader reader;
        try {
            URL apiUrl = new URL("http://oatest.cnfantasia.com:8090/hyn/ht/meeting/hynHtMeeting.do?method=addReviewWithJson&fdTemplateId=166ee40d15518f5451bf85a47f4b15ba&docCreator={\"LoginName\":\"luolp\"}&formValues={\"docContent\":\"一题\",\"docDeptId\":\"OU\\u003dAP集团信息部,OU\\u003dA花样年集团（中国）有限公司,OU\\u003d花样年控股集团有限公司,DC\\u003dhyn,DC\\u003dcom\",\"fdEmceeId\":\"chenpc\",\"fdFinishDate\":\"2019-02-20\",\"fdFinishTime\":\"17:45\",\"fdHoldDate\":\"2019-02-20\",\"fdHoldPlaceId\":\"1419ae5a52e8ba414f69e3c411a89d6e\",\"fdHoldTime\":\"17:45\",\"fdHostId\":\"chenpc\",\"fdKmMeetingMainAttendPersonId\":\"chenpc;liyang1\",\"fdKmMeetingMainCopyToPersonIds\":\"zhanghuay;wanghj1\",\"fdNotifyType\":\"todo\",\"meetingType\":\"166ee40d15518f5451bf85a47f4b15ba\",\"type\":\"2\"}");
            HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();
            connection.setRequestProperty("Cookie", "LRToken=6a1f8708744f7eb69144c41b3eee13d094751676c24a09249b5b62e9eb7128cd2d0077068efeecb6375906af887e176ea93a9d1908b457d74a8818229f2f960a0f6b332e384210cb7b49cd343b8e2ede5d2f4b4556adaeaac545684b7a0c0a504428e485b820706e5ed7b82bb8ba543d3cd2b6ef2ed8c55c6c4314b03f0ed28a;");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("chatset", "UTF-8");
            connection.connect();
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            String line;
            StringBuffer buffer = new StringBuffer();
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            reader.close();

            if (connection != null)
                connection.disconnect();
            connection = null;
            result = buffer.toString();
            System.out.println(result);
        } catch (Exception e) {

        }
    }
}
