package com.atzuche.order.rentercost.service;

import com.atzuche.order.rentercost.entity.RenterOrderSubsidyDetailEntity;
import com.atzuche.order.rentercost.mapper.RenterOrderSubsidyDetailMapper;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 租客补贴明细表
 */
@Service
public class RenterOrderSubsidyDetailService{
    @Autowired
    private RenterOrderSubsidyDetailMapper renterOrderSubsidyDetailMapper;


    /**
     * 获取补贴明细列表
     * @param orderNo 主订单号
     * @param renterOrderNo 租客订单号
     * @return List<RenterOrderSubsidyDetailEntity>
     */
    public List<RenterOrderSubsidyDetailEntity> listRenterOrderSubsidyDetail(String orderNo, String renterOrderNo) {
    	return renterOrderSubsidyDetailMapper.listRenterOrderSubsidyDetail(orderNo, renterOrderNo);
    }
    
    /**
     * 保存租客补贴信息
     * @param renterOrderSubsidyDetailEntity 补贴明细
     * @return Integer
     */
    public Integer saveRenterOrderSubsidyDetail(RenterOrderSubsidyDetailEntity renterOrderSubsidyDetailEntity) {
    	return renterOrderSubsidyDetailMapper.saveRenterOrderSubsidyDetail(renterOrderSubsidyDetailEntity);
    }
    
    /**
     * 批量保存租客补贴信息
     * @param entityList 补贴明细列表
     * @return Integer
     */
    public Integer saveRenterOrderSubsidyDetailBatch(List<RenterOrderSubsidyDetailEntity> entityList) {
    	return renterOrderSubsidyDetailMapper.saveRenterOrderSubsidyDetailBatch(entityList);
    }
}
