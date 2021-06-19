package com.nanaiii.aclservice.service.impl;

import com.nanaiii.aclservice.service.AclCustomerService;
import com.nanaiii.commonutils.JwtUtils;
import com.nanaiii.aclservice.entity.AclCustomer;
import com.nanaiii.aclservice.entity.vo.RegisterVo;
import com.nanaiii.aclservice.mapper.AclCustomerMapper;
import com.nanaiii.commonutils.MD5;
import com.nanaiii.servicebase.exceptionhandler.NanaiiiException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * <p>
 * 顾客表 服务实现类
 * </p>
 *
 * @author nanaiii
 * @since 2021-05-22
 */
@Service
public class AclCustomerServiceImpl extends ServiceImpl<AclCustomerMapper, AclCustomer> implements AclCustomerService {

    @Autowired
    private AclCustomerService memberService;

    //登录的方法
    @Override
    public AclCustomer login(AclCustomer member) {
        //获取登录手机号和密码
        String mobile = member.getMobile();
        String password = member.getPassword();

        //手机号和密码非空判断
        if(StringUtils.isEmpty(mobile) || StringUtils.isEmpty(password)) {
            throw new NanaiiiException(20001,"手机号或密码为空");
        }

        //判断手机号是否正确
        QueryWrapper<AclCustomer> wrapper = new QueryWrapper<>();
        wrapper.eq("mobile",mobile);
        AclCustomer mobileMember = baseMapper.selectOne(wrapper);
        //判断查询对象是否为空
        if(mobileMember == null) {//没有这个手机号
            throw new NanaiiiException(20001,"未查询到用户");
        }

        //判断密码
        //因为存储到数据库密码肯定加密的
        //把输入的密码进行加密，再和数据库密码进行比较
        //加密方式 MD5
        if(!MD5.encrypt(password).equals(mobileMember.getPassword())) {
            throw new NanaiiiException(20001,"密码错误");
        }

        //判断用户是否禁用
        if(mobileMember.getIsDisabled()) {
            throw new NanaiiiException(20001,"用户被禁止使用");
        }

        //登录成功
        //生成token字符串，使用jwt工具类
//        String jwtToken = JwtUtils.getJwtToken(mobileMember.getId(), mobileMember.getMobile());
//        return jwtToken;
        return mobileMember;
    }

    //注册的方法
    @Override
    public void register(String  mobile) {

        //非空判断
        if(StringUtils.isEmpty(mobile)) {
            throw new NanaiiiException(20001,"注册失败");
        }

        //判断手机号是否重复，表里面存在相同手机号不进行添加
        QueryWrapper<AclCustomer> wrapper = new QueryWrapper<>();
        wrapper.eq("mobile",mobile);
        Integer count = baseMapper.selectCount(wrapper);
        if(count > 0) {
            throw new NanaiiiException(20001,"注册失败");
        }

        //数据添加数据库中
        AclCustomer member = new AclCustomer();
        member.setMobile(mobile);
        member.setPassword(MD5.encrypt(mobile));//密码需要加密的
        member.setIsDisabled(true);//新用户禁用
        member.setAvatar("http://thirdwx.qlogo.cn/mmopen/vi_32/DYAIOgq83eoj0hHXhgJNOTSOFsS4uZs8x1ConecaVOB8eIl115xmJZcT4oCicvia7wMEufibKtTLqiaJeanU2Lpg3w/132");
        baseMapper.insert(member);
    }
}
