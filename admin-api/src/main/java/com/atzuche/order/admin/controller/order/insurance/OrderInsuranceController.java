package com.atzuche.order.admin.controller.order.insurance;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atzuche.order.admin.cat.CatLogRecord;
import com.atzuche.order.admin.common.AdminUserUtil;
import com.atzuche.order.admin.constant.cat.UrlConstant;
import com.atzuche.order.admin.constant.description.DescriptionConstant;
import com.atzuche.order.admin.controller.BaseController;
import com.atzuche.order.admin.description.LogDescription;
import com.atzuche.order.admin.dto.InsurancePurchaseDTO;
import com.atzuche.order.admin.dto.InsurancePurchaseResultDTO;
import com.atzuche.order.admin.dto.OrderInsuranceAdditionRequestDTO;
import com.atzuche.order.admin.dto.OrderInsuranceImportRequestDTO;
import com.atzuche.order.admin.enums.insurance.InsuranceCompanyTypeEnum;
import com.atzuche.order.admin.enums.insurance.InsuranceInputTypeEnum;
import com.atzuche.order.admin.exception.insurance.OrderInsuranceException;
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
import java.util.ArrayList;
import java.util.List;

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
        return builder.build();
    }
    @Autowired
    private RestTemplate restTemplate;

    @Value("${auto.insurance.purchase.url}")
    private String insurancePurchaseUrl;


	@AutoDocMethod(description = "购买保险列表", value = "购买保险列表", response = OrderInsuranceResponseVO.class)
	@GetMapping("/list")
	public ResponseData<OrderInsuranceResponseVO> list(OrderInsuranceRequestVO orderInsuranceRequestVO, BindingResult bindingResult) {
        //参数验证
        validateParameter(bindingResult);
        try{
            logger.info(LogDescription.getLogDescription(DescriptionConstant.CONSOLE_ORDER_INSURANCE_LIST, DescriptionConstant.INPUT_TEXT),orderInsuranceRequestVO.toString());
            CatLogRecord.successLog(LogDescription.getCatDescription(DescriptionConstant.CONSOLE_ORDER_INSURANCE_LIST, DescriptionConstant.SUCCESS_TEXT), UrlConstant.CONSOLE_ORDER_INSURANCE_LIST, orderInsuranceRequestVO);
            return ResponseData.success(getPurchaseList(orderInsuranceRequestVO));
        } catch (Exception e) {
            logger.info(LogDescription.getLogDescription(DescriptionConstant.CONSOLE_ORDER_INSURANCE_LIST, DescriptionConstant.EXCEPTION_TEXT),e);
            CatLogRecord.failLog(LogDescription.getCatDescription(DescriptionConstant.CONSOLE_ORDER_INSURANCE_LIST, DescriptionConstant.EXCEPTION_TEXT), UrlConstant.CONSOLE_ORDER_INSURANCE_LIST, orderInsuranceRequestVO, e);
            throw new OrderInsuranceException(ErrorCode.SYS_ERROR.getCode(),ErrorCode.SYS_ERROR.getText());
        }

	}

    @AutoDocMethod(description = "手工录入保险信息", value = "手工录入保险信息", response = ResponseData.class)
    @PostMapping("/add")
    public ResponseData<ResponseData> add(@RequestBody OrderInsuranceAdditionRequestVO orderInsuranceAdditionRequestVO, BindingResult bindingResult) {
        //参数验证
        validateParameter(bindingResult);
        try{

            logger.info(LogDescription.getLogDescription(DescriptionConstant.CONSOLE_ORDER_INSURANCE_ADD, DescriptionConstant.INPUT_TEXT),orderInsuranceAdditionRequestVO.toString());
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
                CatLogRecord.successLog(LogDescription.getCatDescription(DescriptionConstant.CONSOLE_ORDER_INSURANCE_ADD, DescriptionConstant.SUCCESS_TEXT), UrlConstant.CONSOLE_ORDER_INSURANCE_ADD, orderInsuranceAdditionRequestVO);
                return ResponseData.success();
            } else {
                CatLogRecord.failLog(LogDescription.getCatDescription(DescriptionConstant.CONSOLE_ORDER_INSURANCE_ADD, DescriptionConstant.EXCEPTION_TEXT), UrlConstant.CONSOLE_ORDER_INSURANCE_ADD, orderInsuranceAdditionRequestVO, new OrderInsuranceException(resCode, jsonObject.getString(RES_MSG)));
                return new ResponseData<>(resCode, jsonObject.getString(RES_MSG));
            }
        } catch (Exception e) {
            logger.info(LogDescription.getLogDescription(DescriptionConstant.CONSOLE_ORDER_INSURANCE_ADD, DescriptionConstant.EXCEPTION_TEXT),e);
            CatLogRecord.failLog(LogDescription.getCatDescription(DescriptionConstant.CONSOLE_ORDER_INSURANCE_ADD, DescriptionConstant.EXCEPTION_TEXT), UrlConstant.CONSOLE_ORDER_INSURANCE_ADD, orderInsuranceAdditionRequestVO, e);

            throw new OrderInsuranceException(ErrorCode.SYS_ERROR.getCode(),ErrorCode.SYS_ERROR.getText());
        }

    }


    @AutoDocMethod(description = "导入保险信息excel", value = "导入保险信息excel", response = ResponseData.class)
    @PostMapping("/import")
    public ResponseData importExcel(@RequestParam("batchFile") MultipartFile batchFile, OrderInsuranceImportRequestVO orderInsuranceImportRequestVO, BindingResult bindingResult) {
        //参数验证
        validateParameter(bindingResult);
        try{
            logger.info("导入保险信息excel入参:{}",orderInsuranceImportRequestVO.toString());
            if(!batchFile.isEmpty()) {
                String fileType = batchFile.getContentType();
                if (!fileType.equals(SHEET_TYPE) && !fileType.equals(XLS_TYPE)&& !fileType.equals(XLSX_TYPE)) {
                    return new ResponseData<>(ErrorCode.INPUT_ERROR.getCode(), "上传文件类型错误，必须为xls或xlsx！");
                } else {
                    String str=DateUtils.formate(LocalDateTime.now(),DateUtils.DATE_DEFAUTE);
                    String suffixes=batchFile.getOriginalFilename().substring(batchFile.getOriginalFilename().lastIndexOf("."), batchFile.getOriginalFilename().length());
                    String key = "console/import/insurance/"+str.substring(0, 8)+"/"+ CommonUtils.getRandomNumUpChar(8)+suffixes;
                    //上传文件
                    OSSUtils.uploadMultipartFile(key, batchFile);
                    OrderInsuranceImportRequestDTO orderInsuranceImportRequestDTO = new OrderInsuranceImportRequestDTO();
                    orderInsuranceImportRequestDTO.setOssFileKey(key);
                    orderInsuranceImportRequestDTO.setCreateOp(AdminUserUtil.getAdminUser().getAuthName());
                    BeanUtils.copyProperties(orderInsuranceImportRequestVO, orderInsuranceImportRequestDTO);
                    ResponseEntity<String> responseEntity = restTemplate.postForEntity(insurancePurchaseUrl+IMPORT_PURCHASE, orderInsuranceImportRequestDTO,String.class);
                    String body = responseEntity.getBody();
                    if (!StringUtils.isEmpty(body)) {
                        JSONObject jsonObject = JSON.parseObject(body);
                        if(!ErrorCode.SUCCESS.getCode().equals(jsonObject.getString(RES_CODE))){
                            return ResponseData.success();
                        }
                        //调接口保存数据
                        return new ResponseData<>(ErrorCode.INPUT_ERROR.getCode(), "文件导入中，请稍候查询");
                    }
                    return ResponseData.error();
                }
            }else{
                return new ResponseData<>(ErrorCode.INPUT_ERROR.getCode(), "请选择上传文件！");
            }
        } catch (Exception e) {
            logger.info("导入保险信息excel异常{}",e);
            throw new OrderInsuranceException(ErrorCode.SYS_ERROR.getCode(),ErrorCode.SYS_ERROR.getText());
        }

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
                    if (InsuranceInputTypeEnum.IMPORT.getType().equals(String.valueOf(insurance.getType()))) {
                        insurance.setInsuranceCompany(InsuranceCompanyTypeEnum.getDescriptionByType(insurance.getInsuranceCompany()));
                    }
                    OrderInsuranceVO orderInsuranceVO = new OrderInsuranceVO();
                    //bean转换
                    BeanUtils.copyProperties(insurance, orderInsuranceVO);
                    orderInsuranceVO.setType(InsuranceInputTypeEnum.getDescriptionByType(String.valueOf(insurance.getType())));
                    if(InsuranceInputTypeEnum.BI_IMPORT.getType().equals(String.valueOf(insurance.getType()))){
                        exceptedOrderInsuranceList.add(orderInsuranceVO);
                    }
                    if(InsuranceInputTypeEnum.MANUAL.getType().equals(String.valueOf(insurance.getType())) || InsuranceInputTypeEnum.IMPORT.getType().equals(String.valueOf(insurance.getType()))){
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


    /**
     * 验证参数
     * @param bindingResult
     */
    private void validateParameter(BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            throw new OrderInsuranceException(ErrorCode.PARAMETER_ERROR.getCode(),bindingResult.getFieldError().getDefaultMessage());
        }
    }

}
