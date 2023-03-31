package com.example.mypms.model;

import lombok.Data;

@Data
public class ProcurementDemand {
    private int pdid;
    private String p_uid;
    private String uname;
    private String product_name;
    private String amount;
    private String expire_time;
    private String start_time;
    private String remark;
}
