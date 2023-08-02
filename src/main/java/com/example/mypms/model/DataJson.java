package com.example.mypms.model;

import lombok.Data;

@Data
public class DataJson {
    private int code;
    private String msg;
    private int total;
    private Object data;
}
