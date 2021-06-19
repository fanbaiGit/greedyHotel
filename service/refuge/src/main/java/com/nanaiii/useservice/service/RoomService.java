package com.nanaiii.useservice.service;

import com.nanaiii.useservice.entity.Room;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author nanaiii
 * @since 2021-05-24
 */
public interface RoomService extends IService<Room> {

    Double getRoomTemp(Room room);

    Boolean isOk(String room_id, String wind_speed);
}
