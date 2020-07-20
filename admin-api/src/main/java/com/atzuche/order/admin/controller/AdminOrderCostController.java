/**
 * 
 */
package com.atzuche.order.admin.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.atzuche.order.open.vo.BaoFeiInfoVO;
import com.atzuche.order.commons.BindingResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.atzuche.order.admin.service.OrderCostService;
import com.atzuche.order.admin.vo.req.cost.OwnerCostReqVO;
import com.atzuche.order.admin.vo.req.cost.RenterCostReqVO;
import com.atzuche.order.admin.vo.resp.order.cost.OrderOwnerCostResVO;
import com.atzuche.order.admin.vo.resp.order.cost.OrderRenterCostResVO;
import com.autoyol.commons.utils.GsonUtils;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.autoyol.doc.annotation.AutoDocVersion;
import com.dianping.cat.Cat;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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

    @AutoDocMethod(description = "计算租客子订单费用-保费弹窗", value = "计算租客子订单费用-保费弹窗", response = BaoFeiInfoVO.class)
    @RequestMapping(value="/getBaoFeiInfo",method = RequestMethod.GET)
	public ResponseData<BaoFeiInfoVO> getBaoFeiInfo(@RequestParam("orderNo") String orderNo, @RequestParam("renterOwnerNo") String renterOwnerNo,@RequestParam("baoFeiType") int baoFeiType){
        BaoFeiInfoVO baoFeiInfoVO = orderCostService.getBaoFeiInfo(orderNo,renterOwnerNo,baoFeiType);
        return ResponseData.success(baoFeiInfoVO);
    }
    
	@AutoDocMethod(description = "计算租客子订单费用", value = "计算租客子订单费用", response = OrderRenterCostResVO.class)
	@RequestMapping(value="/calculateRenterOrderCost",method = RequestMethod.POST)
	public ResponseData calculateRenterOrderCost(@RequestBody @Validated RenterCostReqVO renterCostReqVO, HttpServletRequest request, HttpServletResponse response,BindingResult bindingResult) {
        logger.info("calculateRenterOrderCost controller params={}",renterCostReqVO.toString());
		BindingResultUtil.checkBindingResult(bindingResult);
        
        try {
        	OrderRenterCostResVO resp = orderCostService.calculateRenterOrderCost(renterCostReqVO);
        	logger.info("calculateRenterOrderCost resp[{}]", GsonUtils.toJson(resp));
        	return ResponseData.success(resp);
		} catch (Exception e) {
			Cat.logError("calculateRenterOrderCost exception params="+renterCostReqVO.toString(),e);
			logger.error("calculateRenterOrderCost exception params="+renterCostReqVO.toString(),e);
			return ResponseData.error();
		}
		
	}

    @AutoDocMethod(description = "长租-计算租客子订单费用", value = "长租-计算租客子订单费用", response = OrderRenterCostResVO.class)
    @RequestMapping(value="/calculateRenterOrderCostLongRent",method = RequestMethod.POST)
    public ResponseData calculateRenterOrderCostLongRent(@RequestBody @Validated RenterCostReqVO renterCostReqVO,BindingResult bindingResult) {
        logger.info("calculateRenterOrderCostLongRent controller params={}",renterCostReqVO.toString());
        BindingResultUtil.checkBindingResult(bindingResult);

        try {
            OrderRenterCostResVO resp = orderCostService.calculateRenterOrderCostLongRent(renterCostReqVO);
            logger.info("calculateRenterOrderCostLongRent resp[{}]", GsonUtils.toJson(resp));
            return ResponseData.success(resp);
        } catch (Exception e) {
            Cat.logError("calculateRenterOrderCostLongRent exception params="+renterCostReqVO.toString(),e);
            logger.error("calculateRenterOrderCostLongRent exception params="+renterCostReqVO.toString(),e);
            return ResponseData.error();
        }

    }
	@AutoDocMethod(description = "计算车主子订单费用", value = "计算车主子订单费用", response = OrderOwnerCostResVO.class)
	@RequestMapping(value="/calculateOwnerOrderCost",method = RequestMethod.POST)
	public ResponseData calculateOwnerOrderCost(@RequestBody @Validated OwnerCostReqVO ownerCostReqVO, HttpServletRequest request, HttpServletResponse response,BindingResult bindingResult) {
        logger.info("calculateOwnerOrderCost controller params={}",ownerCostReqVO.toString());
		BindingResultUtil.checkBindingResult(bindingResult);
        
        try {
        	OrderOwnerCostResVO resp = orderCostService.calculateOwnerOrderCost(ownerCostReqVO);
            //TODO 车载押金 没有
        	logger.info("resp = " + resp.toString());
        	return ResponseData.success(resp);
		} catch (Exception e) {
			Cat.logError("calculateOwnerOrderCost exception params="+ownerCostReqVO.toString(),e);
			logger.error("calculateOwnerOrderCost exception params="+ownerCostReqVO.toString(),e);
			return ResponseData.error();
		}
		
	}
    @AutoDocMethod(description = "长租-计算车主子订单费用", value = "长租-计算车主子订单费用", response = OrderOwnerCostResVO.class)
    @RequestMapping(value="/calculateOwnerOrderCostLong",method = RequestMethod.POST)
    public ResponseData calculateOwnerOrderCostLong(@RequestBody @Validated OwnerCostReqVO ownerCostReqVO, HttpServletRequest request, HttpServletResponse response,BindingResult bindingResult) {
        logger.info("calculateOwnerOrderCost controller params={}",ownerCostReqVO.toString());
        BindingResultUtil.checkBindingResult(bindingResult);

        try {
            OrderOwnerCostResVO resp = orderCostService.calculateOwnerOrderCostLong(ownerCostReqVO);
            logger.info("resp = " + resp.toString());
            return ResponseData.success(resp);
        } catch (Exception e) {
            Cat.logError("calculateOwnerOrderCost exception params="+ownerCostReqVO.toString(),e);
            logger.error("calculateOwnerOrderCost exception params="+ownerCostReqVO.toString(),e);
            return ResponseData.error();
        }

    }
//	private DeliveryCarVO getDeliveryCarVO(String orderNo){
//        DeliveryCarRepVO deliveryCarDTO = new DeliveryCarRepVO();
//        deliveryCarDTO.setOrderNo(orderNo);
//        DeliveryCarVO deliveryCarRepVO = deliveryCarInfoService.findDeliveryListByOrderNo(deliveryCarDTO);
//        return deliveryCarRepVO;
//    }
	

}
