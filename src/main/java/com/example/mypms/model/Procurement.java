package com.example.mypms.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class Procurement {
    private int pid;
    private int qid;
    private String p_uid;
    private String p_name;
    private String v_uid;
    private String v_name;
    private String product_name;
    private int amount;
    private double total_price;
    private int status;
    private String start_time;
    private String cname;
    @JsonIgnore
    private String cpath;
    @JsonIgnore
    private String cexpire_time;
}
