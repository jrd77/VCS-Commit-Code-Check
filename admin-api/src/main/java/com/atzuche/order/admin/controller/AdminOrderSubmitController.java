package com.atzuche.order.admin.controller;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.admin.constant.AdminOpTypeEnum;
import com.atzuche.order.admin.service.OrderSubmitService;
import com.atzuche.order.admin.service.log.AdminLogService;
import com.atzuche.order.admin.vo.req.orderSubmit.AdminTransReqVO;
import com.atzuche.order.commons.vo.res.OrderResVO;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.autoyol.doc.annotation.AutoDocVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RequestMapping("/console/order")
@RestController
@AutoDocVersion(version = "下单")
public class AdminOrderSubmitController {
    @Autowired
    private OrderSubmitService orderSubmitService;

    @Autowired
    private AdminLogService adminLogService;
    
    private final static Logger logger = LoggerFactory.getLogger(AdminOrderSubmitController.class);
    


    @AutoDocMethod(description = "下单", value = "下单", response = OrderResVO.class)
    @PostMapping("/submit")
    public ResponseData<OrderResVO> submit(@Valid @RequestBody AdminTransReqVO adminOrderReqVO, BindingResult bindingResult, HttpServletRequest request) throws Exception {
        if (bindingResult.hasErrors()) {
            return new ResponseData<>(ErrorCode.INPUT_ERROR.getCode(), ErrorCode.INPUT_ERROR.getText());
        }
        OrderResVO orderResVO = orderSubmitService.submit(adminOrderReqVO,request);
        try{
            adminLogService.insertLog(AdminOpTypeEnum.SUBMIT_ORDER,orderResVO.getOrderNo(), JSON.toJSONString(adminOrderReqVO));
        }catch (Exception e){
            logger.warn("后台日志记录错误",e);
        }

        return ResponseData.success(orderResVO);
    }


}
