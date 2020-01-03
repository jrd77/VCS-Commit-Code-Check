package com.atzuche.order.admin.controller;

import com.atzuche.order.admin.vo.request.*;
import com.atzuche.order.admin.vo.response.OrderInsuranceResponseVO;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.autoyol.doc.annotation.AutoDocVersion;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/console/order/other/information")
@RestController
@AutoDocVersion(version = "订单备注接口文档")
public class OrderOtherInformationController {

	@AutoDocMethod(description = "修改租车城市", value = "修改租车城市", response = ResponseData.class)
	@PostMapping("/rent/city/update")
	public ResponseData<?> updateRentCity(@RequestBody OrderRentCityRequestVO orderRentCityRequestVO, BindingResult bindingResult) {
		return ResponseData.success(null);
	}

    @AutoDocMethod(description = "是否风控事故修改", value = "是否风控事故修改", response = ResponseData.class)
    @PostMapping("/risk/status/update")
    public ResponseData<?> updateRiskStatus(@RequestBody OrderRiskStatusRequestVO orderRiskStatusRequestVO, BindingResult bindingResult) {
        return ResponseData.success(null);
    }

    @AutoDocMethod(description = "取送车备注修改", value = "取送车备注修改", response = ResponseData.class)
    @PostMapping("/get/return/car/update")
    public ResponseData<?> updateGetReturnCarRemark(@RequestBody OrderGetReturnCarRemarkRequestVO orderGetReturnCarRemarkRequestVO, BindingResult bindingResult) {
        return ResponseData.success(null);
    }

}
