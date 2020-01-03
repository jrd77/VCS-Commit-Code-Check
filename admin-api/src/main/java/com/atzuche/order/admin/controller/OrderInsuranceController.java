package com.atzuche.order.admin.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atzuche.order.admin.vo.request.OrderInsuranceAdditionRequestVO;
import com.atzuche.order.admin.vo.request.OrderInsuranceRequestVO;
import com.atzuche.order.admin.vo.response.OrderInsuranceResponseVO;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.autoyol.doc.annotation.AutoDocVersion;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/console/order/insurance")
@RestController
@AutoDocVersion(version = "购买保险接口文档")
public class OrderInsuranceController {



    @Value("${auto.insurance.purchase.url}")
    private String insurancePurchaseUrl;

	@AutoDocMethod(description = "购买保险列表", value = "购买保险列表", response = OrderInsuranceResponseVO.class)
	@GetMapping("/list")
	public ResponseData<OrderInsuranceResponseVO> list(@RequestBody OrderInsuranceRequestVO orderInsuranceRequestVO, BindingResult bindingResult) {
        System.out.println("aaaaaa"+insurancePurchaseUrl);
        ResponseEntity<String> response = null;//restTemplate.getForEntity(insurancePurchaseUrl + "insurance/purchase/getByOrderNo/" + orderInsuranceRequestVO.getOrderNo(), String.class);
        String result = response.getBody();
        if (StringUtils.isEmpty(result)) {
            return null;
        }
        JSONObject jsonObject = JSON.parseObject(result);
        String resCode = jsonObject.getString("resCode");
/*        if (ErrorCode.SUCCESS.getCode().equalsIgnoreCase(resCode)) {
            InsurancePurchaseResultVO insurancePurchaseResultVO = jsonObject.getObject("data", InsurancePurchaseResultVO.class);
            List<InsurancePurchaseModel> insuranceList = insurancePurchaseResultVO.getInsuranceList();
            if(CollectionUtils.isNotEmpty(insuranceList)) {
                insuranceList.forEach(insurance -> {
                    if (insurance.getType() == 2) {
                        String insuranceCompany = insurance.getInsuranceCompany();
                        if("1".equalsIgnoreCase(insuranceCompany)) {
                            insurance.setInsuranceCompany("太平洋保险");
                        } else if("2".equalsIgnoreCase(insuranceCompany)) {
                            insurance.setInsuranceCompany("中国人民保险");
                        }
                    }
                });
                return insuranceList;
            }
        }*/
        return null;
		//return ResponseData.success(null);
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
