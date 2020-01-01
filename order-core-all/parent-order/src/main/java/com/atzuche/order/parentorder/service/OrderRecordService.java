package com.atzuche.order.parentorder.service;

import com.atzuche.order.parentorder.entity.OrderRecordEntity;
import com.atzuche.order.parentorder.mapper.OrderRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



/**
 * 下单完成记录表
 *
 * @author ZhangBin
 * @date 2020-01-01 15:06:16
 */
@Service
public class OrderRecordService{
    @Autowired
    private OrderRecordMapper orderRecordMapper;

    public Integer save(OrderRecordEntity orderRecordEntity){
        return orderRecordMapper.insertSelective(orderRecordEntity);
    }

}
