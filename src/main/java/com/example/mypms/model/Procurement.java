package com.example.mypms.model;

import lombok.Data;

@Data
public class Procurement {
    private int pid;
    private String p_uid;
    private String v_uid;
    private int state;
    private String start_time;
    private String cname;
    private String cpath;
    private String cexpire_time;
}
