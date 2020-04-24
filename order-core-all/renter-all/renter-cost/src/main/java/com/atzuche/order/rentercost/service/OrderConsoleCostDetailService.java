package com.atzuche.order.rentercost.service;

import com.atzuche.order.commons.constant.OrderConstant;
import com.atzuche.order.commons.entity.dto.CostBaseDTO;
import com.atzuche.order.commons.enums.SubsidySourceCodeEnum;
import com.atzuche.order.commons.enums.cashcode.ConsoleCashCodeEnum;
import com.atzuche.order.rentercost.entity.OrderConsoleCostDetailEntity;
import com.atzuche.order.rentercost.mapper.OrderConsoleCostDetailMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * 后台管理操作费用表（无条件补贴）
 *
 * @author ZhangBin
 * @date 2020-01-16 15:03:47
 */
@Service
@Slf4j
public class OrderConsoleCostDetailService{
    @Autowired
    private OrderConsoleCostDetailMapper orderConsoleCostDetailMapper;


    public List<OrderConsoleCostDetailEntity> selectByOrderNoAndMemNo(String orderNo,String memNo){
        return orderConsoleCostDetailMapper.selectByOrderNoAndMemNo(orderNo,memNo);
    }

    /**
     * 获取指定费用列表
     *
     * @param orderNo 订单号
     * @param memNo 会员号
     * @param cashNos 费用编码列表
     * @return List<OrderConsoleCostDetailEntity>
     */
    public List<OrderConsoleCostDetailEntity> getOrderConsoleCostByCondition(String orderNo,String memNo,
                                                                             List<String> cashNos){
        List<OrderConsoleCostDetailEntity> list = selectByOrderNoAndMemNo(orderNo,memNo);
        if(CollectionUtils.isEmpty(list)) {
            log.info("管理后台维护费用列表为空.orderNo:[{}],memNo:[{}]", orderNo, memNo);
            return null;
        }
        return list.stream().filter(c -> cashNos.contains(c.getSubsidyCostCode())).collect(Collectors.toList());
    }



    public int getTotalOrderConsoleCostAmt(String orderNo,String memNo){
    	List<OrderConsoleCostDetailEntity> entityList = selectByOrderNoAndMemNo(orderNo, memNo);
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
    	List<OrderConsoleCostDetailEntity> list = orderConsoleCostDetailMapper.selectByOrderNoAndMemNo(record.getOrderNo(),record.getMemNo());
    	
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
     * 新增/更新订单暂扣扣款信息
     *
     * @param records 暂扣扣款信息
     * @return int
     */
    public int saveOrderConsoleCostDetai(List<OrderConsoleCostDetailEntity> records) {
        if (CollectionUtils.isEmpty(records)) {
            log.info("Save order console cost detail. records is empty!");
            return OrderConstant.ZERO;
        }

        int total = OrderConstant.ZERO;
        for (OrderConsoleCostDetailEntity record : records) {

            if (Objects.isNull(record.getId())) {
                total = total + orderConsoleCostDetailMapper.insertSelective(record);
            } else {
                total = total + orderConsoleCostDetailMapper.updateByPrimaryKeySelective(record);
            }
        }
        return total;
    }

    
    /**
     * 数据转化
     * @param costBaseDTO 基础信息
     * @param source 罚金来源编码枚举
     * @return ConsoleRenterOrderFineDeatailEntity
     */
    public OrderConsoleCostDetailEntity buildData(CostBaseDTO costBaseDTO, Integer subsidyAmount, SubsidySourceCodeEnum target, SubsidySourceCodeEnum source, ConsoleCashCodeEnum cash) {
        if (subsidyAmount == null) {
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
