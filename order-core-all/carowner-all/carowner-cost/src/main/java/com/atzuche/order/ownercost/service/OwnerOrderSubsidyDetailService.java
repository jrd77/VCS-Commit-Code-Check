package com.atzuche.order.ownercost.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atzuche.order.commons.entity.dto.CostBaseDTO;
import com.atzuche.order.commons.enums.SubsidySourceCodeEnum;
import com.atzuche.order.commons.enums.SubsidyTypeCodeEnum;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.ownercost.entity.OwnerOrderSubsidyDetailEntity;
import com.atzuche.order.ownercost.mapper.OwnerOrderSubsidyDetailMapper;


@Service
public class OwnerOrderSubsidyDetailService {

	@Autowired
	private OwnerOrderSubsidyDetailMapper ownerOrderSubsidyDetailMapper;
	
	/**
	 * 获取车主补贴明细列表
	 * @param orderNo 主订单号
	 * @param ownerOrderNo 车主订单号
	 * @return List<OwnerOrderSubsidyDetailEntity>
	 */
	public List<OwnerOrderSubsidyDetailEntity> listOwnerOrderSubsidyDetail(String orderNo, String ownerOrderNo) {
		return ownerOrderSubsidyDetailMapper.listOwnerOrderSubsidyDetail(orderNo, ownerOrderNo);
	}
	
	/**
	 * 保存车主补贴明细
	 * @param ownerOrderSubsidyDetailEntity 车主补贴明细
	 * @return Integer
	 */
	public Integer saveOwnerOrderSubsidyDetail(OwnerOrderSubsidyDetailEntity ownerOrderSubsidyDetailEntity) {
		if (ownerOrderSubsidyDetailEntity == null) {
			return 0;
		}
		return ownerOrderSubsidyDetailMapper.saveOwnerOrderSubsidyDetail(ownerOrderSubsidyDetailEntity);
	}
	
	/**
	 * 批量保存车主补贴明细
	 * @param costList 车主补贴明细列表
	 * @return Integer
	 */
	public Integer saveOwnerOrderSubsidyDetailBatch(List<OwnerOrderSubsidyDetailEntity> costList) {
		return ownerOrderSubsidyDetailMapper.saveOwnerOrderSubsidyDetailBatch(costList);
	}
	
	/**
	 * 
	 * @param record
	 * @return
	 */
	public int saveOrUpdateRenterOrderSubsidyDetail(OwnerOrderSubsidyDetailEntity record) {
    	List<OwnerOrderSubsidyDetailEntity> list = listOwnerOrderSubsidyDetail(record.getOrderNo(), record.getOwnerOrderNo()); 
    	boolean isExists = false;
    	for (OwnerOrderSubsidyDetailEntity renterOrderSubsidyDetailEntity : list) {
			//存在
    		if(renterOrderSubsidyDetailEntity.getSubsidySourceCode().equals(record.getSubsidySourceCode()) && renterOrderSubsidyDetailEntity.getSubsidyTargetCode().equals(record.getSubsidyTargetCode()) && renterOrderSubsidyDetailEntity.getSubsidyCostCode().equals(record.getSubsidyCostCode())) {
    			record.setId(renterOrderSubsidyDetailEntity.getId());
    			ownerOrderSubsidyDetailMapper.updateByPrimaryKeySelective(record);
    			isExists = true;
			}
		}
    	
    	if(!isExists) {
    		//增加记录
    		ownerOrderSubsidyDetailMapper.saveOwnerOrderSubsidyDetail(record);
    	}
    	
    	
    	return 1;
    }
    
    /**
     * 数据转化
     * @param costBaseDTO 基础信息
     * @param fineAmt 罚金金额
     * @param code 罚金补贴方编码枚举
     * @param source 罚金来源编码枚举
     * @param type 罚金类型枚举
     * @return ConsoleRenterOrderFineDeatailEntity
     */
    public OwnerOrderSubsidyDetailEntity buildData(CostBaseDTO costBaseDTO, Integer subsidyAmount, SubsidySourceCodeEnum target, SubsidySourceCodeEnum source, SubsidyTypeCodeEnum type,RenterCashCodeEnum cash) {
        if (subsidyAmount == null || subsidyAmount == 0) {
            return null;
        }
        
        OwnerOrderSubsidyDetailEntity entity = new OwnerOrderSubsidyDetailEntity();
        // 补贴金额
        entity.setSubsidyAmount(subsidyAmount);
        
        entity.setSubsidySourceCode(source.getCode());
        entity.setSubsidySourceName(source.getDesc());
        
        entity.setSubsidyTargetCode(target.getCode());
        entity.setSubsidyTargetName(target.getDesc());
        
        entity.setSubsidyTypeCode(type.getCode());
        entity.setSubsidyTypeName(type.getDesc());
        
        entity.setSubsidyCostCode(cash.getCashNo());
        entity.setSubsidyCostName(cash.getTxt());
        
        entity.setMemNo(costBaseDTO.getMemNo());
        entity.setOrderNo(costBaseDTO.getOrderNo());
        ///必传
        entity.setOwnerOrderNo(costBaseDTO.getOwnerOrderNo());
        return entity;
    }
    
}
