package com.atzuche.order.renterorder.service;

import com.atzuche.order.renterorder.entity.RenterOrderChangeApplyEntity;
import com.atzuche.order.renterorder.mapper.RenterOrderChangeApplyMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 租客订单变更申请表
 *
 * @author ZhangBin
 * @date 2019-12-14 17:24:31
 */
@Service
public class RenterOrderChangeApplyService{
    @Autowired
    private RenterOrderChangeApplyMapper renterOrderChangeApplyMapper;
    
    /**
     * 获取租客申请记录根据租客订单号
     * @param renterOrderNo
     * @return RenterOrderChangeApplyEntity
     */
    public RenterOrderChangeApplyEntity getRenterOrderChangeApplyByRenterOrderNo(String renterOrderNo) {
    	return renterOrderChangeApplyMapper.getRenterOrderChangeApplyByRenterOrderNo(renterOrderNo);
    }
    
    
    public RenterOrderChangeApplyEntity getRenterOrderApplyByRenterOrderNo(String renterOrderNo) {
    	return renterOrderChangeApplyMapper.getRenterOrderApplyByRenterOrderNo(renterOrderNo);
    }
    
    
    
    /**
     * 根据主订单号查询修改记录列表
     * @param orderNo
     * @return
     */
    public List<RenterOrderChangeApplyEntity> selectALLByOrderNo(String orderNo){
    	return renterOrderChangeApplyMapper.selectALLByOrderNo(orderNo);
    }
    



    /**
     * 保存租客修改申请记录
     * @param renterOrderChangeApplyEntity 租客修改申请记录
     * @return Integer
     */
    public Integer saveRenterOrderChangeApply(RenterOrderChangeApplyEntity renterOrderChangeApplyEntity) {
    	return renterOrderChangeApplyMapper.saveRenterOrderChangeApply(renterOrderChangeApplyEntity);
    }
    
    /**
     * 修改租客申请记录状态
     * @param id
     * @param auditStatus
     * @return Integer
     */
    public Integer updateRenterOrderChangeApplyStatus(Integer id, Integer auditStatus) {
    	return renterOrderChangeApplyMapper.updateRenterOrderChangeApplyStatus(id, auditStatus);
    }
    /**
     * 获取是否有待处理得申请记录
     * @param orderNo
     * @return Integer
     */
    public Integer getRenterOrderChangeApplyCountByOrderNo(String orderNo) {
    	return renterOrderChangeApplyMapper.getRenterOrderChangeApplyCountByOrderNo(orderNo);
    }
    
    /**
     * 更新申请状态为拒绝根据订单号
     * @param orderNo
     * @return Integer
     */
    public Integer updateRenterOrderChangeApplyStatusByOrderNo(String orderNo) {
    	return renterOrderChangeApplyMapper.updateRenterOrderChangeApplyStatusByOrderNo(orderNo);
    }
}
