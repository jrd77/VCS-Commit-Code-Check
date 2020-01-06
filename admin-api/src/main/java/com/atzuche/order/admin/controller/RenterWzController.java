package com.atzuche.order.admin.controller;

import com.atzuche.order.admin.vo.resp.renterWz.RenterWzCostDetailVO;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.autoyol.doc.annotation.AutoDocVersion;
import com.atzuche.order.admin.vo.resp.renterWz.RenterWzDetailResVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * RenterWzController
 *
 * @author shisong
 * @date 2020/1/5
 */
@Slf4j
@RestController
@AutoDocVersion(version = "管理后台违章押金信息")
public class RenterWzController {

    @GetMapping("/console/wz/detail")
    @AutoDocMethod(description = "违章押金信息", value = "违章押金信息",response = RenterWzDetailResVO.class)
    public ResponseData<RenterWzDetailResVO> queryWzDetailByOrderNo(@RequestParam("orderNo") String orderNo){
        if (StringUtils.isBlank(orderNo)) {
            return ResponseData.createErrorCodeResponse(ErrorCode.ORDER_NO_PARAM_ERROR.getCode(), "订单编号为空");
        }
        return ResponseData.success();
    }

    @PostMapping("/console/update/wz/cost")
    @AutoDocMethod(description = "修改违章费用", value = "修改违章费用",response = ResponseData.class)
    public ResponseData updateWzCost(@Valid @RequestBody List<RenterWzCostDetailVO> costDetails){
        if (CollectionUtils.isEmpty(costDetails)) {
            return ResponseData.success();
        }
        return ResponseData.success();
    }

    @PostMapping("/console/wz/cost/log")
    @AutoDocMethod(description = "修改违章费用", value = "修改违章费用",response = RenterWzDetailResVO.class)
    public ResponseData queryWzCostLogByOrderNo(@RequestParam("orderNo") String orderNo){
        if (StringUtils.isBlank(orderNo)) {
            return ResponseData.createErrorCodeResponse(ErrorCode.ORDER_NO_PARAM_ERROR.getCode(), "订单编号为空");
        }
        return ResponseData.success();
    }

}

