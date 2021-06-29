package com.nanaiii.useservice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

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
@ApiModel(value="Room对象", description="")
public class Room implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableField("room_id")
    private String roomId;

    @ApiModelProperty(value = "风速(高风为3，中风为2，低风为1)")
    @TableField("wind_speed")
    private String windSpeed;

    @ApiModelProperty(value = "空调模式(制冷模式为1，制热模式为2，关机模式为0)")
    private Integer state;

    @TableField("is_disabled")
    private Boolean isDisabled;

    @TableField("now_temp")
    private Double nowTemp;

    @TableField("tar_temp")
    private Double tarTemp;

    @TableField("default_temp")
    private Double defaultTemp;

    @TableField(exist = false)
    private Double val;
}
