package com.atzuche.order.rentercost.service;

import com.atzuche.order.rentercost.entity.RenterOrderSubsidyDetailEntity;
import com.atzuche.order.rentercost.mapper.RenterOrderSubsidyDetailMapper;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 租客补贴明细表
 *
 * @author ZhangBin
 * @date 2019-12-14 17:30:57
 */
@Service
public class RenterOrderSubsidyDetailService{
    @Autowired
    private RenterOrderSubsidyDetailMapper renterOrderSubsidyDetailMapper;


    /**
     * 获取补贴明细列表
     * @param orderNo
     * @param renterOrderNo
     * @return List<RenterOrderSubsidyDetailEntity>
     */
    public List<RenterOrderSubsidyDetailEntity> listRenterOrderSubsidyDetail(String orderNo, String renterOrderNo) {
    	return renterOrderSubsidyDetailMapper.listRenterOrderSubsidyDetail(orderNo, renterOrderNo);
    }
}
