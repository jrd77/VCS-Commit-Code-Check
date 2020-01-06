package com.atzuche.order.renterwz.service;

import com.atzuche.order.renterwz.entity.RenterOrderWzQueryRecordEntity;
import com.atzuche.order.renterwz.mapper.RenterOrderWzQueryRecordMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * RenterOrderWzQueryRecordService
 *
 * @author shisong
 * @date 2020/1/2
 */
@Service
public class RenterOrderWzQueryRecordService {

    @Resource
    private RenterOrderWzQueryRecordMapper renterOrderWzQueryRecordMapper;

    public void insert(RenterOrderWzQueryRecordEntity entity) {
        entity.setCreateTime(new Date());
        renterOrderWzQueryRecordMapper.saveRenterOrderWzQueryRecord(entity);
    }
}
