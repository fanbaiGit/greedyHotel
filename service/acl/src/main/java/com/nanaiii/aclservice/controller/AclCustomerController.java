package com.nanaiii.aclservice.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nanaiii.aclservice.service.AclCustomerService;
import com.nanaiii.commonutils.JwtUtils;
import com.nanaiii.commonutils.R;
import com.nanaiii.aclservice.entity.AclCustomer;
import com.nanaiii.aclservice.entity.vo.RegisterVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 顾客表 前端控制器
 * </p>
 *
 * @author nanaiii
 * @since 2021-05-22
 */
@RestController
@RequestMapping("/aclservice/customer")
@CrossOrigin
public class AclCustomerController {

    @Autowired
    private AclCustomerService memberService;

//    @PostMapping("send")
//    public R demo(@RequestBody demo Demo){
//        System.out.println(Demo.getOperate());
//        return R.ok();
//    }

    //登录
    @PostMapping("login")
    public R loginUser(@RequestBody AclCustomer member) {
        //member对象封装手机号和密码
        //调用service方法实现登录
        //返回token值，使用jwt生成
        AclCustomer aclCustomer = memberService.login(member);
        return R.ok().data("aclCustomer",aclCustomer);
    }

    //注册
    @PostMapping("register")
    public R registerUser(@RequestBody String mobile) {
        memberService.register(mobile);
        return R.ok();
    }

}

