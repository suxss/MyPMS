package com.example.mypms.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class Vendor {
    private String uid;
    private String uname;
    private String email;
    @JsonIgnore
    private String pwd;
    private int rid = 2;
    private float rate = 0;
    private int rate_num = 0;
    private String phone;
    private String address;
}
