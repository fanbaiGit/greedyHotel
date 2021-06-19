package com.nanaiii.useservice.service.impl;

import com.nanaiii.useservice.entity.Room;
import com.nanaiii.useservice.mapper.RoomMapper;
import com.nanaiii.useservice.service.RoomService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author nanaiii
 * @since 2021-05-24
 */
@Service
public class RoomServiceImpl extends ServiceImpl<RoomMapper, Room> implements RoomService {

    @Override
    public Double getRoomTemp(Room room) {
        // TODO 计算房间现在的温度
        return 25.0;
    }

    @Override
    public Boolean isOk(String room_id, String wind_speed) {
        Random r = new Random();
        return r.nextDouble()>=0.3;
    }
}
