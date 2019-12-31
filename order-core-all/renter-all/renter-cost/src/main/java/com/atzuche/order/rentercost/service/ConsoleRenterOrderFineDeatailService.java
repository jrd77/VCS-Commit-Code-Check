package com.atzuche.order.rentercost.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atzuche.order.rentercost.entity.ConsoleRenterOrderFineDeatailEntity;
import com.atzuche.order.rentercost.mapper.ConsoleRenterOrderFineDeatailMapper;



/**
 * 全局的租客订单罚金明细表
 *
 * @author ZhangBin
 * @date 2019-12-27 10:18:00
 */
@Service
public class ConsoleRenterOrderFineDeatailService{
    @Autowired
    private ConsoleRenterOrderFineDeatailMapper consoleRenterOrderFineDeatailMapper;


    /**
     * 获取全局的租客订单罚金明细
     * @param orderNo 主订单号
     * @param memNo 会员号
     * @return List<ConsoleRenterOrderFineDeatailEntity>
     */
    public List<ConsoleRenterOrderFineDeatailEntity> listConsoleRenterOrderFineDeatail(String orderNo, String memNo) {
    	return consoleRenterOrderFineDeatailMapper.listConsoleRenterOrderFineDeatail(orderNo, memNo);
    }
    
    /**
     * 保存全局的租客订单罚金
     * @param consoleFine 全局的租客订单罚金
     * @return Integer
     */
    public Integer saveConsoleRenterOrderFineDeatail(ConsoleRenterOrderFineDeatailEntity consoleFine) {
    	return consoleRenterOrderFineDeatailMapper.insertSelective(consoleFine);
    }
}
