package com.atzuche.order.rentercost.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atzuche.order.commons.enums.YesNoEnum;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.rentercost.entity.OrderSupplementDetailEntity;
import com.atzuche.order.rentercost.entity.vo.PayableVO;
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
     * 根据会员号查询补付列表
     * @param memNo
     * @return
     */
    public List<OrderSupplementDetailEntity> listOrderSupplementDetailByMemNo(String memNo) {
    	List<OrderSupplementDetailEntity> lsEntity = orderSupplementDetailMapper.listOrderSupplementDetailByMemNo(memNo);
    	return lsEntity;
    }
    
    //封装管理后台修改
    public OrderSupplementDetailEntity handleConsoleData(int amt,RenterCashCodeEnum cashCode,String memNo,String orderNo) {
    	OrderSupplementDetailEntity entity = new OrderSupplementDetailEntity();
    	entity.setAmt(amt);
    	entity.setCashNo(cashCode.getCashNo());
    	//费用类型：1-补付费用，2-订单欠款
    	entity.setCashType(1);
    	entity.setCashTypeTxt(cashCode.getTxt());
    	entity.setMemNo(memNo);
    	//操作状态:0,待提交 1,已生效 2,已失效 3,已撤回
    	entity.setOpStatus(1);
    	//操作类型:1,修改订单 2,车管家录入 3,租车押金结算 4,违章押金结算 5,手动添加
    	entity.setOpType(1);
    	entity.setOrderNo(orderNo);
    	//支付状态:0.无需支付 1.未支付 2.已取消 3.已支付 4.支付中，5.支付失败 10.租车押金结算抵扣  20.违章押金结算抵扣
    	entity.setPayFlag(1);
    	entity.setRemark("动态添加记录");
    	//补付类型:1,系统创建 2,手动创建
    	entity.setSupplementType(1);
    	entity.setTitle(cashCode.getTxt());
    	entity.setCreateOp("系统动态创建");
    	entity.setCreateTime(new Date());
    	
    	return entity;
    }
    
    
    //封装欠款
    public OrderSupplementDetailEntity handleDebtData(int amt,RenterCashCodeEnum cashCode,String memNo,String orderNo) {
    	OrderSupplementDetailEntity entity = new OrderSupplementDetailEntity();
    	entity.setAmt(amt);
    	entity.setCashNo(cashCode.getCashNo());
    	//费用类型：1-补付费用，2-订单欠款
    	entity.setCashType(2);
    	entity.setCashTypeTxt(cashCode.getTxt());
    	entity.setMemNo(memNo);
    	//操作状态:0,待提交 1,已生效 2,已失效 3,已撤回
    	entity.setOpStatus(1);
    	//操作类型:1,修改订单 2,车管家录入 3,租车押金结算 4,违章押金结算 5,手动添加
    	entity.setOpType(5);
    	entity.setOrderNo(orderNo);
    	//支付状态:0.无需支付 1.未支付 2.已取消 3.已支付 4.支付中，5.支付失败 10.租车押金结算抵扣  20.违章押金结算抵扣
    	entity.setPayFlag(1);
    	entity.setRemark("动态添加记录");
    	//补付类型:1,系统创建 2,手动创建
    	entity.setSupplementType(2);
    	entity.setTitle(cashCode.getTxt());
    	return entity;
    }
    
    
    
    /**
     * 根据订单号和会员号，查询列表。统计选择补付的订单号集合数据。
     * @param memNo
     * @param orderNoList
     * @return
     */
    public List<OrderSupplementDetailEntity> listOrderSupplementDetailByMemNoAndOrderNos(String memNo,List<String> orderNoList) {
    	return orderSupplementDetailMapper.listOrderSupplementDetailByMemNoAndOrderNos(memNo, orderNoList);
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
    public Integer updatePayFlagById(Integer id, Integer payFlag, Date payTime, Integer amt) {
    	return orderSupplementDetailMapper.updatePayFlagById(id, payFlag, payTime,amt);
    }
    
    public Integer updatePayFlagById(Integer id, Integer payFlag, Date payTime) {
    	return orderSupplementDetailMapper.updatePayFlagNewById(id, payFlag, payTime);
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
