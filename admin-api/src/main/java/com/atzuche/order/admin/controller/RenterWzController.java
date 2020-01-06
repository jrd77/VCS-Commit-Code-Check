package com.atzuche.order.admin.controller;

import com.atzuche.order.admin.vo.req.renterWz.TemporaryRefundReqVO;
import com.atzuche.order.admin.vo.resp.renterWz.TemporaryRefundLogsResVO;
import com.atzuche.order.admin.vo.resp.renterWz.WzCostLogsResVO;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.autoyol.doc.annotation.AutoDocVersion;
import com.atzuche.order.admin.vo.resp.renterWz.RenterWzDetailResVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * RenterWzController
 *
 * @author shisong
 * @date 2020/1/5
 */
@Slf4j
@RestController
@AutoDocVersion(version = "管理后台违章押金信息")
public class RenterWzController extends BaseController {

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
    public ResponseData updateWzCost(@Valid @RequestBody RenterWzDetailResVO costDetail){
        if (costDetail == null || CollectionUtils.isEmpty(costDetail.getCostDetails())) {
            return ResponseData.success();
        }
        return ResponseData.success();
    }

    @GetMapping("/console/wz/cost/log")
    @AutoDocMethod(description = "查询违章费用日志", value = "查询违章费用日志",response = WzCostLogsResVO.class)
    public ResponseData<WzCostLogsResVO> queryWzCostLogsByOrderNo(@RequestParam("orderNo") String orderNo){
        if (StringUtils.isBlank(orderNo)) {
            return ResponseData.createErrorCodeResponse(ErrorCode.ORDER_NO_PARAM_ERROR.getCode(), "订单编号为空");
        }
        return ResponseData.success();
    }

    @GetMapping("/console/temporaryRefund/log")
    @AutoDocMethod(description = "查询暂扣返还日志", value = "查询暂扣返还日志",response = TemporaryRefundLogsResVO.class)
    public ResponseData<TemporaryRefundLogsResVO> queryTemporaryRefundLogsByOrderNo(@RequestParam("orderNo") String orderNo){
        if (StringUtils.isBlank(orderNo)) {
            return ResponseData.createErrorCodeResponse(ErrorCode.ORDER_NO_PARAM_ERROR.getCode(), "订单编号为空");
        }
        return ResponseData.success();
    }

    @PostMapping("/console/add/temporaryRefund")
    @AutoDocMethod(description = "添加暂扣返还", value = "添加暂扣返还",response = ResponseData.class)
    public ResponseData addTemporaryRefund(@Valid @RequestBody TemporaryRefundReqVO req, BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            return validate(bindingResult);
        }
        return ResponseData.success();
    }

}

