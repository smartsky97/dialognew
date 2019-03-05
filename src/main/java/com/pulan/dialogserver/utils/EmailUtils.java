package com.pulan.dialogserver.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class EmailUtils {
    /*public static void main(String[] args) {
        @Autowired
        JavaMailSender mailSender;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("dyc87112@qq.com");
        message.setTo("dyc87112@qq.com");
        message.setSubject("主题：简单邮件");
        message.setText("测试邮件内容");

        mailSender.send(message);
    }*/
}
