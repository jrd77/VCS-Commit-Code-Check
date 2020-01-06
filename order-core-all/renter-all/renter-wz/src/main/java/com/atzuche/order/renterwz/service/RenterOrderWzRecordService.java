package com.atzuche.order.renterwz.service;

import com.atzuche.order.renterwz.mapper.RenterOrderWzRecordMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * RenterOrderWzRecordService
 *
 * @author shisong
 * @date 2020/1/2
 */
@Service
public class RenterOrderWzRecordService {

    @Resource
    private RenterOrderWzRecordMapper renterOrderWzRecordMapper;

    public int deleteNotToday() {
        return renterOrderWzRecordMapper.deleteNotToday();
    }
}
