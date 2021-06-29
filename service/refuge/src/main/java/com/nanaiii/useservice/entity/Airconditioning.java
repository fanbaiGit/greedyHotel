package com.nanaiii.useservice.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Data
public class Airconditioning {

    private int defaultRoomTemp;

    private int tempHighLimit;

    private int tempLowLimit;

    private int defaultTargetTemp;

    private double feeRateHigh;

    private double feeRateMiddle;

    private double feeRateLow;

    private String defaultFunSpeed;

    private int defaultRoomNum;

    private double avoidTime;

    private int state;

    private int timeSlice;

    public Airconditioning() {}

}
