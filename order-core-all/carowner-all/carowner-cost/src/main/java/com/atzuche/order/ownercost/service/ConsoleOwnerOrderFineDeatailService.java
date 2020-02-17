package com.atzuche.order.ownercost.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atzuche.order.commons.entity.dto.CostBaseDTO;
import com.atzuche.order.commons.enums.FineSubsidyCodeEnum;
import com.atzuche.order.commons.enums.FineSubsidySourceCodeEnum;
import com.atzuche.order.commons.enums.FineTypeEnum;
import com.atzuche.order.ownercost.entity.ConsoleOwnerOrderFineDeatailEntity;
import com.atzuche.order.ownercost.mapper.ConsoleOwnerOrderFineDeatailMapper;


/**
 * 全局的车主订单罚金明细表
 *
 * @author ZhangBin
 * @date 2019-12-27 10:18:00
 */
@Service
public class ConsoleOwnerOrderFineDeatailService{

    private static Logger logger = LoggerFactory.getLogger(ConsoleOwnerOrderFineDeatailService.class);

    @Autowired
    private ConsoleOwnerOrderFineDeatailMapper consoleOwnerOrderFineDeatailMapper;



    public int addFineRecord(ConsoleOwnerOrderFineDeatailEntity entity) {
        if(null == entity) {
            logger.warn("Not fund console owner order fine data.");
            return 0;
        }
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


	public int saveOrUpdateConsoleRenterOrderFineDeatail(ConsoleOwnerOrderFineDeatailEntity consoleFine) {
		if(null == consoleFine) {
            logger.warn("Not fund console renter order fine data.");
            return 0;
        }
        

        
        boolean isExists = false;
        int id = 0;
        List<ConsoleOwnerOrderFineDeatailEntity> listFine =  consoleOwnerOrderFineDeatailMapper.selectByOrderNoMemNo(consoleFine.getOrderNo(), consoleFine.getMemNo());
        for (ConsoleOwnerOrderFineDeatailEntity consoleRenterOrderFineDeatailEntity : listFine) {
			if(consoleFine.getFineType().intValue() == consoleRenterOrderFineDeatailEntity.getFineType().intValue()) {
				consoleFine.setId(consoleRenterOrderFineDeatailEntity.getId());
				//不修改
				consoleFine.setCreateOp(null);
				id = consoleOwnerOrderFineDeatailMapper.updateByPrimaryKeySelective(consoleFine);
				//代表存在
				isExists = true;
			}
		}
        
        //新增
        if(!isExists) {
        	id = consoleOwnerOrderFineDeatailMapper.insertSelective(consoleFine);
        }
    	return id;
	}
}
