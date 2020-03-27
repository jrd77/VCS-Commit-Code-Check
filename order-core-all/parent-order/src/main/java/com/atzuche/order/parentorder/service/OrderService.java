package com.atzuche.order.parentorder.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.parentorder.dto.OrderDTO;
import com.atzuche.order.parentorder.dto.SuccessOrderDTO;
import com.atzuche.order.parentorder.entity.OrderEntity;
import com.atzuche.order.parentorder.mapper.OrderMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


/**
 * 主订单表
 *
 * @author ZhangBin
 * @date 2019-12-24 16:19:33
 */
@Service
public class OrderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderService.class);

    @Resource
    private OrderMapper orderMapper;


    /**
     * 主订单信息保存
     *
     * @param orderDTO 主订单信息
     * @return int 操作成功记录数
     */
    public int saveOrderInfo(OrderDTO orderDTO) {
        LOGGER.info("Save master order information. param is, orderDTO:[{}]", JSON.toJSONString(orderDTO));
        OrderEntity orderEntity = orderMapper.selectByOrderNo(orderDTO.getOrderNo());
        OrderEntity record = buildOrderEntity(orderDTO);
        int result;
        if (null == orderEntity) {
            result = orderMapper.insertSelective(record);
        } else {
            record.setId(orderEntity.getId());
            result = orderMapper.updateByPrimaryKeySelective(record);
        }
        LOGGER.info("Save master order information. result is, result:[{}]", result);
        return result;
    }


    /**
     * 组装主订单信息
     *
     * @param orderDTO 主订单信息
     * @return OrderEntity
     */
    private OrderEntity buildOrderEntity(OrderDTO orderDTO) {
        OrderEntity record = new OrderEntity();
        BeanCopier beanCopier = BeanCopier.create(OrderDTO.class, OrderEntity.class, false);
        beanCopier.copy(orderDTO, record, null);

        record.setCreateOp("");
        record.setUpdateOp("");

        LOGGER.info("Build master order information. result is, record:[{}]", JSON.toJSONString(record));
        return record;
    }
    
    /**
     * 根据主订单号获取主订单信息
     * @param orderNo 主订单号
     * @return OrderEntity
     */
    public OrderEntity getOrderEntity(String orderNo) {
    	return orderMapper.selectByOrderNo(orderNo);
    }

    public List<SuccessOrderDTO> queryOrderNoByOrderNos(List<String> orderNos) {
        return orderMapper.queryOrderNoByOrderNos(orderNos);
    }
    
    /**
     * 根据订单号和会员号获取主订单信息
     * @param orderNo 主订单号
     * @param memNo 租客会员号
     * @return OrderEntity
     */
    public OrderEntity getOrderByOrderNoAndMemNo(String orderNo, String memNo) {
    	return orderMapper.getOrderByOrderNoAndMemNo(orderNo, memNo);
    }
    /*
     * @Author ZhangBin
     * @Date 2020/1/11 11:22
     * @Description: 通过订单号更新订单信息
     * 
     **/
    public int updateOrderByOrderNo(OrderEntity orderEntity){
       return orderMapper.updateByOrderNoSelective(orderEntity);
    }

    public List<OrderEntity> getByOrderNos(List<String> orderNos) {
        return orderMapper.getByOrderNos(orderNos);
    }

    public List<String> getorderNoAll() {
        List<String> orderNos = orderMapper.getorderNoAll();
        return orderNos;
    }
}
