package com.atzuche.order.delivery.service;

import com.atzuche.order.delivery.entity.RenterHandoverCarInfoEntity;
import com.atzuche.order.delivery.mapper.RenterHandoverCarInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RenterHandoverCarInfoService {
    @Autowired
    private RenterHandoverCarInfoMapper renterHandoverCarInfoMapper;

    public RenterHandoverCarInfoEntity selectByRenterOrderNoAndType(String renterOrderNo,Integer type) {
        return renterHandoverCarInfoMapper.selectByRenterOrderNoAndType(renterOrderNo, type);
    }
}
