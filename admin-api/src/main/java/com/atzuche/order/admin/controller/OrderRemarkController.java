package com.atzuche.order.admin.controller;

import com.atzuche.order.admin.vo.req.remark.*;
import com.atzuche.order.admin.vo.resp.remark.OrderRemarkLogPageListResponseVO;
import com.atzuche.order.admin.vo.resp.remark.OrderRemarkOverviewResponseVO;
import com.atzuche.order.admin.vo.resp.remark.OrderRemarkPageListResponseVO;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.autoyol.doc.annotation.AutoDocVersion;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/console/order/remark")
@RestController
@AutoDocVersion(version = "订单备注接口文档")
public class OrderRemarkController {

	@AutoDocMethod(description = "备注总览", value = "备注总览", response = OrderRemarkOverviewResponseVO.class)
	@GetMapping("/overview")
	public ResponseData<OrderRemarkOverviewResponseVO> overview(@RequestBody OrderRemarkRequestVO orderRemarkRequestVO, BindingResult bindingResult) {
		return ResponseData.success(null);
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
    public ResponseData<ResponseData> add(@RequestBody OrderRemarkAdditionRequestVO orderRemarkAdditionRequestVO, BindingResult bindingResult) {
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


}
