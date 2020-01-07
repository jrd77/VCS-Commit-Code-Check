package com.atzuche.order.admin.controller.order.remark;

import com.atzuche.order.admin.service.remark.OrderRemarkService;
import com.atzuche.order.admin.vo.req.remark.*;
import com.atzuche.order.admin.vo.resp.remark.*;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.autoyol.doc.annotation.AutoDocVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/console/order/remark")
@RestController
@AutoDocVersion(version = "订单备注接口文档")
public class OrderRemarkController {

    @Autowired
    OrderRemarkService orderRemarkService;

	@AutoDocMethod(description = "备注总览", value = "备注总览", response = OrderRemarkOverviewListResponseVO.class)
	@GetMapping("/overview")
	public ResponseData<OrderRemarkOverviewListResponseVO> getOverview(@RequestBody OrderRemarkRequestVO orderRemarkRequestVO, BindingResult bindingResult) {
		return ResponseData.success(orderRemarkService.getOrderRemarkOverview(orderRemarkRequestVO));
	}

    @AutoDocMethod(description = "备注查询列表", value = "备注查询列表", response = OrderRemarkPageListResponseVO.class)
    @GetMapping("/list")
    public ResponseData<OrderRemarkPageListResponseVO> list(@RequestBody OrderRemarkListRequestVO orderRemarkListRequestVO, BindingResult bindingResult) {
        return ResponseData.success(null);
    }

    @AutoDocMethod(description = "备注日志查询列表", value = "备注日志查询列表", response = OrderRemarkLogPageListResponseVO.class)
    @GetMapping("/log/list")
    public ResponseData<OrderRemarkLogPageListResponseVO> logList(@RequestBody OrderRemarkLogListRequestVO orderRemarkLogListRequestVO, BindingResult bindingResult) {
        return ResponseData.success(null);
    }


    @AutoDocMethod(description = "添加备注", value = "添加备注", response = ResponseData.class)
    @PostMapping("/add")
    public ResponseData<ResponseData> addOrderRemark(@RequestBody OrderRemarkAdditionRequestVO orderRemarkAdditionRequestVO, BindingResult bindingResult) {
        orderRemarkService.addOrderRemark(orderRemarkAdditionRequestVO);
        return ResponseData.success(null);
    }

    @AutoDocMethod(description = "删除备注", value = "删除备注", response = ResponseData.class)
    @DeleteMapping("/delete")
    public ResponseData<ResponseData> delete(@RequestBody OrderRemarkDeleteRequestVO orderRemarkDeleteRequestVO, BindingResult bindingResult) {
        return ResponseData.success(null);
    }

    @AutoDocMethod(description = "编辑备注", value = "编辑备注", response = ResponseData.class)
    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    public ResponseData<ResponseData> update(@RequestBody OrderRemarkUpdateRequestVO orderRemarkUpdateRequestVO, BindingResult bindingResult) {
        return ResponseData.success(null);
    }

    @AutoDocMethod(description = "获取备注信息", value = "获取备注信息", response = OrderRemarkResponseVO.class)
    @GetMapping("/information")
    public ResponseData<ResponseData> getRemarkInformation(@RequestBody OrderRemarkInformationRequestVO orderRemarkInformationRequestVO, BindingResult bindingResult) {
        return ResponseData.success(null);
    }

}
