package com.nanaiii.useservice.service.impl;

import com.nanaiii.useservice.entity.Airconditioning;
import com.nanaiii.useservice.mapper.AirconditionMapper;
import com.nanaiii.useservice.service.AirconditionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class AirconditionServiceImpl extends ServiceImpl<AirconditionMapper, Airconditioning> implements AirconditionService {
}
