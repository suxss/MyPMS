package com.example.mypms.model;

import lombok.Data;

@Data
public class ResultJson {
    private int code;
    private String msg;
    private Object data;

    public ResultJson(int code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public ResultJson(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ResultJson() {
    }
}
