package com.nanaiii.useservice.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nanaiii.commonutils.R;
import com.nanaiii.useservice.entity.*;
import com.nanaiii.useservice.service.AclCustomerService;
import com.nanaiii.useservice.service.LogService;
import com.nanaiii.useservice.service.RoomService;
import io.swagger.annotations.ApiOperation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author nanaiii
 * @since 2021-05-24
 */
@RestController
@RequestMapping("/useservice/receptionist")
//@CrossOrigin
public class LogController {

    @Autowired
    private LogService logService;

    @Autowired
    private AclCustomerService aclCustomerService;

    @Autowired
    private RoomService roomService;

    @ApiOperation("查询详单")
    @PostMapping("createrdr")
    public R createrdr(String room_id, int settime, String start, String end) {
        //确定当前客户
        QueryWrapper<AclCustomer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("room_id", room_id)
                .eq("is_disabled", 0)
                .orderByDesc("gmt_modified");
        AclCustomer customer = aclCustomerService.getOne(queryWrapper);
        if (customer == null) {
            return R.error().data("meg", "无匹配客户");
        }

        QueryWrapper<Log> wrapper = new QueryWrapper<>();
        wrapper.eq("room_id", room_id)
                .ge("op_time", customer.getGmtModified().getTime());
        List<Log> logList = logService.list(wrapper);
        return R.ok().data("logList", logList);
    }

    @ApiOperation("查询账单")
    @PostMapping("createinvoice")
    public R createinvoice(String room_id, int settime, String start, String end) {
        //确定当前客户
        QueryWrapper<AclCustomer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("room_id", room_id)
                .eq("is_disabled", 0)
                .orderByDesc("gmt_modified");
        AclCustomer customer = aclCustomerService.getOne(queryWrapper);
        if (customer == null) {
            return R.error().data("meg", "无匹配客户");
        }

        QueryWrapper<Log> wrapper = new QueryWrapper<>();
        wrapper.eq("room_id", room_id)
                .ge("op_time", customer.getGmtModified());
        List<Log> logList = logService.list(wrapper);
        double val = logService.getVal(logList, (new Date()).getTime());

        Bill bill = new Bill(customer.getGmtModified().toString(), new Date().toString(), room_id, customer.getMobile(), val);
        return R.ok().data("bill", bill);
    }

    @ApiOperation("选择空闲房间")
    @GetMapping("chooseRoom")
    public R chooseRoom() {
        QueryWrapper<Room> wrapper = new QueryWrapper<>();
        wrapper.eq("is_disabled", 1);
        List<Room> roomList = roomService.list(wrapper);
        return R.ok().data("roomList", roomList);
    }

    @ApiOperation("办理入住")
    @PostMapping("adduser")
    public R adduser(String mobile, String room_id) {
        QueryWrapper<AclCustomer> wrapper = new QueryWrapper<>();
        wrapper.eq("mobile", mobile);
        AclCustomer aclCustomer = aclCustomerService.getOne(wrapper);

        if (aclCustomer == null) {
            // 对于新用户先注册再添加入住信息
            aclCustomerService.register(mobile, false, room_id);
        } else {
            if (!aclCustomer.getIsDisabled()) {
                return R.error().data("meg", "该用户仍有未完成订单");
            }
            // 更新顾客状态
            aclCustomer.setIsDisabled(false);
            aclCustomer.setRoomId(room_id);
            aclCustomerService.updateById(aclCustomer);
        }

        // 更新房间状态
        QueryWrapper<Room> wrapper1 = new QueryWrapper<>();
        wrapper1.eq("room_id", room_id);
        Room room = roomService.getOne(wrapper1);
        room.setIsDisabled(false);
        roomService.update(room, wrapper1);

        // 新加入住记录
        Log log = new Log(room_id, 2, null);
        logService.save(log);

        return R.ok();
    }

