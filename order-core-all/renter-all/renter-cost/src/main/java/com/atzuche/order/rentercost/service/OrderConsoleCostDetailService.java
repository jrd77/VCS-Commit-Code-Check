package com.atzuche.order.rentercost.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atzuche.order.commons.entity.dto.CostBaseDTO;
import com.atzuche.order.commons.enums.SubsidySourceCodeEnum;
import com.atzuche.order.commons.enums.SubsidyTypeCodeEnum;
import com.atzuche.order.commons.enums.cashcode.ConsoleCashCodeEnum;
import com.atzuche.order.rentercost.entity.OrderConsoleCostDetailEntity;
import com.atzuche.order.rentercost.entity.OrderConsoleSubsidyDetailEntity;
import com.atzuche.order.rentercost.mapper.OrderConsoleCostDetailMapper;


/**
 * 后台管理操作费用表（无条件补贴）
 *
 * @author ZhangBin
 * @date 2020-01-16 15:03:47
 */
@Service
public class OrderConsoleCostDetailService{
    @Autowired
    private OrderConsoleCostDetailMapper orderConsoleCostDetailMapper;

    public List<OrderConsoleCostDetailEntity> getOrderConsoleCostDetaiByOrderNo(String orderNo){
            return orderConsoleCostDetailMapper.selectByOrderNo(orderNo);
    }

    public int getTotalOrderConsoleCostAmt(String orderNo){
        List<OrderConsoleCostDetailEntity> entityList = getOrderConsoleCostDetaiByOrderNo(orderNo);
        int total = 0;
        for(OrderConsoleCostDetailEntity entity: entityList){
            if(entity.getSubsidyAmount()!=null) {
                total = total + entity.getSubsidyAmount();
            }
        }
        return total;
    }
    
    
    /**
     * 新增或修改操作。
     * @param record
     * @return
     */
    public int saveOrUpdateOrderConsoleCostDetaiByOrderNo(OrderConsoleCostDetailEntity record){
    	List<OrderConsoleCostDetailEntity> list = orderConsoleCostDetailMapper.selectByOrderNo(record.getOrderNo());
    	
    	boolean isExists = false;
    	for (OrderConsoleCostDetailEntity orderConsoleCostDetailEntity : list) {
			if(orderConsoleCostDetailEntity.getSubsidySourceCode().equals(record.getSubsidySourceCode()) && orderConsoleCostDetailEntity.getSubsidyTargetCode().equals(record.getSubsidyTargetCode()) && orderConsoleCostDetailEntity.getSubsidyTypeCode().equals(record.getSubsidyTypeCode())) {
				record.setId(orderConsoleCostDetailEntity.getId());
				//修改的话无需修改创建人
    			record.setCreateOp(null);
				orderConsoleCostDetailMapper.updateByPrimaryKeySelective(record);
				isExists = true;
			}
		}
    	
    	if(!isExists) {
    		orderConsoleCostDetailMapper.insertSelective(record);
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
    public OrderConsoleCostDetailEntity buildData(CostBaseDTO costBaseDTO, Integer subsidyAmount, SubsidySourceCodeEnum target, SubsidySourceCodeEnum source, ConsoleCashCodeEnum cash) {
        if (subsidyAmount == null || subsidyAmount == 0) {
            return null;
        }
        
        OrderConsoleCostDetailEntity entity = new OrderConsoleCostDetailEntity();
        // 补贴金额
        entity.setSubsidyAmount(subsidyAmount);
        
        entity.setSubsidySourceCode(source.getCode());
        entity.setSubsidySourceName(source.getDesc());
        
        entity.setSubsidyTargetCode(target.getCode());
        entity.setSubsidyTargetName(target.getDesc());
        
        entity.setSubsidyTypeCode(cash.getCashNo());
        entity.setSubsidTypeName(cash.getTxt());
        
//        entity.setSubsidyCostCode(cash.getCashNo());
//        entity.setSubsidyCostName(cash.getTxt());
        
        entity.setMemNo(costBaseDTO.getMemNo());
        entity.setOrderNo(costBaseDTO.getOrderNo());
        return entity;
    }
    
    
    
}
