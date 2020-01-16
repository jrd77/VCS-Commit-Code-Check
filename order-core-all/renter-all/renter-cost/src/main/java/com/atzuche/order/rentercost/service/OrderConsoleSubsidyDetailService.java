package com.atzuche.order.rentercost.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atzuche.order.commons.entity.dto.CostBaseDTO;
import com.atzuche.order.commons.enums.RenterCashCodeEnum;
import com.atzuche.order.commons.enums.SubsidySourceCodeEnum;
import com.atzuche.order.commons.enums.SubsidyTypeCodeEnum;
import com.atzuche.order.rentercost.entity.OrderConsoleSubsidyDetailEntity;
import com.atzuche.order.rentercost.mapper.OrderConsoleSubsidyDetailMapper;



/**
 * 后台管理操补贴明细表（无条件补贴）
 *
 * @author ZhangBin
 * @date 2020-01-01 11:01:58
 */
@Service
public class OrderConsoleSubsidyDetailService{
    @Autowired
    private OrderConsoleSubsidyDetailMapper orderConsoleSubsidyDetailMapper;


    /**
     * 获取管理后台无条件补贴列表
     * @param orderNo 主订单号
     * @param memNo 会员号
     * @return List<OrderConsoleSubsidyDetailEntity>
     */
    public List<OrderConsoleSubsidyDetailEntity> listOrderConsoleSubsidyDetailByOrderNoAndMemNo(String orderNo, String memNo) {
    	return orderConsoleSubsidyDetailMapper.listOrderConsoleSubsidyDetailByOrderNoAndMemNo(orderNo, memNo);
    }
    
    public int saveOrUpdateOrderConsoleSubsidyDetailAdjust(OrderConsoleSubsidyDetailEntity record) {
    	
    	//反向记录  取反向值。
    	SubsidySourceCodeEnum targetEnum = SubsidySourceCodeEnum.RENTER;
    	if(SubsidySourceCodeEnum.RENTER.getCode().equals(record.getSubsidyTargetCode())) {
    		targetEnum = SubsidySourceCodeEnum.OWNER;
    	}else if(SubsidySourceCodeEnum.OWNER.getCode().equals(record.getSubsidyTargetCode())) {
    		targetEnum = SubsidySourceCodeEnum.RENTER;
    	}
    	
    	SubsidySourceCodeEnum sourceEnum = SubsidySourceCodeEnum.RENTER;
    	if(SubsidySourceCodeEnum.RENTER.getCode().equals(record.getSubsidyTargetCode())) {
    		sourceEnum = SubsidySourceCodeEnum.OWNER;
    	}else if(SubsidySourceCodeEnum.OWNER.getCode().equals(record.getSubsidyTargetCode())) {
    		sourceEnum = SubsidySourceCodeEnum.RENTER;
    	}
    	
    	//相同，保持相同
    	RenterCashCodeEnum cashConvert = RenterCashCodeEnum.subsidy_ownerToRenterAdjust;
    	if(record.getSubsidyCostCode().equals(cashConvert.getCashNo())) {
    		cashConvert = RenterCashCodeEnum.subsidy_ownerToRenterAdjust;
    	}else if(record.getSubsidyCostCode().equals(RenterCashCodeEnum.subsidy_renterToOwnerAdjust.getCashNo())) {  //租客给车主的调价
    		cashConvert = RenterCashCodeEnum.subsidy_renterToOwnerAdjust;
    	}
    	
    	/**
    	 * 反向
    	 */
    	CostBaseDTO costBaseDTO = new CostBaseDTO();
    	costBaseDTO.setOrderNo(record.getOrderNo());
    	costBaseDTO.setMemNo(record.getMemNo());
    	OrderConsoleSubsidyDetailEntity recordConvert = subsidyDataConvert(costBaseDTO, -record.getSubsidyAmount(), targetEnum, sourceEnum, SubsidyTypeCodeEnum.ADJUST_AMT, cashConvert);
    	
    	List<OrderConsoleSubsidyDetailEntity> list = listOrderConsoleSubsidyDetailByOrderNoAndMemNo(record.getOrderNo(), record.getMemNo()); 
    	boolean isExists = false;
    	boolean isConvertExists = false;
    	for (OrderConsoleSubsidyDetailEntity orderConsoleSubsidyDetailEntity : list) {
			//存在
    		if(orderConsoleSubsidyDetailEntity.getSubsidySourceCode().equals(record.getSubsidySourceCode()) && orderConsoleSubsidyDetailEntity.getSubsidyTargetCode().equals(record.getSubsidyTargetCode()) && orderConsoleSubsidyDetailEntity.getSubsidyCostCode().equals(record.getSubsidyCostCode())) {
    			orderConsoleSubsidyDetailMapper.updateByPrimaryKeySelective(record);
    			isExists = true;
			}
    		//修改反向记录（费用编码相同）
    		if(orderConsoleSubsidyDetailEntity.getSubsidySourceCode().equals(record.getSubsidyTargetCode()) && orderConsoleSubsidyDetailEntity.getSubsidyTargetCode().equals(record.getSubsidySourceCode()) && orderConsoleSubsidyDetailEntity.getSubsidyCostCode().equals(record.getSubsidyCostCode())) {
    			orderConsoleSubsidyDetailMapper.updateByPrimaryKeySelective(recordConvert);
    			isConvertExists = true;
			}
		}
    	
    	if(!isExists) {
    		//增加记录
    		orderConsoleSubsidyDetailMapper.insertSelective(record);
    		
    	}
    	if(!isConvertExists) {
    		//同时增加一条反向记录
    		orderConsoleSubsidyDetailMapper.insertSelective(recordConvert);
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
    public OrderConsoleSubsidyDetailEntity subsidyDataConvert(CostBaseDTO costBaseDTO, Integer subsidyAmount, SubsidySourceCodeEnum target, SubsidySourceCodeEnum source, SubsidyTypeCodeEnum type,RenterCashCodeEnum cash) {
        if (subsidyAmount == null || subsidyAmount == 0) {
            return null;
        }
        
        OrderConsoleSubsidyDetailEntity entity = new OrderConsoleSubsidyDetailEntity();
        // 罚金负数
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
        return entity;
    }
}
