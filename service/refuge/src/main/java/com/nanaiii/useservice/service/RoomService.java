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

    void op2python(Room room);

    void shutdown2python(String room_id);

    Room python2java(String room_id);
}
