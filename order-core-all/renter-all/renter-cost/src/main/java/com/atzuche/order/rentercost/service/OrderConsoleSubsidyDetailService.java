package com.atzuche.order.rentercost.service;

import com.atzuche.order.commons.enums.RenterCashCodeEnum;
import com.atzuche.order.rentercost.entity.OrderConsoleSubsidyDetailEntity;
import com.atzuche.order.rentercost.mapper.OrderConsoleSubsidyDetailMapper;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



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
}
