package com.nanaiii.useservice.entity;

import lombok.Data;

@Data
public class Report {
    private String time;

    private Integer customerNum;

    private Integer midNum;

    private Integer lowNum;

    private Integer highNum;

    public Report(String startTime, Integer num, Integer lowNum, Integer midNum, Integer highNum) {
        this.time = startTime;
        this.customerNum = num;
        this.lowNum = lowNum;
        this.midNum = midNum;
        this.highNum = highNum;
    }
}
