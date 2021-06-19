package com.nanaiii.aclservice.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nanaiii.aclservice.entity.AclUser;
import com.nanaiii.aclservice.service.AclUserService;
import com.nanaiii.commonutils.R;
import com.nanaiii.commonutils.MD5;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author nanaiii
 * @since 2021-05-22
 */
@RestController
@RequestMapping("/aclservice/user")
@CrossOrigin
public class AclUserController {

    @Autowired
    private AclUserService aclUserService;


    //登录
    @PostMapping("login")
    @ApiOperation(value = "管理用户登录")
    public R loginUser(@RequestBody AclUser member) {
        AclUser user = aclUserService.login(member);
        return R.ok().data("user",user);
    }

    @ApiOperation(value = "获取管理用户分页列表")
    @GetMapping("{page}/{limit}")
    public R index(
//            @ApiParam(name = "page", value = "当前页码", required = true)
//            @PathVariable Long page,
//
//            @ApiParam(name = "limit", value = "每页记录数", required = true)
//            @PathVariable Long limit,

            @ApiParam(name = "courseQuery", value = "查询对象", required = false)
                    AclUser userQueryVo) {
        Page<AclUser> pageParam = new Page<>(1, 10);
        QueryWrapper<AclUser> wrapper = new QueryWrapper<>();
        if(!StringUtils.isEmpty(userQueryVo.getUsername())) {
            wrapper.like("username",userQueryVo.getUsername());
        }

        IPage<AclUser> pageModel = aclUserService.page(pageParam, wrapper);
        return R.ok().data("items", pageModel.getRecords()).data("total", pageModel.getTotal());
    }

    @ApiOperation(value = "新增管理用户")
    @PostMapping("save")
    public R save(@RequestBody AclUser user) {
        QueryWrapper<AclUser> wrapper = new QueryWrapper<>();
        wrapper.eq("username",user.getUsername());
        int count = aclUserService.count(wrapper);
        if(count!=0){
            return R.error().data("meg","该用户名已被使用");
        }
        else {
            user.setPassword(MD5.encrypt(user.getPassword()));
            aclUserService.save(user);
            return R.ok().data("meg","新建成功");
        }
    }

    @ApiOperation(value = "修改管理用户")
    @PutMapping("update")
    public R updateById(@RequestBody AclUser user) {
        aclUserService.updateById(user);
        return R.ok();
    }

    @ApiOperation(value = "删除管理用户")
    @DeleteMapping("remove/{id}")
    public R remove(@PathVariable String id) {
        aclUserService.removeById(id);
        return R.ok();
    }

    @ApiOperation(value = "根据id列表删除管理用户")
    @DeleteMapping("batchRemove")
    public R batchRemove(@RequestBody List<String> idList) {
        aclUserService.removeByIds(idList);
        return R.ok();
    }

}

