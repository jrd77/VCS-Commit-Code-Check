package com.atzuche.order.admin.controller;

import com.atzuche.order.commons.entity.dto.RenterGoodsDetailDTO;
import com.atzuche.order.delivery.common.DeliveryErrorCode;
import com.atzuche.order.delivery.entity.OrderCarTrusteeshipEntity;
import com.atzuche.order.delivery.entity.RenterOrderDeliveryEntity;
import com.atzuche.order.delivery.service.OrderCarTrusteeshipService;
import com.atzuche.order.delivery.service.RenterOrderDeliveryService;
import com.atzuche.order.delivery.utils.CommonUtil;
import com.atzuche.order.delivery.vo.trusteeship.OrderCarTrusteeshipReqVO;
import com.atzuche.order.delivery.vo.trusteeship.OrderCarTrusteeshipVO;
import com.atzuche.order.rentercommodity.service.RenterCommodityService;
import com.autoyol.commons.utils.DateUtil;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocGroup;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.autoyol.doc.annotation.AutoDocVersion;
import com.dianping.cat.Cat;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

/**
 * @author 胡春林
 * 托管车控制器
 */
@RestController
@RequestMapping("/console/api")
@Slf4j
public class OrderCarTrusteeshipController extends BaseController {

    @Autowired
    OrderCarTrusteeshipService orderCarTrusteeshipService;
    @Autowired
    RenterCommodityService renterCommodityService;
    @Autowired
    RenterOrderDeliveryService renterOrderDeliveryService;

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
            // 获取租客商品信息
            RenterOrderDeliveryEntity renterOrderDeliveryEntity = renterOrderDeliveryService.findRenterOrderByrOrderNo(orderCarTrusteeshipVO.getOrderNo(), 1);
            RenterGoodsDetailDTO renterGoodsDetailDTO = renterCommodityService.getRenterGoodsDetail(renterOrderDeliveryEntity.getRenterOrderNo(), false);
            if (Objects.nonNull(renterGoodsDetailDTO) && StringUtils.isNotBlank(renterGoodsDetailDTO.getCarStewardPhone())) {
                orderCarTrusteeshipVO.setTrusteeshipTelephone(renterGoodsDetailDTO.getCarStewardPhone());
            }
            OrderCarTrusteeshipEntity orderCarTrusteeshipEntity = new OrderCarTrusteeshipEntity();
            BeanUtils.copyProperties(orderCarTrusteeshipVO, orderCarTrusteeshipEntity);
            orderCarTrusteeshipEntity.setOutDepotTime(DateUtil.asLocalDateTime(orderCarTrusteeshipVO.getOutDepotTime()));
            orderCarTrusteeshipEntity.setInDepotTime(DateUtil.asLocalDateTime(orderCarTrusteeshipVO.getInDepotTime()));
            OrderCarTrusteeshipEntity orderNoAndCar = orderCarTrusteeshipService.selectObjectByOrderNoAndCar(orderCarTrusteeshipVO.getOrderNo(),orderCarTrusteeshipVO.getCarNo());
            if(Objects.nonNull(orderNoAndCar))
            {
                CommonUtil.copyPropertiesIgnoreNull(orderCarTrusteeshipVO,orderNoAndCar);
                orderCarTrusteeshipService.updateOrderCarTrusteeship(orderNoAndCar);
                return ResponseData.success();
            }
            int result = orderCarTrusteeshipService.insertOrderCarTrusteeship(orderCarTrusteeshipEntity);
            if (result > 0) {
                return ResponseData.success();
            } else {
                return ResponseData.error();
            }
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
            OrderCarTrusteeshipEntity orderCarTrusteeshipEntity = orderCarTrusteeshipService.selectObjectByOrderNoAndCar(orderCarTrusteeshipReqVO.getOrderNo(),orderCarTrusteeshipReqVO.getCarNo());
            return ResponseData.success(orderCarTrusteeshipEntity);
        } catch (Exception e) {
            log.error("托管车信息出现异常", e);
            Cat.logError("托管车信息出现异常", e);
            return ResponseData.createErrorCodeResponse(ErrorCode.FAILED.getCode(), "托管车信息出现错误");
        }
    }
}
