package com.nanaiii.aclservice.service;

import com.nanaiii.aclservice.entity.AclUser;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author nanaiii
 * @since 2021-05-22
 */
public interface AclUserService extends IService<AclUser> {

    AclUser login(AclUser member);
}
