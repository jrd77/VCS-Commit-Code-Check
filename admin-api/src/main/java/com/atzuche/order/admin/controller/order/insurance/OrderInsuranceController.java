package com.atzuche.order.admin.controller.order.insurance;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atzuche.order.admin.common.AdminUserUtil;
import com.atzuche.order.admin.controller.BaseController;
import com.atzuche.order.admin.dto.InsurancePurchaseDTO;
import com.atzuche.order.admin.dto.InsurancePurchaseResultDTO;
import com.atzuche.order.admin.dto.OrderInsuranceAdditionRequestDTO;
import com.atzuche.order.admin.dto.OrderInsuranceImportRequestDTO;
import com.atzuche.order.admin.enums.InsuranceCompanyTypeEnum;
import com.atzuche.order.admin.enums.InsuranceInputTypeEnum;
import com.atzuche.order.admin.util.CommonUtils;
import com.atzuche.order.admin.util.oss.OSSUtils;
import com.atzuche.order.admin.vo.req.insurance.OrderInsuranceAdditionRequestVO;
import com.atzuche.order.admin.vo.req.insurance.OrderInsuranceImportRequestVO;
import com.atzuche.order.admin.vo.req.insurance.OrderInsuranceRequestVO;
import com.atzuche.order.admin.vo.resp.insurance.OrderInsuranceResponseVO;
import com.atzuche.order.admin.vo.resp.insurance.OrderInsuranceVO;
import com.atzuche.order.delivery.utils.DateUtils;
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

import java.time.LocalDateTime;
import java.util.*;

