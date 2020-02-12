package com.atzuche.order.coreapi.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atzuche.order.commons.entity.dto.CancelFineAmtDTO;
import com.atzuche.order.commons.entity.dto.CostBaseDTO;
import com.atzuche.order.commons.entity.dto.RenterGoodsDetailDTO;
import com.atzuche.order.commons.enums.OrderStatusEnum;
import com.atzuche.order.parentorder.entity.OrderStatusEntity;
import com.atzuche.order.parentorder.service.OrderStatusService;
import com.atzuche.order.rentercommodity.service.RenterGoodsService;
import com.atzuche.order.rentercost.entity.RenterOrderCostEntity;
import com.atzuche.order.rentercost.service.RenterOrderCostService;
import com.atzuche.order.rentercost.service.RenterOrderFineDeatailService;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.renterorder.service.RenterOrderService;

@Service
public class CancelOrderFeeService {

	@Autowired
	private RenterOrderService renterOrderService;
	@Autowired
	private RenterGoodsService renterGoodsService;
	@Autowired
	private RenterOrderCostService renterOrderCostService;
	@Autowired
	private OrderStatusService orderStatusService;
	@Autowired
	private RenterOrderFineDeatailService renterOrderFineDeatailService;
	
	/**
	 * 取消前计算违约金
	 * @param orderNo
	 * @return Integer
	 */
	public Integer getCancelPenalty(String orderNo) {
		// 获取租客订单信息
        RenterOrderEntity renterOrderEntity = renterOrderService.getRenterOrderByOrderNoAndIsEffective(orderNo);
        // 获取租客订单商品明细
        RenterGoodsDetailDTO goodsDetail = renterGoodsService.getRenterGoodsDetail(renterOrderEntity.getRenterOrderNo()
                , false);
        // 获取租客订单费用明细
        RenterOrderCostEntity renterOrderCostEntity = renterOrderCostService.getByOrderNoAndRenterNo(orderNo,
                renterOrderEntity.getRenterOrderNo());
        // 获取订单状态信息
        OrderStatusEntity orderStatusEntity = orderStatusService.getByOrderNo(orderNo);
        // 罚金计算(罚金和收益)
        // 租客罚金处理
        CancelFineAmtDTO cancelFineAmt = buildCancelFineAmtDTO(renterOrderEntity,
                renterOrderCostEntity, goodsDetail.getCarOwnerType());
        int penalty = renterOrderFineDeatailService.calCancelFine(cancelFineAmt);
        // 罚给平台(保险费)
        if(orderStatusEntity.getStatus() >= OrderStatusEnum.TO_RETURN_CAR.getStatus() && 
        		orderStatusEntity.getStatus() != OrderStatusEnum.CLOSED.getStatus()) {
        	Integer insureAmt = renterOrderCostEntity.getBasicEnsureAmount();
        	if (insureAmt != null) {
        		penalty += Math.abs(insureAmt);
        	}
        }
        return penalty;
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
        cancelFineAmtDTO.setRentAmt(Math.abs(renterOrderCostEntity.getRentCarAmount()));
        cancelFineAmtDTO.setOwnerType(carOwnerType);
        return cancelFineAmtDTO;
    }
}
