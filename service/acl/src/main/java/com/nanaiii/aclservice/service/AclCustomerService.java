package com.nanaiii.aclservice.service;

import com.nanaiii.aclservice.entity.AclCustomer;
import com.baomidou.mybatisplus.extension.service.IService;
import com.nanaiii.aclservice.entity.vo.RegisterVo;

import java.security.acl.Acl;

/**
 * <p>
 * 顾客表 服务类
 * </p>
 *
 * @author nanaiii
 * @since 2021-05-22
 */
public interface AclCustomerService extends IService<AclCustomer> {

    //登录的方法
    AclCustomer login(AclCustomer member);

    //注册的方法
    void register(String  mobile);
}
