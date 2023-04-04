package com.example.mypms.model;

import lombok.Data;

import java.util.ArrayList;

@Data
public class BasicInfo {
    private int demands_count;
    private int quotes_count;
    private int processing_count;
    private float current_rate;
    private ArrayList<String> labels;
    private ArrayList<Integer> counts;
}
