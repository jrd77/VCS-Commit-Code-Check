package com.atzuche.order.admin.controller.order.remark;

import com.atzuche.order.admin.vo.req.remark.OrderGetReturnCarRemarkRequestVO;
import com.atzuche.order.admin.vo.req.remark.OrderRentCityRequestVO;
import com.atzuche.order.admin.vo.req.remark.OrderRiskStatusRequestVO;
import com.atzuche.order.admin.vo.resp.remark.OrderOtherInformationResponseVO;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.autoyol.doc.annotation.AutoDocVersion;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/console/order/other/information")
@RestController
@AutoDocVersion(version = "订单备注接口文档")
public class OrderOtherInformationController {

	@AutoDocMethod(description = "修改租车城市", value = "修改租车城市", response = ResponseData.class)
    @RequestMapping(value = "/rent/city/update", method = RequestMethod.PUT)
	public ResponseData<ResponseData> updateRentCity(@RequestBody OrderRentCityRequestVO orderRentCityRequestVO, BindingResult bindingResult) {
		return ResponseData.success(null);
	}

    @AutoDocMethod(description = "是否风控事故修改", value = "是否风控事故修改", response = ResponseData.class)
    @RequestMapping(value = "/risk/status/update", method = RequestMethod.PUT)
    public ResponseData<ResponseData> updateRiskStatus(@RequestBody OrderRiskStatusRequestVO orderRiskStatusRequestVO, BindingResult bindingResult) {
        return ResponseData.success(null);
    }

    @AutoDocMethod(description = "取送车备注修改", value = "取送车备注修改", response = ResponseData.class)
    @RequestMapping(value = "/get/return/car/update", method = RequestMethod.PUT)
    public ResponseData<ResponseData> updateGetReturnCarRemark(@RequestBody OrderGetReturnCarRemarkRequestVO orderGetReturnCarRemarkRequestVO, BindingResult bindingResult) {
        return ResponseData.success(null);
    }

    @AutoDocMethod(description = "获取其他备注信息(包括租车城市,风控事故，取送车备注)", value = "获取其他备注信息(包括租车城市,风控事故，取送车备注)", response = OrderOtherInformationResponseVO.class)
    @GetMapping("/detail")
    public ResponseData<ResponseData> getOtherRemarkInformation( OrderRentCityRequestVO OrderRentCityRequestVO, BindingResult bindingResult) {
        OrderOtherInformationResponseVO orderOtherInformationResponseVO = new OrderOtherInformationResponseVO();
        orderOtherInformationResponseVO.setRentCity("上海");
        orderOtherInformationResponseVO.setRiskAccidentStatus("1");
        orderOtherInformationResponseVO.setRemarkContent("12点送车到点");
        return ResponseData.success(null);
    }


}
