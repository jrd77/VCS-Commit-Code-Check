package com.atzuche.order.settle.service;

import com.atzuche.order.ownercost.entity.OwnerOrderEntity;
import com.atzuche.order.ownercost.service.OwnerOrderService;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.renterorder.service.RenterOrderService;
import com.atzuche.order.settle.service.notservice.OrderSettleNoTService;
import com.atzuche.order.settle.vo.req.SettleOrders;
import com.autoyol.doc.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * 车辆结算
 * @author haibao.yan
 */
@Service
public class OrderSettleService {
    @Autowired private OrderSettleNoTService orderSettleNoTService;
    @Autowired private RenterOrderService renterOrderService;
    @Autowired private OwnerOrderService ownerOrderService;
    /**
     * 车辆押金结算
     */
    @Async
    @Transactional(rollbackFor=Exception.class)
    public void settleOrder(String orderNo) {
        //1 校验参数
        if(StringUtil.isBlank(orderNo)){
            return;
        }
        RenterOrderEntity renterOrder = renterOrderService.getRenterOrderByOrderNoAndIsEffective(orderNo);
        if(Objects.isNull(renterOrder) || Objects.isNull(renterOrder.getRenterOrderNo())){
            return;
        }
        OwnerOrderEntity ownerOrder = ownerOrderService.getOwnerOrderByOrderNoAndIsEffective(orderNo);
        if(Objects.isNull(ownerOrder) || Objects.isNull(ownerOrder.getOwnerOrderNo())){
            return;
        }
        // 2 TODO 校验订单状态 以及是否存在 理赔暂扣 存在不能进行结算 并CAT告警
        orderSettleNoTService.check(renterOrder);
        // 3 初始化数据

        // 3.1获取租客子订单 和 租客会员号
        String renterOrderNo = renterOrder.getRenterOrderNo();
        String renterMemNo = "";
        //3.2获取车主子订单 和 车主会员号
        String ownerOrderNo = ownerOrder.getOwnerOrderNo();
        String ownerMemNo = ownerOrder.getMemNo();
        //3.3 初始化结算对象
        SettleOrders settleOrders =  orderSettleNoTService.initSettleOrders(orderNo,renterOrderNo,ownerOrderNo,renterMemNo,ownerMemNo);
        //3.4 查询所有租客费用明细
        orderSettleNoTService.getRenterCostSettleDetail(settleOrders,renterOrder);
        //3.5 查询所有租客费用明细
        orderSettleNoTService.getOwnerCostSettleDetail(settleOrders,ownerOrder);

        //4 计算费用统计 落库





        //1 根据订单号校验是否可以结算  ，订单状态 已交车待结算，没有暂扣违章
        //2 查询租客所有费用（包含：租车费用，租客补贴费用，罚金费用，交接车产生费用）
        //3 查询车主费用 （包含: 车主补贴明细表，车主罚金 车主订单采购费用，增值订单费用）
        //4
    }
}
