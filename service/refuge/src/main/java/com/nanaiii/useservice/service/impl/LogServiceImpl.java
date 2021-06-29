package com.nanaiii.useservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nanaiii.useservice.entity.AclCustomer;
import com.nanaiii.useservice.entity.Log;
import com.nanaiii.useservice.entity.Report;
import com.nanaiii.useservice.entity.Room;
import com.nanaiii.useservice.mapper.LogMapper;
import com.nanaiii.useservice.mapper.RoomMapper;
import com.nanaiii.useservice.service.LogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
    public double calcFee(List<Log> logList, long endTime) {
        double val = 0.0;
        double cost = 0;
        long now = 0;
        long lastTime = 0;

        for (Log i : logList) {
            switch (i.getOperation()) {
                case 0:
                    now = i.getOpTime().getTime();
                    if (cost > 0) {
                        val += ((double) now - (double) lastTime) / (1000.0 * 60) / cost;
                    }
                    lastTime = now;
                    cost = 0;
                    break;
                case 1:
                    now = i.getOpTime().getTime();
                    if (cost > 0) {
                        val += ((double) now - (double) lastTime) / (1000.0 * 60) / cost;
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
                        val += ((double) now - (double) lastTime) / (1000.0 * 60) / cost;
                    }
                    return val;
                default:
                    break;
            }
        }

        if (cost > 0) {
            val += ((double) endTime - (double) lastTime) / (1000.0 * 60) / cost;
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

    @Resource
    private LogMapper logMapper;

    @Override
    public List<Log> listByCustomer(String roomId, AclCustomer customer) {
        QueryWrapper<Log> wrapper = new QueryWrapper<>();
        wrapper.eq("room_id", roomId)
                .ge("op_time", customer.getGmtModified());
        return logMapper.selectList(wrapper);
    }

    @Resource
    private RoomMapper roomMapper;

    @Resource
    private LogService logService;

    @Override
    public List<Report> createReport(String startTime, String endTime, Integer grain) throws ParseException {
        QueryWrapper<Log> wrapper = new QueryWrapper<>();
        wrapper.between("op_time", startTime, endTime);
        List<Report> reportList = new ArrayList<>();

        //获取房间号列表
        List<Room> roomList = roomMapper.selectList(null);
        List<String> roomIdList = new ArrayList<>();
        for (Room room : roomList) {
            roomIdList.add(room.getRoomId());
        }
        //累计收入
        List<Double> feeList = new ArrayList<>();

        if (grain == 0) {
            //日报
            //获取日志
            wrapper.select("date_format(op_time,'%Y-%m-%d') as time",
                    "count(operation=2 or null) as num",
                    "count(wind_speed='LOW' or null) as lowNum",
                    "count(wind_speed='MIDDLE' or null) as midNum",
                    "count(wind_speed='HIGH' or null) as highNum")
                    .groupBy("date_format(op_time,'%Y-%m-%d')");
            List<Log> logList = logMapper.selectList(wrapper);

            for (Log log : logList) {
                double val = 0;
                //统计所有房间收入和
                for (String roomId : roomIdList) {
                    QueryWrapper<Log> wrapper1 = new QueryWrapper<>();
                    wrapper1.eq("room_id", roomId)
                            .ge("op_time", log.getTime());
                    List<Log> dayLogList = logMapper.selectList(wrapper1);
                    val += logService.calcFee(dayLogList, new Date().getTime());
                }
                feeList.add(val);

                //初步生成报表，无收入
                Report report = new Report(log.getTime(), log.getNum(), log.getLowNum(), log.getMidNum(), log.getHighNum());
                reportList.add(report);
            }
        } else if (grain == 1) {
            wrapper.select("date_format(op_time,'%Y-%u') as time",
                    "count(operation=2 or null) as num",
                    "count(wind_speed='LOW' or null) as lowNum",
                    "count(wind_speed='MIDDLE' or null) as midNum",
                    "count(wind_speed='HIGH' or null) as highNum")
                    .groupBy("date_format(op_time,'%Y-%u')");
            List<Log> logList = logService.list(wrapper);

            SimpleDateFormat weekFormat = new SimpleDateFormat("yyyy-ww");
            SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd");

            for (Log log : logList) {
                double val = 0;
                Date date = weekFormat.parse(log.getTime());
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                calendar.setFirstDayOfWeek(Calendar.MONDAY);
                calendar.add(Calendar.DATE, 1);
                String day = dayFormat.format(calendar.getTime());

                //统计所有房间收入和
                for (String roomId : roomIdList) {
                    QueryWrapper<Log> wrapper1 = new QueryWrapper<>();
                    wrapper1.eq("room_id", roomId)
                            .ge("op_time", day);
                    List<Log> monthLogList = logMapper.selectList(wrapper1);
                    val += logService.calcFee(monthLogList, new Date().getTime());
                }
                feeList.add(val);

                //初步生成报表，无收入
                Report report = new Report(log.getTime(), log.getNum(), log.getLowNum(), log.getMidNum(), log.getHighNum());
                reportList.add(report);
            }
        } else if (grain == 2) {
            wrapper.select("date_format(op_time,'%Y-%m') as time",
                    "count(operation=2 or null) as num",
                    "count(wind_speed='LOW' or null) as lowNum",
                    "count(wind_speed='MIDDLE' or null) as midNum",
                    "count(wind_speed='HIGH' or null) as highNum")
                    .groupBy("date_format(op_time,'%Y-%m')");
            List<Log> logList = logService.list(wrapper);

            for (Log log : logList) {
                double val = 0;
                //统计所有房间收入和
                for (String roomId : roomIdList) {
                    QueryWrapper<Log> wrapper1 = new QueryWrapper<>();
                    wrapper1.eq("room_id", roomId)
                            .ge("op_time", log.getTime());
                    List<Log> monthLogList = logMapper.selectList(wrapper1);
                    val += logService.calcFee(monthLogList, new Date().getTime());
                }
                feeList.add(val);

                //初步生成报表，无收入
                Report report = new Report(log.getTime(), log.getNum(), log.getLowNum(), log.getMidNum(), log.getHighNum());
                reportList.add(report);
            }
        }

        //逐差法求各时间段收入
        int size = reportList.size();
        reportList.get(size - 1).setIncome(feeList.get(size - 1));
        for (int i = 0; i < size - 1; i++) {
            reportList.get(i).setIncome(feeList.get(i) - feeList.get(i + 1));
        }

        return reportList;
    }

}
