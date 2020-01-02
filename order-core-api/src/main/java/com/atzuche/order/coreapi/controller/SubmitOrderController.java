package com.atzuche.order.coreapi.controller;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.OrderException;
import com.atzuche.order.commons.vo.req.AdminOrderReqVO;
import com.atzuche.order.commons.vo.req.NormalOrderReqVO;
import com.atzuche.order.commons.vo.req.OrderReqVO;
import com.atzuche.order.commons.vo.res.AdminOrderResVO;
import com.atzuche.order.commons.vo.res.NormalOrderResVO;
import com.atzuche.order.commons.vo.res.OrderResVO;
import com.atzuche.order.coreapi.service.SubmitOrderService;
import com.atzuche.order.parentorder.entity.OrderRecordEntity;
import com.atzuche.order.parentorder.service.OrderRecordService;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.autoyol.doc.annotation.AutoDocVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 下单
 *
 * @author pengcheng.fu
 * @date 2019/12/23 12:00
 */
@RequestMapping("/order")
@RestController
@AutoDocVersion(version = "普通订单下单接口文档")
public class SubmitOrderController {
    private static final Logger LOGGER = LoggerFactory.getLogger(SubmitOrderController.class);

    @Resource
    private SubmitOrderService submitOrderService;
    @Autowired
    private OrderRecordService orderRecordService;


    @AutoDocMethod(description = "提交订单", value = "提交订单", response = NormalOrderResVO.class)
    @PostMapping("/normal/req")
    public ResponseData<OrderResVO> submitOrder(@RequestBody NormalOrderReqVO normalOrderReqVO, BindingResult bindingResult) throws Exception {
        LOGGER.info("Submit order.param is,normalOrderReqVO:[{}]", JSON.toJSONString(normalOrderReqVO));
        if (bindingResult.hasErrors()) {
            return new ResponseData<>(ErrorCode.INPUT_ERROR.getCode(), ErrorCode.INPUT_ERROR.getText());
        }
        String memNo = normalOrderReqVO.getMemNo();
        if (null == memNo) {
            return new ResponseData<>(ErrorCode.NEED_LOGIN.getCode(), ErrorCode.NEED_LOGIN.getText());
        }
        OrderResVO orderResVO = null;
        try{
            BeanCopier beanCopier = BeanCopier.create(NormalOrderReqVO.class, OrderReqVO.class, false);
            OrderReqVO orderReqVO = new OrderReqVO();
            beanCopier.copy(normalOrderReqVO, orderReqVO, null);
            orderResVO = submitOrderService.submitOrder(orderReqVO);
            OrderRecordEntity orderRecordEntity = new OrderRecordEntity();
            orderRecordEntity.setErrorCode(ErrorCode.SUCCESS.getCode());
            orderRecordEntity.setErrorTxt(ErrorCode.SUCCESS.getText());
            orderRecordEntity.setMemNo(normalOrderReqVO.getMemNo());
            orderRecordEntity.setOrderNo(orderResVO.getOrderNo());
            orderRecordEntity.setParam(JSON.toJSONString(normalOrderReqVO));
            orderRecordEntity.setResult(JSON.toJSONString(orderResVO));
            orderRecordService.save(orderRecordEntity);
        }catch(OrderException orderException){
            OrderRecordEntity orderRecordEntity = new OrderRecordEntity();
            orderRecordEntity.setErrorCode(orderException.getErrorCode());
            orderRecordEntity.setErrorTxt(orderException.getErrorMsg());
            orderRecordEntity.setMemNo(normalOrderReqVO.getMemNo());
            orderRecordEntity.setOrderNo(orderResVO.getOrderNo());
            orderRecordEntity.setParam(JSON.toJSONString(normalOrderReqVO));
            orderRecordEntity.setResult(JSON.toJSONString(orderResVO));
            orderRecordService.save(orderRecordEntity);
            throw orderException;
        }catch (Exception e){
            OrderRecordEntity orderRecordEntity = new OrderRecordEntity();
            orderRecordEntity.setErrorCode(ErrorCode.SYS_ERROR.getCode());
            orderRecordEntity.setErrorTxt(ErrorCode.SYS_ERROR.getText());
            orderRecordEntity.setMemNo(normalOrderReqVO.getMemNo());
            orderRecordEntity.setOrderNo(orderResVO.getOrderNo());
            orderRecordEntity.setParam(JSON.toJSONString(normalOrderReqVO));
            orderRecordEntity.setResult(JSON.toJSONString(orderResVO));
            orderRecordService.save(orderRecordEntity);
            throw e;
        }
        return ResponseData.success(orderResVO);
    }



    @AutoDocMethod(description = "管理后台提交订单", value = "管理后台提交订单", response = AdminOrderResVO.class)
    @PostMapping("/admin/req")
    public ResponseData<AdminOrderResVO> submitOrder(@RequestBody AdminOrderReqVO adminOrderReqVO, BindingResult bindingResult) {




        return ResponseData.success(null);
    }



}
