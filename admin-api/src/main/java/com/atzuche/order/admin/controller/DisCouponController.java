package com.atzuche.order.admin.controller;

import com.atzuche.order.admin.vo.req.discoupon.CarDisCouponRequestVO;
import com.atzuche.order.admin.vo.req.discoupon.OwnerDisCouponRequestVO;
import com.atzuche.order.admin.vo.req.discoupon.PlatformDisCouponRequestVO;
import com.atzuche.order.admin.vo.resp.discoupon.CarDisCouponListResponseVO;
import com.atzuche.order.admin.vo.resp.discoupon.OwnerDisCouponListResponseVO;
import com.atzuche.order.admin.vo.resp.discoupon.PlatformDisCouponListResponseVO;

import com.atzuche.order.admin.vo.resp.insurance.OrderInsuranceResponseVO;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.autoyol.doc.annotation.AutoDocVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/console/order/")
@RestController
@AutoDocVersion(version = "订单接口文档")
public class DisCouponController {

    private static final Logger logger = LoggerFactory.getLogger(DisCouponController.class);


	@AutoDocMethod(description = "获取平台优惠券列表", value = "获取平台优惠券列表", response = PlatformDisCouponListResponseVO.class)
	@GetMapping("platform/discoupon/list")
	public ResponseData platformDisCouponList(@RequestBody PlatformDisCouponRequestVO platformDisCouponRequestVO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseData<>(ErrorCode.INPUT_ERROR.getCode(), ErrorCode.INPUT_ERROR.getText());
        }
		return ResponseData.success(null);
	}


	@AutoDocMethod(description = "获取车主优惠券列表", value = "获取车主优惠券列表", response = OwnerDisCouponListResponseVO.class)
	@GetMapping("owner/discoupon/list")
	public ResponseData<OrderInsuranceResponseVO> ownerDisCouponList(@RequestBody OwnerDisCouponRequestVO ownerDisCouponRequestVO, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return new ResponseData<>(ErrorCode.INPUT_ERROR.getCode(), ErrorCode.INPUT_ERROR.getText());
		}
		return ResponseData.success(null);
	}

	@AutoDocMethod(description = "获取取还车优惠券列表", value = "获取取还车优惠券列表", response = CarDisCouponListResponseVO.class)
	@GetMapping("car/discoupon/list")
	public ResponseData<OrderInsuranceResponseVO> ownerDisCouponList(@RequestBody CarDisCouponRequestVO carDisCouponRequestVO, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return new ResponseData<>(ErrorCode.INPUT_ERROR.getCode(), ErrorCode.INPUT_ERROR.getText());
		}
		return ResponseData.success(null);
	}



}
