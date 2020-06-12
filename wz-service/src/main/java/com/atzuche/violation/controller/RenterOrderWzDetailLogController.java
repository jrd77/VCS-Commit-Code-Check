package com.atzuche.violation.controller;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.BindingResultUtil;
import com.atzuche.order.commons.entity.dto.RenterOrderWzDetailLogList;
import com.atzuche.violation.common.PageParam;
import com.atzuche.violation.service.RenterOrderWzDetailLogService;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.autoyol.doc.annotation.AutoDocVersion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;


@Slf4j
@RestController
@RequestMapping("/console/wzDetaillog")
@AutoDocVersion(version = "违章日志接口文档")
public class RenterOrderWzDetailLogController {
    @Autowired
    private RenterOrderWzDetailLogService renterOrderWzDetailLogService;
    
    /*
     * @Author ZhangBin
     * @Date 2020/6/9 10:53
     * @Description: 违章日志查询
     * 
     **/
    @RequestMapping("/queryList")
    @AutoDocMethod(description = "违章日志列表", value = "违章日志列表", response = RenterOrderWzDetailLogList.class)
    public ResponseData<RenterOrderWzDetailLogList> queryList(@Valid PageParam pageParam, BindingResult bindingResult){
        BindingResultUtil.checkBindingResult(bindingResult);
        log.info("违章日志查询pageBean={}", JSON.toJSONString(pageParam));
        RenterOrderWzDetailLogList data =  renterOrderWzDetailLogService.queryList(pageParam);
        log.info("违章日志查询-参数pageBean={},data={}", JSON.toJSONString(pageParam),JSON.toJSONString(data));
        return ResponseData.success(data);
    }

}
