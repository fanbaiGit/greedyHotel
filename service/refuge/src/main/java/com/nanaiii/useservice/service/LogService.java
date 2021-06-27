package com.nanaiii.useservice.service;

import com.nanaiii.useservice.entity.Log;
import com.baomidou.mybatisplus.extension.service.IService;
import com.nanaiii.useservice.entity.Room;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author nanaiii
 * @since 2021-05-24
 */
public interface LogService extends IService<Log> {

    double getVal(List<Log> logList,long endTime);

    void addNewLog(Room newRoom, Room oldRoom);
}
