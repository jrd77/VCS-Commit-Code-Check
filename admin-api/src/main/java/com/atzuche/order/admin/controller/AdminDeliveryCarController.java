package com.atzuche.order.admin.controller;

import com.atzuche.order.admin.service.AdminDeliveryCarService;
import com.atzuche.order.commons.exceptions.DeliveryOrderException;
import com.atzuche.order.commons.vo.delivery.DeliveryCarRepVO;
import com.atzuche.order.commons.vo.delivery.DeliveryCarVO;
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

import java.util.Objects;


/**
 * @author 胡春林
 * 配送服务接口
 */
@RestController
@RequestMapping("/console/api")
@Slf4j
public class AdminDeliveryCarController extends BaseController {

    @Autowired
    private AdminDeliveryCarService deliveryCarInfoService;

    /**
     * 获取配送信息
     * @param deliveryCarDTO
     * @return
     */
    @AutoDocVersion(version = "管理后台取还车配送服务信息")
    @AutoDocGroup(group = "管理后台取还车配送服务信息")
    @AutoDocMethod(description = "取还车配送", value = "取还车配送",response = DeliveryCarVO.class)
    @PostMapping("/delivery/list")
    public ResponseData<DeliveryCarVO> findDeliveryListByOrderNo(@RequestBody DeliveryCarRepVO deliveryCarDTO) {
        if (null == deliveryCarDTO || StringUtils.isBlank(deliveryCarDTO.getOrderNo())) {
            return ResponseData.createErrorCodeResponse(ErrorCode.ORDER_NO_PARAM_ERROR.getCode(), "租客订单编号为空");
        }
        try {
            DeliveryCarVO deliveryCarRepVO = deliveryCarInfoService.findDeliveryListByOrderNo(deliveryCarDTO);
            if (Objects.nonNull(deliveryCarRepVO)) {
                return ResponseData.success(deliveryCarRepVO);
            }
            return ResponseData.success();
        } catch (DeliveryOrderException ex) {
            DeliveryCarVO deliveryCarVO = new DeliveryCarVO();
            deliveryCarVO.setIsGetCar(0);
            deliveryCarVO.setIsReturnCar(0);
            return ResponseData.success(deliveryCarVO);
        } catch (Exception e) {
            log.error("取还车配送接口出现异常", e);
            Cat.logError("取还车配送接口出现异常", e);
            return ResponseData.error();
        }
    }

    /**
     * 取还车（是否取还车）更新接口
     * @return
     */
    @AutoDocVersion(version = "管理后台取还车更新")
    @AutoDocGroup(group = "管理后台取还车更新")
    @AutoDocMethod(description = "取还车更新", value = "取还车更新",response = ResponseData.class)
    @RequestMapping(value = "/delivery/update", method = RequestMethod.POST)
    public ResponseData<?> updateDeliveryCarInfo(@RequestBody @Validated DeliveryCarVO deliveryCarVO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return validate(bindingResult);
        }
        try {
            deliveryCarInfoService.updateDeliveryCarInfo(deliveryCarVO);
            return ResponseData.success();
        } catch (DeliveryOrderException ex) {
            log.error("配送取还车更新接口有問題", ex);
            Cat.logError("配送取还车更新接口有問題", ex);
            return ResponseData.createErrorCodeResponse(ex.getErrorCode(), ex.getMessage());
        } catch (Exception e) {
            log.error("配送取还车更新接口出现异常", e);
            Cat.logError("配送取还车更新出现异常", e);
            return ResponseData.createErrorCodeResponse(ErrorCode.FAILED.getCode(), "配送取还车更新接口出现错误");
        }
    }


    /**
     * 取还车服务数据更新接口
     * @return
     */
    @AutoDocVersion(version = "管理后台取还车更新")
    @AutoDocGroup(group = "管理后台取还车更新")
    @AutoDocMethod(description = "交接车更新", value = "交接车更新",response = ResponseData.class)
    @RequestMapping(value = "/handover/update", method = RequestMethod.POST)
    public ResponseData<?> updateHandoverCarInfo(@RequestBody @Validated DeliveryCarVO deliveryCarVO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return validate(bindingResult);
        }
        try {
            deliveryCarInfoService.updateHandoverCarInfo(deliveryCarVO);
            return ResponseData.success();
        } catch (Exception e) {
            log.error("取还车更新接口出现异常", e);
            Cat.logError("取还车更新接口出现异常", e);
            return ResponseData.createErrorCodeResponse(ErrorCode.FAILED.getCode(), "取还车更新接口出现错误");
        }
    }

}
