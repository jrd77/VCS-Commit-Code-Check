package com.atzuche.order.admin.controller;

import com.atzuche.order.admin.vo.request.OrderInsuranceAdditionRequestVO;
import com.atzuche.order.admin.vo.request.OrderInsuranceRequestVO;
import com.atzuche.order.admin.vo.response.OrderInsuranceResponseVO;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.autoyol.doc.annotation.AutoDocVersion;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/console/order/insurance")
@RestController
@AutoDocVersion(version = "购买保险接口文档")
public class OrderInsuranceController {

	@AutoDocMethod(description = "购买保险列表", value = "购买保险列表", response = OrderInsuranceResponseVO.class)
	@GetMapping("/list")
	public ResponseData<OrderInsuranceResponseVO> list(@RequestBody OrderInsuranceRequestVO orderInsuranceRequestVO, BindingResult bindingResult) {
		return ResponseData.success(null);
	}

    @AutoDocMethod(description = "手工录入保险信息", value = "手工录入保险信息", response = ResponseData.class)
    @PostMapping("/add")
    public ResponseData<ResponseData> add(@RequestBody OrderInsuranceAdditionRequestVO additionOrderInsuranceRequestVO, BindingResult bindingResult) {
        return ResponseData.success(null);
    }

    @AutoDocMethod(description = "导入保险信息excel", value = "导入保险信息excel", response = ResponseData.class)
    @PostMapping("/import")
    public ResponseData<ResponseData> importExcel(@RequestParam("batchFile") MultipartFile file, BindingResult bindingResult) {
        return ResponseData.success(null);
    }
}
