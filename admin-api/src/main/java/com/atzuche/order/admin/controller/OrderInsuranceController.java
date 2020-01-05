package com.atzuche.order.admin.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atzuche.order.admin.dto.InsurancePurchaseDTO;
import com.atzuche.order.admin.dto.InsurancePurchaseResultDTO;
import com.atzuche.order.admin.dto.OrderInsuranceAdditionRequestDTO;
import com.atzuche.order.admin.vo.request.OrderInsuranceAdditionRequestVO;
import com.atzuche.order.admin.vo.request.OrderInsuranceRequestVO;
import com.atzuche.order.admin.vo.response.OrderInsuranceResponseVO;
import com.atzuche.order.admin.vo.response.OrderInsuranceVO;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.autoyol.doc.annotation.AutoDocVersion;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/console/order/insurance")
@RestController
@AutoDocVersion(version = "购买保险接口文档")
public class OrderInsuranceController {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderInsuranceController.class);
    public static final String GET_PURCHASE_BY_ORDERNO = "insurance/purchase/getByOrderNo/";

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        // Do any additional configuration here
        return builder.build();
    }
    @Autowired
    private RestTemplate restTemplate;

    @Value("${auto.insurance.purchase.url}")
    private String insurancePurchaseUrl;


	@AutoDocMethod(description = "购买保险列表", value = "购买保险列表", response = OrderInsuranceResponseVO.class)
	@GetMapping("/list")
	public ResponseData<OrderInsuranceResponseVO> list(@RequestBody OrderInsuranceRequestVO orderInsuranceRequestVO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseData<>(ErrorCode.INPUT_ERROR.getCode(), ErrorCode.INPUT_ERROR.getText());
        }
		return ResponseData.success(getPurchaseList(orderInsuranceRequestVO));
	}

    @AutoDocMethod(description = "手工录入保险信息", value = "手工录入保险信息", response = ResponseData.class)
    @PostMapping("/add")
    public ResponseData<ResponseData> add(@RequestBody OrderInsuranceAdditionRequestVO additionOrderInsuranceRequestVO, BindingResult bindingResult) {
        OrderInsuranceAdditionRequestDTO orderInsuranceAdditionRequestDTO = new OrderInsuranceAdditionRequestDTO();
        //属性拷贝
	    BeanUtils.copyProperties(additionOrderInsuranceRequestVO,orderInsuranceAdditionRequestDTO);
        //param.put("operator", getCurrOperator(request).getLoginName());
        //param.put("insuranceDate", DateUtils.formate(new Date(), "yyyy-MM-dd HH:mm:ss"));
        //ResponseEntity<String> responseEntity = restTemplate.postForEntity(INSURANCE_PURCHASE_URL + "insurance/purchase/manual/add", additionOrderInsuranceRequestVO, String.class);
        //String result = responseEntity.getBody();
        return ResponseData.success(null);
    }

    @AutoDocMethod(description = "导入保险信息excel", value = "导入保险信息excel", response = ResponseData.class)
    @PostMapping("/import")
    public ResponseData<ResponseData> importExcel(@RequestParam("batchFile") MultipartFile file, BindingResult bindingResult) {
        return ResponseData.success(null);
    }


    /**
     * 获取保险列表
     * @param orderInsuranceRequestVO
     * @return
     */
    private OrderInsuranceResponseVO getPurchaseList(OrderInsuranceRequestVO orderInsuranceRequestVO){
        ResponseEntity<String> response = restTemplate.getForEntity(insurancePurchaseUrl + GET_PURCHASE_BY_ORDERNO + orderInsuranceRequestVO.getOrderNo(), String.class);
        String result = response.getBody();
        if (StringUtils.isEmpty(result)) {
            return null;
        }
        JSONObject jsonObject = JSON.parseObject(result);
        String resCode = jsonObject.getString("resCode");
        if (ErrorCode.SUCCESS.getCode().equalsIgnoreCase(resCode)) {
            OrderInsuranceResponseVO orderInsuranceResponseVO = new OrderInsuranceResponseVO();
            List<OrderInsuranceVO> exceptedOrderInsuranceList = new ArrayList<OrderInsuranceVO>();
            List<OrderInsuranceVO> realOrderInsuranceList = new ArrayList<OrderInsuranceVO>();
            InsurancePurchaseResultDTO insurancePurchaseResultVO = jsonObject.getObject("data", InsurancePurchaseResultDTO.class);
            List<InsurancePurchaseDTO> insuranceList = insurancePurchaseResultVO.getInsuranceList();
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
                    OrderInsuranceVO orderInsuranceVO = new OrderInsuranceVO();
                    //bean转换
                    BeanUtils.copyProperties(insurance, orderInsuranceVO);
                    if(insurance.getType() == 3){
                        exceptedOrderInsuranceList.add(orderInsuranceVO);
                    }
                    if(insurance.getType() == 1 || insurance.getType() == 2){
                        realOrderInsuranceList.add(orderInsuranceVO);
                    }
                    orderInsuranceResponseVO.setExceptedOrderInsuranceList(exceptedOrderInsuranceList);
                    orderInsuranceResponseVO.setRealOrderInsuranceList(realOrderInsuranceList);
                });
                return orderInsuranceResponseVO;
            }

        }
        return null;
    }

}
