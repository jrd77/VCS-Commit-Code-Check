package com.atzuche.order.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import com.atzuche.order.mapper.CashierMapper;
import com.atzuche.order.entity.CashierEntity;
import com.atzuche.order.service.CashierService;


/**
 * 收银表
 *
 * @author ZhangBin
 * @date 2019-12-11 11:17:59
 */
@Service
public class CashierServiceImpl implements CashierService {
    @Autowired
    private CashierMapper cashierMapper;

    @Override
    public CashierEntity selectByPrimaryKey(Integer id){
        return cashierMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<CashierEntity> selectALL(){
        return cashierMapper.selectALL();
    }

    @Override
    public int insert(CashierEntity record){
        return cashierMapper.insert(record);
    }
    
    @Override
    public int insertSelective(CashierEntity record){
  		return cashierMapper.insertSelective(record);
    }

    @Override
    public int updateByPrimaryKey(CashierEntity record){
  		return cashierMapper.updateByPrimaryKey(record);
    }

    @Override
    public int updateByPrimaryKeySelective(CashierEntity record){
    	return cashierMapper.updateByPrimaryKeySelective(record);
    }
}
