package com.atzuche.order.coreapi.controller;

import com.atzuche.order.commons.vo.req.NormalOrderCostCalculateReqVO;
import com.atzuche.order.commons.vo.req.NormalOrderReqVO;
import com.atzuche.order.commons.vo.res.NormalOrderCostCalculateResVO;
import com.atzuche.order.commons.vo.res.OrderResVO;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.autoyol.doc.annotation.AutoDocVersion;
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


    @AutoDocMethod(description = "提交订单前费用计算", value = "提交订单前费用计算", response = NormalOrderCostCalculateResVO.class)
    @PostMapping("/normal/pre/cost/calculate")
    public ResponseData<NormalOrderCostCalculateResVO> submitOrderBeforeCostCalculate(@Valid @RequestBody NormalOrderCostCalculateReqVO reqVO,
                                                                                      BindingResult bindingResult) {





        return ResponseData.success(new NormalOrderCostCalculateResVO());

    }



}
