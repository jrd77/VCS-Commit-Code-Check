package com.atzuche.order.accountownerincome.service.notservice;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.accountownerincome.entity.AccountOwnerIncomeExamineEntity;
import com.atzuche.order.accountownerincome.exception.AccountOwnerIncomeExamineException;
import com.atzuche.order.accountownerincome.exception.AccountOwnerIncomeExamineRepeatException;
import com.atzuche.order.accountownerincome.exception.AccountOwnerIncomeSettleException;
import com.atzuche.order.accountownerincome.mapper.AccountOwnerIncomeExamineMapper;
import com.atzuche.order.commons.entity.orderDetailDto.AccountOwnerIncomeExamineDTO;
import com.atzuche.order.commons.enums.account.income.AccountOwnerIncomeExamineStatus;
import com.atzuche.order.commons.enums.account.income.AccountOwnerIncomeExamineType;
import com.atzuche.order.commons.exceptions.OwnerIncomeExamineInsertException;
import com.atzuche.order.commons.exceptions.OwnerIncomeExamineNotFoundException;
import com.atzuche.order.commons.vo.req.AdjustmentOwnerIncomeExamVO;
import com.atzuche.order.commons.vo.req.income.AccountOwnerIncomeExamineOpReqVO;
import com.atzuche.order.commons.vo.req.income.AccountOwnerIncomeExamineReqVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


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
    @Autowired
    private AccountOwnerIncomeNoTService accountOwnerIncomeNoTService;

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
        if(AccountOwnerIncomeExamineStatus.PASS_EXAMINE.getStatus() == accountOwnerIncomeExamine.getStatus()){
            log.error("车主收益审核已经通过，禁止二次审核accountOwnerIncomeExamineOpReq={}",JSON.toJSONString(accountOwnerIncomeExamineOpReq));
            throw new AccountOwnerIncomeExamineRepeatException();
        }
        LocalDateTime now = LocalDateTime.now();
        AccountOwnerIncomeExamineEntity accountOwnerIncomeExamineUpdate = new AccountOwnerIncomeExamineEntity();
        BeanUtils.copyProperties(accountOwnerIncomeExamineOpReq,accountOwnerIncomeExamineUpdate);
        accountOwnerIncomeExamineUpdate.setVersion(accountOwnerIncomeExamine.getVersion());
        accountOwnerIncomeExamineUpdate.setId(accountOwnerIncomeExamine.getId());
        accountOwnerIncomeExamineUpdate.setStatus(accountOwnerIncomeExamineOpReq.getStatus());
        accountOwnerIncomeExamineUpdate.setTime(now);
        int result = accountOwnerIncomeExamineMapper.updateByPrimaryKeySelective(accountOwnerIncomeExamineUpdate);
        if(result==0){
            throw new AccountOwnerIncomeExamineException();
        }
    }
    /*
     * @Author ZhangBin
     * @Date 2020/3/30 15:34
     * @Description: 通过examineId查询
     *
     **/
    public List<AccountOwnerIncomeExamineEntity> getAccountOwnerIncomeExamineById(Integer accountOwnerIncomeExamineId) {
       return accountOwnerIncomeExamineMapper.selectByExamineId(accountOwnerIncomeExamineId);
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
    @Transactional
    public void adjustmentOwnerIncomeExam(AdjustmentOwnerIncomeExamVO adjustmentOwnerIncomeExamVO) {
        AccountOwnerIncomeExamineEntity accountOwnerIncomeExamineEntity = accountOwnerIncomeExamineMapper.selectByPrimaryKey(adjustmentOwnerIncomeExamVO.getExamineId());
        if(accountOwnerIncomeExamineEntity == null || accountOwnerIncomeExamineEntity.getId() == null){
            throw new OwnerIncomeExamineNotFoundException();
        }
        if(AccountOwnerIncomeExamineStatus.PASS_EXAMINE.getStatus() == accountOwnerIncomeExamineEntity.getStatus()){
            log.info("已经审核通过,禁止二次审核 adjustmentOwnerIncomeExamVO={}",JSON.toJSONString(adjustmentOwnerIncomeExamVO));
            return;
        }

        int adjustmentAmt = adjustmentOwnerIncomeExamVO.getAdjustmentAmt();
        //Integer settAmt = accountOwnerIncomeExamineEntity.getAmt();
        AccountOwnerIncomeExamineEntity accountOwnerIncomeExamine = new AccountOwnerIncomeExamineEntity();
        accountOwnerIncomeExamine.setAmt(adjustmentAmt);
        accountOwnerIncomeExamine.setStatus(adjustmentOwnerIncomeExamVO.getAuditStatus());
        accountOwnerIncomeExamine.setType(AccountOwnerIncomeExamineType.OWNER_ADJUSTMENT.getStatus());
        accountOwnerIncomeExamine.setVersion(NumberUtils.INTEGER_ONE);
        accountOwnerIncomeExamine.setDetail(AccountOwnerIncomeExamineType.OWNER_ADJUSTMENT.getDesc());
        accountOwnerIncomeExamine.setOpName(adjustmentOwnerIncomeExamVO.getAuditOp());
        accountOwnerIncomeExamine.setTime(LocalDateTime.now());
        accountOwnerIncomeExamine.setRemark(AccountOwnerIncomeExamineType.OWNER_ADJUSTMENT.getDesc());
        accountOwnerIncomeExamine.setExamineId(adjustmentOwnerIncomeExamVO.getExamineId());
        accountOwnerIncomeExamine.setOwnerOrderNo(accountOwnerIncomeExamineEntity.getOwnerOrderNo());
        accountOwnerIncomeExamine.setMemNo(accountOwnerIncomeExamineEntity.getMemNo());
        accountOwnerIncomeExamine.setOrderNo(accountOwnerIncomeExamineEntity.getOwnerOrderNo());
        accountOwnerIncomeExamine.setOrderNo(accountOwnerIncomeExamineEntity.getOrderNo());
        int i = accountOwnerIncomeExamineMapper.insertSelective(accountOwnerIncomeExamine);
        if(i <=0){
            OwnerIncomeExamineInsertException e = new OwnerIncomeExamineInsertException();
            log.error("车主调账收益审核录入失败accountOwnerIncomeExamine={}",JSON.toJSONString(accountOwnerIncomeExamine),e);
            throw e;
        }
        log.error("车主调账收益审核录入成功 i={}，accountOwnerIncomeExamine={}",i,JSON.toJSONString(accountOwnerIncomeExamine));
        AccountOwnerIncomeExamineEntity accountOwnerIncomeExamineEntityNew = new AccountOwnerIncomeExamineEntity();
        accountOwnerIncomeExamineEntityNew.setVersion(accountOwnerIncomeExamineEntity.getVersion());
        accountOwnerIncomeExamineEntityNew.setId(accountOwnerIncomeExamineEntity.getId());
        //accountOwnerIncomeExamineEntityNew.setStatus(adjustmentOwnerIncomeExamVO.getAuditStatus());
        accountOwnerIncomeExamineEntityNew.setExamineId(adjustmentOwnerIncomeExamVO.getExamineId());
        accountOwnerIncomeExamineMapper.updateByPrimaryKeySelective(accountOwnerIncomeExamineEntityNew);
        //审核通过后统计到收益表中
        /*if(AccountOwnerIncomeExamineStatus.PASS_EXAMINE.getStatus() == adjustmentOwnerIncomeExamVO.getAuditStatus()){
            int currIncomAmt = settAmt + adjustmentAmt;
            log.info("车主调账收益审核更新车主收益 currIncomAmt={}",currIncomAmt);
            //更新收益
            AccountOwnerIncomeDetailEntity accountOwnerIncomeDetailEntity = new AccountOwnerIncomeDetailEntity();
            accountOwnerIncomeDetailEntity.setMemNo(accountOwnerIncomeExamineEntity.getMemNo());
            accountOwnerIncomeDetailEntity.setAmt(currIncomAmt);
            accountOwnerIncomeNoTService.updateOwnerIncomeAmt(accountOwnerIncomeDetailEntity);
        }*/
    }

    public List<AccountOwnerIncomeExamineEntity> getIncomByOwnerMemAndStatus(String ownerMemeNo,List<AccountOwnerIncomeExamineStatus> statusList) {
        List<Integer> status = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(statusList)){
            status = statusList.stream().map(x -> x.getStatus()).collect(Collectors.toList());
        }
        List<AccountOwnerIncomeExamineEntity> accountOwnerIncomeExamineEntityList = accountOwnerIncomeExamineMapper.getIncomByOwnerMemAndStatus(ownerMemeNo,status);
        return accountOwnerIncomeExamineEntityList;
    }


    public List<AccountOwnerIncomeExamineDTO> getIncomByOwnerMem(String ownerMemeNo) {
        List<AccountOwnerIncomeExamineEntity> incomByOwnerMemAndStatus = getIncomByOwnerMemAndStatus(ownerMemeNo, null);
        List<AccountOwnerIncomeExamineDTO> accountOwnerIncomeExamineDTOS = new ArrayList<>();
        incomByOwnerMemAndStatus.stream().forEach(x->{
            AccountOwnerIncomeExamineDTO accountOwnerIncomeExamineDTO = new AccountOwnerIncomeExamineDTO();
            BeanUtils.copyProperties(x,accountOwnerIncomeExamineDTO);
            accountOwnerIncomeExamineDTOS.add(accountOwnerIncomeExamineDTO);
        });
        return accountOwnerIncomeExamineDTOS;
    }
}
