package com.atzuche.order.coreapi.controller;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.BindingResultUtil;
import com.atzuche.order.commons.LocalDateTimeUtils;
import com.atzuche.order.commons.vo.req.AdminGetDisCouponListReqVO;
import com.atzuche.order.commons.vo.req.NormalOrderCostCalculateReqVO;
import com.atzuche.order.commons.vo.req.OrderReqVO;
import com.atzuche.order.commons.vo.res.AdminGetDisCouponListResVO;
import com.atzuche.order.commons.vo.res.NormalOrderCostCalculateResVO;
import com.atzuche.order.coreapi.service.SubmitOrderBeforeCostCalService;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.autoyol.doc.annotation.AutoDocVersion;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 *
 *
 * @author pengcheng.fu
 * @date 2020/1/11 14:10
 */

@RequestMapping("/order")
@RestController
@AutoDocVersion(version = "订单接口文档")
public class SubmitOrderBeforeCostCalculateController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SubmitOrderBeforeCostCalculateController.class);


    @Autowired
    private SubmitOrderBeforeCostCalService submitOrderBeforeCostCalService;


    @AutoDocMethod(description = "提交订单前费用计算", value = "提交订单前费用计算", response = NormalOrderCostCalculateResVO.class)
    @PostMapping("/normal/pre/cost/calculate")
    public ResponseData<NormalOrderCostCalculateResVO> submitOrderBeforeCostCalculate(@Valid @RequestBody NormalOrderCostCalculateReqVO reqVO,
                                                                                      BindingResult bindingResult) {
        LOGGER.info("Submit order before cost calculate.param is,reqVO:[{}]", JSON.toJSONString(reqVO));
        BindingResultUtil.checkBindingResult(bindingResult);
        String memNo = reqVO.getMemNo();
        if (StringUtils.isBlank(memNo)) {
            return new ResponseData<>(ErrorCode.NEED_LOGIN.getCode(), ErrorCode.NEED_LOGIN.getText());
        }

        BeanCopier beanCopier = BeanCopier.create(NormalOrderCostCalculateReqVO.class, OrderReqVO.class, false);
        OrderReqVO orderReqVO = new OrderReqVO();
        beanCopier.copy(reqVO, orderReqVO, null);
        orderReqVO.setAbatement(Integer.valueOf(reqVO.getAbatement()));
        orderReqVO.setRentTime(LocalDateTimeUtils.parseStringToDateTime(reqVO.getRentTime(),
                LocalDateTimeUtils.DEFAULT_PATTERN));
        orderReqVO.setRevertTime(LocalDateTimeUtils.parseStringToDateTime(reqVO.getRevertTime(),
                LocalDateTimeUtils.DEFAULT_PATTERN));

        LOGGER.info("Submit order before cost calculate.conversion param is,orderReqVO:[{}]", JSON.toJSONString(orderReqVO));
        NormalOrderCostCalculateResVO resVO = submitOrderBeforeCostCalService.costCalculate(orderReqVO);
        LOGGER.info("Submit order before cost calculate.result is,resVO:[{}]", JSON.toJSONString(resVO));
        return ResponseData.success(resVO);
    }


    /**
     * 获取订单内租客优惠抵扣信息
     * <p>可用平台券列表</p>
     * <p>可用送取服务券列表</p>
     * <p>可用车主券列表</p>
     * <p>可用钱包余额</p>
     *
     * @param adminGetDisCouponListReqVo 请求参数
     * @param bindingResult 参数校验结果
     * @return ResponseData<AdminGetDisCouponListResVO> 返回信息
     */
    @AutoDocMethod(description = "获取订单内租客优惠抵扣信息", value = "获取订单内租客优惠抵扣信息", response = AdminGetDisCouponListResVO.class)
    @PostMapping("/admin/queryDisCouponByOrderNo")
    public ResponseData<AdminGetDisCouponListResVO> getDisCouponListByOrderNo(@Valid @RequestBody AdminGetDisCouponListReqVO adminGetDisCouponListReqVo,
                                          BindingResult bindingResult){
        LOGGER.info("Get a list of tenant coupons.param is,adminGetDisCouponListReqVo:[{}]", JSON.toJSONString(adminGetDisCouponListReqVo));
        BindingResultUtil.checkBindingResult(bindingResult);



        return ResponseData.success(null);

    }


}
