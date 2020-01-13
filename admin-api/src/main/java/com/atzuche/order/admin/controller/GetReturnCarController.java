/**
 * 
 */
package com.atzuche.order.admin.controller;

import java.time.LocalDateTime;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.atzuche.order.admin.service.GetReturnCarService;
import com.atzuche.order.admin.vo.req.cost.GetReturnRequestVO;
import com.atzuche.order.admin.vo.resp.cost.GetReturnCostVO;
import com.atzuche.order.commons.LocalDateTimeUtils;
import com.autoyol.commons.utils.StringUtils;
import com.autoyol.commons.web.ErrorCode;
import com.autoyol.commons.web.ResponseData;
import com.autoyol.doc.annotation.AutoDocMethod;
import com.autoyol.doc.annotation.AutoDocVersion;
import com.dianping.cat.Cat;

/**
 * @author jing.huang
 * GetBackCarController 取还车费用记录
 */
@RequestMapping("/console/getreturncar/")
@RestController
@AutoDocVersion(version = "普通订单 - 确认车辆-取还车费用计算接口文档")
public class GetReturnCarController {

	private static final Logger logger = LoggerFactory.getLogger(GetReturnCarController.class);
	@Autowired
	GetReturnCarService GetReturnCarService;
	

	@AutoDocMethod(description = "计算取车费用或还车费用", value = "计算取车费用或还车费用", response = GetReturnCostVO.class)
//	@GetMapping("calculateGetOrReturnCost")
	@RequestMapping(value="calculateGetOrReturnCost",method = RequestMethod.POST)
	public ResponseData calculateGetOrReturnCost(@RequestBody GetReturnRequestVO getReturnRequestVO, HttpServletRequest request, HttpServletResponse response,BindingResult bindingResult) {
        logger.info("calculateGetOrReturnCost controller params={}",getReturnRequestVO.toString());
		if (bindingResult.hasErrors()) {
            return new ResponseData<>(ErrorCode.INPUT_ERROR.getCode(), ErrorCode.INPUT_ERROR.getText());
        }
        
        try {
        	//前置检查
        	String flag = getReturnRequestVO.getFlag();
    		if("1".equals(flag)) {  //取车
    			if(StringUtils.isBlank(getReturnRequestVO.getGetCarLan())) {
    				return new ResponseData<>(ErrorCode.INPUT_ERROR.getCode(), "取车维度不能为空");
    			}else if(StringUtils.isBlank(getReturnRequestVO.getGetCarLon())) {
    				return new ResponseData<>(ErrorCode.INPUT_ERROR.getCode(), "取车经度不能为空");
    			}
    		}else if("2".equals(flag)){ //还车
    			if(StringUtils.isBlank(getReturnRequestVO.getReturnCarLan())) {
    				return new ResponseData<>(ErrorCode.INPUT_ERROR.getCode(), "还车维度不能为空");
    			}else if(StringUtils.isBlank(getReturnRequestVO.getReturnCarLon())) {
    				return new ResponseData<>(ErrorCode.INPUT_ERROR.getCode(), "还车经度不能为空");
    			}
    		}
    		
    		LocalDateTime startTime = null;
    		LocalDateTime endTime = null;
    		try {
    			startTime = LocalDateTimeUtils.parseStringToDateTime(getReturnRequestVO.getRentTime());
			} catch (Exception e) {
				return new ResponseData<>(ErrorCode.INPUT_ERROR.getCode(), "起租时间数据格式错误，应为yyyy-MM-dd HH:mm:ss");
			}
    		try {
    			endTime = LocalDateTimeUtils.parseStringToDateTime(getReturnRequestVO.getRevertTime());
			} catch (Exception e) {
				return new ResponseData<>(ErrorCode.INPUT_ERROR.getCode(), "还车时间数据格式错误，应为yyyy-MM-dd HH:mm:ss");
			}
    		
    		
        	GetReturnCostVO resp = GetReturnCarService.calculateGetOrReturnCost(getReturnRequestVO,startTime,endTime);
        	return ResponseData.success(resp);
		} catch (Exception e) {
			Cat.logError("calculateGetOrReturnCost exception params="+getReturnRequestVO.toString(),e);
			logger.error("calculateGetOrReturnCost exception params="+getReturnRequestVO.toString(),e);
			return ResponseData.error();
		}
		
	}
	
	
}
