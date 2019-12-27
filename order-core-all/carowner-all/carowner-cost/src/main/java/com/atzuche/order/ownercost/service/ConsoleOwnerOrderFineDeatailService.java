package com.atzuche.order.ownercost.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atzuche.order.ownercost.mapper.ConsoleOwnerOrderFineDeatailMapper;



/**
 * 全局的车主订单罚金明细表
 *
 * @author ZhangBin
 * @date 2019-12-27 10:18:00
 */
@Service
public class ConsoleOwnerOrderFineDeatailService{
    @Autowired
    private ConsoleOwnerOrderFineDeatailMapper consoleOwnerOrderFineDeatailMapper;


}
