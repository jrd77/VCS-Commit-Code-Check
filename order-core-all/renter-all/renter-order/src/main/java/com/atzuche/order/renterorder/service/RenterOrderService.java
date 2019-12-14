package com.atzuche.order.renterorder.service;

import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.renterorder.mapper.RenterOrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * 租客订单子表
 *
 * @author ZhangBin
 * @date 2019-12-14 17:24:31
 */
@Service
public class RenterOrderService{
    @Autowired
    private RenterOrderMapper renterOrderMapper;


    public List<RenterOrderEntity> listAgreeRenterOrderByOrderNo(Long orderNo) {
        return null;
    }
}
