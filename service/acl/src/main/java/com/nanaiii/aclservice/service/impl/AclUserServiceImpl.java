package com.nanaiii.aclservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nanaiii.aclservice.entity.AclUser;
import com.nanaiii.aclservice.mapper.AclUserMapper;
import com.nanaiii.aclservice.service.AclUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nanaiii.commonutils.MD5;
import com.nanaiii.servicebase.exceptionhandler.NanaiiiException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author nanaiii
 * @since 2021-05-22
 */
@Service
public class AclUserServiceImpl extends ServiceImpl<AclUserMapper, AclUser> implements AclUserService {

    //登录的方法
    @Override
    public AclUser login(AclUser member) {
        //获取登录手机号和密码
        String username = member.getUsername();
        String password = member.getPassword();

        //手机号和密码非空判断
        if(StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            throw new NanaiiiException(20001,"用户名或密码为空");
        }

        //判断手机号是否正确
        QueryWrapper<AclUser> wrapper = new QueryWrapper<>();
        wrapper.eq("username",username);
        AclUser Member = baseMapper.selectOne(wrapper);
        //判断查询对象是否为空
        if(Member == null) {
            throw new NanaiiiException(20001,"未查询到用户");
        }

        //判断密码
        //因为存储到数据库密码肯定加密的
        //把输入的密码进行加密，再和数据库密码进行比较
        //加密方式 MD5
        if(!MD5.encrypt(password).equals(Member.getPassword())) {
            throw new NanaiiiException(20001,"密码错误");
        }

        //判断用户是否禁用
        if(Member.getIsDeleted()) {
            throw new NanaiiiException(20001,"用户被禁止使用");
        }

        //登录成功
        //生成token字符串，使用jwt工具类
//        String jwtToken = JwtUtils.getJwtToken(mobileMember.getId(), mobileMember.getUsername());
//        return jwtToken;
        return Member;
    }
}
