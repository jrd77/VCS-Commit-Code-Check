package com.atzuche.order.delivery.service;

import com.atzuche.order.delivery.entity.OwnerHandoverCarInfoEntity;
import com.atzuche.order.delivery.mapper.OwnerHandoverCarInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OwnerHandoverCarInfoService {
    @Autowired
    private OwnerHandoverCarInfoMapper ownerHandoverCarInfoMapper;

    public OwnerHandoverCarInfoEntity selectByRenterOrderNoAndType(String renterOrderNo, Integer type) {
        return ownerHandoverCarInfoMapper.selectByOwnerOrderNoAndType(renterOrderNo, type);
    }


}
