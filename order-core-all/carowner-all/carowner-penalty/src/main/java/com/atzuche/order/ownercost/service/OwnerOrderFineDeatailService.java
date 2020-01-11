package com.atzuche.order.ownercost.service;

import com.atzuche.order.commons.entity.dto.CostBaseDTO;
import com.atzuche.order.commons.enums.FineSubsidyCodeEnum;
import com.atzuche.order.commons.enums.FineSubsidySourceCodeEnum;
import com.atzuche.order.commons.enums.FineTypeEnum;
import com.atzuche.order.ownercost.entity.OwnerOrderFineDeatailEntity;
import com.atzuche.order.ownercost.mapper.OwnerOrderFineDeatailMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



/**
 * 车主订单罚金明细表
 *
 * @author ZhangBin
 * @date 2020-01-09 11:30:07
 */
@Service
public class OwnerOrderFineDeatailService{

    @Autowired
    private OwnerOrderFineDeatailMapper ownerOrderFineDeatailMapper;



    public int addOwnerOrderFineRecord(OwnerOrderFineDeatailEntity entity) {
        return  ownerOrderFineDeatailMapper.insertSelective(entity);
    }

    /**
     * 罚金数据转化
     * @param costBaseDTO 基础信息
     * @param fineAmt 罚金金额
     * @param code 罚金补贴方编码枚举
     * @param source 罚金来源编码枚举
     * @param type 罚金类型枚举
     * @return OwnerOrderFineDeatailEntity
     */
    public OwnerOrderFineDeatailEntity fineDataConvert(CostBaseDTO costBaseDTO, Integer fineAmt, FineSubsidyCodeEnum code, FineSubsidySourceCodeEnum source, FineTypeEnum type) {
        if (fineAmt == null || fineAmt == 0) {
            return null;
        }
        OwnerOrderFineDeatailEntity fineEntity = new OwnerOrderFineDeatailEntity();
        // 罚金负数
        fineEntity.setFineAmount(-fineAmt);
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
