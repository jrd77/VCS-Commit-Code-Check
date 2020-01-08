package com.atzuche.order.coreapi.service;

import com.atzuche.order.commons.entity.dto.CancelFineAmtDTO;
import com.atzuche.order.commons.entity.dto.CostBaseDTO;
import com.atzuche.order.commons.entity.dto.RenterGoodsDetailDTO;
import com.atzuche.order.commons.enums.FineSubsidyCodeEnum;
import com.atzuche.order.commons.enums.FineSubsidySourceCodeEnum;
import com.atzuche.order.commons.enums.FineTypeEnum;
import com.atzuche.order.coreapi.entity.dto.CancelOrderResDTO;
import com.atzuche.order.coreapi.entity.vo.req.CarDispatchReqVO;
import com.atzuche.order.ownercost.entity.ConsoleOwnerOrderFineDeatailEntity;
import com.atzuche.order.ownercost.entity.OwnerOrderEntity;
import com.atzuche.order.ownercost.service.OwnerOrderService;
import com.atzuche.order.parentorder.entity.OrderEntity;
import com.atzuche.order.parentorder.entity.OrderStatusEntity;
import com.atzuche.order.parentorder.service.OrderService;
import com.atzuche.order.parentorder.service.OrderStatusService;
import com.atzuche.order.rentercommodity.service.RenterGoodsService;
import com.atzuche.order.rentercost.entity.RenterOrderCostEntity;
import com.atzuche.order.rentercost.entity.RenterOrderFineDeatailEntity;
import com.atzuche.order.rentercost.service.RenterOrderCostService;
import com.atzuche.order.rentercost.service.RenterOrderFineDeatailService;
import com.atzuche.order.renterorder.entity.OrderCouponEntity;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.renterorder.service.OrderCouponService;
import com.atzuche.order.renterorder.service.RenterOrderService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 车主取消
 *
 * @author pengcheng.fu
 * @date 2020/1/7 16:22
 */
@Service
public class OwnerCancelOrderService {

    @Autowired
    private CarRentalTimeApiService carRentalTimeApiService;
    @Autowired
    OrderCouponService orderCouponService;
    @Autowired
    RenterOrderService renterOrderService;
    @Autowired
    RenterGoodsService renterGoodsService;
    @Autowired
    RenterOrderCostService renterOrderCostService;
    @Autowired
    OwnerOrderService ownerOrderService;
    @Autowired
    OrderStatusService orderStatusService;
    @Autowired
    OrderService orderService;
    @Autowired
    RenterOrderFineDeatailService renterOrderFineDeatailService;


    /**
     * 取消处理
     *
     * @param orderNo      主订单号
     * @param cancelReason 取消原因
     * @return CancelOrderResDTO 返回信息
     */
    @Transactional(rollbackFor = Exception.class)
    public CancelOrderResDTO cancel(String orderNo, String cancelReason) {
        //校验
        check();
        //返回信息
        CancelOrderResDTO cancelOrderResDTO = new CancelOrderResDTO();

        //获取订单信息
        OrderEntity orderEntity = orderService.getOrderEntity(orderNo);
        //获取租客订单信息
        RenterOrderEntity renterOrderEntity = renterOrderService.getRenterOrderByOrderNoAndIsEffective(orderNo);
        //获取租客订单商品明细
        RenterGoodsDetailDTO goodsDetail = renterGoodsService.getRenterGoodsDetail(renterOrderEntity.getRenterOrderNo()
                , false);
        //获取租客订单费用明细
        RenterOrderCostEntity renterOrderCostEntity = renterOrderCostService.getByOrderNoAndRenterNo(orderNo,
                renterOrderEntity.getRenterOrderNo());
        //获取车主订单信息
        OwnerOrderEntity ownerOrderEntity = ownerOrderService.getOwnerOrderByOrderNoAndIsEffective(orderNo);
        //获取订单状态信息
        OrderStatusEntity orderStatusEntity = orderStatusService.getByOrderNo(orderNo);
        //获取车主券信息
        OrderCouponEntity ownerCouponEntity = orderCouponService.getOwnerCouponByOrderNoAndRenterOrderNo(orderNo,
                renterOrderEntity.getRenterOrderNo());
        //调度判定
        //todo
        boolean isDispatch = carRentalTimeApiService.checkCarDispatch(buildCarDispatchReqVO(orderEntity,
                orderStatusEntity, ownerCouponEntity));
        if (isDispatch) {
            //取消进调度
            cancelOrderResDTO.setIsReturnDisCoupon(false);
            cancelOrderResDTO.setIsReturnOwnerCoupon(false);


        } else {
            //取消不进调度
            cancelOrderResDTO.setIsReturnDisCoupon(true);
            cancelOrderResDTO.setIsReturnOwnerCoupon(true);
            cancelOrderResDTO.setOwnerCouponNo(null == ownerCouponEntity ? null : ownerCouponEntity.getCouponId());
            //罚金计算(罚金和收益)
            //车主罚金处理
            CancelFineAmtDTO cancelFineAmt = buildCancelFineAmtDTO(renterOrderEntity,
                    renterOrderCostEntity, goodsDetail.getCarOwnerType());
            int penalty = renterOrderFineDeatailService.calCancelFine(cancelFineAmt);
            int fineAmt = penalty + renterOrderCostEntity.getBasicEnsureAmount();

            //租客收益处理


        }


        //订单状态更新
        //todo

        //去库存
        //todo

        //落库
        //todo


        //返回信息处理


        return null;

    }


