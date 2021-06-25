package com.nanaiii.useservice.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nanaiii.commonutils.R;
import com.nanaiii.useservice.entity.AclCustomer;
import com.nanaiii.useservice.entity.Log;
import com.nanaiii.useservice.entity.Room;
import com.nanaiii.useservice.service.AclCustomerService;
import com.nanaiii.useservice.service.LogService;
import com.nanaiii.useservice.service.RoomService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 会员表 前端控制器
 * </p>
 *
 * @author nanaiii
 * @since 2021-05-24
 */
@RestController
@RequestMapping("/useservice/customer")
@CrossOrigin
public class AclCustomerController {

    @Autowired
    private AclCustomerService aclCustomerService;

    @Autowired
    private LogService logService;

    @Autowired
    private RoomService roomService;


    @ApiOperation(value = "关空调")
    @PostMapping("requestOff/{room_id}")
    public R requestOff(@PathVariable String room_id){
        QueryWrapper<Room> wrapper = new QueryWrapper<>();
        wrapper.eq("room_id",room_id);
        Room room = roomService.getOne(wrapper);
        if(room.getState()!=0){
            room.setState(0);
            roomService.update(room,wrapper);

            Log log = new Log(room_id,0,null);
            logService.save(log);
            return R.ok().data("meg","空调成功关闭");
        }
        else
        {
            return R.error().data("meg","空调已关闭");
        }
    }

    @ApiOperation("查询空调使用情况")
    @GetMapping("requestRoomState/{room_id}")
    public R requestRoomState(@PathVariable String room_id){
        QueryWrapper<Room> wrapper = new QueryWrapper<>();
        wrapper.eq("room_id",room_id);
        Room room = roomService.getOne(wrapper);
        return R.ok().data("room",room);
    }

    @ApiOperation("开空调")
    @PostMapping("requestOn/{room_id}")
    public R requestOn(@PathVariable String room_id){
        QueryWrapper<Room> wrapper = new QueryWrapper<>();
        wrapper.eq("room_id",room_id);
        Room room = roomService.getOne(wrapper);

        if(room.getState()==0){
//             log记录不在此处添加，如果同意送风再添加.此处只为Demo处使用。
            // TODO 此处需要优先级调度
            Boolean isOk = roomService.isOk(room_id,room.getWindSpeed());
            System.out.println("\n\nIn requestOn isOK:"+isOk+"\n\n");
            if (isOk){
                if(room.getTarTemp()>25){room.setState(2);}
                else{room.setState(1);}
                roomService.update(room,wrapper);
                Log log =new Log(room_id,1,room.getWindSpeed());
                logService.save(log);
                return R.ok().data("meg","空调成功开启");
            }
            else {
                return R.ok().data("meg","请稍后重试");
            }

        }
        else {
            return R.error().data("meg","空调已开启");
        }
    }

    @ApiOperation("调整温度")
    @PostMapping("changeTargetTemp/{room_id}/{temperature}")
    public R changeTargetTemp(@PathVariable String room_id,@PathVariable double temperature){
        QueryWrapper<Room> wrapper = new QueryWrapper<>();
        wrapper.eq("room_id",room_id);
        Room room=roomService.getOne(wrapper);
        if(room.getState()==0){
            return R.error().data("meg","空调已关机");
        }
        room.setTarTemp(temperature);
        if (temperature>25){
            room.setState(2);
        }
        else if (temperature<25){
            room.setState(1);
        }
        else {
            room.setState(0);
        }
        roomService.update(room,wrapper);
        return R.ok();
    }

    @ApiOperation("调整风速")
    @PostMapping("changeFanSpeed/{room_id}/{wind_speed}")
    public R changeFanSpeed(@PathVariable String room_id,@PathVariable String wind_speed){
        QueryWrapper<Room> wrapper = new QueryWrapper<>();
        wrapper.eq("room_id",room_id);
        Room room=roomService.getOne(wrapper);
        room.setWindSpeed(wind_speed);
        if(room.getState()==0){
            return R.error().data("meg","空调已关机");
        }

        // 先停止上一次送风服务再请求开启下一次服务
        Log log1 = new Log(room_id,0,null);
        room.setState(0);
        roomService.update(room,wrapper);
        logService.save(log1);

        // TODO 此处需要优先级调度
        Boolean isOk = roomService.isOk(room_id,wind_speed);
        System.out.println("\n\nIn changeFanSpeed isOK:"+isOk+"\n\n");
        if(isOk){
            room.setWindSpeed(wind_speed);
            roomService.update(room,wrapper);
            Log log = new Log(room_id,1,wind_speed);
            logService.save(log);
            return R.ok().data("meg","成功调整风速");
        }
        else{
            roomService.update(room,wrapper);
            return R.error().data("meg","请稍后重试");
        }
    }

    @ApiOperation("查询请求是否可用")
    @PostMapping("isAvailable")
    public R isAvailable(String room_id,String wind_speed){
        Boolean isOk = roomService.isOk(room_id,wind_speed);
        System.out.println("\n\nIn isAvailable isOK:"+isOk+"\n\n");
        QueryWrapper<Room> wrapper = new QueryWrapper<>();
        wrapper.eq("room_id",room_id);
        Room room = roomService.getOne(wrapper);
        if(isOk){
            if(room.getState()!=0){//正在运行的空调检查状态
                return R.ok().data("meg","继续运行");
            }
            else {
                if (room.getTarTemp()>25){
                    room.setState(2);
                }
                else if (room.getTarTemp()<25){
                    room.setState(1);
                }
                Log log = new Log(room_id,2,wind_speed);
                logService.save(log);
                roomService.update(room,wrapper);
                return R.ok().data("meg","可以开始运行");
            }
        }
        else {
            if(room.getState()!=0){//正在运行的空调检查状态
                room.setState(0);
                Log log = new Log(room_id,0,null);
                logService.save(log);
                roomService.update(room,wrapper);
                return R.ok().data("meg","中止运行");
            }
            else {
                return R.error().data("meg","请稍后重试");
            }
        }

    }
}

