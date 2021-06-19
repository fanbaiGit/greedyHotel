package com.nanaiii.useservice.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;

/**
 * <p>
 * 
 * </p>
 *
 * @author nanaiii
 * @since 2021-05-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="Log对象", description="")
public class Log implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableField(fill = FieldFill.INSERT)
    private Date opTime;

    @TableField("room_id")
    private String roomId;

    @ApiModelProperty(value = "操作请求(请求送风为1、请求停止送风为0,办理入住为2，办理退房为3)")
    @TableField("operation")
    private Integer operation;

    @ApiModelProperty(value = "风速(高风为3，中风为2，低风为1)")
    @TableField("wind_speed")
    private String windSpeed;

    public Log(String room_id, Integer op, String ws){
        this.roomId= room_id;
        this.operation = op;
        this.windSpeed = ws;
    }

    public Log(){}


}
