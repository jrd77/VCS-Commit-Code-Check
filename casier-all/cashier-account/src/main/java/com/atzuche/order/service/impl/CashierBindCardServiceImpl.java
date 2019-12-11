package com.atzuche.order.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import com.atzuche.order.mapper.CashierBindCardMapper;
import com.atzuche.order.entity.CashierBindCardEntity;
import com.atzuche.order.service.CashierBindCardService;


/**
 * 个人免押绑卡信息表
 *
 * @author ZhangBin
 * @date 2019-12-11 11:17:59
 */
@Service
public class CashierBindCardServiceImpl implements CashierBindCardService {
    @Autowired
    private CashierBindCardMapper cashierBindCardMapper;

    @Override
    public CashierBindCardEntity selectByPrimaryKey(Integer id){
        return cashierBindCardMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<CashierBindCardEntity> selectALL(){
        return cashierBindCardMapper.selectALL();
    }

    @Override
    public int insert(CashierBindCardEntity record){
        return cashierBindCardMapper.insert(record);
    }
    
    @Override
    public int insertSelective(CashierBindCardEntity record){
  		return cashierBindCardMapper.insertSelective(record);
    }

    @Override
    public int updateByPrimaryKey(CashierBindCardEntity record){
  		return cashierBindCardMapper.updateByPrimaryKey(record);
    }

    @Override
    public int updateByPrimaryKeySelective(CashierBindCardEntity record){
    	return cashierBindCardMapper.updateByPrimaryKeySelective(record);
    }
}
