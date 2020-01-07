package com.atzuche.order.coreapi.controller;

import com.atzuche.order.commons.vo.res.OrderResVO;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.autoyol.doc.annotation.AutoDocVersion;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 取消
 *
 * @author pengcheng.fu
 * @date 2020/1/7 11:49
 */

@RequestMapping("/order")
@RestController
@AutoDocVersion(version = "订单接口文档")
public class CancelOrderController {



    @AutoDocMethod(description = "取消订单", value = "取消订单")
    @PostMapping("/normal/cancel")
    public ResponseData<?> cancelOrder() {



        return ResponseData.success();
    }



}
