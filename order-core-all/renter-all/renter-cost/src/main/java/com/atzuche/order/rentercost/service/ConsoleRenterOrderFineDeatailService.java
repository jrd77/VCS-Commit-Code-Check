package com.atzuche.order.rentercost.service;

import java.util.List;

import com.atzuche.order.commons.entity.dto.CostBaseDTO;
import com.atzuche.order.commons.enums.FineSubsidyCodeEnum;
import com.atzuche.order.commons.enums.FineSubsidySourceCodeEnum;
import com.atzuche.order.commons.enums.FineTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static Logger logger = LoggerFactory.getLogger(ConsoleRenterOrderFineDeatailService.class);

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
        if(null == consoleFine) {
            logger.warn("Not fund console renter order fine data.");
            return 0;
        }
    	return consoleRenterOrderFineDeatailMapper.insertSelective(consoleFine);
    }


    /**
     * 罚金数据转化
     * @param costBaseDTO 基础信息
     * @param fineAmt 罚金金额
     * @param code 罚金补贴方编码枚举
     * @param source 罚金来源编码枚举
     * @param type 罚金类型枚举
     * @return ConsoleRenterOrderFineDeatailEntity
     */
    public ConsoleRenterOrderFineDeatailEntity fineDataConvert(CostBaseDTO costBaseDTO, Integer fineAmt, FineSubsidyCodeEnum code, FineSubsidySourceCodeEnum source, FineTypeEnum type) {
        if (fineAmt == null || fineAmt == 0) {
            return null;
        }
        ConsoleRenterOrderFineDeatailEntity fineEntity = new ConsoleRenterOrderFineDeatailEntity();
        // 罚金负数
        fineEntity.setFineAmount(fineAmt);
        fineEntity.setFineSubsidyCode(code.getFineSubsidyCode());
        fineEntity.setFineSubsidyDesc(code.getFineSubsidyDesc());
        fineEntity.setFineSubsidySourceCode(source.getFineSubsidySourceCode());
        fineEntity.setFineSubsidySourceDesc(source.getFineSubsidySourceDesc());
        fineEntity.setFineType(type.getFineType());
        fineEntity.setFineTypeDesc(type.getFineTypeDesc());
        fineEntity.setMemNo(costBaseDTO.getMemNo());
        fineEntity.setOrderNo(costBaseDTO.getOrderNo());
        return fineEntity;
    }
}
