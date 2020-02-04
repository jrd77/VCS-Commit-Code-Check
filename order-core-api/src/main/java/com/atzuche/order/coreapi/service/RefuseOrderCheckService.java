package com.atzuche.order.coreapi.service;

import com.atzuche.order.commons.entity.dto.OwnerGoodsDetailDTO;
import com.atzuche.order.commons.enums.CarOwnerTypeEnum;
import com.atzuche.order.commons.enums.ErrorCode;
import com.atzuche.order.commons.enums.OrderStatusEnum;
import com.atzuche.order.coreapi.submitOrder.exception.OrderDetailException;
import com.atzuche.order.coreapi.submitOrder.exception.RefuseOrderCheckException;
import com.atzuche.order.owner.commodity.service.OwnerGoodsService;
import com.atzuche.order.ownercost.entity.OwnerOrderEntity;
import com.atzuche.order.ownercost.service.OwnerOrderService;
import com.atzuche.order.parentorder.entity.OrderStatusEntity;
import com.atzuche.order.parentorder.service.OrderStatusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * RefuseOrderController 相关业务校验
 *
 * @author pengcheng.fu
 * @date 2020/2/3 14:30
 */

@Service
public class RefuseOrderCheckService {

    private static Logger logger = LoggerFactory.getLogger(RefuseOrderCheckService.class);


    @Resource
    private OwnerOrderService ownerOrderService;
    @Resource
    private OwnerGoodsService ownerGoodsService;
    @Resource
    private OrderStatusService orderStatusService;



    /**
     * 车主同意订单校验
     *
     * @param orderNo 主订单号
     * @param isConsoleInvoke 是否是管理后台请求操作:true,是 false,否
     */
    public void checkOwnerAgreeOrRefuseOrder(String orderNo, boolean isConsoleInvoke) {
        //车辆类型校验(非后台同意)
        if(!isConsoleInvoke) {
            OwnerOrderEntity ownerOrderEntity = ownerOrderService.getOwnerOrderByOrderNoAndIsEffective(orderNo);
            if(null == ownerOrderEntity) {
                logger.info("No valid owner order found. orderNo:[{}]", orderNo);
                throw new RefuseOrderCheckException(ErrorCode.NO_EFFECTIVE_ERR);
            }
            OwnerGoodsDetailDTO ownerGoodsDetail = ownerGoodsService.getOwnerGoodsDetail(ownerOrderEntity.getOwnerOrderNo(), false);
            if(null == ownerGoodsDetail) {
                logger.info("No owner order product information found. ownerOrderNo:[{}]", ownerOrderEntity.getOwnerOrderNo());
                throw new RefuseOrderCheckException(ErrorCode.OWNER_ORDER_GOODS_NOT_EXIST);
            }

            Integer carOwnerType = ownerGoodsDetail.getCarOwnerType();
            if(null != carOwnerType) {
                logger.info("Car owner type is :[{}]", carOwnerType);
                if(carOwnerType == CarOwnerTypeEnum.F.getCode()) {
                    throw new RefuseOrderCheckException(ErrorCode.MANAGED_CAR_CAN_NOT_OPT_TRANS);
                } else if (carOwnerType == CarOwnerTypeEnum.G.getCode()) {
                    throw new RefuseOrderCheckException(ErrorCode.PROXY_CAR_CAN_NOT_OPT_TRANS);
                }
            } else {
                logger.warn("Car owner type is empty. ownerOrderNo:[{}]", ownerOrderEntity.getOwnerOrderNo());
            }

        }
        //订单状态校验
        OrderStatusEntity orderStatusEntity = orderStatusService.getByOrderNo(orderNo);

        if(null != orderStatusEntity && null != orderStatusEntity.getStatus()) {
            if(OrderStatusEnum.TO_CONFIRM.getStatus() != orderStatusEntity.getStatus()) {
                throw new RefuseOrderCheckException();
            }
        }
    }


}
