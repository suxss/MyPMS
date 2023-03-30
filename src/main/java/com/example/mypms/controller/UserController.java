package com.example.mypms.controller;

import com.example.mypms.model.Purchaser;
import com.example.mypms.model.ResultJson;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.mypms.service.UserService;

import javax.annotation.security.PermitAll;
import java.util.Map;

@RestController
public class UserController {
    @Autowired
    UserService userService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @PermitAll
    @RequestMapping(value = "/p_register", method = RequestMethod.POST)
    public ResultJson RegisterPurchaser(@RequestParam Map<String, Object> params) {
        int r = userService.insertPurchaser((String) params.get("username"), (String) params.get("email"), (String) params.get("password"));
        String msg = (r > 0) ? "注册成功" : "邮箱已被注册";
        logger.info("p_register >> email:" + params.get("email") + ", msg: " + msg);
        Map data = Map.of("url", "/login.html");
        ResultJson resultJson = new ResultJson((r > 0) ? 0 : -1, msg, data);
        return resultJson;
    }

    @PermitAll
    @RequestMapping(value = "/v_register", method = RequestMethod.POST)
    public ResultJson RegisterVendor(@RequestParam Map<String, Object> params) {
        int r = userService.insertVendor((String) params.get("username"), (String) params.get("email"), (String) params.get("password"), (String) params.get("phone"), (String) params.get("address"));
        String msg = (r > 0) ? "注册成功" : "邮箱已被注册";
        logger.info("v_register >> email:" + params.get("email") + ", msg: " + msg);
        Map data = Map.of("url", "/login.html");
        ResultJson resultJson = new ResultJson((r > 0) ? 0 : -1, msg, data);
        return resultJson;
    }

}
