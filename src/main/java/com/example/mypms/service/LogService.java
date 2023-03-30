package com.example.mypms.service;

import com.example.mypms.mapper.LogMapper;
import com.example.mypms.model.RequestLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LogService {
    @Autowired
    LogMapper logMapper;

    public int insertLog(String method, String path, String param, String ip, String uid) {
        RequestLog requestLog = new RequestLog(method, path, null, ip, uid);
        return logMapper.insertLog(requestLog);
    }

    public int deleteLog(String time) {
        return logMapper.deleteLog(time);
    }

    public int deleteLog() {
        String time = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
        return logMapper.deleteLog(time);
    }
}
