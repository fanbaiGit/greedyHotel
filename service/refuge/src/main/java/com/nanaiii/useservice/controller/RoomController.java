package com.nanaiii.useservice.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nanaiii.commonutils.R;
import com.nanaiii.useservice.entity.Airconditioning;
import com.nanaiii.useservice.entity.Room;
import com.nanaiii.useservice.service.RoomService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
@RequestMapping("/useservice/admin")
@CrossOrigin
public class RoomController {

    @Autowired
    private RoomService roomService;

    @ApiOperation("获得所有房间空调使用情况")
    @GetMapping("getAllRoom")
    public R getAllRoom(){
        List<Room> roomList = roomService.list(null);
        for(Room room : roomList){
            room.setNowTemp(roomService.getRoomTemp(room));
        }
        return R.ok().data("roomlist",roomList);
    }

    @PostMapping("powerOn")
    public R powerOn(int defaultRoomNum,int avoidTime){
        Airconditioning airconditioning = new Airconditioning();
        airconditioning.setState(1);
        airconditioning.setDefaultRoomNum(defaultRoomNum);
        airconditioning.setAvoidTime(avoidTime);
        return R.ok();
    }

    @ApiOperation("根据房间号获取房间空调使用情况")
    @GetMapping("roomState/{room_id}")
    public R roomState(@PathVariable String room_id){
        QueryWrapper<Room> wrapper = new QueryWrapper<>();
        wrapper.eq("room_id",room_id);
        Room room = roomService.getOne(null);
        room.setNowTemp(roomService.getRoomTemp(room));
        return R.ok().data("room",room);
    }
}

