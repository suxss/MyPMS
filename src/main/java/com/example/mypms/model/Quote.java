package com.example.mypms.model;

import lombok.Data;

@Data
public class Quote {
    private int qid;
    private int p_id;
    private String v_uid;
    private int amount;
    private float total_price;
    private String remark;
}
