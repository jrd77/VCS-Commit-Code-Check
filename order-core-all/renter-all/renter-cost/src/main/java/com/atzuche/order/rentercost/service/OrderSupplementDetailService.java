package com.atzuche.order.rentercost.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atzuche.order.rentercost.entity.OrderSupplementDetailEntity;
import com.atzuche.order.rentercost.mapper.OrderSupplementDetailMapper;



/**
 * 订单补付表
 *
 */
@Service
public class OrderSupplementDetailService{
    @Autowired
    private OrderSupplementDetailMapper orderSupplementDetailMapper;


    /**
     * 获取租客补付记录
     * @param orderNo 主订单号
     * @param memNo 会员号
     * @return List<OrderSupplementDetailEntity>
     */
    public List<OrderSupplementDetailEntity> listOrderSupplementDetailByOrderNoAndMemNo(String orderNo, String memNo) {
    	return orderSupplementDetailMapper.listOrderSupplementDetailByOrderNoAndMemNo(orderNo, memNo);
    }

    /**
     * 返回补付的总额
     * @param orderNo
     * @param memNo
     * @return
     */
    public int getTotalSupplementAmt(String orderNo,String memNo){
        List<OrderSupplementDetailEntity> entityList = listOrderSupplementDetailByOrderNoAndMemNo(orderNo,memNo);
        int total = 0;
        for(OrderSupplementDetailEntity entity:entityList){
            if(entity.getAmt()!=null){
                total = total +entity.getAmt();
            }
        }
        return total;
    }


    
    /**
     * 保存租客补付记录
     * @param supplementEntity 补付记录
     * @return Integer
     */
    public Integer saveOrderSupplementDetail(OrderSupplementDetailEntity supplementEntity) {
    	return orderSupplementDetailMapper.insertSelective(supplementEntity);
    }
    
    /**
     * 更新补付支付状态
     * @param id
     * @param payFlag
     * @return Integer
     */
    public Integer updatePayFlagById(Integer id, Integer payFlag, Date payTime) {
    	return orderSupplementDetailMapper.updatePayFlagById(id, payFlag, payTime);
    }
    
    /**
     * 根据订单号获取补付记录
     * @param orderNo
     * @return List<OrderSupplementDetailEntity>
     */
    public List<OrderSupplementDetailEntity> listOrderSupplementDetailByOrderNo(String orderNo) {
    	return orderSupplementDetailMapper.listOrderSupplementDetailByOrderNo(orderNo);
    }
    
    public Integer updateDeleteById(Integer id) {
    	return orderSupplementDetailMapper.updateDeleteById(id);
    }


    /**
     * 违章结算获取未支付的补付信息
     *
     * @param orderNo 订单号
     * @return List<OrderSupplementDetailEntity> 补付记录
     */
    public List<OrderSupplementDetailEntity> queryNotPaySupplementByOrderNoAndMemNo(String orderNo) {
        return orderSupplementDetailMapper.selectNotPayByOrderNo(orderNo);
    }


    /**
     * 更新补付操作状态
     *
     * @param primaryKey 主键
     * @param opStatus   操作状态
     * @return int 操作记录数
     */
    public int updateOpStatusByPrimaryKey(Integer primaryKey, Integer opStatus) {
        return orderSupplementDetailMapper.updateOpStatusByPrimaryKey(primaryKey, opStatus);
    }
}
