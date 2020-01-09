package com.atzuche.order.ownercost.service;

import com.atzuche.order.commons.entity.dto.CostBaseDTO;
import com.atzuche.order.commons.enums.FineSubsidyCodeEnum;
import com.atzuche.order.commons.enums.FineSubsidySourceCodeEnum;
import com.atzuche.order.commons.enums.FineTypeEnum;
import com.atzuche.order.ownercost.entity.ConsoleOwnerOrderFineDeatailEntity;
import com.atzuche.order.ownercost.mapper.ConsoleOwnerOrderFineDeatailMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


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



    public int addFineRecord(ConsoleOwnerOrderFineDeatailEntity entity) {
        return consoleOwnerOrderFineDeatailMapper.insertSelective(entity);
    }


    /**
     * 罚金数据转化
     * @param costBaseDTO 基础信息
     * @param fineAmt 罚金金额
     * @param code 罚金补贴方编码枚举
     * @param source 罚金来源编码枚举
     * @param type 罚金类型枚举
     * @return ConsoleOwnerOrderFineDeatailEntity
     */
    public ConsoleOwnerOrderFineDeatailEntity fineDataConvert(CostBaseDTO costBaseDTO, Integer fineAmt, FineSubsidyCodeEnum code, FineSubsidySourceCodeEnum source, FineTypeEnum type) {
        if (fineAmt == null || fineAmt == 0) {
            return null;
        }
        ConsoleOwnerOrderFineDeatailEntity fineEntity = new ConsoleOwnerOrderFineDeatailEntity();
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

    public List<ConsoleOwnerOrderFineDeatailEntity> selectByOrderNo(String orderNo){
        return consoleOwnerOrderFineDeatailMapper.selectByOrderNo(orderNo);
    }
}