    public void check() {
        //todo


    }


    /**
     * 判定调度方法参数处理
     *
     * @param orderEntity       主订单信息
     * @param orderStatusEntity 订单状态信息
     * @param orderCouponEntity 订单车主券信息
     * @return CarDispatchReqVO 判定调度信息
     */
    public CarDispatchReqVO buildCarDispatchReqVO(OrderEntity orderEntity, OrderStatusEntity orderStatusEntity,
                                                  OrderCouponEntity orderCouponEntity) {

        CarDispatchReqVO carDispatchReqVO = new CarDispatchReqVO();
        carDispatchReqVO.setType(2);
        carDispatchReqVO.setCityCode(Integer.valueOf(orderEntity.getCityCode()));
        carDispatchReqVO.setReqTime(orderEntity.getReqTime());
        carDispatchReqVO.setRentTime(orderEntity.getExpRentTime());
        carDispatchReqVO.setRevertTime(orderEntity.getExpRevertTime());
        carDispatchReqVO.setPayFlag(orderStatusEntity.getRentCarPayStatus());
        carDispatchReqVO.setCouponFlag(null == orderCouponEntity || orderCouponEntity.getStatus() == 0 ? 0 : 1);
        return carDispatchReqVO;
    }


    /**
     * 组装计算取消订单罚金请求参数
     *
     * @param renterOrderEntity     租客订单信息
     * @param renterOrderCostEntity 租客订单费用信息
     * @param carOwnerType          车辆类型
     * @return CancelFineAmtDTO
     */
    private CancelFineAmtDTO buildCancelFineAmtDTO(RenterOrderEntity renterOrderEntity,
                                                   RenterOrderCostEntity renterOrderCostEntity,
                                                   Integer carOwnerType) {
        CancelFineAmtDTO cancelFineAmtDTO = new CancelFineAmtDTO();
        CostBaseDTO costBaseDTO = new CostBaseDTO();
        costBaseDTO.setOrderNo(renterOrderEntity.getOrderNo());
        costBaseDTO.setRenterOrderNo(renterOrderEntity.getRenterOrderNo());
        costBaseDTO.setMemNo(renterOrderCostEntity.getMemNo());
        costBaseDTO.setStartTime(renterOrderEntity.getExpRentTime());
        costBaseDTO.setEndTime(renterOrderEntity.getExpRevertTime());

        cancelFineAmtDTO.setCostBaseDTO(costBaseDTO);
        cancelFineAmtDTO.setCancelTime(LocalDateTime.now());
        cancelFineAmtDTO.setRentAmt(renterOrderCostEntity.getRentCarAmount());
        cancelFineAmtDTO.setOwnerType(carOwnerType);
        return cancelFineAmtDTO;
    }
}
