package com.atzuche.order.rentercost.service;

import com.atzuche.order.commons.entity.dto.CostBaseDTO;
import com.atzuche.order.commons.enums.SubsidySourceCodeEnum;
import com.atzuche.order.commons.enums.SubsidyTypeCodeEnum;
import com.atzuche.order.commons.enums.cashcode.OwnerCashCodeEnum;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.rentercost.entity.OrderConsoleSubsidyDetailEntity;
import com.atzuche.order.rentercost.mapper.OrderConsoleSubsidyDetailMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;



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

    /**
     * 获取管理后台无条件补贴总额
     * @param orderNo
     * @param memNo
     * @return
     */
    public int getTotalRenterOrderConsoleSubsidy(String orderNo, String memNo){
        List<OrderConsoleSubsidyDetailEntity> entityList = listOrderConsoleSubsidyDetailByOrderNoAndMemNo(orderNo,memNo);
        int total = 0;
        for(OrderConsoleSubsidyDetailEntity entity:entityList){
            if(entity.getSubsidyAmount()!=null){
                total = total +entity.getSubsidyAmount();
            }
        }
        return total;
    }
    
    /**
     * 调价的公共方法，在一个方法内完成。
     * @param record
     * @return
     */
    public int saveOrUpdateOrderConsoleSubsidyDetail(OrderConsoleSubsidyDetailEntity record) {
    	List<OrderConsoleSubsidyDetailEntity> list = listOrderConsoleSubsidyDetailByOrderNoAndMemNo(record.getOrderNo(), record.getMemNo()); 
    	boolean isExists = false;
    	for (OrderConsoleSubsidyDetailEntity orderConsoleSubsidyDetailEntity : list) {
			//存在
    		if(orderConsoleSubsidyDetailEntity.getSubsidySourceCode().equals(record.getSubsidySourceCode()) && orderConsoleSubsidyDetailEntity.getSubsidyTargetCode().equals(record.getSubsidyTargetCode()) && orderConsoleSubsidyDetailEntity.getSubsidyCostCode().equals(record.getSubsidyCostCode())) {
    			record.setId(orderConsoleSubsidyDetailEntity.getId());
    			//修改的话无需修改创建人
    			record.setCreateOp(null);
    			orderConsoleSubsidyDetailMapper.updateByPrimaryKeySelective(record);
    			isExists = true;
			}
		}
    	
    	if(!isExists) {
    		//增加记录
    		orderConsoleSubsidyDetailMapper.insertSelective(record);
    	}
    	
    	
    	return 1;
    }
    public int saveOrUpdateOrderConsoleSubsidyDetailByMemNo(OrderConsoleSubsidyDetailEntity record) {
        List<OrderConsoleSubsidyDetailEntity> list = listOrderConsoleSubsidyDetailByOrderNoAndMemNo(record.getOrderNo(), record.getMemNo());
        boolean isExists = false;
        for (OrderConsoleSubsidyDetailEntity orderConsoleSubsidyDetailEntity : list) {
            //存在
            if(orderConsoleSubsidyDetailEntity.getSubsidySourceCode().equals(record.getSubsidySourceCode())
                    && orderConsoleSubsidyDetailEntity.getSubsidyTargetCode().equals(record.getSubsidyTargetCode())
                    && orderConsoleSubsidyDetailEntity.getSubsidyCostCode().equals(record.getSubsidyCostCode())
                    && orderConsoleSubsidyDetailEntity.getMemNo().equals(record.getMemNo())) {
                record.setId(orderConsoleSubsidyDetailEntity.getId());
                //修改的话无需修改创建人
                record.setCreateOp(null);
                orderConsoleSubsidyDetailMapper.updateByPrimaryKeySelective(record);
                isExists = true;
            }
        }

        if(!isExists) {
            //增加记录
            orderConsoleSubsidyDetailMapper.insertSelective(record);
        }


        return 1;
    }
    
    /**
     * 调价的公共方法，在一个方法内完成。    使用上面的saveOrUpdateOrderConsoleSubsidyDetail方法。
     * @param record
     * @return
     */
