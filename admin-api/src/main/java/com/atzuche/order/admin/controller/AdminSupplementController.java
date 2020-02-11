package com.atzuche.order.admin.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.admin.service.AdminSupplementService;
import com.atzuche.order.commons.entity.dto.OrderSupplementDetailDTO;
import com.atzuche.order.commons.vo.res.rentcosts.OrderSupplementDetailEntity;
import com.autoyol.commons.web.ErrorCode;
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
	
	
    @AutoDocMethod(description = "订单补付列表", value = "订单补付列表",response = SupplementRecord.class)
    @RequestMapping(value="console/order/supplement/list",method = RequestMethod.GET)
    public ResponseData<SupplementRecord> listSupplement(@RequestParam(value="orderNo",required = true) String orderNo){
        log.info("AdminSupplementController.listSupplement orderNo=[{}]", orderNo);
        return ResponseData.success(new SupplementRecord(adminSupplementService.listSupplement(orderNo)));
    }
	
	
    @AutoDocMethod(description = "新增补付", value = "新增补付",response = ResponseData.class)
    @RequestMapping(value="console/order/supplement/add",method = RequestMethod.POST)
    public ResponseData addSupplement(@Valid @RequestBody OrderSupplementDetailDTO orderSupplementDetailDTO, BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            Optional<FieldError> error = bindingResult.getFieldErrors().stream().findFirst();
            return new ResponseData<>(ErrorCode.INPUT_ERROR.getCode(), error.isPresent() ?
                    error.get().getDefaultMessage() : ErrorCode.INPUT_ERROR.getText());
        }
        log.info("AdminSupplementController.addSupplement orderSupplementDetailDTO=[{}]", JSON.toJSONString(orderSupplementDetailDTO));
        adminSupplementService.addSupplement(orderSupplementDetailDTO);
        return ResponseData.success();
    }
	
	
    @AutoDocMethod(description = "删除补付", value = "删除补付",response = ResponseData.class)
    @RequestMapping(value="console/order/supplement/del",method = RequestMethod.POST)
    public ResponseData delSupplement(@RequestParam(value="id",required = true) Integer id){
        log.info("AdminSupplementController.delSupplement id=[{}]", id);
        adminSupplementService.delSupplement(id);
        return ResponseData.success();
    }
    
    
    public static class SupplementRecord {
		private List<OrderSupplementDetailEntity> list = new ArrayList<>();

		public List<OrderSupplementDetailEntity> getList() {
			return list;
		}

		public void setList(List<OrderSupplementDetailEntity> list) {
			this.list = list;
		}

		public SupplementRecord(List<OrderSupplementDetailEntity> list) {
			this.list = list;
		}
	}
}
