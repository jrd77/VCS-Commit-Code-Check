package com.atzuche.order.renterwz.service;

import com.atzuche.order.renterwz.entity.RenterOrderWzStatusEntity;
import com.atzuche.order.renterwz.enums.WzStatusEnums;
import com.atzuche.order.renterwz.mapper.RenterOrderWzStatusMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * RenterOrderWzStatusService
 *
 * @author shisong
 * @date 2019/12/30
 */
@Service
public class RenterOrderWzStatusService {

    @Resource
    private RenterOrderWzStatusMapper renterOrderWzStatusMapper;

    public void updateTransIllegalQuery(Integer illegalQuery, String orderNo, String carNum) {
        renterOrderWzStatusMapper.updateTransIllegalQuery(illegalQuery,orderNo,carNum);
    }

    public void updateTransIllegalStatus(Integer status, String orderNo, String carNum) {
        renterOrderWzStatusMapper.updateTransIllegalStatus(status,orderNo,carNum, WzStatusEnums.getStatusDesc(status));
    }

    public void updateIllegalHandle(Integer managementMode, String orderNo, String carNum) {
        renterOrderWzStatusMapper.updateIllegalHandle(managementMode,orderNo,carNum);
    }

    public RenterOrderWzStatusEntity selectByOrderNo(String orderNo, String carNum) {
        return renterOrderWzStatusMapper.selectByOrderNo(orderNo,carNum);
    }

    public void updateStatusByOrderNoAndCarNum(String orderNo, Integer status, String carNum) {
        renterOrderWzStatusMapper.updateStatusByOrderNoAndCarNum(orderNo,status,carNum,WzStatusEnums.getStatusDesc(status));
    }

    public Integer getTransWzDisposeStatusByOrderNo(String orderNo, String plateNum) {
        return renterOrderWzStatusMapper.getTransWzDisposeStatusByOrderNo(orderNo,plateNum);
    }

    public void updateTransWzDisposeStatus(String orderNo, String carNumber, int wzDisposeStatus) {
        renterOrderWzStatusMapper.updateTransWzDisposeStatus(orderNo,carNumber,wzDisposeStatus);
    }
}
