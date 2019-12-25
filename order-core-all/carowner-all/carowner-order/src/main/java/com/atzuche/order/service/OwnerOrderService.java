package com.atzuche.order.service;

import com.atzuche.order.entity.OwnerOrderEntity;
import com.atzuche.order.mapper.OwnerOrderMapper;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.time.LocalDateTime;


@Service
public class OwnerOrderService implements Serializable {
    @Autowired
    private OwnerOrderMapper ownerOrderMapper;

    /*
     * @Author ZhangBin
     * @Date 2019/12/25 10:08
     * @Description: 查询有效的子订单
     *
     **/
    public OwnerOrderEntity getOwnerOrderByOrderNoAndIsEffective(String orderNo){
        return ownerOrderMapper.getOwnerOrderByOrderNoAndIsEffective(orderNo);
    }
}
