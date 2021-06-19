package com.nanaiii.useservice.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.util.Date;

import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

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

    private int avoidTime;

    private int state;
}