    @ApiOperation("使用用户名获取房间号")
    @PostMapping("findRoomIdByMobile")
    public R findRoomIdByMobile(String mobile) {

        QueryWrapper<AclCustomer> wrapper = new QueryWrapper<>();
        wrapper.eq("mobile", mobile);
        wrapper.eq("is_disabled", false);
        AclCustomer aclCustomer = aclCustomerService.getOne(wrapper);

//        // 新加退房记录
//        Log log = new Log(aclCustomer.getRoomId(),3,null);
//        logService.save(log);
//
//
//        QueryWrapper<Room> wrapper1 = new QueryWrapper<>();
//        wrapper1.eq("room_id",aclCustomer.getRoomId());
//        Room room = roomService.getOne(wrapper1);
//        room.setIsDisabled(true);
//        room.setState(0);
//        roomService.update(room,wrapper1);
//
//        //更新顾客状态
//        aclCustomer.setIsDisabled(true);
//        aclCustomer.setRoomId("0");
//        aclCustomerService.updateById(aclCustomer);
        return R.ok().data("room_id", aclCustomer.getRoomId());
    }

    @ApiOperation("使用房间号退房")
    @PostMapping("checkout")
    public R checkout(String room_id) {

        QueryWrapper<Room> wrapper = new QueryWrapper<>();
        wrapper.eq("room_id", room_id);
        Room room = roomService.getOne(wrapper);
        room.setIsDisabled(true);

        //如果没有关空调先关空调
        if (room.getState() != 0) {
            Log log1 = new Log(room_id, 0, null);
            room.setState(0);
            logService.save(log1);
        }
        // 新加退房记录
        Log log = new Log(room_id, 3, null);
        logService.save(log);

//        room.setIsDisabled(false);
        roomService.update(room, wrapper);

        //更新顾客状态
        QueryWrapper<AclCustomer> wrapper1 = new QueryWrapper<>();
        wrapper1.eq("is_disabled", false);
        wrapper1.eq("room_id", room_id);
        AclCustomer aclCustomer = aclCustomerService.getOne(wrapper1);
        aclCustomer.setIsDisabled(true);
        aclCustomer.setRoomId("Checked out");
        aclCustomerService.updateById(aclCustomer);
        return R.ok();
    }

    @ApiOperation("格式化报表")
    @GetMapping("report")
    public R report(String startTime, String endTime, Integer grain) throws ParseException {
        QueryWrapper<Log> queryWrapper = new QueryWrapper<>();
        queryWrapper.between("op_time", startTime, endTime);
        //grain 0日报，1周报，2月报
        List<Report> reportList = new ArrayList<>();
        if (grain == 0) {
            queryWrapper.select("date_format(op_time,'%Y-%m-%d') as time",
                    "count(operation=2 or null) as num",
                    "count(wind_speed='LOW' or null) as lowNum",
                    "count(wind_speed='MIDDLE' or null) as midNum",
                    "count(wind_speed='HIGH' or null) as highNum")
                    .groupBy("date_format(op_time,'%Y-%m-%d')");
            List<Log> logList = logService.list(queryWrapper);

            for (Log log : logList) {
                Report report = new Report(log.getTime(), log.getNum(), log.getLowNum(), log.getMidNum(), log.getHighNum());
                reportList.add(report);
            }
        } else if (grain == 1) {
            queryWrapper.select("date_format(op_time,'%Y-%u') as time",
                    "count(operation=2 or null) as num",
                    "count(wind_speed='LOW' or null) as lowNum",
                    "count(wind_speed='MIDDLE' or null) as midNum",
                    "count(wind_speed='HIGH' or null) as highNum")
                    .groupBy("date_format(op_time,'%Y-%u')");
            List<Log> logList = logService.list(queryWrapper);

            for (Log log : logList) {
                Report report = new Report(log.getTime(), log.getNum(), log.getLowNum(), log.getMidNum(), log.getHighNum());
                reportList.add(report);
            }
        } else if (grain == 2) {
            queryWrapper.select("date_format(op_time,'%Y-%m') as time",
                    "count(operation=2 or null) as num",
                    "count(wind_speed='LOW' or null) as lowNum",
                    "count(wind_speed='MIDDLE' or null) as midNum",
                    "count(wind_speed='HIGH' or null) as highNum")
                    .groupBy("date_format(op_time,'%Y-%m')");
            List<Log> logList = logService.list(queryWrapper);
            for (Log log : logList) {
                Report report = new Report(log.getTime(), log.getNum(), log.getLowNum(), log.getMidNum(), log.getHighNum());
                reportList.add(report);
            }
        }

        return R.ok().data("reportList", reportList);
    }

}

