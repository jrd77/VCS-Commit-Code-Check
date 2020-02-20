package com.atzuche.order.delivery.service;

import com.atzuche.order.delivery.entity.RenterDeliveryAddrEntity;
import com.atzuche.order.delivery.entity.RenterOrderDeliveryEntity;
import com.atzuche.order.delivery.mapper.RenterDeliveryAddrMapper;
import com.atzuche.order.delivery.mapper.RenterOrderDeliveryMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;


@Service
public class RenterOrderDeliveryService {

    @Resource
    private RenterOrderDeliveryMapper renterOrderDeliveryMapper;
    @Resource
    private RenterDeliveryAddrMapper renterDeliveryAddrMapper;

    /**
     * 根据租客子单号获取配送信息列表
     * @param renterOrderNo 租客子单号
     * @return List<RenterOrderDeliveryEntity>
     */
    public List<RenterOrderDeliveryEntity> listRenterOrderDeliveryByRenterOrderNo(String renterOrderNo) {
        List<RenterOrderDeliveryEntity> deliveryList = renterOrderDeliveryMapper.listRenterOrderDeliveryByRenterOrderNo(renterOrderNo);
        if (deliveryList == null || deliveryList.isEmpty()) {
            return null;
        }
        // 根据type去重
        deliveryList = deliveryList.stream().collect(
                Collectors.collectingAndThen(
                        Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(RenterOrderDeliveryEntity::getType))), ArrayList::new));
        return deliveryList;
    }

    /**
     * 查找最近的一笔订单
     * @param orderNo
     * @param type
     * @return
     */
    public RenterOrderDeliveryEntity findRenterOrderByrOrderNo(String orderNo, Integer type) {
        RenterOrderDeliveryEntity lastOrderDeliveryEntity = renterOrderDeliveryMapper.findRenterOrderByrOrderNo(orderNo, type);
        return lastOrderDeliveryEntity;
    }

    /**
     * 根据子订单号获取配送订单地址信息
     * @param renterOrderNo
     * @return
     */
    public RenterDeliveryAddrEntity selectAddrByRenterOrderNo(String renterOrderNo) {
        RenterDeliveryAddrEntity renterDeliveryAddrEntity = renterDeliveryAddrMapper.selectByRenterOrderNo(renterOrderNo);
        return renterDeliveryAddrEntity;
    }

    /**
     * 新增配送订单信息
     * @param renterOrderDeliveryEntity
     */
    public void insert(RenterOrderDeliveryEntity renterOrderDeliveryEntity) {
        renterOrderDeliveryMapper.insertSelective(renterOrderDeliveryEntity);
    }

    /**
     * 根据子订单号和类型
     * @param renterOrderNo
     * @param type
     * @return
     */
    public RenterOrderDeliveryEntity findRenterOrderByRenterOrderNo(String renterOrderNo, Integer type) {
        return renterOrderDeliveryMapper.findRenterOrderByRenterOrderNo(renterOrderNo, type);
    }

    /**
     * 根据子订单号和类型
     * @param renterOrderNo
     * @param type
     * @return
     */
    public RenterOrderDeliveryEntity findRenterOrderByRenterOrderNoHistory(String renterOrderNo, Integer type) {
        return renterOrderDeliveryMapper.findRenterOrderByRenterOrderNoHistory(renterOrderNo, type);
    }

    /**
     * 根据ID更新状态值(取消配送订单)
     * @param id
     * @return
     */
    public Integer updateStatusById(Integer id) {
        return renterOrderDeliveryMapper.updateStatusById(id);
    }

    /**
     * 新增配送订单地址信息
     * @param renterDeliveryAddrEntity
     */
    public void insertDeliveryAddr(RenterDeliveryAddrEntity renterDeliveryAddrEntity) {
        renterDeliveryAddrMapper.insertSelective(renterDeliveryAddrEntity);
    }

    /**
     * 更新配送订单地址信息
     * @param renterDeliveryAddrEntity
     */
    public void updateDeliveryAddrByPrimaryKey(RenterDeliveryAddrEntity renterDeliveryAddrEntity) {
        renterDeliveryAddrMapper.updateByPrimaryKey(renterDeliveryAddrEntity);
    }


    /**
     * 更新配送订单信息
     * @param renterOrderDeliveryEntity
     */
    public void updateDeliveryByPrimaryKey(RenterOrderDeliveryEntity renterOrderDeliveryEntity) {
        renterOrderDeliveryMapper.updateByPrimaryKey(renterOrderDeliveryEntity);
    }

    /**
     * 通过租客子订单号查询配送订单
     * @param renterOrderNo
     * @return
     */
    public List<RenterOrderDeliveryEntity> selectByRenterOrderNo(String renterOrderNo) {
        return renterOrderDeliveryMapper.selectByRenterOrderNo(renterOrderNo);
    }

    /**
     * 通过订单号查询配送订单
     * @param orderNo
     * @return
     */
    public List<RenterOrderDeliveryEntity> findRenterOrderListByOrderNo(String orderNo) {
        return renterOrderDeliveryMapper.findRenterOrderListByorderNo(orderNo);
    }
}
