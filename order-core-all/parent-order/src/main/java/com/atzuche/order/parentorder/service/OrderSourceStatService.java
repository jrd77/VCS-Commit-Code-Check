package com.atzuche.order.parentorder.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.constant.OrderConstant;
import com.atzuche.order.parentorder.dto.OrderSourceStatDTO;
import com.atzuche.order.parentorder.entity.OrderSourceStatEntity;
import com.atzuche.order.parentorder.mapper.OrderSourceStatMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


/**
 * 订单来源统计
 *
 * @author ZhangBin
 * @date 2019-12-24 16:19:32
 */
@Service
public class OrderSourceStatService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderSourceStatService.class);


    @Resource
    private OrderSourceStatMapper orderSourceStatMapper;


    /**
     * 主订单来源统计信息保存
     *
     * @param orderSourceStatDTO 主订单来源统计信息
     * @return int 操作成功记录数
     */
    public int saveOrderSourceStatInfo(OrderSourceStatDTO orderSourceStatDTO) {

        LOGGER.info("Save master order source stat information. param is, orderSourceStatDTO:[{}]",
                JSON.toJSONString(orderSourceStatDTO));

        OrderSourceStatEntity orderSourceStatEntity = orderSourceStatMapper.selectByOrderNo(orderSourceStatDTO.getOrderNo());
        OrderSourceStatEntity record = buildOrderSourceStatEntity(orderSourceStatDTO);
        int result;
        if (null == orderSourceStatEntity) {
            result = orderSourceStatMapper.insertSelective(record);
        } else {
            record.setId(orderSourceStatEntity.getId());
            result = orderSourceStatMapper.updateByPrimaryKeySelective(record);
        }

        LOGGER.info("Save master order source stat information. result is, result:[{}]", result);
        return result;
    }


    /**
     * 组装主订单来源统计信息
     *
     * @param orderSourceStatDTO 主订单来源统计信息
     * @return OrderSourceStatEntity
     */
    private OrderSourceStatEntity buildOrderSourceStatEntity(OrderSourceStatDTO orderSourceStatDTO) {
        OrderSourceStatEntity record = new OrderSourceStatEntity();
        BeanCopier beanCopier = BeanCopier.create(OrderSourceStatDTO.class, OrderSourceStatEntity.class, false);
        beanCopier.copy(orderSourceStatDTO, record, null);

        record.setCreateOp(OrderConstant.SYSTEM_OPERATOR);
        record.setUpdateOp(OrderConstant.SYSTEM_OPERATOR);

        LOGGER.info("Build master order source stat information. result is, record:[{}]", JSON.toJSONString(record));
        return record;
    }

    public boolean isCtripOrderByOrderNo(String orderNo) {
        Integer count = orderSourceStatMapper.queryCtripOrderByOrderNo(orderNo);
        return (count == null || count.equals(0)) ? false : true;
    }

    /*
     * @Author ZhangBin
     * @Date 2020/1/9 11:39
     * @Description: 通过订单号获取订单状态
     *
     **/
    public OrderSourceStatEntity selectByOrderNo(String orderNo){
        return orderSourceStatMapper.selectByOrderNo(orderNo);
    }
}
