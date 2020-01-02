package com.atzuche.order.rentercost.service;

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
}
