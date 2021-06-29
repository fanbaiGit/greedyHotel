package com.nanaiii.useservice.service;

import com.nanaiii.useservice.entity.AclCustomer;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 会员表 服务类
 * </p>
 *
 * @author nanaiii
 * @since 2021-05-24
 */
public interface AclCustomerService extends IService<AclCustomer> {
    //注册的方法
    void register(String  mobile,boolean is_disabled,String room_id);

    //查询房间住客
    AclCustomer findByRoom(String room_id);

    AclCustomer login(AclCustomer member);
}
