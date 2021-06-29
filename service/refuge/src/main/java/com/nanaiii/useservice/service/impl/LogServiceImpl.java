package com.nanaiii.useservice.service.impl;

import com.nanaiii.useservice.entity.Log;
import com.nanaiii.useservice.entity.Room;
import com.nanaiii.useservice.mapper.LogMapper;
import com.nanaiii.useservice.service.LogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author nanaiii
 * @since 2021-05-24
 */
@Service
public class LogServiceImpl extends ServiceImpl<LogMapper, Log> implements LogService {

    @Override
    public double getVal(List<Log> logList,long endTime) {
        double val = 0;
        double cost = 0;
        long now = 0;
        long lastTime = 0;

        for (Log i : logList) {
//            System.out.println(cost);
//            System.out.println(val);
            switch (i.getOperation()) {
                case 0:
                    now = i.getOpTime().getTime();
                    if (cost > 0) {

                        val += (double) (now - lastTime) / (1000 * 60) / cost;
                    }
                    lastTime = now;
                    cost = 0;
                    break;
                case 1:
                    now = i.getOpTime().getTime();
                    if (cost > 0) {
                        val += (double) (now - lastTime) / (1000 * 60) / cost;
                    }
                    lastTime = now;
                    switch (i.getWindSpeed()) {
                        case "HIGH":
                            cost = 0.5;
                            break;
                        case "MIDDLE":
                            cost = 1;
                            break;
                        case "LOW":
                            cost = 1.5;
                            break;
                    }
                    break;
                case 3:
                    if (cost > 0) {
                        now = i.getOpTime().getTime();
                        val += (double) (now - lastTime) / (1000 * 60) / cost;
                    }
                    return val;
                default:
                    break;
            }
        }

        if (cost > 0) {
            val += (double) (endTime - lastTime) / (1000 * 60) / cost;
        }

        return val;
    }

    @Override
    public void addNewLog(Room newRoom, Room oldRoom) {
        if (newRoom.getState() == 0 && oldRoom.getState() > 0) {
            Log log = new Log();
            log.setRoomId(oldRoom.getRoomId())
                    .setOperation(0)
                    .setWindSpeed(newRoom.getWindSpeed());
            baseMapper.insert(log);
        } else if (newRoom.getState() > 0 && oldRoom.getState() == 0) {
            Log log = new Log();
            log.setRoomId(oldRoom.getRoomId())
                    .setOperation(1)
                    .setWindSpeed(newRoom.getWindSpeed());
            baseMapper.insert(log);
        } else if (newRoom.getState() > 0) {
            if (!newRoom.getState().equals(oldRoom.getState()) ||
                    !newRoom.getWindSpeed().equals(oldRoom.getWindSpeed())) {
                Log log = new Log();
                log.setRoomId(oldRoom.getRoomId())
                        .setOperation(1)
                        .setWindSpeed(newRoom.getWindSpeed());
                baseMapper.insert(log);
            }
        }
    }
}
