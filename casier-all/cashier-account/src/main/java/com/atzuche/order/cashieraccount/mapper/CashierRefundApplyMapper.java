package com.atzuche.order.cashieraccount.mapper;

import com.atzuche.order.cashieraccount.entity.CashierRefundApplyEntity;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 退款申请表
 * 
 * @author ZhangBin
 * @date 2019-12-11 11:17:59
 */
@Mapper
public interface CashierRefundApplyMapper{

    CashierRefundApplyEntity selectByPrimaryKey(Integer id);

    int insert(CashierRefundApplyEntity record);
    
    int updateByPrimaryKeySelective(CashierRefundApplyEntity record);

}
