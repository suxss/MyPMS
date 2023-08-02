package com.example.mypms.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Service
@EnableAsync
public class MailService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private JavaMailSender mailSender;

    @Value(value = "${spring.mail.username}")
    private static String MAIL_FROM;

    private String sendHtmlMail(String email_address, String subject, String content) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setFrom(new InternetAddress(MAIL_FROM));
            helper.setTo(email_address);
            helper.setSubject(subject);
            helper.setText(content, true);
            mailSender.send(mimeMessage);
            return "success";
        } catch (Exception e) {
            logger.error("发送邮件失败", e);
            return "failure";
        }
    }

    public String sendMailNotice(String username, String email_address, String content) {
        String template = "<div style=\"border-radius: 10px 10px 10px 10px;font-size:14px;color: #555555;width: 666px;font-family:'Century Gothic','Trebuchet MS','Hiragino Sans GB',微软雅黑,'Microsoft Yahei',Tahoma,Helvetica,Arial,'SimSun',sans-serif;margin:50px auto;border:1px solid #eee;max-width:100%;background: #ffffff repeating-linear-gradient(-45deg,#fff,#fff 1.125rem,transparent 1.125rem,transparent 2.25rem);box-shadow: 0 1px 5px rgba(0, 0, 0, 0.15);\">\n" +
                "\t<div style=\"width:100%;background:#49BDAD;color:#ffffff;border-radius: 10px 10px 0 0;background-image: -moz-linear-gradient(0deg, rgb(67, 198, 184), rgb(255, 209, 244));background-image: -webkit-linear-gradient(0deg, rgb(67, 198, 184), rgb(255, 209, 244));height: 66px;\">\n" +
                "\t<p style=\"font-size:15px;word-break:break-all;padding: 23px 32px;margin:0;background-color: hsla(0,0%,100%,.4);border-radius: 10px 10px 0 0;\">尊敬的<a style=\"text-decoration:none;color: #ffffff;\" target=\"_blank\">" + username + "</a>:</p>\n" +
                "\t</div>\n" +
                "\t\t<div style=\"margin:40px auto;width:90%\"><p>" + content + "</p>\n" +
                "\t\t<hr>\n" +
                "\t\t<span class=\"text_6\" style=\"font-size: 12px;  font-family: PingFangSC-Regular, PingFang SC;  font-weight: 400;  color: #00000045;\">\n" +
                "\t\t\t此邮件由系统自动发出，直接回复无效。\n" +
                "\t\t</span>" +
                "\t\t<p>\n" +
                "\t\t<span class=\"text_6\" style=\"  font-size: 12px;  font-family: PingFangSC-Regular, PingFang SC;  font-weight: 400;  color: #00000045;\">\n" +
                "\t\t\tby 采购管理系统\n" +
                "\t\t</span>\n" +
                "\t\t</p>" +
                "\t\t</div>\n" +
                "\t</div>\n" +
                "\t";
        return sendHtmlMail(email_address, "采购管理系统: 新通知", template);
    }

}
