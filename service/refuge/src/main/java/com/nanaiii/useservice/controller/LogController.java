package com.nanaiii.useservice.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nanaiii.commonutils.R;
import com.nanaiii.useservice.entity.AclCustomer;
import com.nanaiii.useservice.entity.Bill;
import com.nanaiii.useservice.entity.Log;
import com.nanaiii.useservice.entity.Room;
import com.nanaiii.useservice.service.AclCustomerService;
import com.nanaiii.useservice.service.LogService;
import com.nanaiii.useservice.service.RoomService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.acl.Acl;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author nanaiii
 * @since 2021-05-24
 */
@RestController
@RequestMapping("/receptionist")
@CrossOrigin
public class LogController {

    @Autowired
    private LogService logService;

    @Autowired
    private AclCustomerService aclCustomerService;

    @Autowired
    private RoomService roomService;

    @ApiOperation("查询详单")
    @PostMapping("createrdr")
    // TODO 附加功能： 选择是否打印详单
    public R createrdr(String room_id, int settime, String start, String end){
        QueryWrapper<Log> wrapper = new QueryWrapper<>();
        wrapper.eq("room_id",room_id);
        if(settime==1){
            wrapper.between("op_time",start,end);
        }
        wrapper.orderByDesc("op_time");
        List<Log> logList = logService.list(wrapper);

        // 只找最后一次入住到退房间所有记录
        List<Log> newLogList = new ArrayList<>();
        for (Log log : logList){
            newLogList.add(log);
            if(log.getOperation()==2){
                break;
            }
        }
        double val = logService.getVal(newLogList);
        return R.ok().data("logList",newLogList).data("val",val);
    }

    @ApiOperation("查询账单")
    @PostMapping("createinvoice")
    public R createinvoice(String room_id, int settime, String start, String end){
        QueryWrapper<Log> wrapper = new QueryWrapper<>();
        wrapper.eq("room_id",room_id);
        if(settime==1){
            wrapper.between("op_time",start,end);
        }
        wrapper.orderByDesc("op_time");
        List<Log> logList = logService.list(wrapper);
        // TODO 根据log生成账单计算金额
        double val = logService.getVal(logList);

        QueryWrapper<AclCustomer> wrapper1 = new QueryWrapper<>();
        wrapper1.eq("room_id",room_id);
//        wrapper1.eq("is_disabled",false);
        AclCustomer aclCustomer= aclCustomerService.getOne(wrapper1);
        if(aclCustomer==null)
        {
            return R.error().data("meg","该房间没有房客正在使用");
        }
        Bill bill = new Bill("startTime","stopTime",room_id, aclCustomer.getMobile(), val);
        return R.ok().data("bill",bill);
    }

    @ApiOperation("选择空闲房间")
    @GetMapping("chooseRoom")
    public R chooseRoom(){
        QueryWrapper<Room> wrapper = new QueryWrapper<>();
        wrapper.eq("is_disabled",true);
        List<Room> roomList = roomService.list(wrapper);
        return R.ok().data("roomList",roomList);
    }

    @ApiOperation("办理入住")
    @PostMapping("adduser")
    public R adduser(String mobile,String room_id){
        QueryWrapper<AclCustomer> wrapper = new QueryWrapper<>();
        wrapper.eq("mobile",mobile);
        AclCustomer aclCustomer = aclCustomerService.getOne(wrapper);

        if(aclCustomer==null){
            // 对于新用户先注册再添加入住信息
            aclCustomerService.register(mobile,false,room_id);
        }
        else {
            if(!aclCustomer.getIsDisabled()){
                return R.error().data("meg","该用户仍有未完成订单");
            }
            // 更新顾客状态
            aclCustomer.setIsDisabled(false);
            aclCustomer.setRoomId(room_id);
            aclCustomerService.updateById(aclCustomer);
        }

        // 更新房间状态
        QueryWrapper<Room> wrapper1 = new QueryWrapper<>();
        wrapper1.eq("room_id",room_id);
        Room room = roomService.getOne(wrapper1);
        room.setIsDisabled(false);
        roomService.update(room,wrapper1);

        // 新加入住记录
        Log log = new Log(room_id,2,null);
        logService.save(log);

        return R.ok();
    }

    @ApiOperation("使用用户名获取房间号")
    @PostMapping("findRoomIdByMobile")
    public R findRoomIdByMobile(String mobile){

        QueryWrapper<AclCustomer> wrapper = new QueryWrapper<>();
        wrapper.eq("mobile",mobile);
        wrapper.eq("is_disabled",false);
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
        return R.ok().data("room_id",aclCustomer.getRoomId());
    }

    @ApiOperation("使用房间号退房")
    @PostMapping("checkout")
    public R checkout(String room_id){

        QueryWrapper<Room> wrapper = new QueryWrapper<>();
        wrapper.eq("room_id",room_id);
        Room room = roomService.getOne(wrapper);
        room.setIsDisabled(true);

        //如果没有关空调先关空调
        if(room.getState()!=0){
            Log log1 = new Log(room_id,0,null);
            room.setState(0);
            logService.save(log1);
        }
        // 新加退房记录
        Log log = new Log(room_id,3,null);
        logService.save(log);

        roomService.update(room,wrapper);

        //更新顾客状态
        QueryWrapper<AclCustomer> wrapper1 = new QueryWrapper<>();
        wrapper1.eq("is_disabled",false);
        wrapper1.eq("room_id",room_id);
        AclCustomer aclCustomer = aclCustomerService.getOne(wrapper1);
        aclCustomer.setIsDisabled(true);
        aclCustomer.setRoomId("Checked out");
        aclCustomerService.updateById(aclCustomer);
        return R.ok();
    }
}

