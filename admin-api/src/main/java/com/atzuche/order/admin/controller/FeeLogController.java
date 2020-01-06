package com.atzuche.order.admin.controller;

import com.atzuche.order.admin.vo.req.fee.FeeLogReqVO;
import com.atzuche.order.admin.vo.resp.car.CarDepositReturnDetailResVO;
import com.atzuche.order.admin.vo.resp.fee.FeeLogRespVO;
import com.autoyol.car.api.model.vo.ResponseObject;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.autoyol.doc.annotation.AutoDocVersion;
import org.springframework.web.bind.annotation.*;

@RestController
@AutoDocVersion(version = "费用操作日志")
public class FeeLogController {

    @AutoDocMethod(description = "费用操作日志", value = "费用操作日志", response = FeeLogRespVO.class)
    @PostMapping(value = "/fee/log/list")
    public ResponseObject<?> getFeeLog(@RequestBody FeeLogReqVO feeLogReqVO){

        return null;
    }
}
