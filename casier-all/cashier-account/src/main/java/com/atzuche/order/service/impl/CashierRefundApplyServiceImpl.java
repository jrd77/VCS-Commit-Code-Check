package com.atzuche.order.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import com.atzuche.order.mapper.CashierRefundApplyMapper;
import com.atzuche.order.entity.CashierRefundApplyEntity;
import com.atzuche.order.service.CashierRefundApplyService;


/**
 * 退款申请表
 *
 * @author ZhangBin
 * @date 2019-12-11 11:17:59
 */
@Service
public class CashierRefundApplyServiceImpl implements CashierRefundApplyService {
    @Autowired
    private CashierRefundApplyMapper cashierRefundApplyMapper;

    @Override
    public CashierRefundApplyEntity selectByPrimaryKey(Integer id){
        return cashierRefundApplyMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<CashierRefundApplyEntity> selectALL(){
        return cashierRefundApplyMapper.selectALL();
    }

    @Override
    public int insert(CashierRefundApplyEntity record){
        return cashierRefundApplyMapper.insert(record);
    }
    
    @Override
    public int insertSelective(CashierRefundApplyEntity record){
  		return cashierRefundApplyMapper.insertSelective(record);
    }

    @Override
    public int updateByPrimaryKey(CashierRefundApplyEntity record){
  		return cashierRefundApplyMapper.updateByPrimaryKey(record);
    }

    @Override
    public int updateByPrimaryKeySelective(CashierRefundApplyEntity record){
    	return cashierRefundApplyMapper.updateByPrimaryKeySelective(record);
    }
}
