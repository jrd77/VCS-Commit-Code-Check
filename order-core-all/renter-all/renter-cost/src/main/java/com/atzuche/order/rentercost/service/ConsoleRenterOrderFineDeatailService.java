package com.atzuche.order.rentercost.service;

import com.atzuche.order.commons.entity.dto.CostBaseDTO;
import com.atzuche.order.commons.enums.FineSubsidyCodeEnum;
import com.atzuche.order.commons.enums.FineSubsidySourceCodeEnum;
import com.atzuche.order.commons.enums.FineTypeEnum;
import com.atzuche.order.rentercost.entity.ConsoleRenterOrderFineDeatailEntity;
import com.atzuche.order.rentercost.mapper.ConsoleRenterOrderFineDeatailMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;



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
     * 获取某个租客某个订单的全局罚金
     * @param orderNo
     * @param memNo
     * @return
     */
    public int getTotalConsoleFineAmt(String orderNo,String memNo){
        List<ConsoleRenterOrderFineDeatailEntity> consoleRenterOrderFineDeatailEntityList = listConsoleRenterOrderFineDeatail(orderNo,memNo);
        int total=0;
        for(ConsoleRenterOrderFineDeatailEntity entity:consoleRenterOrderFineDeatailEntityList){
            if(entity.getFineAmount()!=null) {
                total = total + entity.getFineAmount();
            }
        }
        return total;
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
    
    //先查询遍历，是否存在。管理后台修改
    public Integer saveOrUpdateConsoleRenterOrderFineDeatail(ConsoleRenterOrderFineDeatailEntity consoleFine) {
        if(null == consoleFine) {
            logger.warn("Not fund console renter order fine data.");
            return 0;
        }
        

        
        boolean isExists = false;
        int id = 0;
        List<ConsoleRenterOrderFineDeatailEntity> listFine =  consoleRenterOrderFineDeatailMapper.listConsoleRenterOrderFineDeatail(consoleFine.getOrderNo(), consoleFine.getMemNo());
        for (ConsoleRenterOrderFineDeatailEntity consoleRenterOrderFineDeatailEntity : listFine) {
			if(consoleFine.getFineType().intValue() == consoleRenterOrderFineDeatailEntity.getFineType().intValue()) {
				consoleFine.setId(consoleRenterOrderFineDeatailEntity.getId());
				//不修改
				consoleFine.setCreateOp(null);
				id = consoleRenterOrderFineDeatailMapper.updateByPrimaryKeySelective(consoleFine);
				//代表存在
				isExists = true;
			}
		}
        
        //新增
        if(!isExists) {
        	id = consoleRenterOrderFineDeatailMapper.insertSelective(consoleFine);
        }
    	return id;
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
        if (fineAmt == null/* || fineAmt == 0*/) {
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
