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

    public Airconditioning(int defaultRoomTemp, int tempHighLimit, int tempLowLimit, int defaultTargetTemp, double feeRateHigh, double feeRateMiddle, double feeRateLow, String defaultFunSpeed, int defaultRoomNum, double avoidTime, int state) {
        this.defaultRoomTemp = defaultRoomTemp;
        this.tempHighLimit = tempHighLimit;
        this.tempLowLimit = tempLowLimit;
        this.defaultTargetTemp = defaultTargetTemp;
        this.feeRateHigh = feeRateHigh;
        this.feeRateMiddle = feeRateMiddle;
        this.feeRateLow = feeRateLow;
        this.defaultFunSpeed = defaultFunSpeed;
        this.defaultRoomNum = defaultRoomNum;
        this.avoidTime = avoidTime;
        this.state = state;
    }
}
