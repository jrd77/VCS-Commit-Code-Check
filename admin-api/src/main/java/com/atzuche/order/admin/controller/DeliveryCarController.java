package com.atzuche.order.admin.controller;

import com.atzuche.order.admin.service.DeliveryCarInfoService;
import com.atzuche.order.admin.vo.req.DeliveryCarRepVO;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 胡春林
 * 配送服务接口
 */
@RestController
@RequestMapping("/api")
public class DeliveryCarController {

    @Autowired
    private DeliveryCarInfoService deliveryCarInfoService;

    @PostMapping("/delivery/list")
    public ResponseData<?> findDeliveryListByOrderNo(@RequestBody DeliveryCarRepVO deliveryCarDTO) {
        if (null == deliveryCarDTO || StringUtils.isBlank(deliveryCarDTO.getRenterOrderNo())) {
            return ResponseData.createErrorCodeResponse(ErrorCode.ORDER_NO_PARAM_ERROR.getCode(), "租客子订单编号为空");
        }
        DeliveryCarRepVO deliveryCarRepVO = deliveryCarInfoService.findDeliveryListByOrderNo(deliveryCarDTO);
        return ResponseData.success(deliveryCarRepVO);
    }


}