//    public int saveOrUpdateOrderConsoleSubsidyDetailAdjust(OrderConsoleSubsidyDetailEntity record) {
//    	/**
//    	 * 保持一致
//    	 */
//    	//反向记录  取反向值。
//    	SubsidySourceCodeEnum targetEnum = SubsidySourceCodeEnum.RENTER;
//    	if(SubsidySourceCodeEnum.RENTER.getCode().equals(record.getSubsidyTargetCode())) {
//    		targetEnum = SubsidySourceCodeEnum.RENTER;
//    	}else if(SubsidySourceCodeEnum.OWNER.getCode().equals(record.getSubsidyTargetCode())) {
//    		targetEnum = SubsidySourceCodeEnum.OWNER;
//    	}
//    	
//    	SubsidySourceCodeEnum sourceEnum = SubsidySourceCodeEnum.RENTER;
//    	if(SubsidySourceCodeEnum.RENTER.getCode().equals(record.getSubsidySourceCode())) {
//    		sourceEnum = SubsidySourceCodeEnum.RENTER;
//    	}else if(SubsidySourceCodeEnum.OWNER.getCode().equals(record.getSubsidySourceCode())) {
//    		sourceEnum = SubsidySourceCodeEnum.OWNER;
//    	}
//    	
//    	//相同，保持相同
//    	RenterCashCodeEnum cashConvert = RenterCashCodeEnum.SUBSIDY_OWNERTORENTER_ADJUST;
//    	if(record.getSubsidyCostCode().equals(cashConvert.getCashNo())) {
//    		cashConvert = RenterCashCodeEnum.SUBSIDY_OWNERTORENTER_ADJUST;
//    	}else if(record.getSubsidyCostCode().equals(RenterCashCodeEnum.SUBSIDY_RENTERTOOWNER_ADJUST.getCashNo())) {  //租客给车主的调价
//    		cashConvert = RenterCashCodeEnum.SUBSIDY_RENTERTOOWNER_ADJUST;
//    	}
//    	/**
//    	 * 反向
//    	 */
//    	CostBaseDTO costBaseDTO = new CostBaseDTO();
//    	costBaseDTO.setOrderNo(record.getOrderNo());
//    	costBaseDTO.setMemNo(record.getMemNo());
//    	OrderConsoleSubsidyDetailEntity recordConvert = buildData(costBaseDTO, -record.getSubsidyAmount(), targetEnum, sourceEnum, SubsidyTypeCodeEnum.ADJUST_AMT, cashConvert);
//    	
//    	List<OrderConsoleSubsidyDetailEntity> list = listOrderConsoleSubsidyDetailByOrderNoAndMemNo(record.getOrderNo(), record.getMemNo()); 
//    	boolean isExists = false;
//    	boolean isConvertExists = false;
//    	for (OrderConsoleSubsidyDetailEntity orderConsoleSubsidyDetailEntity : list) {
//			//存在
//    		if(orderConsoleSubsidyDetailEntity.getSubsidySourceCode().equals(record.getSubsidySourceCode()) && orderConsoleSubsidyDetailEntity.getSubsidyTargetCode().equals(record.getSubsidyTargetCode()) && orderConsoleSubsidyDetailEntity.getSubsidyCostCode().equals(record.getSubsidyCostCode())) {
//    			record.setId(orderConsoleSubsidyDetailEntity.getId());
//    			orderConsoleSubsidyDetailMapper.updateByPrimaryKeySelective(record);
//    			isExists = true;
//			}
//    		//修改反向记录（费用编码相同）
//    		if(orderConsoleSubsidyDetailEntity.getSubsidySourceCode().equals(record.getSubsidyTargetCode()) && orderConsoleSubsidyDetailEntity.getSubsidyTargetCode().equals(record.getSubsidySourceCode()) && orderConsoleSubsidyDetailEntity.getSubsidyCostCode().equals(record.getSubsidyCostCode())) {
//    			record.setId(orderConsoleSubsidyDetailEntity.getId());
//    			orderConsoleSubsidyDetailMapper.updateByPrimaryKeySelective(recordConvert);
//    			isConvertExists = true;
//			}
//		}
//    	
//    	if(!isExists) {
//    		//增加记录
//    		orderConsoleSubsidyDetailMapper.insertSelective(record);
//    		
//    	}
//    	if(!isConvertExists) {
//    		//同时增加一条反向记录
//    		orderConsoleSubsidyDetailMapper.insertSelective(recordConvert);
//    	}
//    	
//    	return 1;
//    }
    
    
    
    /**
     * 数据转化
     * @param costBaseDTO 基础信息
     * @param fineAmt 罚金金额
     * @param code 罚金补贴方编码枚举
     * @param source 罚金来源编码枚举
     * @param type 罚金类型枚举
     * @return ConsoleRenterOrderFineDeatailEntity
     */
    public OrderConsoleSubsidyDetailEntity buildData(CostBaseDTO costBaseDTO, Integer subsidyAmount, SubsidySourceCodeEnum target, SubsidySourceCodeEnum source, SubsidyTypeCodeEnum type,RenterCashCodeEnum cash) {
        if (subsidyAmount == null ) { //|| subsidyAmount == 0
            return null;
        }
        
        OrderConsoleSubsidyDetailEntity entity = new OrderConsoleSubsidyDetailEntity();
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
        return entity;
    }
    
    /**
     * 封装车主
     * @param costBaseDTO
     * @param subsidyAmount
     * @param target
     * @param source
     * @param type
     * @param cash
     * @return
     */
    public OrderConsoleSubsidyDetailEntity buildDataOwner(CostBaseDTO costBaseDTO, Integer subsidyAmount, SubsidySourceCodeEnum target, SubsidySourceCodeEnum source, SubsidyTypeCodeEnum type,OwnerCashCodeEnum cash) {
        if (subsidyAmount == null ) { //|| subsidyAmount == 0
            return null;
        }
        
        OrderConsoleSubsidyDetailEntity entity = new OrderConsoleSubsidyDetailEntity();
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
        return entity;
    }
    
    
    /**
     * 保存管理后台补贴
     * @param consoleSubsidy
     * @return Integer
     */
    public Integer saveConsoleSubsidy(OrderConsoleSubsidyDetailEntity consoleSubsidy) {
    	return orderConsoleSubsidyDetailMapper.insertSelective(consoleSubsidy);
    }
    
    /**
     * 删除管理后台补贴
     * @param orderNo
     * @param subsidyCostCode
     * @return Integer
     */
    public Integer deleteConsoleSubsidyByOrderNoAndCode(String orderNo, String subsidyCostCode) {
    	return orderConsoleSubsidyDetailMapper.deleteConsoleSubsidyByOrderNoAndCode(orderNo, subsidyCostCode);
    }
    
    /**
     * 保存升级补贴
     * @param orderNo
     * @param subsidyCostCode
     * @param consoleSubsidy
     */
    public void saveDispatchingSubsidy(String orderNo, OrderConsoleSubsidyDetailEntity consoleSubsidy) {
    	if (consoleSubsidy == null) {
    		return;
    	}
    	// 先删以前的升级补贴
    	deleteConsoleSubsidyByOrderNoAndCode(orderNo, RenterCashCodeEnum.DISPATCHING_AMT.getCashNo());
    	// 再新增当前升级补贴
    	saveConsoleSubsidy(consoleSubsidy);
    }
    
    
    /**
     * 仁云追加补贴调用
     * @param record
     * @return int
     */
    public int saveOrUpdateOrderConsoleSubsidyDetailForRenyun(OrderConsoleSubsidyDetailEntity record) {
    	List<OrderConsoleSubsidyDetailEntity> list = listOrderConsoleSubsidyDetailByOrderNoAndMemNo(record.getOrderNo(), record.getMemNo()); 
    	boolean isExists = false;
    	for (OrderConsoleSubsidyDetailEntity orderConsoleSubsidyDetailEntity : list) {
			//存在
    		if(orderConsoleSubsidyDetailEntity.getSubsidySourceCode().equals(record.getSubsidySourceCode()) && orderConsoleSubsidyDetailEntity.getSubsidyTargetCode().equals(record.getSubsidyTargetCode()) && orderConsoleSubsidyDetailEntity.getSubsidyCostCode().equals(record.getSubsidyCostCode())) {
    			record.setId(orderConsoleSubsidyDetailEntity.getId());
    			//修改的话无需修改创建人
    			record.setCreateOp(null);
    			if (orderConsoleSubsidyDetailEntity.getSubsidyAmount() != null && record.getSubsidyAmount() != null) {
    				record.setSubsidyAmount(orderConsoleSubsidyDetailEntity.getSubsidyAmount()+record.getSubsidyAmount());
    			}
    			orderConsoleSubsidyDetailMapper.updateByPrimaryKeySelective(record);
    			isExists = true;
			}
		}
    	
    	if(!isExists) {
    		//增加记录
    		orderConsoleSubsidyDetailMapper.insertSelective(record);
    	}
    	
    	
    	return 1;
    }
    
    
    /**
     * 获取平台给租客的补贴
     * @param orderNo
     * @param memNo
     * @return List<OrderConsoleSubsidyDetailEntity>
     */
    public List<OrderConsoleSubsidyDetailEntity> listPlatformToRenterSubsidy(String orderNo, String memNo){
        List<OrderConsoleSubsidyDetailEntity> entityList = listOrderConsoleSubsidyDetailByOrderNoAndMemNo(orderNo,memNo);
        if (entityList == null || entityList.isEmpty()) {
        	return null;
        }
        List<OrderConsoleSubsidyDetailEntity> list = new ArrayList<OrderConsoleSubsidyDetailEntity>();
        for(OrderConsoleSubsidyDetailEntity entity:entityList){
        	if (SubsidySourceCodeEnum.PLATFORM.getCode().equals(entity.getSubsidySourceCode()) && 
        			SubsidySourceCodeEnum.RENTER.getCode().equals(entity.getSubsidyTargetCode())) {
        		list.add(entity);
        	}
        }
        return list;
    }
    
    
    
    /**
     * 获取平台给租客的补贴总额
     * @param orderNo
     * @param memNo
     * @return int
     */
    public int getPlatformToRenterSubsidyAmt(String orderNo, String memNo){
        List<OrderConsoleSubsidyDetailEntity> entityList = listPlatformToRenterSubsidy(orderNo,memNo);
        if (entityList == null || entityList.isEmpty()) {
        	return 0;
        }
        int total = 0;
        for(OrderConsoleSubsidyDetailEntity entity:entityList){
            if(entity != null && entity.getSubsidyAmount() != null){
                total = total + entity.getSubsidyAmount();
            }
        }
        return total;
    }
    
    
    /**
     * 获取平台给租客的补贴总额
     * @param entityList
     * @return int
     */
    public int getPlatformToRenterSubsidyAmt(List<OrderConsoleSubsidyDetailEntity> entityList){
    	if (entityList == null || entityList.isEmpty()) {
        	return 0;
        }
        int total = 0;
        for(OrderConsoleSubsidyDetailEntity entity:entityList){
            if(entity != null && entity.getSubsidyAmount() != null){
                total = total + entity.getSubsidyAmount();
            }
        }
        return total;
    }
}
