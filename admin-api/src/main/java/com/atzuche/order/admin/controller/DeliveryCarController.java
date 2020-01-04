package com.atzuche.order.admin.controller;

import com.atzuche.order.admin.service.DeliveryCarInfoService;
import com.atzuche.order.admin.service.HandoverCarInfoService;
import com.atzuche.order.admin.vo.rep.delivery.RenterDeliveryCarVO;
import com.atzuche.order.admin.vo.req.DeliveryCarRepVO;
import com.atzuche.order.admin.vo.req.delivery.CarConditionPhotoUploadVO;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocGroup;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.autoyol.doc.annotation.AutoDocVersion;
import com.dianping.cat.Cat;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * @author 胡春林
 * 配送服务接口
 */
@RestController
@RequestMapping("/api")
@Slf4j
public class DeliveryCarController extends BaseController {

    @Autowired
    private DeliveryCarInfoService deliveryCarInfoService;
    @Autowired
    HandoverCarInfoService handoverCarInfoService;

    /**
     * 获取配送信息
     * @param deliveryCarDTO
     * @return
     */
    @AutoDocVersion(version = "管理后台取还车配送服务信息")
    @AutoDocGroup(group = "管理后台取还车配送服务信息")
    @AutoDocMethod(description = "取还车配送", value = "取还车配送",response = RenterDeliveryCarVO.class)
    @PostMapping("/delivery/list")
    public ResponseData<?> findDeliveryListByOrderNo(@RequestBody DeliveryCarRepVO deliveryCarDTO) {
        if (null == deliveryCarDTO || StringUtils.isBlank(deliveryCarDTO.getRenterOrderNo())) {
            return ResponseData.createErrorCodeResponse(ErrorCode.ORDER_NO_PARAM_ERROR.getCode(), "租客子订单编号为空");
        }
        DeliveryCarRepVO deliveryCarRepVO = deliveryCarInfoService.findDeliveryListByOrderNo(deliveryCarDTO);
        return ResponseData.success(deliveryCarRepVO);
    }

    /**
     * 交接车照片上传
     * @return
     */
    @AutoDocVersion(version = "管理后台交接车照片上传")
    @AutoDocGroup(group = "管理后台交接车照片上传")
    @AutoDocMethod(description = "交接车照片上传", value = "交接车照片上传",response = ResponseData.class)
    @RequestMapping(value = "/photo/upload", method = RequestMethod.POST)
    public ResponseData<?> upload(@RequestBody @Validated CarConditionPhotoUploadVO photoUploadReqVo, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return validate(bindingResult);
        }
        try {
            boolean result = handoverCarInfoService.uploadByOrderNo(photoUploadReqVo);
            return ResponseData.success(result);
        } catch (Exception e) {
            log.error("交接车照片上传接口出现异常", e);
            Cat.logError("交接车照片上传接口出现异常", e);
            return ResponseData.createErrorCodeResponse(ErrorCode.FAILED.getCode(), "交接车照片上传接口出现错误");
        }
    }




}
