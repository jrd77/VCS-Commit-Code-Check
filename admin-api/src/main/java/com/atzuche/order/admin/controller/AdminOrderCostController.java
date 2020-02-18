/**
 * 
 */
package com.atzuche.order.admin.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.atzuche.order.admin.service.AdminDeliveryCarService;
import com.atzuche.order.commons.enums.cashcode.OwnerCashCodeEnum;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.delivery.vo.delivery.rep.DeliveryCarVO;
import com.atzuche.order.delivery.vo.delivery.rep.OwnerGetAndReturnCarDTO;
import com.atzuche.order.delivery.vo.delivery.rep.RenterGetAndReturnCarDTO;
import com.atzuche.order.delivery.vo.delivery.req.DeliveryCarRepVO;
import com.atzuche.order.ownercost.entity.OwnerOrderIncrementDetailEntity;
import com.atzuche.order.ownercost.service.OwnerOrderIncrementDetailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.atzuche.order.admin.service.OrderCostService;
import com.atzuche.order.admin.vo.req.cost.OwnerCostReqVO;
import com.atzuche.order.admin.vo.req.cost.RenterCostReqVO;
import com.atzuche.order.admin.vo.resp.order.cost.OrderOwnerCostResVO;
import com.atzuche.order.admin.vo.resp.order.cost.OrderRenterCostResVO;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.autoyol.doc.annotation.AutoDocVersion;
import com.dianping.cat.Cat;

import java.util.List;
import java.util.Objects;

/**
 * @author jing.huang
 * 订单详细信息  租车费用  车主费用
 */
@RequestMapping("/console/ordercost/")
@RestController
@AutoDocVersion(version = "订单详细信息  租车费用  车主费用接口文档")
public class AdminOrderCostController {
	private static final Logger logger = LoggerFactory.getLogger(AdminOrderCostController.class);
	
	@Autowired
	OrderCostService orderCostService;
	
	@AutoDocMethod(description = "计算租客子订单费用", value = "计算租客子订单费用", response = OrderRenterCostResVO.class)
	@RequestMapping(value="calculateRenterOrderCost",method = RequestMethod.POST)
	public ResponseData calculateRenterOrderCost(@RequestBody @Validated RenterCostReqVO renterCostReqVO, HttpServletRequest request, HttpServletResponse response,BindingResult bindingResult) {
        logger.info("calculateRenterOrderCost controller params={}",renterCostReqVO.toString());
		if (bindingResult.hasErrors()) {
            return new ResponseData<>(ErrorCode.INPUT_ERROR.getCode(), ErrorCode.INPUT_ERROR.getText());
        }
        
        try {
        	
        	OrderRenterCostResVO resp = orderCostService.calculateRenterOrderCost(renterCostReqVO);
            // 计算油量和超里程费用
            DeliveryCarVO deliveryCarRepVO = getDeliveryCarVO(renterCostReqVO.getOrderNo());
            if(Objects.nonNull(deliveryCarRepVO) && Objects.nonNull(deliveryCarRepVO.getOwnerGetAndReturnCarDTO())){
                RenterGetAndReturnCarDTO renterGetAndReturnCarDTO = deliveryCarRepVO.getRenterGetAndReturnCarDTO();
                if(Objects.nonNull(renterGetAndReturnCarDTO)){
                    resp.setOilAmt(renterGetAndReturnCarDTO.getOilDifferenceCrash());
                    resp.setBeyondMileAmt(renterGetAndReturnCarDTO.getOverKNCrash());
                }
            }
        	return ResponseData.success(resp);
		} catch (Exception e) {
			Cat.logError("calculateRenterOrderCost exception params="+renterCostReqVO.toString(),e);
			logger.error("calculateRenterOrderCost exception params="+renterCostReqVO.toString(),e);
			return ResponseData.error();
		}
		
	}

    @Autowired
    private OwnerOrderIncrementDetailService ownerOrderIncrementDetailService;
    @Autowired
    private AdminDeliveryCarService deliveryCarInfoService;

	@AutoDocMethod(description = "计算车主子订单费用", value = "计算车主子订单费用", response = OrderOwnerCostResVO.class)
	@RequestMapping(value="calculateOwnerOrderCost",method = RequestMethod.POST)
	public ResponseData calculateOwnerOrderCost(@RequestBody @Validated OwnerCostReqVO ownerCostReqVO, HttpServletRequest request, HttpServletResponse response,BindingResult bindingResult) {
        logger.info("calculateOwnerOrderCost controller params={}",ownerCostReqVO.toString());
		if (bindingResult.hasErrors()) {
            return new ResponseData<>(ErrorCode.INPUT_ERROR.getCode(), ErrorCode.INPUT_ERROR.getText());
        }
        
        try {
        	
        	OrderOwnerCostResVO resp = orderCostService.calculateOwnerOrderCost(ownerCostReqVO);
            // 计算 Gps 和平台服务费
        	List<OwnerOrderIncrementDetailEntity> list=  ownerOrderIncrementDetailService.listOwnerOrderIncrementDetail(ownerCostReqVO.getOrderNo(),ownerCostReqVO.getOwnerOrderNo());
            if(!CollectionUtils.isEmpty(list)){
                int gpsAmt = list.stream().filter(obj ->{
                    return OwnerCashCodeEnum.GPS_SERVICE_AMT.getCashNo().equals(obj.getCostCode());
                }).mapToInt(OwnerOrderIncrementDetailEntity::getTotalAmount).sum();
                int serviceAmt = list.stream().filter(obj ->{
                    return OwnerCashCodeEnum.SERVICE_CHARGE.getCashNo().equals(obj.getCostCode());
                }).mapToInt(OwnerOrderIncrementDetailEntity::getTotalAmount).sum();
                resp.setGpsAmt(String.valueOf(Math.abs(gpsAmt)));
                resp.setPlatformSrvFeeAmt(String.valueOf(Math.abs(serviceAmt)));
            }else{
                resp.setGpsAmt("0");
                resp.setPlatformSrvFeeAmt("0");
            }
            // 计算油量和超里程费用
            DeliveryCarVO deliveryCarRepVO = getDeliveryCarVO(ownerCostReqVO.getOrderNo());
            if(Objects.nonNull(deliveryCarRepVO) && Objects.nonNull(deliveryCarRepVO.getOwnerGetAndReturnCarDTO())){
                OwnerGetAndReturnCarDTO ownerGetAndReturnCarDTO = deliveryCarRepVO.getOwnerGetAndReturnCarDTO();
                if(Objects.nonNull(ownerGetAndReturnCarDTO)){
                    resp.setOilAmt(ownerGetAndReturnCarDTO.getOilDifferenceCrash());
                    resp.setBeyondMileAmt(ownerGetAndReturnCarDTO.getOverKNCrash());
                }
            }
        	logger.info("resp = " + resp.toString());
        	return ResponseData.success(resp);
		} catch (Exception e) {
			Cat.logError("calculateOwnerOrderCost exception params="+ownerCostReqVO.toString(),e);
			logger.error("calculateOwnerOrderCost exception params="+ownerCostReqVO.toString(),e);
			return ResponseData.error();
		}
		
	}

	private DeliveryCarVO getDeliveryCarVO(String orderNo){
        DeliveryCarRepVO deliveryCarDTO = new DeliveryCarRepVO();
        deliveryCarDTO.setOrderNo(orderNo);
        DeliveryCarVO deliveryCarRepVO = deliveryCarInfoService.findDeliveryListByOrderNo(deliveryCarDTO);
        return deliveryCarRepVO;
    }

}
