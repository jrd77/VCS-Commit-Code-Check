package com.atzuche.order.owner.cost.mapper;

import com.atzuche.order.owner.cost.entity.OwnerOrderPurchaseDetailEntity;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 车主端采购费用明细表
 * 
 * @author ZhangBin
 * @date 2019-12-11 17:37:50
 */
@Mapper
public interface OwnerOrderPurchaseDetailMapper{

    OwnerOrderPurchaseDetailEntity selectByPrimaryKey(Integer id);

    List<OwnerOrderPurchaseDetailEntity> selectALL();

    int insert(OwnerOrderPurchaseDetailEntity record);
    
    int insertSelective(OwnerOrderPurchaseDetailEntity record);

    int updateByPrimaryKey(OwnerOrderPurchaseDetailEntity record);
    
    int updateByPrimaryKeySelective(OwnerOrderPurchaseDetailEntity record);

}
