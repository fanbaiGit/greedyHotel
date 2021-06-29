package com.nanaiii.useservice.service;

import com.nanaiii.useservice.entity.AclCustomer;
import com.nanaiii.useservice.entity.Log;
import com.baomidou.mybatisplus.extension.service.IService;
import com.nanaiii.useservice.entity.Report;
import com.nanaiii.useservice.entity.Room;

import java.text.ParseException;
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

    double calcFee(List<Log> logList,long endTime);

    void addNewLog(Room newRoom, Room oldRoom);

    List<Log> listByCustomer(String roomId,AclCustomer customer);

    List<Report> createReport(String startTime,String endTime,Integer grain) throws ParseException;

}
