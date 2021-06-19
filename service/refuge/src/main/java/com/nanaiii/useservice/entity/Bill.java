package com.nanaiii.useservice.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.util.Date;

import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.swing.*;

/**
 * <p>
 * 会员表
 * </p>
 *
 * @author nanaiii
 * @since 2021-05-24
 */
@Data
public class Bill {

    private String billId;

    private String startTime;

    private String stopTime;

    private String roomId;

    private String userId;

    private Double fee;

    public Bill(String startTime,String stopTime,String roomId,String userId,Double fee){
        this.startTime = startTime;
        this.stopTime = stopTime;
        this.roomId = roomId;
        this.userId = userId;
        this.fee = fee;
    }
}
