package com.example.mypms;

import com.example.mypms.service.MailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MyPmsApplicationTests {
    @Autowired
    private MailService mailService;

    @Test
    void contextLoads() {
    }

    @Test
    public void testEmail() {
        assert mailService.sendMailNotice("采购员", "3154529265@qq.com", "this is a test").equals("success");
    }

}
