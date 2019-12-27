package com.atzuche.order.ownercost.mapper;

import com.atzuche.order.ownercost.entity.OwnerOrderPurchaseDetailEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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
    
    int saveOwnerOrderPurchaseDetail(OwnerOrderPurchaseDetailEntity record);

    int updateByPrimaryKey(OwnerOrderPurchaseDetailEntity record);
    
    int updateByPrimaryKeySelective(OwnerOrderPurchaseDetailEntity record);
    
    List<OwnerOrderPurchaseDetailEntity> listOwnerOrderPurchaseDetail(@Param("orderNo") String orderNo, @Param("ownerOrderNo") String ownerOrderNo);

    Integer saveOwnerOrderPurchaseDetailBatch(@Param("costList") List<OwnerOrderPurchaseDetailEntity> costList);
}
