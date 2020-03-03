package com.atzuche.order.coreapi.service;

import com.atzuche.order.commons.entity.dto.OwnerGoodsDetailDTO;
import com.atzuche.order.commons.entity.dto.RenterGoodsDetailDTO;
import com.atzuche.order.commons.enums.CarOwnerTypeEnum;
import com.atzuche.order.commons.enums.MemRoleEnum;
import com.atzuche.order.commons.vo.req.AdminCancelOrderReqVO;
import com.atzuche.order.commons.vo.req.OrderCancelAppealReqVO;
import com.atzuche.order.coreapi.submitOrder.exception.RefuseOrderCheckException;
import com.atzuche.order.owner.commodity.service.OwnerGoodsService;
import com.atzuche.order.ownercost.entity.OwnerOrderEntity;
import com.atzuche.order.ownercost.service.OwnerOrderService;
import com.atzuche.order.parentorder.entity.OrderCancelAppealEntity;
import com.atzuche.order.parentorder.service.OrderCancelAppealService;
import com.atzuche.order.rentercommodity.service.RenterGoodsService;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.renterorder.service.RenterOrderService;
import com.autoyol.commons.web.ErrorCode;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 订单取消申诉业务处理
 *
 * @author pengcheng.fu
 * @date 2020/3/2 14:21
 */

@Service
public class CancelOrderAppealService {

    private static Logger logger = LoggerFactory.getLogger(CancelOrderAppealService.class);

    @Autowired
    private OrderCancelAppealService orderCancelAppealService;
    @Autowired
    private CancelOrderService cancelOrderService;
    @Autowired
    private RenterOrderService renterOrderService;
    @Autowired
    private OwnerOrderService ownerOrderService;
    @Autowired
    private RenterGoodsService renterGoodsService;
    @Autowired
    private OwnerGoodsService ownerGoodsService;

    /**
     * 申诉
     *
     * @param reqVO 申诉请求参数
     */
    public void appeal(OrderCancelAppealReqVO reqVO) {
        //申诉信息处理
        String subOrderNo = "";
        Integer carOwnerType = null;
        if (StringUtils.equals(MemRoleEnum.RENTER.getCode(), reqVO.getMemRole())) {
            RenterOrderEntity renterOrderEntity =
                    renterOrderService.getRenterOrderByOrderNoAndIsEffective(reqVO.getOrderNo());
            subOrderNo = renterOrderEntity.getRenterOrderNo();

            RenterGoodsDetailDTO goodsDetailDTO = renterGoodsService.getRenterGoodsDetail(subOrderNo, false);
            carOwnerType = goodsDetailDTO.getCarOwnerType();
        } else if (StringUtils.equals(MemRoleEnum.OWNER.getCode(), reqVO.getMemRole())) {
            OwnerOrderEntity ownerOrderEntity =
                    ownerOrderService.getOwnerOrderByOrderNoAndIsEffective(reqVO.getOrderNo());
            subOrderNo = ownerOrderEntity.getOwnerOrderNo();

            OwnerGoodsDetailDTO goodsDetailDTO = ownerGoodsService.getOwnerGoodsDetail(subOrderNo, false);
            carOwnerType = goodsDetailDTO.getCarOwnerType();
        }
        //车辆类型校验
        if (null != carOwnerType) {
            logger.info("Car owner type is :[{}]", carOwnerType);
            if (carOwnerType == CarOwnerTypeEnum.F.getCode()) {
                throw new RefuseOrderCheckException(ErrorCode.MANAGED_CAR_CAN_NOT_OPT_TRANS);
            } else if (carOwnerType == CarOwnerTypeEnum.G.getCode()) {
                throw new RefuseOrderCheckException(ErrorCode.PROXY_CAR_CAN_NOT_OPT_TRANS);
            }
        } else {
            logger.warn("Car owner type is empty.");
        }

        OrderCancelAppealEntity record = buildOrderCancelAppealEntity(reqVO, subOrderNo);
        int result = orderCancelAppealService.addOrderCancelAppeal(record);
        if (result > 0) {
            //取消订单处理(走管理后台取消流程)
            AdminCancelOrderReqVO adminCancelOrderReqVO = new AdminCancelOrderReqVO();
            BeanUtils.copyProperties(reqVO, adminCancelOrderReqVO);
            adminCancelOrderReqVO.setOperatorName("H5SystemOperator");
            cancelOrderService.cancel(adminCancelOrderReqVO, false);
        }
    }


    /**
     * 构建申诉信息
     *
     * @param reqVO      申诉请求信息
     * @param subOrderNo 子订单号
     * @return OrderCancelAppealEntity 申诉信息
     */
    private OrderCancelAppealEntity buildOrderCancelAppealEntity(OrderCancelAppealReqVO reqVO, String subOrderNo) {
        OrderCancelAppealEntity orderCancelAppealEntity = new OrderCancelAppealEntity();
        orderCancelAppealEntity.setOrderNo(reqVO.getOrderNo());
        orderCancelAppealEntity.setSubOrderNo(subOrderNo);
        orderCancelAppealEntity.setMemNo(Integer.valueOf(reqVO.getMemNo()));
        orderCancelAppealEntity.setMemRole(Integer.valueOf(reqVO.getMemRole()));
        orderCancelAppealEntity.setAppealReason(reqVO.getAppealReason());
        return orderCancelAppealEntity;
    }


}
