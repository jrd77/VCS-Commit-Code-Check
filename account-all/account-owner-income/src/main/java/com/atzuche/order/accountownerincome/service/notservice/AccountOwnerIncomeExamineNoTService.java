package com.atzuche.order.accountownerincome.service.notservice;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.accountownerincome.entity.AccountOwnerIncomeExamineEntity;
import com.atzuche.order.accountownerincome.exception.AccountOwnerIncomeExamineException;
import com.atzuche.order.accountownerincome.exception.AccountOwnerIncomeSettleException;
import com.atzuche.order.accountownerincome.mapper.AccountOwnerIncomeExamineMapper;
import com.atzuche.order.commons.enums.account.income.AccountOwnerIncomeExamineType;
import com.atzuche.order.commons.exceptions.OwnerIncomeExamineInsertException;
import com.atzuche.order.commons.exceptions.OwnerIncomeExamineNotFoundException;
import com.atzuche.order.commons.vo.req.AdjustmentOwnerIncomeExamVO;
import com.atzuche.order.commons.vo.req.income.AccountOwnerIncomeExamineOpReqVO;
import com.atzuche.order.commons.vo.req.income.AccountOwnerIncomeExamineReqVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;


/**
 * 车主收益待审核表
 *
 * @author ZhangBin
 * @date 2019-12-11 17:44:19
 */
@Slf4j
@Service
public class AccountOwnerIncomeExamineNoTService {
    @Autowired
    private AccountOwnerIncomeExamineMapper accountOwnerIncomeExamineMapper;


    /**
     * 结算后产生待审核收益 落库
     */
    public void insertOwnerIncomeExamine(AccountOwnerIncomeExamineReqVO accountOwnerIncomeExamineReq) {
        AccountOwnerIncomeExamineEntity accountOwnerIncomeExamineEntity = new AccountOwnerIncomeExamineEntity();
        BeanUtils.copyProperties(accountOwnerIncomeExamineReq,accountOwnerIncomeExamineEntity);
        accountOwnerIncomeExamineEntity.setAmt(accountOwnerIncomeExamineReq.getAmt());
        accountOwnerIncomeExamineEntity.setStatus(accountOwnerIncomeExamineReq.getStatus().getStatus());
        accountOwnerIncomeExamineEntity.setType(accountOwnerIncomeExamineReq.getType().getStatus());
        accountOwnerIncomeExamineEntity.setVersion(NumberUtils.INTEGER_ONE);
        accountOwnerIncomeExamineEntity.setIsDelete(NumberUtils.INTEGER_ZERO);

        int result = accountOwnerIncomeExamineMapper.insertSelective(accountOwnerIncomeExamineEntity);
        log.info("车主收益审核录入结果result={},accountOwnerIncomeExamineEntity={}",result, JSON.toJSONString(accountOwnerIncomeExamineEntity));
        if(result==0){
            log.error("车主收益审核录入失败result={},accountOwnerIncomeExamineEntity={}",result,JSON.toJSONString(accountOwnerIncomeExamineEntity));
            throw new AccountOwnerIncomeSettleException();
        }
    }

    /**
     * 审核 更新车主收益审核状态
     * @param accountOwnerIncomeExamineOpReq
     */
    public void updateOwnerIncomeExamine(AccountOwnerIncomeExamineOpReqVO accountOwnerIncomeExamineOpReq) {
        AccountOwnerIncomeExamineEntity accountOwnerIncomeExamine =  accountOwnerIncomeExamineMapper.selectByPrimaryKey(accountOwnerIncomeExamineOpReq.getAccountOwnerIncomeExamineId());
        if(Objects.isNull(accountOwnerIncomeExamine) || Objects.isNull(accountOwnerIncomeExamine.getId())){
            throw new AccountOwnerIncomeExamineException();
        }
        LocalDateTime now = LocalDateTime.now();
        AccountOwnerIncomeExamineEntity accountOwnerIncomeExamineUpdate = new AccountOwnerIncomeExamineEntity();
        BeanUtils.copyProperties(accountOwnerIncomeExamineOpReq,accountOwnerIncomeExamineUpdate);
        accountOwnerIncomeExamineUpdate.setVersion(accountOwnerIncomeExamine.getVersion());
        accountOwnerIncomeExamineUpdate.setId(accountOwnerIncomeExamine.getId());
        accountOwnerIncomeExamineUpdate.setStatus(accountOwnerIncomeExamineOpReq.getStatus().getStatus());
        accountOwnerIncomeExamineUpdate.setTime(now);
        int result = accountOwnerIncomeExamineMapper.updateByPrimaryKeySelective(accountOwnerIncomeExamineUpdate);
        if(result==0){
            throw new AccountOwnerIncomeExamineException();
        }
    }

