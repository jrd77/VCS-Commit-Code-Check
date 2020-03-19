package com.atzuche.order.settle.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.constant.OrderConstant;
import com.atzuche.order.commons.enums.SupplemOpStatusEnum;
import com.atzuche.order.commons.enums.SupplementPayFlagEnum;
import com.atzuche.order.rentercost.entity.OrderSupplementDetailEntity;
import com.atzuche.order.rentercost.service.OrderSupplementDetailService;
import com.atzuche.order.settle.vo.req.SettleOrdersAccount;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 违章押金结算,补付记录处理
 *
 * @author pengcheng.fu
 * @date 2020/3/18 16:15
 */

@Service
public class OrderWzSettleSupplementHandleService {

    private static Logger logger = LoggerFactory.getLogger(OrderWzSettleSupplementHandleService.class);

    @Autowired
    private OrderSupplementDetailService orderSupplementDetailService;

    /**
     * 处理订单未支付的补付记录
     *
     * @param settleOrdersAccount 订单结算信息
     */
    public void supplementCostHandle(SettleOrdersAccount settleOrdersAccount) {
        logger.info("Illegal settlement of orders, supplementary payment of fees. settleOrdersAccount:[{}]", JSON.toJSONString(settleOrdersAccount));

        List<OrderSupplementDetailEntity> entityList =
                orderSupplementDetailService.queryNotPaySupplementByOrderNoAndMemNo(settleOrdersAccount.getOrderNo());

        if (CollectionUtils.isEmpty(entityList)) {
            logger.warn("No record of supplement was found.");
        } else {
            if (settleOrdersAccount.getDepositSurplusAmt() > OrderConstant.ZERO) {
                int totalSupplementAmt = entityList.stream().mapToInt(OrderSupplementDetailEntity::getAmt).sum();
                //todo 抵扣未支付的补付金额
                if (settleOrdersAccount.getDepositSurplusAmt() >= totalSupplementAmt) {
                    //todo 更新补付记录支付状态(已完成抵扣的改为:20,违章押金结算抵扣)
                    entityList.forEach(entity ->
                            orderSupplementDetailService.updatePayFlagById(entity.getId(),
                                    SupplementPayFlagEnum.PAY_FLAG_VIOLATION_DEPOSIT_SETTLE_DEDUCT.getCode(), null)
                    );
                } else {
                    OrderSupplementDetailEntity splitCriticalPoint = getSplitCriticalPoint(entityList,
                            settleOrdersAccount.getDepositSurplusAmt());

                    boolean mark = false;
                    for (OrderSupplementDetailEntity entity : entityList) {

                        if (mark || entity.getId().intValue() == splitCriticalPoint.getId().intValue()) {
                            if (mark) {
                                //todo 临界点之后的数据直接记欠款

                            } else {
                                //todo 临界点拆分,满足的部分进行抵扣；不满足的部分转入欠款
                                orderSupplementDetailService.updateOpStatusByPrimaryKey(splitCriticalPoint.getId(),
                                        SupplemOpStatusEnum.OP_STATUS_LOSE_EFFECT.getCode());

                                mark = true;
                            }

                        } else {
                            //todo 临界点之前的数据直接记抵扣
                            orderSupplementDetailService.updatePayFlagById(entity.getId(),
                                    SupplementPayFlagEnum.PAY_FLAG_VIOLATION_DEPOSIT_SETTLE_DEDUCT.getCode(), null);
                        }


                    }


                    //todo 更新补付记录支付状态(已完成抵扣的改为:20,违章押金结算抵扣)

                    //todo 拆分补付记录(剩余押金不足抵扣补付金额)

                    //todo 转入个人欠款(剩余押金不足抵扣补付金额)

                    //todo 更新补付记录支付状态(剩余押金不足抵扣补付金额):30,违章押金结算转欠款
                }


            } else {
                //todo 转入个人欠款(剩余押金不足抵扣补付金额)
                //todo 更新补付记录支付状态(剩余押金不足抵扣补付金额):30,违章押金结算转欠款
                entityList.forEach(entity -> {


                });
            }


        }
    }

    /**
     * 有序List依次叠加刚好满足surplusAmt(total >= surplusAmt)对应的数据
     *
     * @param list       叠加数据
     * @param surplusAmt 剩余押金(临界阈值)
     * @return OrderSupplementDetailEntity 临界点数据
     */
    private OrderSupplementDetailEntity getSplitCriticalPoint(List<OrderSupplementDetailEntity> list, int surplusAmt) {


        return null;
    }


}
