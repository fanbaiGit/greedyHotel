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

import java.util.Date;

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
public class AclCustomerController {

    @Autowired
    private LogService logService;


    @Autowired
    private RoomService roomService;

    // TODO 将isavailable中读到的信息写入log
//    private Logger logger = Logger.getLogger("com.nanaiii.useservice.controller");

    @ApiOperation("查询空调使用情况")
    @GetMapping("requestRoomState/{room_id}")
    public R requestRoomState(@PathVariable String room_id) {
        QueryWrapper<Room> wrapper = new QueryWrapper<>();
        wrapper.eq("room_id", room_id);
        Room room = roomService.getOne(wrapper);
        return R.ok().data("room", room);
    }

    @ApiOperation(value = "关空调")
    @PostMapping("requestOff/{room_id}")
    public R requestOff(@PathVariable String room_id) {
        QueryWrapper<Room> wrapper = new QueryWrapper<>();
        wrapper.eq("room_id", room_id);
//        Room room = roomService.python2java(room_id);
//        if (room == null) {
        Room room = roomService.getOne(wrapper);
//        }

        roomService.shutdown2python(room_id);
//        if(room.getState()!=0){
        room.setState(0);
        //room.setNowTemp(room.getDefaultTemp());
        room.setTarTemp(25.0);
        room.setWindSpeed("MIDDLE");

        roomService.update(room, wrapper);

        // TODO 这里的log是否加到addnewlog中
        Log log = new Log(room_id, 0, null);
        logService.save(log);
        return R.ok().data("meg", "空调成功关闭");
//        }
//        else
//        {
//            return R.error().data("meg","空调已关闭");
//        }
//        return R.ok().data("meg","已成功发送请求");
    }

    @ApiOperation("开空调")
    @PostMapping("requestOn/{room_id}")
    public R requestOn(@PathVariable String room_id) {
        QueryWrapper<Room> wrapper = new QueryWrapper<>();
        wrapper.eq("room_id", room_id);
        Room room = roomService.getOne(wrapper);


        // TODO 是否加一条log
        if (room.getState() == 0) {
            if (room.getTarTemp() > 25) {
                room.setState(2);
            } else {
                room.setState(1);
            }
            roomService.op2python(room);
            return R.ok().data("meg", "成功发送");
        } else {
            return R.error().data("meg", "空调已开启");
        }
    }

    @ApiOperation("调整温度")
    @PostMapping("changeTargetTemp/{room_id}/{temperature}")
    public R changeTargetTemp(@PathVariable String room_id, @PathVariable double temperature) {
        System.out.println("changeTargetTemping...\n");
        QueryWrapper<Room> wrapper = new QueryWrapper<>();
        wrapper.eq("room_id", room_id);
        Room room = roomService.python2java(room_id);
//        if(room.getState()==0){
//            return R.error().data("meg","空调已关机");
//        }
        if (room == null) {
            room = roomService.getOne(wrapper);
        }
        room.setTarTemp(temperature);
        if (temperature <= 25) {
            room.setState(1);
        } else if (temperature > 25) {
            room.setState(2);
        }
        roomService.update(room, wrapper);
        roomService.op2python(room);
        return R.ok().data("meg", "成功发送");
    }

