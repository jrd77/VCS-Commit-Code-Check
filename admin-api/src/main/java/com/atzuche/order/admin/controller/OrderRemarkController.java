package com.atzuche.order.admin.controller;

import com.atzuche.order.admin.vo.request.*;
import com.atzuche.order.admin.vo.response.*;
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
	@PostMapping("/overview")
	public ResponseData<?> overview(@RequestBody OrderRemarkRequestVO orderRemarkRequestVO, BindingResult bindingResult) {
		return ResponseData.success(null);
	}

    @AutoDocMethod(description = "备注查询列表", value = "备注查询列表", response = OrderRemarkPageListResponseVO.class)
    @PostMapping("/list")
    public ResponseData<?> list(@RequestBody OrderRemarkListRequestVO orderRemarkListRequestVO, BindingResult bindingResult) {
        return ResponseData.success(null);
    }

    @AutoDocMethod(description = "备注日志查询列表", value = "备注日志查询列表", response = OrderRemarkLogPageListResponseVO.class)
    @PostMapping("/log/list")
    public ResponseData<?> logList(@RequestBody OrderRemarkLogListRequestVO orderRemarkLogListRequestVO, BindingResult bindingResult) {
        return ResponseData.success(null);
    }


    @AutoDocMethod(description = "添加备注", value = "添加备注", response = ResponseData.class)
    @PostMapping("/add")
    public ResponseData<?> add(@RequestBody OrderRemarkAdditionRequestVO orderRemarkAdditionRequestVO, BindingResult bindingResult) {
        return ResponseData.success(null);
    }

    @AutoDocMethod(description = "删除备注", value = "删除备注", response = ResponseData.class)
    @PostMapping("/del")
    public ResponseData<?> del(@RequestBody OrderRemarkDeleteRequestVO orderRemarkDeleteRequestVO, BindingResult bindingResult) {
        return ResponseData.success(null);
    }

    @AutoDocMethod(description = "编辑备注", value = "编辑备注", response = ResponseData.class)
    @PostMapping("/update")
    public ResponseData<?> update(@RequestBody OrderRemarkUpdateRequestVO orderRemarkUpdateRequestVO, BindingResult bindingResult) {
        return ResponseData.success(null);
    }


}
