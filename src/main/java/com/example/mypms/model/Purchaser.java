package com.example.mypms.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
public class Purchaser {
    private String uid;
    private String uname;
    private String email;
    @JsonIgnore
    private String pwd;
    private int rid = 1;
    private float rate = 0;
    private int rate_num = 0;
}