    @ApiOperation("调整风速")
    @PostMapping("changeFanSpeed/{room_id}/{wind_speed}")
    public R changeFanSpeed(@PathVariable String room_id, @PathVariable String wind_speed) {
        System.out.println("changeFanSpeeding...\n");
        Room room = roomService.python2java(room_id);
        roomService.shutdown2python(room_id);
        QueryWrapper<Room> wrapper = new QueryWrapper<>();
        wrapper.eq("room_id", room_id);
        if (room == null) {
            room = roomService.getOne(wrapper);        // 先停止上一次送风服务再请求开启下一次服务
        }
        room.setState(0);
        roomService.update(room, wrapper);
        if (room.getTarTemp() <= 25) {
            room.setState(1);
        } else
            room.setState(2);
        room.setWindSpeed(wind_speed);
        roomService.op2python(room);

        // TODO 先停止上一次送风服务再请求开启下一次服务，这个停风是否写到addnewlog中
        logService.save(new Log(room_id, 0, null));

        return R.ok().data("meg", "成功发送");

        // TODO此处需要优先级调度
//        Boolean isOk = roomService.isOk(room_id,wind_speed);
//        System.out.println("\n\nIn changeFanSpeed isOK:"+isOk+"\n\n");
//        if(isOk){
//            room.setWindSpeed(wind_speed);
//            roomService.update(room,wrapper);
//            Log log = new Log(room_id,1,wind_speed);
//            logService.save(log);
//            return R.ok().data("meg","成功调整风速");
//        }
//        else{
//            roomService.update(room,wrapper);
//            return R.error().data("meg","请稍后重试");
//        }
    }

    @ApiOperation("查询请求是否可用")
    @PostMapping("isAvailable")
    public R isAvailable(String room_id) {
        Room newRoom = roomService.python2java(room_id);
        QueryWrapper<Room> wrapper = new QueryWrapper<>();
        wrapper.eq("room_id", room_id);
        Room oldRoom = roomService.getOne(wrapper);

        // TODO 特判newRoom为null的情况,这里对addnewlog有没有影响
        if (newRoom == null) {
            newRoom = oldRoom;
            newRoom.setState(0);

            //读取上次更新时间
            QueryWrapper<Log> wrapper1 = new QueryWrapper<>();
            wrapper1.eq("room_id", room_id)
                    .orderByDesc("op_time");
            Log log = logService.getOne(wrapper1);

            //计算自动变化的温度
            System.out.println(log.getOpTime());
            double diff = (double) (new Date().getTime() - log.getOpTime().getTime()) / 1000 / 60 * 1;
            double preTemp = newRoom.getNowTemp();
            double defaultTemp = newRoom.getDefaultTemp();
            double nowTemp = preTemp;
            if (preTemp < defaultTemp) {
                nowTemp = preTemp + diff;
                if (nowTemp > defaultTemp) {
                    nowTemp = defaultTemp;
                }
            } else if (preTemp > defaultTemp) {
                nowTemp = preTemp - diff;
                if (nowTemp < defaultTemp) {
                    nowTemp = defaultTemp;
                }
            }

            //更新当前室温
            newRoom.setNowTemp(nowTemp);
            if (nowTemp!=preTemp){
                Log newLog = new Log(room_id, 0, null);
                logService.save(newLog);
            }
        }

        roomService.update(newRoom, wrapper);
        logService.addNewLog(newRoom, oldRoom);
        return R.ok().data("room", newRoom);
//        Room oldRoom = roomService.getOne(wrapper);
//        if (!newRoom.getState().equals(oldRoom.getState())||!newRoom.getNowWindSpeed().equals(oldRoom.getNowWindSpeed())||!newRoom.get)
//
//
//            if(room.getState()!=0){//正在运行的空调检查状态
//                return R.ok().data("meg","继续运行");
//            }
//            else {
//                if (room.getTarTemp()>25){
//                    room.setState(2);
//                }
//                else if (room.getTarTemp()<25){
//                    room.setState(1);
//                }
//                Log log = new Log(room_id,2,wind_speed);
//                logService.save(log);
//                roomService.update(room,wrapper);
//                return R.ok().data("meg","可以开始运行");
//            }
//        }
//        else {
//            if(room.getState()!=0){//正在运行的空调检查状态
//                room.setState(0);
//                Log log = new Log(room_id,0,null);
//                logService.save(log);
//                roomService.update(room,wrapper);
//                return R.ok().data("meg","中止运行");
//            }
//            else {
//                return R.error().data("meg","请稍后重试");
//            }
//        }
    }
}