package com.atzuche.order.admin.controller;

import com.atzuche.order.admin.constant.AdminOpTypeEnum;
import com.atzuche.order.admin.service.RenterWzService;
import com.atzuche.order.admin.service.log.AdminLogService;
import com.atzuche.order.admin.vo.req.renterWz.CarDepositTemporaryRefundReqVO;
import com.atzuche.order.admin.vo.req.renterWz.RenterWzCostReqVO;
import com.atzuche.order.admin.vo.req.renterWz.TemporaryRefundReqVO;
import com.atzuche.order.admin.vo.resp.renterWz.RenterWzDetailResVO;
import com.atzuche.order.admin.vo.resp.renterWz.WzCostLogsResVO;
import com.atzuche.order.commons.BindingResultUtil;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.autoyol.doc.annotation.AutoDocVersion;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
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
public class AdminRenterWzController extends BaseController {

    @Resource
    private RenterWzService renterWzService;

    @Resource
    private AdminLogService adminLogService;

    @GetMapping("/console/wz/detail")
    @AutoDocMethod(description = "违章押金信息", value = "违章押金信息",response = RenterWzDetailResVO.class)
    public ResponseData<RenterWzDetailResVO> queryWzDetailByOrderNo(@RequestParam("orderNo") String orderNo){
        if (StringUtils.isBlank(orderNo)) {
            return ResponseData.createErrorCodeResponse(ErrorCode.ORDER_NO_PARAM_ERROR.getCode(), "订单编号为空");
        }
        RenterWzDetailResVO res = renterWzService.queryWzDetailByOrderNo(orderNo);
        return ResponseData.success(res);
    }

    @PostMapping("/console/update/wz/cost")
    @AutoDocMethod(description = "修改违章费用", value = "修改违章费用",response = ResponseData.class)
    public ResponseData updateWzCost(@Valid @RequestBody RenterWzCostReqVO costDetail){
        if (costDetail == null || CollectionUtils.isEmpty(costDetail.getCostDetails())) {
            return ResponseData.success();
        }
        if(StringUtils.isBlank(costDetail.getOrderNo())){
            return ResponseData.createErrorCodeResponse(ErrorCode.ORDER_NO_PARAM_ERROR.getCode(), "订单编号为空");
        }

        renterWzService.updateWzCost(costDetail.getOrderNo(),costDetail.getCostDetails());
        try {
            String opDesc = costDetail.toString();
            adminLogService.insertLog(AdminOpTypeEnum.CHANGE_WZ_FEE, costDetail.getOrderNo(), opDesc);
        }catch (Exception e){
            log.warn("修改违章费用日志记录失败",e);
        }

        return ResponseData.success();
    }

    @GetMapping("/console/wz/cost/log")
    @AutoDocMethod(description = "查询违章费用日志", value = "查询违章费用日志",response = WzCostLogsResVO.class)
    public ResponseData<WzCostLogsResVO> queryWzCostLogsByOrderNo(@RequestParam("orderNo") String orderNo){
        if (StringUtils.isBlank(orderNo)) {
            return ResponseData.createErrorCodeResponse(ErrorCode.ORDER_NO_PARAM_ERROR.getCode(), "订单编号为空");
        }
        WzCostLogsResVO res = renterWzService.queryWzCostLogsByOrderNo(orderNo);

        return ResponseData.success(res);
    }

    @PostMapping("/console/add/temporaryRefund")
    @AutoDocMethod(description = "暂扣/取消暂扣违章押金", value = "暂扣/取消暂扣违章押金",response = ResponseData.class)
    public ResponseData addTemporaryRefund(@Valid @RequestBody TemporaryRefundReqVO req, BindingResult bindingResult){
        BindingResultUtil.checkBindingResult(bindingResult);

        renterWzService.addTemporaryRefund(req);
        try{
            adminLogService.insertLog(AdminOpTypeEnum.TEMPORARY_WZ_REFUND,req.getOrderNo(),req.toString());
        }catch (Exception e){
            log.warn("暂扣违章押金日志记录失败",e);
        }

        return ResponseData.success();
    }

}

