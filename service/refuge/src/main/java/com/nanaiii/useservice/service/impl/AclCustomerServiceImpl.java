package com.nanaiii.useservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nanaiii.commonutils.MD5;
import com.nanaiii.servicebase.exceptionhandler.NanaiiiException;
import com.nanaiii.useservice.entity.AclCustomer;
import com.nanaiii.useservice.mapper.AclCustomerMapper;
import com.nanaiii.useservice.service.AclCustomerService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * <p>
 * 会员表 服务实现类
 * </p>
 *
 * @author nanaiii
 * @since 2021-05-24
 */
@Service
public class AclCustomerServiceImpl extends ServiceImpl<AclCustomerMapper, AclCustomer> implements AclCustomerService {
    //注册的方法
    @Override
    public void register(String  mobile,boolean is_disabled,String room_id) {

        //非空判断
        if(StringUtils.isEmpty(mobile)) {
            throw new NanaiiiException(20001,"用户名为空");
        }

        //判断手机号是否重复，表里面存在相同手机号不进行添加
        QueryWrapper<AclCustomer> wrapper = new QueryWrapper<>();
        wrapper.eq("mobile",mobile);
        Integer count = baseMapper.selectCount(wrapper);
        if(count > 0) {
            throw new NanaiiiException(20001,"用户名重复");
        }

        //数据添加数据库中
        AclCustomer member = new AclCustomer();
        member.setRoomId(room_id);
        member.setMobile(mobile);
        member.setPassword(MD5.encrypt(mobile));//密码需要加密的
        member.setIsDisabled(is_disabled);//新用户禁用
        member.setAvatar("http://thirdwx.qlogo.cn/mmopen/vi_32/DYAIOgq83eoj0hHXhgJNOTSOFsS4uZs8x1ConecaVOB8eIl115xmJZcT4oCicvia7wMEufibKtTLqiaJeanU2Lpg3w/132");
        baseMapper.insert(member);
    }
}
