package com.atzuche.order.admin.controller;

import com.atzuche.order.admin.service.DeliveryRemoteService;
import com.atzuche.order.commons.vo.delivery.OrderCarTrusteeshipEntity;
import com.atzuche.order.commons.vo.delivery.OrderCarTrusteeshipReqVO;
import com.atzuche.order.commons.vo.delivery.OrderCarTrusteeshipVO;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocGroup;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.autoyol.doc.annotation.AutoDocVersion;
import com.dianping.cat.Cat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 胡春林
 * 托管车控制器
 */
@RestController
@RequestMapping("/console/api")
@Slf4j
public class AdminOrderCarTrusteeshipController extends BaseController {

	@Autowired
    private DeliveryRemoteService deliveryRemoteService;
	
    /**
     * 托管车数据新增接口
     *
     * @return
     */
    @AutoDocVersion(version = "管理后台托管车新增")
    @AutoDocGroup(group = "管理后台托管车新增")
    @AutoDocMethod(description = "托管车新增", value = "托管车新增", response = ResponseData.class)
    @RequestMapping(value = "/trusteeship/add", method = RequestMethod.POST)
    public ResponseData<?> addOrderCarTrusteeship(@RequestBody @Validated OrderCarTrusteeshipVO orderCarTrusteeshipVO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return validate(bindingResult);
        }
        try {
        	deliveryRemoteService.addOrderCarTrusteeship(orderCarTrusteeshipVO);
        	return ResponseData.success();
        } catch (Exception e) {
            log.error("取还车更新接口出现异常", e);
            Cat.logError("取还车更新接口出现异常", e);
            return ResponseData.createErrorCodeResponse(ErrorCode.FAILED.getCode(), "取还车更新接口出现错误");
        }
    }

    /**
     * 托管车数据新增接口
     * @return
     */
    @AutoDocVersion(version = "管理后台托管车信息")
    @AutoDocGroup(group = "管理后台托管车信息")
    @AutoDocMethod(description = "托管车信息", value = "托管车信息", response = OrderCarTrusteeshipEntity.class)
    @RequestMapping(value = "/trusteeship/get", method = RequestMethod.POST)
    public ResponseData<?> getOrderCarTrusteeship(@RequestBody @Validated OrderCarTrusteeshipReqVO orderCarTrusteeshipReqVO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return validate(bindingResult);
        }
        try {
        	OrderCarTrusteeshipEntity orderCarTrusteeshipEntity = deliveryRemoteService.getOrderCarTrusteeshipEntity(orderCarTrusteeshipReqVO);
            return ResponseData.success(orderCarTrusteeshipEntity);
        } catch (Exception e) {
            log.error("托管车信息出现异常", e);
            Cat.logError("托管车信息出现异常", e);
            return ResponseData.createErrorCodeResponse(ErrorCode.FAILED.getCode(), "托管车信息出现错误");
        }
    }
}