    public AccountOwnerIncomeExamineEntity getAccountOwnerIncomeExamineById(Integer accountOwnerIncomeExamineId) {
       return accountOwnerIncomeExamineMapper.selectByPrimaryKey(accountOwnerIncomeExamineId);
    }
    
    public List<AccountOwnerIncomeExamineEntity> getAccountOwnerIncomeExamineByOrderNo(String orderNo,String memNo) {
        return accountOwnerIncomeExamineMapper.selectByOrderNo(orderNo,memNo);
     }

	public Integer getTotalAccountOwnerIncomeExamineByOrderNo(String orderNo) {
		return accountOwnerIncomeExamineMapper.getTotalAccountOwnerIncomeExamineByOrderNo(orderNo);
	}

    /**
     * 查询收益类型
     * @param orderNo
     * @param memNo
     * @param type
     * @return
     */
    public List<AccountOwnerIncomeExamineEntity> getOwnerIncomeByOrderAndType(String orderNo, String memNo, int type) {
        return accountOwnerIncomeExamineMapper.getOwnerIncomeByOrderAndType(orderNo,memNo,type);
    }
    /*
     * @Author ZhangBin
     * @Date 2020/2/25 12:28
     * @Description: 根据车主子订单好查询
     *
     **/
    public List<AccountOwnerIncomeExamineEntity> selectByOwnerOrderNo(String ownerOrderNo) {
        return accountOwnerIncomeExamineMapper.selectByOwnerOrderNo(ownerOrderNo);
    }

    public void adjustmentOwnerIncomeExam(AdjustmentOwnerIncomeExamVO adjustmentOwnerIncomeExamVO) {
        AccountOwnerIncomeExamineEntity accountOwnerIncomeExamineEntity = accountOwnerIncomeExamineMapper.selectByPrimaryKey(adjustmentOwnerIncomeExamVO.getExamineId());
        if(accountOwnerIncomeExamineEntity == null || accountOwnerIncomeExamineEntity.getId() == null){
            throw new OwnerIncomeExamineNotFoundException();
        }
       AccountOwnerIncomeExamineEntity accountOwnerIncomeExamine = new AccountOwnerIncomeExamineEntity();
       accountOwnerIncomeExamine.setAmt(adjustmentOwnerIncomeExamVO.getAdjustmentAmt());
       accountOwnerIncomeExamine.setStatus(adjustmentOwnerIncomeExamVO.getAuditStatus());
       accountOwnerIncomeExamine.setType(AccountOwnerIncomeExamineType.OWNER_ADJUSTMENT.getStatus());
       accountOwnerIncomeExamine.setVersion(NumberUtils.INTEGER_ONE);
       accountOwnerIncomeExamine.setDetail(AccountOwnerIncomeExamineType.OWNER_ADJUSTMENT.getDesc());
       accountOwnerIncomeExamine.setOrderNo(accountOwnerIncomeExamineEntity.getOwnerOrderNo());
       accountOwnerIncomeExamine.setOrderNo(accountOwnerIncomeExamineEntity.getOrderNo());
       accountOwnerIncomeExamine.setOpName(adjustmentOwnerIncomeExamVO.getAuditOp());
       accountOwnerIncomeExamine.setTime(LocalDateTime.now());
       accountOwnerIncomeExamine.setRemark(AccountOwnerIncomeExamineType.OWNER_ADJUSTMENT.getDesc());
       accountOwnerIncomeExamine.setExamineId(adjustmentOwnerIncomeExamVO.getExamineId());
        int i = accountOwnerIncomeExamineMapper.insertSelective(accountOwnerIncomeExamine);
        if(i <=0 ){
            OwnerIncomeExamineInsertException e = new OwnerIncomeExamineInsertException();
            log.error("车主调账收益录入失败accountOwnerIncomeExamine={}",JSON.toJSONString(accountOwnerIncomeExamine),e);
            throw e;
        }
        log.error("车主调账收益录入成功 i={}，accountOwnerIncomeExamine={}",i,JSON.toJSONString(accountOwnerIncomeExamine));
    }
}
