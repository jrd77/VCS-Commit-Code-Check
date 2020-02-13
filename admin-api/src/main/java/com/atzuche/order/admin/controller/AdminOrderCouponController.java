package com.atzuche.order.admin.controller;

import com.atzuche.order.commons.vo.req.AdminGetDisCouponListReqVO;
import com.atzuche.order.commons.vo.res.AdminGetDisCouponListResVO;
import com.atzuche.order.open.service.FeignOrderCouponService;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.autoyol.doc.annotation.AutoDocVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author pengcheng.fu
 * 订单详细信息  优惠券  优惠券列表
 */
@RequestMapping("/console/orderCoupon/")
@RestController
@AutoDocVersion(version = "订单详细信息  优惠券  优惠券列表接口文档")
public class AdminOrderCouponController {

    private static final Logger logger = LoggerFactory.getLogger(AdminOrderCouponController.class);

    @Autowired
    private FeignOrderCouponService feignOrderCouponService;

    /**
     * 获取订单内租客优惠抵扣信息
     *
     * @param req
     * @return ResponseData<AdminGetDisCouponListResVO>
     */
    @AutoDocMethod(value = "获取订单内租客优惠抵扣信息接口", description = "获取订单内租客优惠抵扣信息接口", response = AdminGetDisCouponListResVO.class)
    @PostMapping("queryDisCouponByOrderNo")
    public ResponseData<AdminGetDisCouponListResVO> queryDisCouponByOrderNo(@Valid @RequestBody AdminGetDisCouponListReqVO req, BindingResult bindingResult) {
        logger.info("AdminOrderCouponController queryDisCouponByOrderNo start. param [{}]", req);
        if (bindingResult.hasErrors()) {
            return new ResponseData<>(ErrorCode.INPUT_ERROR.getCode(), ErrorCode.INPUT_ERROR.getText());
        }
        ResponseData<AdminGetDisCouponListResVO>  result = feignOrderCouponService.getDisCouponListByOrderNo(req);
        logger.info("AdminOrderCouponController queryDisCouponByOrderNo end. result [{}]",result);
        return result;
    }


}
