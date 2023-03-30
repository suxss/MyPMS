package com.example.mypms.model;

import lombok.Data;

import java.math.BigInteger;

@Data
public class RequestLog {
    private BigInteger request_id;
    private String method;
    private String path;
    private String param;
    private String time;
    private String ip;
    private String uid;

    public RequestLog(String method, String path, String param, String ip, String uid) {
        this.method = method;
        this.path = path;
        this.param = param;
        this.time = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
        this.ip = ip;
        this.uid = uid;
    }
}
