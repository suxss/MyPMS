package com.example.mypms.model;

import lombok.Data;

@Data
public class Purchaser extends User {
    private String uid;
    private String uname;
    private String email;
    private String pwd;
    private int rid=1;
    private float rate=0;
    private int rate_num=0;
}