@RequestMapping("/console/order/insurance")
@RestController
@AutoDocVersion(version = "购买保险接口文档")
public class OrderInsuranceController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(OrderInsuranceController.class);
    public static final String GET_PURCHASE_BY_ORDERNO = "insurance/purchase/getByOrderNo/";
    public static final String ADD_PURCHASE = "insurance/purchase/manual/add";
    public static final String IMPORT_PURCHASE = "insurance/purchase/addImportTask";
    public static final String RES_CODE = "resCode";
    public static final String RES_MSG = "resMsg";
    public static final String DATA = "data";
    public static final String SHEET_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    public static final String XLS_TYPE = "application/vnd.ms-excel";
    public static final String XLSX_TYPE = "application/x-excel";

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
	public ResponseData<OrderInsuranceResponseVO> list(OrderInsuranceRequestVO orderInsuranceRequestVO, BindingResult bindingResult) {
        validate(bindingResult);
		return ResponseData.success(getPurchaseList(orderInsuranceRequestVO));
	}

    @AutoDocMethod(description = "手工录入保险信息", value = "手工录入保险信息", response = ResponseData.class)
    @PostMapping("/add")
    public ResponseData<ResponseData> add(@RequestBody OrderInsuranceAdditionRequestVO orderInsuranceAdditionRequestVO, BindingResult bindingResult) {
        validate(bindingResult);
        logger.info("手工录入保险信息");
        try{
            OrderInsuranceAdditionRequestDTO orderInsuranceAdditionRequestDTO = new OrderInsuranceAdditionRequestDTO();
            //属性拷贝
            BeanUtils.copyProperties(orderInsuranceAdditionRequestVO,orderInsuranceAdditionRequestDTO);
            //获取操作人
            orderInsuranceAdditionRequestDTO.setInsuranceCompany(InsuranceCompanyTypeEnum.getDescriptionByType(orderInsuranceAdditionRequestVO.getInsuranceCompanyType()));
            orderInsuranceAdditionRequestDTO.setOperator(AdminUserUtil.getAdminUser().getAuthName());
            orderInsuranceAdditionRequestDTO.setInsuranceDate(DateUtils.formate(LocalDateTime.now(),DateUtils.DATE_DEFAUTE_4));
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(insurancePurchaseUrl + ADD_PURCHASE, orderInsuranceAdditionRequestDTO, String.class);
            String result = responseEntity.getBody();
            if (StringUtils.isEmpty(result)) {
                return ResponseData.error();
            }
            JSONObject jsonObject = JSON.parseObject(result);
            String resCode = jsonObject.getString(RES_CODE);
            if (ErrorCode.SUCCESS.getCode().equalsIgnoreCase(resCode)) {
                return ResponseData.success(null);
            } else {
                return new ResponseData<>(resCode, jsonObject.getString(RES_MSG));
            }
        } catch (Exception e) {
            logger.info("手工录入保险信息",e);
        }
	    return null;

    }


    @AutoDocMethod(description = "导入保险信息excel", value = "导入保险信息excel", response = ResponseData.class)
    @PostMapping("/import")
    public ResponseData<ResponseData> importExcel(@RequestParam("batchFile") MultipartFile batchFile, OrderInsuranceImportRequestVO orderInsuranceImportRequestVO, BindingResult bindingResult) {
        validate(bindingResult);
        try {
            if(!batchFile.isEmpty()) {
                String fileType = batchFile.getContentType();
                if (!fileType.equals(SHEET_TYPE) && !fileType.equals(XLS_TYPE)&& !fileType.equals(XLSX_TYPE)) {
                    return new ResponseData<>(ErrorCode.INPUT_ERROR.getCode(), "上传文件类型错误，必须为xls或xlsx！");
                } else {
                    String str=DateUtils.formate(LocalDateTime.now(),DateUtils.DATE_DEFAUTE);
                    String suffixes=batchFile.getOriginalFilename().substring(batchFile.getOriginalFilename().lastIndexOf("."), batchFile.getOriginalFilename().length());
   ;
                    String key = "console/import/insurance/"+str.substring(0, 8)+"/"+ CommonUtils.getRandomNumUpChar(8)+suffixes;
                    //上传文件

                    OSSUtils.uploadMultipartFile(key, batchFile);
                    OrderInsuranceImportRequestDTO orderInsuranceImportRequestDTO = new OrderInsuranceImportRequestDTO();
                    orderInsuranceImportRequestDTO.setOssFileKey(key);
                    orderInsuranceImportRequestDTO.setCreateOp("test");
                    BeanUtils.copyProperties(orderInsuranceImportRequestVO, orderInsuranceImportRequestDTO);
                    ResponseEntity<String> responseEntity = restTemplate.postForEntity(insurancePurchaseUrl+IMPORT_PURCHASE, orderInsuranceImportRequestDTO,String.class);
                    String body = responseEntity.getBody();
                    if (!StringUtils.isEmpty(body)) {
                        JSONObject jsonObject = JSON.parseObject(body);
                        if(!"000000".equals(jsonObject.getString(RES_CODE))){
                            return ResponseData.success(null);
                        }
                        //调接口保存数据
                        return new ResponseData<>(ErrorCode.INPUT_ERROR.getCode(), "文件导入中，请稍候查询");
                    }
                }
            }else{
                return new ResponseData<>(ErrorCode.INPUT_ERROR.getCode(), "请选择上传文件！");
            }
        } catch (Exception e) {
            logger.error("上传保险信息失败：", e);
            return new ResponseData<>(ErrorCode.INPUT_ERROR.getCode(), "上传失败，请稍后再试");
        }
        return ResponseData.error();
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
        String resCode = jsonObject.getString(RES_CODE);
        if (ErrorCode.SUCCESS.getCode().equalsIgnoreCase(resCode)) {
            OrderInsuranceResponseVO orderInsuranceResponseVO = new OrderInsuranceResponseVO();
            List<OrderInsuranceVO> exceptedOrderInsuranceList = new ArrayList<OrderInsuranceVO>();
            List<OrderInsuranceVO> realOrderInsuranceList = new ArrayList<OrderInsuranceVO>();
            InsurancePurchaseResultDTO insurancePurchaseResultVO = jsonObject.getObject(DATA, InsurancePurchaseResultDTO.class);
            List<InsurancePurchaseDTO> insuranceList = insurancePurchaseResultVO.getInsuranceList();
            if(CollectionUtils.isNotEmpty(insuranceList)) {
                insuranceList.forEach(insurance -> {
                    if (insurance.getType() == 2) {
                        insurance.setInsuranceCompany(InsuranceCompanyTypeEnum.getDescriptionByType(insurance.getInsuranceCompany()));
                    }
                    OrderInsuranceVO orderInsuranceVO = new OrderInsuranceVO();
                    //bean转换
                    BeanUtils.copyProperties(insurance, orderInsuranceVO);
                    orderInsuranceVO.setType(InsuranceInputTypeEnum.getDescriptionByType(String.valueOf(insurance.getType())));
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
