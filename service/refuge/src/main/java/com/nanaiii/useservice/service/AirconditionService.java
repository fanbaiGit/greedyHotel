package com.nanaiii.useservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nanaiii.useservice.entity.Airconditioning;

/**
 * <p>
 * 会员表 服务类
 * </p>
 *
 * @author nanaiii
 * @since 2021-05-24
 */
public interface AirconditionService extends IService<Airconditioning>{
    void startAircondition(Airconditioning airconditioning);
}
