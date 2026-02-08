package com.inn.cafe.utils;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailUtils {
    @Autowired
    private JavaMailSender mailSender;
    public void sendSimpleMessage(String to, String subject,String text, List<String> list) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("baazaouisami136@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        if (list != null && list.size() > 0) {
        message.setCc(getCcArray(list));}
        mailSender.send(message);

    }
    private String[] getCcArray(List<String> cclist) {
       String[] cc = new String[cclist.size()];
       for (int i = 0; i < cclist.size(); i++) {
           cc[i] = cclist.get(i);
       }
       return cc;
    }
    public void forgetMail(String to, String subject, String password) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom("baazaouisami136@gmail.com");
            helper.setTo(to);
            helper.setSubject(subject);

            String htmlMsg = "<p><b>Your Login details for Cafe Management System</b><br>"
                    + "<b>Email:</b> " + to + "<br>"
                    + "<b>Password:</b> " + password + "</p>";

            message.setContent(htmlMsg, "text/html");

            mailSender.send(message);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
