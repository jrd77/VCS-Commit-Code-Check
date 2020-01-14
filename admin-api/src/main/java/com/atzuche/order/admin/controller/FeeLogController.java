package com.atzuche.order.admin.controller;

import com.atzuche.order.admin.vo.req.fee.FeeLogReqVO;
import com.atzuche.order.admin.vo.resp.car.CarDepositReturnDetailResVO;
import com.atzuche.order.admin.vo.resp.fee.FeeLogRespVO;

import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.autoyol.doc.annotation.AutoDocVersion;
import org.springframework.web.bind.annotation.*;

@RestController
@AutoDocVersion(version = "管理后台操作日志")
public class FeeLogController {

    @AutoDocMethod(description = "管理后台操作日志", value = "管理后台操作日志", response = FeeLogRespVO.class)
    @PostMapping(value = "console/admin/log/list")
    public ResponseData <?> getFeeLog(@RequestBody FeeLogReqVO feeLogReqVO){

        return null;
    }
}
