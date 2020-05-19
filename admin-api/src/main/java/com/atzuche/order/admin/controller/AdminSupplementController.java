package com.atzuche.order.admin.controller;


import javax.validation.Valid;

import com.atzuche.order.admin.common.Page;
import com.atzuche.order.admin.service.log.SupplementSendmsgLogService;
import com.atzuche.order.admin.vo.req.supplement.BufuMessagePushRecordListReqVO;
import com.atzuche.order.admin.vo.req.supplement.MessagePushSendReqVO;
import com.atzuche.order.admin.vo.resp.supplement.MessagePushRecordListResVO;
import com.atzuche.order.commons.BindingResultUtil;
import com.dianping.cat.Cat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.admin.service.AdminSupplementService;
import com.atzuche.order.admin.vo.req.DelSupplementReqVO;
import com.atzuche.order.commons.entity.dto.OrderSupplementDetailDTO;
import com.atzuche.order.commons.vo.res.rentcosts.SupplementVO;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.autoyol.doc.annotation.AutoDocVersion;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@AutoDocVersion(version = "订单补付")
public class AdminSupplementController {
	@Autowired
	private AdminSupplementService adminSupplementService;

    @Autowired
    private SupplementSendmsgLogService supplementSendmsgLogService;


    @AutoDocMethod(description = "订单补付列表", value = "订单补付列表",response = SupplementVO.class)
    @RequestMapping(value="console/order/supplement/list",method = RequestMethod.GET)
    public ResponseData<SupplementVO> listSupplement(@RequestParam(value="orderNo",required = true) String orderNo){
        log.info("AdminSupplementController.listSupplement orderNo=[{}]", orderNo);
        return ResponseData.success(adminSupplementService.listSupplement(orderNo));
    }


    @AutoDocMethod(description = "新增补付", value = "新增补付",response = ResponseData.class)
    @RequestMapping(value="console/order/supplement/add",method = RequestMethod.POST)
    public ResponseData addSupplement(@Valid @RequestBody OrderSupplementDetailDTO orderSupplementDetailDTO, BindingResult bindingResult){
        BindingResultUtil.checkBindingResult(bindingResult);
        log.info("AdminSupplementController.addSupplement orderSupplementDetailDTO=[{}]", JSON.toJSONString(orderSupplementDetailDTO));
        adminSupplementService.addSupplement(orderSupplementDetailDTO);
        return ResponseData.success();
    }


    @AutoDocMethod(description = "删除补付", value = "删除补付",response = ResponseData.class)
    @RequestMapping(value="console/order/supplement/del",method = RequestMethod.POST)
    public ResponseData delSupplement(@Valid @RequestBody DelSupplementReqVO delReq, BindingResult bindingResult){
    	BindingResultUtil.checkBindingResult(bindingResult);
        log.info("AdminSupplementController.delSupplement delReq=[{}]", delReq);
        adminSupplementService.delSupplement(delReq.getId());
        return ResponseData.success();
    }
    @AutoDocMethod(description = "新后台发送补付消息", value = "新后台发送补付消息")
    @RequestMapping(value = "console/order/supplement/sendMsg", method = RequestMethod.POST)
    public ResponseData<?> send(@Validated @RequestBody MessagePushSendReqVO messagePushSendReqVO, BindingResult bindingResult) {
        log.info("发送消息开始,messagePushSendReqVO:[{}]", JSON.toJSONString(messagePushSendReqVO));
        BindingResultUtil.checkBindingResult(bindingResult);
        Integer res;
        try {
            res = supplementSendmsgLogService.insert(messagePushSendReqVO);
        } catch (Exception e) {
            log.error("发送消息异常,messagePushSendReqVO:[{}]", JSON.toJSONString(messagePushSendReqVO), e);
            Cat.logError("发送消息异常," + JSON.toJSONString(messagePushSendReqVO), e);
            return ResponseData.error();
        }
        log.info("发送消息结束,res:[{}]", JSON.toJSONString(res));
        return ResponseData.success();
    }

    @AutoDocMethod(description = "查询新后台补付消息列表", value = "查询新后台补付消息列表", response = MessagePushRecordListResVO.class)
    @RequestMapping(value = "console/order/supplement/massageList", method = RequestMethod.GET)
    public ResponseData<?> list(@Validated BufuMessagePushRecordListReqVO reqVO, BindingResult bindingResult) {
        log.info("查询消息列表开始,bufuMessagePushRecordListReqNewVO:[{}]", JSON.toJSONString(reqVO));
        BindingResultUtil.checkBindingResult(bindingResult);
        try {
            Page<MessagePushRecordListResVO> bufuRecordListByPage = supplementSendmsgLogService.selectByPage(reqVO);
            log.info("查询消息列表结束,bufuRecordListByPage:[{}]", JSON.toJSONString(bufuRecordListByPage));
            return ResponseData.success(bufuRecordListByPage);
        } catch (Exception e) {
            log.error("查询补付消息列表异常,bufuMessagePushRecordListReqVO:[{}]",JSON.toJSONString(reqVO), e);
            Cat.logError("查询补付消息列表异常," + JSON.toJSONString(reqVO), e);
            return ResponseData.error();
        }
    }
}
