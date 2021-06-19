package com.nanaiii.useservice.service.impl;

import com.nanaiii.useservice.entity.Log;
import com.nanaiii.useservice.mapper.LogMapper;
import com.nanaiii.useservice.service.LogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author nanaiii
 * @since 2021-05-24
 */
@Service
public class LogServiceImpl extends ServiceImpl<LogMapper, Log> implements LogService {

    @Override
    public double getVal(List<Log> logList) {
        return 100.0;
    }
}
