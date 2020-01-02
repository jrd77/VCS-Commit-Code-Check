package com.atzuche.order.renterwz.service;

import com.atzuche.order.renterwz.entity.RenterOrderWzDetailEntity;
import com.atzuche.order.renterwz.mapper.RenterOrderWzDetailMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * RenterOrderWzDetailService
 *
 * @author shisong
 * @date 2019/12/30
 */
@Service
public class RenterOrderWzDetailService {

    @Resource
    private RenterOrderWzDetailMapper renterOrderWzDetailMapper;

    public void updateIsValid(String orderNo, String carNum) {
        renterOrderWzDetailMapper.updateIsValid(orderNo,carNum);
    }

    public void addIllegalDetailFromRenyun(RenterOrderWzDetailEntity illegal) {
        renterOrderWzDetailMapper.addIllegalDetailFromRenyun(illegal);
    }

    int updateFeeByWzCode(RenterOrderWzDetailEntity illegal){
        return renterOrderWzDetailMapper.updateFeeByWzCode(illegal);
    }

    public String queryWzCodeByOrderNo(String orderNo, String carPlateNum) {
        return renterOrderWzDetailMapper.queryWzCodeByOrderNo(orderNo,carPlateNum);
    }
}
