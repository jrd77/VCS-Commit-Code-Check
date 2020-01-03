package com.atzuche.order.settle.vo.req;

import com.atzuche.order.ownercost.entity.OwnerOrderIncrementDetailEntity;
import com.atzuche.order.ownercost.entity.OwnerOrderPurchaseDetailEntity;
import com.atzuche.order.ownercost.entity.OwnerOrderSubsidyDetailEntity;
import lombok.Data;

import java.util.List;

/**
 * 车主费用信息
 * @author haibao.yan
 */
@Data
public class OwnerCosts {

    /**
     * 车主端代管车服务费
     */
    private OwnerOrderPurchaseDetailEntity proxyExpense;
    /**
     * 车主端平台服务费
     */
    private OwnerOrderPurchaseDetailEntity serviceExpense;
    /**
     * 获取车主补贴明细列表
     */
    private List<OwnerOrderSubsidyDetailEntity> ownerOrderSubsidyDetail;
    /**
     * 获取车主费用列表
     */
    private List<OwnerOrderPurchaseDetailEntity> ownerOrderPurchaseDetail;
    /**
     * 获取车主增值服务费用列表
     */
    private List<OwnerOrderIncrementDetailEntity> ownerOrderIncrementDetail;

    /**
     * 获取gps服务费
     */
    private List<OwnerOrderPurchaseDetailEntity> gpsCost;

    /**
     * 获取车主油费
     */
    private OwnerOrderPurchaseDetailEntity renterOrderCostDetail;
}
